package by.citech.reqrsp.adapter

import by.citech.reqrsp.data.RspMeta
import by.citech.reqrsp.data.ReqMeta

/**
 * Input messages adapter
 */
interface InputAdapter<I> {
    fun extractRspMeta(body: I): RspMeta?
    fun extractReqMeta(body: I): ReqMeta?
}
