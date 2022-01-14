package by.citech.reqrsp.adapter

import by.citech.reqrsp.adapter.outputsrc.OutputSrc

/**
 * Remote requests handler
 */
fun interface ReqHandler<I, O> {
    /**
     * @param body remote request
     * @return local response source
     */
    fun handle(body: I): OutputSrc<O>
}
