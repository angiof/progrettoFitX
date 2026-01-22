# Integrazione Wger - Enciclopedia esercizi (Compose)

Questo documento riassume lo sviluppo fatto per integrare l'API Wger nel flusso
di creazione e consultazione delle schede in Compose.

## Obiettivo
- Autocomplete esercizi con suggerimenti in tempo reale.
- Dialog di dettaglio (info esercizio) con media, muscoli e descrizione HTML.
- Salvataggio del `wgerId` per collegare esercizi locali ai dati Wger.
- Pulsante Info in archivio per aprire il dialog su esercizi gia salvati.

## Flusso "Crea Scheda" (Compose)
File: `app/src/main/java/com/app/fityo/ui/schedecreate/compose/EsercissiListScreen.kt`

- Nel form esercizio e stato aggiunto un toggle (icona Info) sul campo "Nome esercizio".
- Quando attivo, parte l'autocomplete via API Wger (debounce 350ms).
- I risultati vengono mostrati in una dropdown con nome, categoria e miniatura.
- Se si seleziona un suggerimento:
  - Si apre il dialog di dettaglio Wger.
  - Premendo "OK" nel dialog:
    - il nome viene auto-compilato,
    - il `wgerId` viene salvato nel form (e quindi in Room).

## Flusso "Archivio Schede" (Compose)
File: `app/src/main/java/com/app/fityo/ui/compose/SchedeListActivity.kt`

- Ogni esercizio con `wgerId` mostra il pulsante Info.
- Al click, si apre lo stesso dialog di dettaglio Wger in sola lettura.

## Dialog dettaglio Wger
File: `app/src/main/java/com/app/fityo/ui/wger/WgerExerciseInfoDialog.kt`

Contenuto:
- Loading con `CircularProgressIndicator` (placeholder semplice).
- Gestione errori con messaggio e pulsante "Riprova".
- Titolo esercizio.
- Media:
  - Video: ExoPlayer se disponibile.
  - Immagine: fallback se video non presente.
- Mappa muscolare (SVG/immagine).
- Lista muscoli (chip).
- Descrizione HTML via `HtmlCompat`.

## API e Repository
Gia presenti e riusati:
- `app/src/main/java/com/app/fityo/data_layer/network/WgerApi.kt`
- `app/src/main/java/com/app/fityo/data_layer/repository/WgerRepository.kt`
- `app/src/main/java/com/app/fityo/dominio/WgerModels.kt`

Note tecniche:
- Header `Accept: application/json`.
- Lingua italiana: `language=7`.
- URL relative normalizzate con `https://wger.de`.

## Media e dipendenze
- Immagini: Coil (`coil-compose` + `coil-svg`).
- Video: Media3 ExoPlayer (gia presente nel progetto).

## Error handling
- Se la ricerca fallisce: messaggio "Errore di rete".
- Se il dettaglio fallisce: dialog mostra errore e consente retry.

## File principali toccati
- `app/src/main/java/com/app/fityo/ui/wger/WgerExerciseInfoDialog.kt`
- `app/src/main/java/com/app/fityo/ui/schedecreate/compose/EsercissiListScreen.kt`
- `app/src/main/java/com/app/fityo/ui/compose/SchedeListActivity.kt`

## Verifica manuale rapida
1) Home -> Crea scheda (Compose).
2) Aggiungi esercizio -> attiva icona Info sul campo nome.
3) Digita 2+ caratteri, seleziona un suggerimento.
4) Verifica dialog e conferma con "OK".
5) Salva scheda, apri archivio, controlla pulsante Info sugli esercizi con `wgerId`.
