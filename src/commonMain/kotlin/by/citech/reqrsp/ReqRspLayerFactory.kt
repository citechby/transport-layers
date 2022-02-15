package by.citech.reqrsp

import by.citech.reqrsp.adapter.InputAdapter
import by.citech.reqrsp.adapter.OutputAdapter
import by.citech.reqrsp.adapter.ReqHandler
import by.citech.reqrsp.adapter.TransportAdapter
import by.citech.reqrsp.adapter.log.LogDst

class ReqRspLayerFactory<I, O> {
    fun create(
        logId: String,
        inputAdapter: InputAdapter<I>,
        outputAdapter: OutputAdapter<O>,
        reqHandler: ReqHandler<I, O>,
        transportAdapter: TransportAdapter<O>,
        reqTimings: ReqTimings = ReqTimings(30_000, 500),
        layerTimings: LayerTimings = LayerTimings(120_000, 120_000),
        logDst: LogDst = LogDst { _, _ -> }
    ): ReqRspLayer<O, I> {
        return ReqRspLayerImpl(
            logId,
            inputAdapter,
            outputAdapter,
            reqHandler,
            transportAdapter,
            reqTimings,
            layerTimings,
            logDst
        )
    }
}
