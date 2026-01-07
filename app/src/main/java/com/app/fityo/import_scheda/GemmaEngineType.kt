package com.app.fityo.import_scheda

/**
 * Tipi di engine disponibili per Gemma.
 * GPU: Più veloce, richiede driver OpenCL (non disponibile su tutti i dispositivi)
 * CPU: Compatibile con tutti i dispositivi, ma più lento
 */
enum class GemmaEngineType(
    val displayName: String,
    val modelFileName: String,
    val description: String
) {
    GPU(
        "GPU (Veloce)",
        "gemma-2b-it-gpu-int4.bin",
        "Richiede driver OpenCL. Più veloce ma non compatibile con tutti i dispositivi (es. Android 16 beta)."
    ),
    CPU(
        "CPU (Compatibile)",
        "gemma-2b-it-cpu-int4.bin",
        "Funziona su tutti i dispositivi. Più lento ma universalmente compatibile."
    );

    companion object {
        /**
         * Restituisce tutti i tipi di engine disponibili
         */
        fun getAll(): List<GemmaEngineType> = entries
    }
}
