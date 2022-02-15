package by.citech.reqrsp.adapter

import by.citech.reqrsp.adapter.outputsrc.OutputSrc

/**
 * Remote requests handler
 */
abstract class ReqHandler<I, O> {
    /**
     * @param body remote request
     * @return local response source
     */
    abstract fun handle(body: I): OutputSrc<O>
}
