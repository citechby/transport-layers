package by.citech.reqrsp.adapter

/**
 * Transport adapter. Hides implementation of your actual transport.
 */
fun interface TransportAdapter<O> {
    /**
     * Send message to remote with your transport
     */
    fun send(body: O)
}
