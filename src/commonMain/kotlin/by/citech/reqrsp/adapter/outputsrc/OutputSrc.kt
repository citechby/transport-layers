package by.citech.reqrsp.adapter.outputsrc

abstract class OutputSrc<O> {
    abstract fun tryTake(): O?
}
