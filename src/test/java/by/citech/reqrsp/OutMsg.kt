package by.citech.reqrsp

data class OutMsg(
    val data: List<String>,
    val reqId: String? = null,
    val reqCnt: Long? = null,
    val rspReqId: String? = null,
    val rspReqCnt: Long? = null,
    val rspCnt: Long? = null,
)
