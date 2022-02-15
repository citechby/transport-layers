package by.citech.reqrsp.adapter.log

fun interface LogDst {
    fun log(logLvl: LogLvl, msg: String)
}
