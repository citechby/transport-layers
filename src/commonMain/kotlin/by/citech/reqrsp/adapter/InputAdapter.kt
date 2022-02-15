package by.citech.reqrsp.adapter

import by.citech.reqrsp.data.RspMeta
import by.citech.reqrsp.data.ReqMeta

/**
 * Input messages adapter
 */
abstract class InputAdapter<I> {
    abstract fun extractRspMeta(body: I): RspMeta?
    abstract fun extractReqMeta(body: I): ReqMeta?
}
