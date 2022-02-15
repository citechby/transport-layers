package by.citech.reqrsp.adapter

import by.citech.reqrsp.data.Rsp

/**
 * Remote response handler
 */
abstract class RspHandler<I> {
    /**
     * @param rsp remote response to local request
     */
    abstract fun handle(rsp: Rsp<I>)
}
