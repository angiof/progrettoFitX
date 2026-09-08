package com.app.fityo.data_layer.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "essercissi",
    foreignKeys = [ForeignKey(
        entity = SchedeEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("schedaId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [androidx.room.Index(value = ["schedaId"])]
)
data class EsserciziEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int? = null,

    @ColumnInfo(name = "nome") val nome: String,
    @ColumnInfo(name = "attrezzo") val attrezzo: String,

    @ColumnInfo(name = "nRipetizione") val nRipetizione: Int,
    @ColumnInfo(name = "nSerie") val nSerie: Int,

    @ColumnInfo(name = "insometria") val insometria: Int?,

    @ColumnInfo(name = "intervallo") val intervallo: Int?,

    @ColumnInfo(name = "peso") val peso: Float? = null, // Campo peso in kg (opzionale)

    @ColumnInfo(name = "completed") val completed: Boolean = false, // Stato completamento esercizio

    @ColumnInfo(name = "notes") val notes: String? = null, // Note specifiche per esercizio


    @ColumnInfo(name = "wgerId") val wgerId: Int? = null, // ID esercizio Wger
    @ColumnInfo(name = "schedaId") val schedaId: Int, // Foreign Key

    // Schede complesse: una scheda semplice resta tutta su settimana 1 / giorno 1, quindi le
    // schermate che non conoscono questi campi continuano a vedere la lista di sempre.
    @ColumnInfo(name = "settimana") val settimana: Int = 1,
    @ColumnInfo(name = "giorno") val giorno: Int = 1,
    @ColumnInfo(name = "ordine") val ordine: Int = 0,

    // Esercizi consecutivi con lo stesso gruppo sono un superset: e un numero e non una
    // gerarchia proprio per non toccare liste, swipe e export gia esistenti.
    @ColumnInfo(name = "supersetGroup") val supersetGroup: Int? = null,

    // Campi da scheda avanzata: testo perche in palestra si scrive "8-9" o "RIR 2".
    @ColumnInfo(name = "rpe") val rpe: String? = null,
    @ColumnInfo(name = "tempo") val tempo: String? = null,
    @ColumnInfo(name = "percentuale") val percentuale: Float? = null,

    // Riga e gruppo di colonne (la "settimana" del foglio) da cui viene questo esercizio.
    // Il numero di riga e quello vero di Excel: XlsxReader ricostruisce le righe vuote saltate
    // leggendo l'attributo r, quindi il riferimento resta valido.
    @ColumnInfo(name = "sourceRow") val sourceRow: Int? = null,
    @ColumnInfo(name = "sourceVariant") val sourceVariant: Int? = null,

    // Zone muscolari lavorate: facoltative, ma quando ci sono il grafico della scheda smette
    // di essere una stima e diventa un conto sul volume vero.
    @ColumnInfo(name = "gruppiMuscolari") val gruppiMuscolari: List<String>? = null
) : Serializable

