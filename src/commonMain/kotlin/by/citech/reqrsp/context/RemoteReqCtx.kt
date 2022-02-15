package by.citech.reqrsp.context

import by.citech.reqrsp.adapter.outputsrc.OutputSrc

data class RemoteReqCtx<O>(
    val rspBodySrc: OutputSrc<O>,
    val handledTs: Long,
    var rspCnt: Long
)
