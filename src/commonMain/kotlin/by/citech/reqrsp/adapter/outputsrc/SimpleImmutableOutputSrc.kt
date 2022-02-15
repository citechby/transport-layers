package by.citech.reqrsp.adapter.outputsrc

class SimpleImmutableOutputSrc<O>(private val output: O) : OutputSrc<O>() {
    override fun tryTake(): O? {
        return output
    }
}
