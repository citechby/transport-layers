package by.citech.reqrsp.context

import by.citech.reqrsp.adapter.RspHandler

data class LocalReqCtx<I, O>(
    val reqBody: O,
    val repeatInterval: Long,
    val timeoutAfterTs: Long,
    val rspHandler: RspHandler<I>? = null,
    var reqCnt: Long,
    var nextRepeatAfterTs: Long,
    var handledTs: Long? = null
)
