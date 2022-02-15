package by.citech.reqrsp

import by.citech.reqrsp.adapter.*
import by.citech.reqrsp.adapter.log.LogDst
import by.citech.reqrsp.adapter.log.LogLvl
import by.citech.reqrsp.context.LocalReqCtx
import by.citech.reqrsp.context.RemoteReqCtx
import by.citech.reqrsp.data.ReqMeta
import by.citech.reqrsp.data.Rsp
import by.citech.reqrsp.data.RspMeta
import by.citech.reqrsp.data.RspResult

internal class ReqRspLayerImpl<O, I>(
    private val logId: String,
    private val inputAdapter: InputAdapter<I>,
    private val outputAdapter: OutputAdapter<O>,
    private val reqHandler: ReqHandler<I, O>,
    private val transportAdapter: TransportAdapter<O>,
    private val defaultReqTimings: ReqTimings,
    private val layerTimings: LayerTimings,
    private val logDst: LogDst
) : ReqRspLayer<O, I>() {

    private val localReqMap: MutableMap<String, LocalReqCtx<I, O>> = mutableMapOf()
    private val remoteReqMap: MutableMap<String, RemoteReqCtx<O>> = mutableMapOf()
    private var reqIdSrc = 0

    override fun processState(currTs: Long) {
        val staleRemoteReq = mutableListOf<String>()
        remoteReqMap.forEach { (reqId: String, ctx: RemoteReqCtx<O>) ->
            if (currTs > ctx.handledTs + layerTimings.reqStoreTimeout)
                staleRemoteReq.add(reqId)
        }
        staleRemoteReq.forEach(remoteReqMap::remove)

        val staleLocalReq = mutableListOf<String>()
        localReqMap.forEach { (reqId: String, ctx: LocalReqCtx<I, O>) ->
            val handledTs = ctx.handledTs
            if (handledTs != null) {
                if (currTs > handledTs + layerTimings.rspStoreTimeout)
                    staleLocalReq.add(reqId)
            } else if (currTs > ctx.timeoutAfterTs) {
                ctx.handledTs = currTs
                ctx.rspHandler?.handle(Rsp(RspResult.RSP_TIMEOUT, null, currTs))
            }
        }
        staleLocalReq.forEach(localReqMap::remove)

        localReqMap.forEach { (reqId: String?, ctx: LocalReqCtx<I, O>) ->
            if (ctx.handledTs == null && currTs > ctx.nextRepeatAfterTs) {
                val reqCnt = ctx.reqCnt + 1
                ctx.reqCnt = reqCnt
                ctx.nextRepeatAfterTs = ctx.nextRepeatAfterTs + ctx.repeatInterval
                transportAdapter.send(
                    outputAdapter.insertReqMeta(
                        ctx.reqBody,
                        ReqMeta(reqId, reqCnt)
                    )
                )
            }
        }
    }

    override fun output(
        reqBody: O,
        currTs: Long,
        reqTimings: ReqTimings?,
        rspHandler: RspHandler<I>?
    ) {
        val timings = reqTimings ?: defaultReqTimings
        val reqId = reqIdSrc.toString()
        reqIdSrc += 2
        val reqCnt = 1L
        val ctx = LocalReqCtx(
            reqBody,
            timings.reqRepeatInterval,
            currTs + timings.rspTimeout,
            rspHandler,
            reqCnt,
            currTs + timings.reqRepeatInterval,
            null
        )
        localReqMap[reqId] = ctx
        transportAdapter.send(
            outputAdapter.insertReqMeta(
                reqBody,
                ReqMeta(reqId, reqCnt)
            )
        )
    }

    override fun input(rspBody: I, currTs: Long): InputResult {
        val handleResult = InputResult(
            handleReq(rspBody, currTs),
            handleRsp(rspBody, currTs)
        )
        logDst.log(LogLvl.DEBUG, "$logId handled=$handleResult")
        return handleResult
    }

    private fun handleReq(body: I, ts: Long): ReqInputResult {
        val reqMeta = inputAdapter.extractReqMeta(body) ?: return ReqInputResult.NO_REQ
        val reqId = reqMeta.reqId ?: return ReqInputResult.NO_REQ

        val handleResult: ReqInputResult
        var ctx = remoteReqMap[reqId]
        if (ctx == null) {
            logDst.log(LogLvl.DEBUG, "$logId reqId=$reqId, handling first time")
            ctx = RemoteReqCtx(reqHandler.handle(body), ts, 0L)
            remoteReqMap[reqId] = ctx
            handleResult = ReqInputResult.CONSUMED
        } else {
            logDst.log(LogLvl.DEBUG, "$logId reqId=$reqId, already handled")
            handleResult = ReqInputResult.DUPLICATE
        }

        val rspBody = ctx.rspBodySrc.tryTake()
        if (rspBody != null) {
            logDst.log(LogLvl.DEBUG, "$logId reqId=$reqId, rspBody available, sending")
            val rspCnt = ctx.rspCnt + 1
            ctx.rspCnt = rspCnt
            transportAdapter.send(
                outputAdapter.insertRspMeta(
                    rspBody,
                    RspMeta(reqId, reqMeta.reqCnt, rspCnt)
                )
            )
        } else {
            logDst.log(LogLvl.DEBUG, "$logId reqId=$reqId, rspBody not available, not sending")
        }

        return handleResult
    }

    private fun handleRsp(body: I, ts: Long): RspInputResult {
        val rspMeta = inputAdapter.extractRspMeta(body) ?: return RspInputResult.NO_RSP
        val reqId = rspMeta.rspReqId ?: return RspInputResult.NO_RSP

        val ctx = localReqMap[reqId]
        if (ctx == null) {
            // no such handler: rare case, generally should not happen
            logDst.log(LogLvl.DEBUG, "$logId rspReqId=$reqId, no handler, msg=$body")
            return RspInputResult.UNKNOWN_REQ
        }

        if (ctx.handledTs == null) {
            // not handled: standard case, most frequent
            logDst.log(LogLvl.DEBUG, "$logId rspReqId=$reqId, handling")
            ctx.handledTs = ts
            val handler = ctx.rspHandler
            return if (handler != null) {
                handler.handle(Rsp(RspResult.OK, body, ts))
                RspInputResult.CONSUMED_WITH_HANDLER
            } else {
                RspInputResult.CONSUMED_WITHOUT_HANDLER
            }
        }

        // already handled: standard case, could happen quite often
        logDst.log(LogLvl.DEBUG, "$logId rspReqId=$reqId, already handled at ${ctx.handledTs}")
        return RspInputResult.DUPLICATE
    }
}
