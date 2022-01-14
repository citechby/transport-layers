package by.citech.reqrsp

data class InputResult(
    val req: ReqInputResult,
    val rsp: RspInputResult
) {

    fun isReq(): Boolean {
        return req != ReqInputResult.NO_REQ
    }

    fun isRsp(): Boolean {
        return rsp != RspInputResult.NO_RSP
    }
}
