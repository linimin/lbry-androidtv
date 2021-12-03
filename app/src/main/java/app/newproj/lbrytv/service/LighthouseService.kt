package app.newproj.lbrytv.service

import app.newproj.lbrytv.data.dto.RelatedClaim
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A lightning fast search for the LBRY blockchain.
 */
interface LighthouseService {
    @GET("search")
    suspend fun search(
        @Query("s") query: String,
        @Query("resolve") resolve: Boolean = false,
        @Query("size") pageSize: Int,
        @Query("from") page: Int,
        @Query("related_to") relatedTo: String? = null,
        @Query("nsfw") canShowMatureContent: Boolean = false,
    ): List<RelatedClaim>
}
