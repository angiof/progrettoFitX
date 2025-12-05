# ProgrettoFitX

Applicazione Android nata come progetto da palestra per gestire schede di allenamento tramite un CRUD locale. L'app usa UI XML tradizionali, Room per il database ed MPAndroidChart per visualizzare trend e riepiloghi grafici.

## Caratteristiche principali
- **Schede di allenamento**: creazione guidata con tab (form, elenco esercizi, riepilogo) tramite `AcitivySheda` e relativo `NavHost`.
- **Archivio locale**: Room (`DbFit`) con entita' `SchedeEntity` ed `EsserciziEntity`, DAO dedicati e repository/UseCase (`SchedeRepository`, `UsesCasesSheda`, `UsesCasesEssercissi`).
- **Lista schede (Compose)**: activity `SchedeListActivity` scritta in Jetpack Compose con `LazyColumn`, schede preferite e bottom sheet di dettaglio.
- **Filtri e ricerca**: activity `ActivityFilterSchede` ospita `ShedeFragments` con lista filtrabile per data, intensita', gruppo muscolare e preferiti.
- **Dashboard statistica**: `DashFrag` mostra grafici a torta e barre (MPAndroidChart) filtrabili per periodo tramite `MaterialDatePicker`.
- **Notifiche di promemoria**: `NotificationWorker` (WorkManager) legge le schede pianificate e invia una notifica di reminder.
- **UI adattata al mobile**: layout XML con Material Components, ViewBinding attivato, Navigation Component per la bottom navigation (`MainActivity`).

## Architettura
```
app/
|-- data_layer/         # Room entities + DAO + database
|-- dominio/            # Model e UseCase
|-- ui/
|   |-- home/           # HomeFragment + Adapter
|   |-- dashboard/      # Grafici/statistiche
|   |-- shedeForms/     # Frammenti/tab form scheda
|   |-- forms/          # Activity/tabs container
|   |-- filter/         # Activity filtro
|   \-- notifications/  # Schermate notifiche
|-- fragments/filtro/   # Lista schede con filtri (riuso in ActivityFilter)
\-- workMangerFit/      # Worker schedulato per promemoria
```
- **UI layer**: frammenti/activities orchestrano ViewModel e adapter. `HomeFragment` espone tre azioni rapide, `ShedeFragments` gestisce lista + filtri, `DashFrag` il grafico.
- **Domain layer**: UseCase incapsulano logica di business e orchestrano i repository.
- **Data layer**: Room fornisce persistenza locale; l'accesso avviene tramite DAO esposti dai repository.
- **Background**: WorkManager (`NotificationWorker`) per notifiche ricorrenti in base agli orari salvati.

## Requisiti & setup
1. Android Studio Iguana/Koala o superiore con JDK 17.
2. Apri il progetto (`File > Open`), consenti la sincronizzazione Gradle.
3. Se necessario aggiorna gli SDK (compileSdk 35, targetSdk 34).
4. Esegui su dispositivo/emulatore Android 10+ (`Run > Run 'app'`).

> Il database e' locale: la prima esecuzione parte vuota; crea una scheda dalla home per popolare i dati.

## Testing
- Test strumentali ed unitari sono configurati (JUnit, Espresso). Non sono stati eseguiti automaticamente in questa sessione.
- Per validare manualmente: `./gradlew testDebug` e `./gradlew connectedDebugAndroidTest` (richiede device/emulatore).

## Aggiornamenti principali dell'ultima iterazione
- **Bug Apri Schede**: l'accesso all'activity dei filtri non forza piu' la navigazione verso la creazione schede; viene mostrato uno stato "vuoto" con CTA se il DB e' privo di schede.
- **UI Home**: `AdapterHome` ora delega gli eventi alla `HomeFragment`, supporta l'enum `HomeMenuAction` e usa direttamente le risorse drawable.
- **Activity Compose Apri Schede**: lo shortcut "Apri Schede" apre ora `SchedeListActivity` (Compose) con lista, riepilogo esercizi e CTA dirette verso `AcitivySheda` per aggiungere/modificare esercizi riusando il flusso esistente.
- **Export/Share PDF**: da `SchedeListActivity` puoi salvare la scheda (Documenti/ProgrettoFitX) e condividerla subito con WhatsApp/Telegram tramite il FileProvider interno.
- **Dettaglio scheda Compose**: dialog full screen brandizzato (toolbar #455A64, font Montserrat) con CRUD esercizi inline e CTA coerenti con il resto dell’app.
- **Tema dark coerente**: tutte le schermate principali (home, dashboard, form di creazione, Compose dialog) utilizzano ora la palette `second/#455A64` + surface scure per ridurre l’affaticamento visivo.
- **Dashboard powerlifting**: la sezione ora ha background gradiente, card moderne e metriche aggiornate (schede, preferite, esercizi, ultima sessione) per avere un colpo d’occhio immediato sui progressi.
- **Notifiche**: la tab notifiche mostra la cronologia in una LazyColumn Compose, pronta a diventare la timeline ufficiale degli alert.
- **Miglioria Activity Schede**: `BaseAcitivity` espone il binding ai derivati e `AcitivySheda` inizializza correttamente `SchedeViewModel`, evitando view gonfiate inutilmente.
- **Repository**: rimosse duplicazioni nella definizione di `SchedeRepository`.
- **Documentazione**: questo README riassume struttura, dipendenze e modalita' operative del progetto.

## Prossimi passi suggeriti
1. Aggiungere test strumentali sul flusso di creazione/lettura schede.
2. Validare i filtri con dati reali e gestire stati di caricamento/errore.
3. Esporre le notifiche/run del WorkManager tramite una schermata di impostazioni.
4. Internazionalizzare tutte le stringhe rimanenti e correggere i refusi nei nomi (es. "Schede").
