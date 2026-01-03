package com.app.fityo.dominio

/**
 * Enum che rappresenta i distretti muscolari analizzabili.
 * Ogni distretto ha associati i punti MediaPipe Pose Landmarker necessari per l'analisi.
 *
 * Riferimento punti MediaPipe:
 * 11 = spalla sinistra, 12 = spalla destra
 * 13 = gomito sinistro, 14 = gomito destro
 * 15 = polso sinistro, 16 = polso destro
 * 23 = anca sinistra, 24 = anca destra
 * 25 = ginocchio sinistro, 26 = ginocchio destro
 * 27 = caviglia sinistra, 28 = caviglia destra
 */
enum class MuscleDistrict(
    val landmarks: List<Int>,
    val displayName: String
) {
    /**
     * Braccia: analizza bicipiti e tricipiti.
     * Usa spalle, gomiti e polsi per calcolare il diametro trasversale.
     */
    ARMS(
        landmarks = listOf(11, 12, 13, 14, 15, 16),
        displayName = "Braccia"
    ),

    /**
     * Addominali: analizza la zona centrale del torso.
     * Usa spalle e anche per calcolare contrasto ombre e larghezza vita.
     */
    ABS(
        landmarks = listOf(11, 12, 23, 24),
        displayName = "Addominali"
    ),

    /**
     * Gambe: analizza quadricipiti e polpacci.
     * Usa anche, ginocchia e caviglie per calcolare la circonferenza.
     */
    LEGS(
        landmarks = listOf(23, 24, 25, 26, 27, 28),
        displayName = "Gambe"
    ),

    /**
     * Glutei: analizza la curva del profilo laterale.
     * Usa anche e ginocchia per calcolare la proiezione laterale.
     */
    GLUTES(
        landmarks = listOf(23, 24, 25, 26),
        displayName = "Glutei"
    );

    companion object {
        /**
         * Punti di riferimento per la normalizzazione della scala.
         * Usa la distanza tra le spalle come riferimento costante.
         */
        val NORMALIZATION_LANDMARKS = listOf(11, 12)

        /**
         * Soglia minima di variazione per considerare un risultato "notevole"
         */
        const val NOTABLE_THRESHOLD = 3.0f
    }
}
