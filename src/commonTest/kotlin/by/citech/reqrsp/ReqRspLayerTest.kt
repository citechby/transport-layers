package by.citech.reqrsp

import by.citech.reqrsp.adapter.*
import by.citech.reqrsp.adapter.outputsrc.OutputSrc
import by.citech.reqrsp.data.ReqMeta
import by.citech.reqrsp.data.Rsp
import by.citech.reqrsp.data.RspMeta
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ReqRspLayerTest {

    @Test
    fun test() {
        var inAdapterReqCnt = 0
        var inAdapterRspCnt = 0

        val inAdapter = object : InputAdapter<InMsg>() {
            override fun extractRspMeta(body: InMsg): RspMeta {
                ++inAdapterRspCnt
                return RspMeta(
                    body.rspReqId,
                    body.rspReqCnt,
                    body.rspCnt
                )
            }

            override fun extractReqMeta(body: InMsg): ReqMeta {
                ++inAdapterReqCnt
                return ReqMeta(
                    body.reqId,
                    body.reqCnt
                )
            }
        }

        var outAdapterReqCnt = 0
        var outAdapterRspCnt = 0
        val outAdapter = object : OutputAdapter<OutMsg>() {
            override fun insertReqMeta(body: OutMsg, m: ReqMeta): OutMsg {
                ++outAdapterReqCnt
                return body.copy(
                    reqId = m.reqId,
                    reqCnt = m.reqCnt
                )
            }

            override fun insertRspMeta(body: OutMsg, m: RspMeta): OutMsg {
                ++outAdapterRspCnt
                return body.copy(
                    rspReqId = m.rspReqId,
                    rspReqCnt = m.rspReqCnt,
                    rspCnt = m.rspCnt
                )
            }
        }

        var transAdapterCnt = 0
        var reqId: String? = null
        val transAdapter = object : TransportAdapter<OutMsg>() {
            override fun send(body: OutMsg) {
                ++transAdapterCnt
                reqId = body.reqId
            }
        }

        var reqHandlerCnt = 0
        var outSrcCnt = 0
        val reqHandler = object : ReqHandler<InMsg, OutMsg>() {
            override fun handle(body: InMsg): OutputSrc<OutMsg> {
                println("req: $body")
                ++reqHandlerCnt
                return object : OutputSrc<OutMsg>() {
                    override fun tryTake(): OutMsg {
                        ++outSrcCnt
                        return OutMsg(
                            data = listOf("ACK"),
                            rspReqId = body.reqId
                        )
                    }
                }
            }

        }

        // create delivery layer
        val layer = ReqRspLayerFactory<InMsg, OutMsg>().create(
            "test-id",
            inAdapter,
            outAdapter,
            reqHandler,
            transAdapter,
            ReqTimings(100, 10),
            LayerTimings(500, 500)
        ) { logLvl, msg ->
            println("$logLvl $msg")
        }

        var currTs = 0L

        // send msg
        var rspHandleCnt = 0
        layer.output(
            OutMsg(listOf("data1, data2")),
            currTs,
            ReqTimings(100, 10),
            object: RspHandler<InMsg>() {
                override fun handle(rsp: Rsp<InMsg>) {
                    ++rspHandleCnt
                }

            }
        )

        // process state 2 times
        currTs += 10
        layer.processState(currTs)
        currTs += 10
        layer.processState(currTs)
        assertEquals(0, inAdapterReqCnt)  // no msg received
        assertEquals(0, inAdapterRspCnt)  // no msg received
        assertEquals(2, outAdapterReqCnt) // 3 req sent
        assertEquals(0, outAdapterRspCnt) // no rsp sent
        assertEquals(2, transAdapterCnt)  // 2 msg sent
        assertEquals("0", reqId)          // first message id is "0"
        assertEquals(0, reqHandlerCnt)    // no req received
        assertEquals(0, outSrcCnt)        // no req received
        assertEquals(0, rspHandleCnt)     // no rsp received

        // receive rsp message first time
        val goodRsp = InMsg("RSP", rspReqId = reqId)
        currTs += 10
        assertEquals(
            layer.input(goodRsp, currTs),
            InputResult(ReqInputResult.NO_REQ, RspInputResult.CONSUMED_WITH_HANDLER)
        )
        assertEquals(1, inAdapterReqCnt) // 1 msg received
        assertEquals(1, inAdapterRspCnt) // 1 msg received
        assertEquals(1, rspHandleCnt)    // 1 rsp received, callback executed

        // receive rsp message second time (duplicate)
        currTs += 10
        assertEquals(
            layer.input(goodRsp, currTs),
            InputResult(ReqInputResult.NO_REQ, RspInputResult.DUPLICATE)
        )
        assertEquals(2, inAdapterReqCnt) // 2 msg received
        assertEquals(2, inAdapterRspCnt) // 2 msg received
        assertEquals(1, rspHandleCnt)    // 1 rsp duplicate received, no callback on duplicate

        // receive rsp message for unknown req
        val unknownRsp = InMsg("RSP", rspReqId = "S)DJ)SAJDSJKLJK")
        currTs += 10
        assertEquals(
            layer.input(unknownRsp, currTs),
            InputResult(ReqInputResult.NO_REQ, RspInputResult.UNKNOWN_REQ)
        )
        assertEquals(3, inAdapterReqCnt) // 3 msg received
        assertEquals(3, inAdapterRspCnt) // 3 msg received

        // process state once again
        currTs += 10
        layer.processState(currTs)
        assertEquals(2, transAdapterCnt)  // no more msg after rsp

        // receive req msg
        val goodReq = InMsg("REQ", reqId = "1")
        currTs += 10
        assertEquals(
            layer.input(goodReq, currTs),
            InputResult(ReqInputResult.CONSUMED, RspInputResult.NO_RSP)
        )
        assertEquals(4, inAdapterReqCnt)  // 4 msg received
        assertEquals(4, inAdapterRspCnt)  // 4 msg received
        assertEquals(2, outAdapterReqCnt) // still 2 req sent
        assertEquals(1, outAdapterRspCnt) // rsp sent
        assertEquals(3, transAdapterCnt)  // 3 msg sent
        assertEquals(1, reqHandlerCnt)    // req received, callback executed
        assertEquals(1, outSrcCnt)        // req received
        assertEquals(1, rspHandleCnt)     // still 1 rsp received

        // receive req msg second time (duplicate)
        currTs += 10
        assertEquals(
            layer.input(goodReq, currTs),
            InputResult(ReqInputResult.DUPLICATE, RspInputResult.NO_RSP)
        )
        assertEquals(1, reqHandlerCnt) // req duplicate received, no callback on duplicate
    }
}
