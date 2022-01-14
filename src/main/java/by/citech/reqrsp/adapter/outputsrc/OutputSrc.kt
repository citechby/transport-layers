package by.citech.reqrsp.adapter.outputsrc

interface OutputSrc<O> {
    fun tryTake(): O?
}
