package app.newproj.lbrytv.data.dto

data class JsonRpcResponse<Result>(
    val jsonrpc: String,
    val id: Long,
    val result: Result,
)
