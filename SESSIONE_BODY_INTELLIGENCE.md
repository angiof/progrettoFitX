# Sessione Body Intelligence - Summary

## Obiettivo
Integrare una valutazione reale del corpo in modalita Body Intelligence, riusando la stessa UI, con pipeline biometrica (pose, segmentazione, profondita) e un classificatore TFLite per "in forma / fat / powerlifter".

## Modelli usati
- MoveNet Thunder: `app/src/main/assets/models/3.tflite`
- MediaPipe Pose Landmarker: `pose_landmarker_full.task`
- MediaPipe Image Segmenter (selfie): via `ImageSegmenterHelper`
- MiDaS (depth): `app/src/main/assets/models/midas/midas_v2.tflite`
- Body composition classifier: `app/src/main/assets/models/model_unquant.tflite`
  - Label mapping: `0 = in forma`, `1 = fat`, `2 = powerlifter`

## Pipeline Body Intelligence (foto)
1) Pose detection (MediaPipe) per struttura e landmark.
2) Segmentazione del corpo (mask) per misure su massa.
3) MiDaS depth per correzione prospettiva / scala.
4) Metriche biometriche:
   - V-taper (Adonis ratio)
   - Simmetria destra/sinistra
   - Postura (allineamento orecchie-spalle-fianchi)
   - Stime spalle/vita in cm
   - Depth scale
5) Classificazione "in forma / fat / powerlifter" con TFLite.

## Output UI (BodyIntelligenceResultScreen)
- Overall score + Body Metrics (BMI, BF%, FFMI, ecc.)
- Card "Forma del corpo" con:
  - label principale
  - confidence
  - percentuali per tutte le classi
- Card "Metriche Biometriche" (V-taper, simmetria, postura, spalle, vita, prospettiva)
- Zone analysis + raccomandazioni
- CTA "Genera Avatar 3D"

## Flusso 3D
- Avatar 3D (video 360): `Video360Processor` -> `Avatar3DReady`
- TrueClone (set foto): `TrueCloneProcessor` -> `TrueCloneReady`
- Salvataggi su DB: tabella `avatar_3d` + storico in UI

## Logging utili
Tag: `BodyComposition`
- Caricamento modello (shape input/output)
- Raw scores
- Probabilities
- Stato classifier ready

## Note tecniche
- Input RGB normalizzato 0..1 (mean=0, std=255).
- Output gestito per 1, 2 o 3 valori. Se non normalizzato, viene applicata softmax.

## Next steps
- Validare il mapping delle classi con immagini note.
- Se label invertiti, correggere mapping in `BodyCompositionClassifier`.
- Se il modello richiede diversa normalizzazione, aggiornare `TfliteUtils.bitmapToBuffer`.
