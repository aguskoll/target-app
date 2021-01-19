package com.rootstrap.android.network.models

import com.squareup.moshi.Json

data class FacebookSignIn(
    @Json(name = "access_token") val access_token: String = ""
)
