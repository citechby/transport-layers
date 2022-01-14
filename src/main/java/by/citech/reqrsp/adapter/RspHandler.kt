package by.citech.reqrsp.adapter

import by.citech.reqrsp.data.Rsp

/**
 * Remote response handler
 */
fun interface RspHandler<I> {
    /**
     * @param rsp remote response to local request
     */
    fun handle(rsp: Rsp<I>)
}
