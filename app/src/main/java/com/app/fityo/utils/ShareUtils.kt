package com.app.fityo.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.app.fityo.R
import java.io.File

object ShareUtils {
    fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(sendIntent, context.getString(R.string.share_pdf_title))
        )
    }
}

