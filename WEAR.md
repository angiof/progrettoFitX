# Fityo Wear – note operative

## Cosa include questa versione
- **Modulo Wear OS dedicato** (`wear/`) con `applicationId` allineato al telefono (`com.app.fityo`) per pairing e Data Layer coerente.
- UI Compose Wear: `WearMainActivity` mostra la lista schede, dettaglio compatto con esercizi togglabili e azione “Completa scheda” che aggiorna stato ed esercizi.
- Riuso del **modello Room** del telefono: lo stesso schema `DbFit`, `SchedeEntity`, `EsserciziEntity` e converter sono condivisi via `sourceSets` (data_layer + dominio).
- **Sync Data Layer**:
  - Wear → Telefono: `WearSyncClient` invia messaggi su `/fityo/sync` per toggle esercizi e completamento scheda.
  - Telefono → Wear: `PhoneWearListenerService` (registrato in manifest) riceve i messaggi e aggiorna il DB locale.
  - Capability `fityo_sync` registrata su entrambe le app per scoprire i nodi disponibili.
- R8/ProGuard attivi anche in debug su entrambi i moduli (ottimizzazione disabilitata per build debuggable, warning previsto).

## Dipendenze chiave
- `play-services-wearable` su entrambi i moduli per Data Layer.
- `gson` nel modulo Wear (usato dai converter condivisi).

## Note di build
- Il modulo Wear riutilizza gli asset launcher del modulo phone (`ic_launcher_background/foreground` copiati in `wear/src/main/res`).
- Tema Wear minimale (`@android:style/Theme.DeviceDefault`) per compatibilità rapida; personalizzabile in `wear/src/main/res/values/themes.xml`.

## Checklist di pairing e sync
1) Assicurarsi che telefono e Wear siano abbinati con lo stesso account/`applicationId`.
2) Build/installare entrambi gli APK (`assembleDebug` e `:wear:assembleDebug`).
3) Verificare che la capability `fityo_sync` sia registrata (parte dalla `APPlicationServices` del phone e dal `WearSyncClient` lato Wear).
4) Aprire la scheda su Wear, togglare esercizi o completare la scheda: i cambi devono riflettersi sul DB telefono.
