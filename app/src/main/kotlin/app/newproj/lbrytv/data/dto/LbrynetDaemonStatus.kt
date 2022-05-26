/*
 * MIT License
 *
 * Copyright (c) 2022 LIN I MIN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

data class LbrynetDaemonStatus(
    @SerializedName("blob_manager")
    val blobManager: BlobManager? = null,

    @SerializedName("disk_space")
    val diskSpace: DiskSpace? = null,

    @SerializedName("ffmpeg_status")
    val ffmpegStatus: FfmpegStatus? = null,

    @SerializedName("file_manager")
    val fileManager: FileManager? = null,

    /**
     * Installation id, base58 encoded.
     */
    @SerializedName("installation_id")
    val installationId: String? = null,

    @SerializedName("is_running")
    val isRunning: Boolean? = null,

    @SerializedName("skipped_components")
    val skippedComponents: List<String?>? = null,

    @SerializedName("startup_status")
    val startupStatus: StartupStatus? = null,

    @SerializedName("wallet")
    val wallet: Wallet? = null,

    @SerializedName("wallet_server_payments")
    val walletServerPayments: WalletServerPayments? = null
) {
    data class BlobManager(
        @SerializedName("connections")
        val connections: Connections? = null,

        @SerializedName("finished_blobs")
        val finishedBlobs: Int? = null
    ) {
        data class Connections(
            /**
             * Bytes per second received.
             */
            @SerializedName("incoming_bps")
            val incomingBps: Any? = null,

            /**
             * Bytes per second sent.
             */
            @SerializedName("outgoing_bps")
            val outgoingBps: Any? = null,

            /**
             * Megabytes per second received.
             */
            @SerializedName("total_incoming_mbs")
            val totalIncomingMbs: Double? = null,

            /**
             * Megabytes per second sent.
             */
            @SerializedName("total_outgoing_mbs")
            val totalOutgoingMbs: Double? = null,

            /**
             * Maximum bandwidth (megabytes per second) received, since the daemon was started.
             */
            @SerializedName("max_incoming_mbs")
            val maxIncomingMbs: Double? = null,

            /**
             * Maximum bandwidth (megabytes per second) sent, since the daemon was started.
             */
            @SerializedName("max_outgoing_mbs")
            val maxOutgoingMbs: Double? = null,

            /**
             * Total number of bytes received since the daemon was started.
             */
            @SerializedName("total_received")
            val totalReceived: Int? = null,

            /**
             * Total number of bytes sent since the daemon was started.
             */
            @SerializedName("total_sent")
            val totalSent: Int? = null
        )
    }

    data class DiskSpace(
        @SerializedName("running")
        val running: Boolean? = null,

        @SerializedName("space_used")
        val spaceUsed: String? = null
    )

    data class FfmpegStatus(
        @SerializedName("analyze_audio_volume")
        val analyzeAudioVolume: Boolean? = null,

        @SerializedName("available")
        val available: Boolean? = null,

        @SerializedName("which")
        val which: String? = null
    )

    data class FileManager(
        @SerializedName("managed_files")
        val managedFiles: Int? = null
    )

    data class StartupStatus(
        @SerializedName("blob_manager")
        val blobManager: Boolean? = null,

        @SerializedName("database")
        val database: Boolean? = null,

        @SerializedName("disk_space")
        val diskSpace: Boolean? = null,

        @SerializedName("exchange_rate_manager")
        val exchangeRateManager: Boolean? = null,

        @SerializedName("file_manager")
        val fileManager: Boolean? = null,

        @SerializedName("wallet")
        val wallet: Boolean? = null,

        @SerializedName("wallet_server_payments")
        val walletServerPayments: Boolean? = null
    )

    data class Wallet(
        @SerializedName("available_servers")
        val availableServers: Int? = null,

        /**
         * Block hash of most recent block.
         */
        @SerializedName("best_blockhash")
        val bestBlockhash: String? = null,

        /**
         * Local blockchain height.
         */
        @SerializedName("blocks")
        val blocks: Int? = null,

        /**
         * Remote_height - local_height.
         */
        @SerializedName("blocks_behind")
        val blocksBehind: Int? = null,

        /**
         * Host and port of the connected spv server.
         */
        @SerializedName("connected")
        val connected: String? = null,

        @SerializedName("connected_features")
        val connectedFeatures: ConnectedFeatures? = null,

        @SerializedName("headers_synchronization_progress")
        val headersSynchronizationProgress: Int? = null,

        @SerializedName("known_servers")
        val knownServers: Int? = null,

        @SerializedName("servers")
        val servers: List<Server?>? = null
    ) {
        data class ConnectedFeatures(
            @SerializedName("daily_fee")
            val dailyFee: String? = null,

            @SerializedName("description")
            val description: String? = null,

            @SerializedName("donation_address")
            val donationAddress: String? = null,

            @SerializedName("genesis_hash")
            val genesisHash: String? = null,

            @SerializedName("hash_function")
            val hashFunction: String? = null,

            @SerializedName("hosts")
            val hosts: Hosts? = null,

            @SerializedName("payment_address")
            val paymentAddress: String? = null,

            @SerializedName("protocol_max")
            val protocolMax: String? = null,

            @SerializedName("protocol_min")
            val protocolMin: String? = null,

            @SerializedName("pruning")
            val pruning: Any? = null,

            @SerializedName("server_version")
            val serverVersion: String? = null,

            @SerializedName("trending_algorithm")
            val trendingAlgorithm: String? = null
        ) {
            class Hosts
        }

        data class Server(
            @SerializedName("availability")
            val availability: Boolean? = null,

            @SerializedName("host")
            val host: String? = null,

            @SerializedName("latency")
            val latency: Double? = null,

            @SerializedName("port")
            val port: Int? = null
        )
    }

    data class WalletServerPayments(
        @SerializedName("max_fee")
        val maxFee: String? = null,

        @SerializedName("running")
        val running: Boolean? = null
    )
}
