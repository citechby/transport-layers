package by.citech.reqrsp

import by.citech.reqrsp.adapter.OutputAdapter
import by.citech.reqrsp.data.ReqMeta
import by.citech.reqrsp.data.RspMeta

class OutputAdapterImpl : OutputAdapter<OutMsg> {
    override fun insertReqMeta(body: OutMsg, m: ReqMeta): OutMsg {
        return body.copy(
            reqId = m.reqId,
            reqCnt = m.reqCnt
        )
    }

    override fun insertRspMeta(body: OutMsg, m: RspMeta): OutMsg {
        return body.copy(
            rspReqId = m.rspReqId,
            rspReqCnt = m.rspReqCnt,
            rspCnt = m.rspCnt
        )
    }
}
