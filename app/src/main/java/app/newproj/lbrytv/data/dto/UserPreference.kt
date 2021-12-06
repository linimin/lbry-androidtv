package app.newproj.lbrytv.data.dto

import com.google.gson.annotations.SerializedName

data class UserPreference(
    @SerializedName("shared")
    val shared: Shared? = null
) {
    data class Shared(
        @SerializedName("type")
        val type: String? = null,

        @SerializedName("value")
        val value: Value? = null,

        @SerializedName("version")
        val version: String? = null
    ) {
        data class Value(
            @SerializedName("app_welcome_version")
            val appWelcomeVersion: Int? = null,

            @SerializedName("blocked")
            val blocked: List<Any?>? = null,

            @SerializedName("builtinCollections")
            val builtinCollections: BuiltinCollections? = null,

            @SerializedName("coin_swap_codes")
            val coinSwapCodes: List<Any?>? = null,

            @SerializedName("editedCollections")
            val editedCollections: EditedCollections? = null,

            @SerializedName("following")
            val following: List<Following?>? = null,

            @SerializedName("settings")
            val settings: Settings? = null,

            @SerializedName("sharing_3P")
            val sharing3P: Boolean? = null,

            @SerializedName("subscriptions")
            val subscriptions: List<String?>? = null,

            @SerializedName("tags")
            val tags: List<Any?>? = null,

            @SerializedName("unpublishedCollections")
            val unpublishedCollections: UnpublishedCollections? = null
        ) {
            data class BuiltinCollections(
                @SerializedName("favorites")
                val favorites: Favorites? = null,

                @SerializedName("watchlater")
                val watchlater: Watchlater? = null
            ) {
                data class Favorites(
                    @SerializedName("id")
                    val id: String? = null,

                    @SerializedName("items")
                    val items: List<Any?>? = null,

                    @SerializedName("name")
                    val name: String? = null,

                    @SerializedName("type")
                    val type: String? = null,

                    @SerializedName("updatedAt")
                    val updatedAt: Int? = null
                )

                data class Watchlater(
                    @SerializedName("id")
                    val id: String? = null,

                    @SerializedName("items")
                    val items: List<Any?>? = null,

                    @SerializedName("name")
                    val name: String? = null,

                    @SerializedName("type")
                    val type: String? = null,

                    @SerializedName("updatedAt")
                    val updatedAt: Int? = null
                )
            }

            class EditedCollections

            data class Following(
                @SerializedName("notificationsDisabled")
                val notificationsDisabled: Boolean? = null,

                @SerializedName("uri")
                val uri: String? = null
            )

            data class Settings(
                @SerializedName("automatic_dark_mode_enabled")
                val automaticDarkModeEnabled: Boolean? = null,

                @SerializedName("autoplay")
                val autoplay: Boolean? = null,

                @SerializedName("autoplay_next")
                val autoplayNext: Boolean? = null,

                @SerializedName("dark_mode_times")
                val darkModeTimes: DarkModeTimes? = null,

                @SerializedName("floating_player")
                val floatingPlayer: Boolean? = null,

                @SerializedName("hide_balance")
                val hideBalance: Boolean? = null,

                @SerializedName("hide_reposts")
                val hideReposts: Boolean? = null,

                @SerializedName("hide_splash_animation")
                val hideSplashAnimation: Boolean? = null,

                @SerializedName("instant_purchase_enabled")
                val instantPurchaseEnabled: Boolean? = null,

                @SerializedName("instant_purchase_max")
                val instantPurchaseMax: InstantPurchaseMax? = null,

                @SerializedName("language")
                val language: Any? = null,

                @SerializedName("show_mature")
                val showMature: Boolean? = null,

                @SerializedName("theme")
                val theme: String? = null
            ) {
                data class DarkModeTimes(
                    @SerializedName("from")
                    val from: From? = null,

                    @SerializedName("to")
                    val to: To? = null
                ) {
                    data class From(
                        @SerializedName("formattedTime")
                        val formattedTime: String? = null,

                        @SerializedName("hour")
                        val hour: String? = null,

                        @SerializedName("min")
                        val min: String? = null
                    )

                    data class To(
                        @SerializedName("formattedTime")
                        val formattedTime: String? = null,

                        @SerializedName("hour")
                        val hour: String? = null,

                        @SerializedName("min")
                        val min: String? = null
                    )
                }

                data class InstantPurchaseMax(
                    @SerializedName("amount")
                    val amount: Double? = null,

                    @SerializedName("currency")
                    val currency: String? = null
                )
            }

            class UnpublishedCollections
        }
    }
}
