package by.citech.reqrsp.adapter.outputsrc

interface MutableOutputSrc<O> : OutputSrc<O> {
    fun put(output: O?)
}
