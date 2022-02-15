package by.citech.reqrsp.adapter.outputsrc

abstract class MutableOutputSrc<O> : OutputSrc<O>() {
    abstract fun put(output: O?)
}
