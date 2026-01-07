# Modifiche sessione

## Funzionalita aggiunte
- Auto compilazione IA scheda: dialog con stile, intensita e gruppi muscolari; genera esercizi e permette editing manuale.
- Generatore IA Gemma per schede: prompt JSON + parsing risultati in esercizi.
- Pulizia esercizi esistenti prima di inserire quelli auto generati.
- Rotazione frame/thumbnail per analisi tutor (metadati video).

## File modificati
- app/src/main/java/com/app/fityo/ui/schedecreate/compose/SchedeFormScreen.kt: bottone "Auto compilazione IA", dialog selezione parametri, loader/errori, riuso UI per intensita e gruppi muscolari.
- app/src/main/java/com/app/fityo/ui/schedecreate/SchedeCreateViewModel.kt: flusso auto compilazione, stato loading/error, upsert scheda, generazione esercizi via Gemma, reset esercizi preesistenti.
- app/src/main/java/com/app/fityo/ui/schedecreate/SchedeCreateActivity.kt: passaggio opzioni stile allenamento e hook del nuovo flusso.
- app/src/main/java/com/app/fityo/import_scheda/GemmaLlmHelper.kt: aggiunta risposta generica da prompt per generazione schede.
- app/src/main/java/com/app/fityo/data_layer/db/dao/DaoEssercissi.kt: deleteBySchedaId per pulire esercizi della scheda.
- app/src/main/java/com/app/fityo/data_layer/db/repos/EsserciziRepository.kt: wrapper deleteBySchedaId.
- app/src/main/res/values/strings.xml: nuove stringhe auto compilazione + lista training_style_options.
- app/src/main/java/com/app/fityo/tutor/analysis/VideoFrameExtractor.kt: rotazione frame/thumbnail basata su metadata.

## File nuovi
- app/src/main/java/com/app/fityo/ui/schedecreate/AutoCompileModels.kt: modello dati per opzioni auto compilazione.
- app/src/main/java/com/app/fityo/schede/ai/WorkoutPlanModels.kt: modelli request/response e interfaccia generator.
- app/src/main/java/com/app/fityo/schede/ai/GemmaWorkoutPlanGenerator.kt: generatore Gemma per schede.

## Note operative
- Camera OCR import: Gemma rimane disattivo (flow invariato).
- Gemma per auto compilazione: richiede modello disponibile e inizializzato; in caso contrario mostra errore.

## Screen controllate (6)
1) app/src/main/java/com/app/fityo/ui/compose/SchedeListActivity.kt: lista schede + bottone Importa.
2) app/src/main/java/com/app/fityo/import_scheda/ImportSchedaActivity.kt: scelta import FitYo/PDF/Camera e assegnazione profilo.
3) app/src/main/java/com/app/fityo/import_scheda/ImportSchedaScreen.kt: UI OCR camera/galleria e review risultati.
4) app/src/main/java/com/app/fityo/ui/schedecreate/compose/SchedeFormScreen.kt: form creazione scheda (titolo, data, intensita, gruppi).
5) app/src/main/java/com/app/fityo/ui/tutor/compose/RecordingScreen.kt: registrazione tutor con skeleton overlay e status posa.
6) app/src/main/java/com/app/fityo/ui/tutor/compose/TutorResultScreen.kt: risultato analisi con score ed errori.
