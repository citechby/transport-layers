package by.citech.reqrsp.adapter.outputsrc

class SimpleMutableOutputSrc<O> : MutableOutputSrc<O>() {

    private var output: O? = null

    override fun put(output: O?) {
        this.output = output
    }

    override fun tryTake(): O? {
        return output
    }
}
