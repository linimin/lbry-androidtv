package app.newproj.lbrytv.data.dto

annotation class JsonRpc(
    /// The name of RPC method being invoked by this call.
    val method: String = ""
)
