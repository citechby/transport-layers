package by.citech.reqrsp.adapter

/**
 * Transport adapter. Hides implementation of your actual transport.
 */
abstract class TransportAdapter<O> {
    /**
     * Send message to remote with your transport
     */
    abstract fun send(body: O)
}
