package by.citech.reqrsp.data

data class Rsp<I>(
    val result: RspResult,
    val body: I?,
    val ts: Long
)
