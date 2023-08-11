package com.app.progrettofitx.db

import java.io.Serializable

data class SchedeEntity(
    val gruppoMuscolare: String,
    val intesita: String,
    val titolo:String,
    val data:String,
    val notes:String?=null,
):Serializable
