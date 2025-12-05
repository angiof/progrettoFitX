package com.app.fityo.utils

data class ExportMetadata(
    val customTitle: String = "",
    val coachName: String = "",
    val athleteName: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val pdfFormat: String = "CLASSIC" // "CLASSIC" o "MODERN"
)

