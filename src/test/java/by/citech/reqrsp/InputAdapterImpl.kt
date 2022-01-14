package by.citech.reqrsp

import by.citech.reqrsp.adapter.InputAdapter
import by.citech.reqrsp.data.ReqMeta
import by.citech.reqrsp.data.RspMeta

class InputAdapterImpl : InputAdapter<InMsg> {
    override fun extractRspMeta(body: InMsg): RspMeta {
        return RspMeta(
            body.rspReqId,
            body.rspReqCnt,
            body.rspCnt
        )
    }

    override fun extractReqMeta(body: InMsg): ReqMeta {
        return ReqMeta(
            body.reqId,
            body.reqCnt
        )
    }
}
