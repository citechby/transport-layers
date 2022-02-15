package by.citech.reqrsp.adapter

import by.citech.reqrsp.data.ReqMeta
import by.citech.reqrsp.data.RspMeta

/**
 * Output messages adapter
 */
abstract class OutputAdapter<O> {
    abstract fun insertReqMeta(body: O, m: ReqMeta): O
    abstract fun insertRspMeta(body: O, m: RspMeta): O
}
