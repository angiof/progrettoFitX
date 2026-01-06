package com.app.fityo.dominio

/**
 * Tipo di vista per la foto: frontale o posteriore.
 * Questo influenza quali punti MediaPipe sono più affidabili per l'analisi.
 */
enum class ViewType(
    val displayName: String,
    val instructions: String,
    val iconDescription: String
) {
    /**
     * Vista frontale - Full body frontale.
     * Migliore per: braccia (bicipiti), addominali, quadricipiti.
     */
    FRONTAL(
        displayName = "Frontale",
        instructions = "Posizionati di fronte alla camera.\nMostra il corpo intero dalla testa ai piedi.",
        iconDescription = "Silhouette frontale"
    ),

    /**
     * Vista posteriore - Schiena.
     * Migliore per: dorsali, tricipiti, glutei, polpacci.
     */
    POSTERIOR(
        displayName = "Posteriore",
        instructions = "Posizionati di schiena alla camera.\nMostra il corpo intero dalla testa ai piedi.",
        iconDescription = "Silhouette posteriore"
    )
}

/**
 * Risultato del confronto per un singolo distretto muscolare.
 */
data class DistrictResult(
    val district: MuscleDistrict,
    val variationPercent: Float,
    val pixelsA: Int,
    val pixelsB: Int
) {
    /**
     * Indica se il risultato è considerato "notevole" (sopra la soglia).
     */
    val isNotable: Boolean
        get() = kotlin.math.abs(variationPercent) >= MuscleDistrict.NOTABLE_THRESHOLD

    /**
     * Indica se c'è stato un aumento (variazione positiva).
     */
    val isIncrease: Boolean
        get() = variationPercent > 0
}

/**
 * Risultato completo del confronto muscolare tra due foto.
 */
data class CompareResult(
    val armsResult: DistrictResult,
    val absResult: DistrictResult,
    val legsResult: DistrictResult,
    val glutesResult: DistrictResult,
    val scaleFactorA: Float,
    val scaleFactorB: Float,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Lista di tutti i risultati per iterazione.
     */
    val allResults: List<DistrictResult>
        get() = listOf(armsResult, absResult, legsResult, glutesResult)

    /**
     * Numero di risultati notevoli.
     */
    val notableCount: Int
        get() = allResults.count { it.isNotable }

    /**
     * Media delle variazioni (valore assoluto).
     */
    val averageVariation: Float
        get() = allResults.map { kotlin.math.abs(it.variationPercent) }.average().toFloat()
}

/**
 * Stato UI per il processo di confronto.
 */
sealed class CompareState {
    object Idle : CompareState()
    object SelectingViewType : CompareState()
    data class CapturingPhotoA(val viewType: ViewType) : CompareState()
    data class PhotoACaptured(val photoPath: String, val viewType: ViewType) : CompareState()
    data class CapturingPhotoB(val viewType: ViewType) : CompareState()
    data class PhotoBCaptured(val photoAPath: String, val photoBPath: String, val viewType: ViewType) : CompareState()
    object Processing : CompareState()
    data class ResultReady(val result: CompareResult) : CompareState()
    data class Error(val message: String) : CompareState()
}

/**
 * Stato UI per la schermata storico.
 */
sealed class HistoryState {
    object Loading : HistoryState()
    data class Success(val comparisons: List<CompareHistoryItem>) : HistoryState()
    data class Error(val message: String) : HistoryState()
    object Empty : HistoryState()
}

/**
 * Item per la lista storico.
 */
data class CompareHistoryItem(
    val id: Int,
    val createdAt: Long,
    val armsVariation: Float,
    val absVariation: Float,
    val legsVariation: Float,
    val glutesVariation: Float,
    val photoAPath: String,
    val photoBPath: String,
    val scaleFactorA: Float = 1.0f,
    val scaleFactorB: Float = 1.0f
) {
    val averageVariation: Float
        get() = listOf(
            kotlin.math.abs(armsVariation),
            kotlin.math.abs(absVariation),
            kotlin.math.abs(legsVariation),
            kotlin.math.abs(glutesVariation)
        ).average().toFloat()
}
