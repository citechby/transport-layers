package by.citech.reqrsp.adapter

import by.citech.reqrsp.data.ReqMeta
import by.citech.reqrsp.data.RspMeta

/**
 * Output messages adapter
 */
interface OutputAdapter<O> {
    fun insertReqMeta(body: O, m: ReqMeta): O
    fun insertRspMeta(body: O, m: RspMeta): O
}
