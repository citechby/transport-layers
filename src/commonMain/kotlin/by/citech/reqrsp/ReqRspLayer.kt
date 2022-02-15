package by.citech.reqrsp

import by.citech.reqrsp.adapter.RspHandler

abstract class ReqRspLayer<O, I> {
    /**
     * Process state of layer.
     *
     * @param currTs Current timestamp.
     */
    abstract fun processState(currTs: Long)

    /**
     * @param reqBody    Message to send and to apply request-response semantics.
     * @param currTs     Current timestamp.
     * @param reqTimings Timing settings.
     * @param rspHandler Response callback.
     */
    abstract fun output(
        reqBody: O,
        currTs: Long,
        reqTimings: ReqTimings?,
        rspHandler: RspHandler<I>?
    )

    /**
     * @param rspBody Received message, possibly response.
     * @param currTs  Current timestamp.
     */
    abstract fun input(
        rspBody: I,
        currTs: Long
    ): InputResult
}
