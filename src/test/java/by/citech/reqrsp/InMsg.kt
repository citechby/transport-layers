package by.citech.reqrsp

data class InMsg(
    val data: String,
    val reqId: String? = null,
    val reqCnt: Long? = null,
    val rspReqId: String? = null,
    val rspReqCnt: Long? = null,
    val rspCnt: Long? = null
)
