package app.newproj.lbrytv.data.dto

data class JsonRpcRequest<T>(
    val jsonrpc: String,
    val id: Long,
    val method: String,
    val params: T?,
)
