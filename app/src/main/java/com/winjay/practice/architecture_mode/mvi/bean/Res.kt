package com.winjay.practice.architecture_mode.mvi.bean

import com.squareup.moshi.Json

data class Res(
    @Json(name = "vertical")
    val vertical: List<Vertical>
)