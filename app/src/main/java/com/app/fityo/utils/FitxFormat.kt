package com.app.fityo.utils

import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import org.json.JSONArray
import org.json.JSONObject

/**
 * Formato .fitx per import/export schede
 * Versione 1.0
 */
data class FitxFormat(
    val version: String = "1.0",
    val scheda: FitxScheda
) {
    data class FitxScheda(
        val titolo: String,
        val gruppoMuscolare: String,
        val gruppiMuscolari: List<String>?,
        val intensita: String,
        val data: String,
        val notes: String?,
        val esercizi: List<FitxEsercizio>
    )

    data class FitxEsercizio(
        val nome: String,
        val serie: Int,
        val ripetizioni: Int,
        val attrezzo: String,
        val peso: Float?,
        val recupero: Int?,
        val isometria: Int?,
        val notes: String? = null // Note specifiche per esercizio
    )

    companion object {
        /**
         * Crea FitxFormat da SchedeEntity ed esercizi
         */
        fun fromScheda(scheda: SchedeEntity, esercizi: List<EsserciziEntity>): FitxFormat {
            return FitxFormat(
                version = "1.0",
                scheda = FitxScheda(
                    titolo = scheda.titolo,
                    gruppoMuscolare = scheda.gruppoMuscolare,
                    gruppiMuscolari = scheda.gruppiMuscolari,
                    intensita = scheda.intesita,
                    data = scheda.data,
                    notes = scheda.notes,
                    esercizi = esercizi.map { esercizio ->
                        FitxEsercizio(
                            nome = esercizio.nome,
                            serie = esercizio.nSerie,
                            ripetizioni = esercizio.nRipetizione,
                            attrezzo = esercizio.attrezzo,
                            peso = esercizio.peso,
                            recupero = esercizio.intervallo,
                            isometria = esercizio.insometria,
                            notes = esercizio.notes
                        )
                    }
                )
            )
        }

        /**
         * Converte FitxFormat in JSON string
         */
        fun toJson(fitx: FitxFormat): String {
            val json = JSONObject()
            json.put("version", fitx.version)

            val schedaJson = JSONObject()
            schedaJson.put("titolo", fitx.scheda.titolo)
            schedaJson.put("gruppoMuscolare", fitx.scheda.gruppoMuscolare)

            fitx.scheda.gruppiMuscolari?.let {
                val gruppiArray = JSONArray(it)
                schedaJson.put("gruppiMuscolari", gruppiArray)
            }

            schedaJson.put("intensita", fitx.scheda.intensita)
            schedaJson.put("data", fitx.scheda.data)
            schedaJson.put("notes", fitx.scheda.notes ?: "")

            val eserciziArray = JSONArray()
            fitx.scheda.esercizi.forEach { esercizio ->
                val esercizioJson = JSONObject()
                esercizioJson.put("nome", esercizio.nome)
                esercizioJson.put("serie", esercizio.serie)
                esercizioJson.put("ripetizioni", esercizio.ripetizioni)
                esercizioJson.put("attrezzo", esercizio.attrezzo)
                esercizioJson.put("peso", esercizio.peso ?: JSONObject.NULL)
                esercizioJson.put("recupero", esercizio.recupero ?: JSONObject.NULL)
                esercizioJson.put("isometria", esercizio.isometria ?: JSONObject.NULL)
                esercizioJson.put("notes", esercizio.notes ?: JSONObject.NULL)
                eserciziArray.put(esercizioJson)
            }
            schedaJson.put("esercizi", eserciziArray)

            json.put("scheda", schedaJson)
            return json.toString(2) // Pretty print con indentazione 2
        }

        /**
         * Parse JSON string in FitxFormat
         */
        fun fromJson(jsonString: String): FitxFormat? {
            return try {
                val json = JSONObject(jsonString)
                val version = json.getString("version")
                val schedaJson = json.getJSONObject("scheda")

                val gruppiMuscolari = if (schedaJson.has("gruppiMuscolari") &&
                                          !schedaJson.isNull("gruppiMuscolari")) {
                    val gruppiArray = schedaJson.getJSONArray("gruppiMuscolari")
                    List(gruppiArray.length()) { i -> gruppiArray.getString(i) }
                } else null

                val eserciziArray = schedaJson.getJSONArray("esercizi")
                val esercizi = mutableListOf<FitxEsercizio>()

                for (i in 0 until eserciziArray.length()) {
                    val esercizioJson = eserciziArray.getJSONObject(i)
                    esercizi.add(
                        FitxEsercizio(
                            nome = esercizioJson.getString("nome"),
                            serie = esercizioJson.getInt("serie"),
                            ripetizioni = esercizioJson.getInt("ripetizioni"),
                            attrezzo = esercizioJson.getString("attrezzo"),
                            peso = if (esercizioJson.isNull("peso")) null
                                   else esercizioJson.getDouble("peso").toFloat(),
                            recupero = if (esercizioJson.isNull("recupero")) null
                                       else esercizioJson.getInt("recupero"),
                            isometria = if (esercizioJson.isNull("isometria")) null
                                        else esercizioJson.getInt("isometria"),
                            notes = if (esercizioJson.has("notes") && !esercizioJson.isNull("notes"))
                                    esercizioJson.getString("notes")
                                    else null
                        )
                    )
                }

                FitxFormat(
                    version = version,
                    scheda = FitxScheda(
                        titolo = schedaJson.getString("titolo"),
                        gruppoMuscolare = schedaJson.getString("gruppoMuscolare"),
                        gruppiMuscolari = gruppiMuscolari,
                        intensita = schedaJson.getString("intensita"),
                        data = schedaJson.getString("data"),
                        notes = if (schedaJson.has("notes") && !schedaJson.isNull("notes"))
                                schedaJson.getString("notes")
                                else null,
                        esercizi = esercizi
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Converte FitxFormat in entities per il database
         * Restituisce Pair(SchedeEntity, List<EsserciziEntity>)
         * NOTA: schedaId verra assegnato dopo l'insert della scheda
         */
        fun toEntities(fitx: FitxFormat): Pair<SchedeEntity, List<FitxEsercizio>> {
            val scheda = SchedeEntity(
                titolo = fitx.scheda.titolo,
                gruppoMuscolare = fitx.scheda.gruppoMuscolare,
                gruppiMuscolari = fitx.scheda.gruppiMuscolari,
                intesita = fitx.scheda.intensita,
                data = fitx.scheda.data,
                notes = fitx.scheda.notes
            )
            return Pair(scheda, fitx.scheda.esercizi)
        }
    }
}

