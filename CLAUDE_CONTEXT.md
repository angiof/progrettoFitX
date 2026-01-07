# Contesto Sessione Claude - Dual Engine Gemma

## Stato Attuale: IN CORSO

### Obiettivo
Implementare supporto **dual-engine GPU/CPU** per Gemma LLM nell'app FitYo, con fallback automatico da GPU a CPU quando OpenCL non è disponibile (es. Android 16 beta).

---

## File Creati (da copiare)

### FASE 1: Dual Engine Import

| File Sorgente | Destinazione | Stato |
|---------------|--------------|-------|
| `GemmaLlmHelper_NUOVO.txt` | `app/src/main/java/com/app/fityo/import_scheda/GemmaLlmHelper.kt` | DA COPIARE |
| `GemmaEngineSelectionDialog_NUOVO.txt` | `app/src/main/java/com/app/fityo/import_scheda/GemmaEngineSelectionDialog.kt` | DA COPIARE |
| `ImportSchedaScreen_MODIFICHE.txt` | Istruzioni per modificare ImportSchedaScreen.kt | DA APPLICARE |

### FASE 2: Analytics Dashboard con Gemma LIVE

| File Sorgente | Destinazione | Stato |
|---------------|--------------|-------|
| `GemmaAnalyticsHelper_NUOVO.txt` | `app/src/main/java/com/app/fityo/analytics/GemmaAnalyticsHelper.kt` | DA COPIARE |
| `GemmaAnalyticsHelper_ENHANCED.txt` | Prompt ENHANCED con TUTTI i dati | DA INTEGRARE |
| `GemmaInsightsCards_NUOVO.txt` | `app/src/main/java/com/app/fityo/analytics/ui/GemmaInsightsCards.kt` | DA COPIARE |
| `AnalyticsViewModel_MODIFICHE.txt` | Istruzioni per modificare AnalyticsViewModel.kt | DA APPLICARE |
| `GemmaAnalytics_PROFILI_UPDATE.txt` | Supporto multi-profilo per Gemma Analytics | DA INTEGRARE |

## File Esistente (già ok)

| File | Stato |
|------|-------|
| `app/src/main/java/com/app/fityo/import_scheda/GemmaEngineType.kt` | OK - Enum GPU/CPU |

---

## Task Pendente: Riattivare Camera OCR

### Problema
In `ImportSchedaScreen.kt` l'opzione Camera OCR è commentata con `// DISABLED:`:

**Blocco 1 (linee ~110-117)** - Nel dialog ImportOptionsDialog:
```kotlin
// DISABLED:                 // Opzione 3: Camera OCR
// DISABLED:                 ImportOptionCard(
// DISABLED:                     icon = Icons.Default.CameraAlt,
// DISABLED:                     title = stringResource(R.string.import_option_camera),
// DISABLED:                     subtitle = stringResource(R.string.import_option_camera_desc),
// DISABLED:                     accentColor = AccentGreen,
// DISABLED:                     onClick = onSelectCamera
// DISABLED:                 )
```

**Blocco 2 (linee ~417-431)** - In CaptureSelectionContent:
```kotlin
// DISABLED:         // Camera button
// DISABLED:         Button(
// DISABLED:             onClick = onCameraClick,
// DISABLED:             ...
// DISABLED:         }
// DISABLED:
```

### Soluzione
Rimuovere `// DISABLED:` da entrambi i blocchi per riattivare la funzionalità.

---

## Modifiche Implementate in GemmaLlmHelper_NUOVO.txt

1. **MAX_TOKENS = 2048** (era 1024) - Per supportare più gruppi muscolari
2. **currentEngineType** - Variabile per tracciare engine corrente (GPU/CPU)
3. **setEngineType()** - Cambia engine e reinizializza
4. **getEngineType()** - Restituisce engine corrente
5. **getAvailableEngines()** - Lista engine con modello disponibile
6. **initializeModelWithFallback()** - Tenta GPU, fallback a CPU su errore OpenCL
7. **parseRestTime()** - Parsing recupero: "02.00", "1'30", "2 min", "90s"
8. **inferEquipmentFromName()** - Inferisce attrezzo da nome esercizio
9. **ParsedExercise.equipment** - Nuovo campo per attrezzo

## Componenti in GemmaEngineSelectionDialog_NUOVO.txt

1. **GemmaEngineSelectionDialog** - Dialog per scegliere GPU/CPU
2. **EngineOptionCard** - Card per opzione engine
3. **CurrentEngineChip** - Chip da mettere in TopAppBar

---

## Modelli Richiesti sul Device

```
/data/local/tmp/llm/gemma-2b-it-gpu-int4.bin  (~1.35GB)
/data/local/tmp/llm/gemma-2b-it-cpu-int4.bin  (~1.35GB)
```

Copia via ADB:
```bash
adb push gemma-2b-it-gpu-int4.bin /data/local/tmp/llm/
adb push gemma-2b-it-cpu-int4.bin /data/local/tmp/llm/
```

---

## Prossimi Passi

1. [ ] Chiudere Android Studio
2. [ ] Scommentare Camera OCR in `ImportSchedaScreen.kt`
3. [ ] Copiare contenuto `GemmaLlmHelper_NUOVO.txt` in `GemmaLlmHelper.kt`
4. [ ] Creare `GemmaEngineSelectionDialog.kt` con contenuto di `GemmaEngineSelectionDialog_NUOVO.txt`
5. [ ] Aggiungere CurrentEngineChip nella TopAppBar di CameraOcrImportScreen
6. [ ] Testare build
7. [ ] Testare su device con entrambi i modelli

---

## Note Tecniche

- **GPU Engine**: Più veloce ma richiede driver OpenCL (non disponibile su Android 16 beta)
- **CPU Engine**: Universalmente compatibile ma più lento
- **Fallback**: Se GPU fallisce con errore OpenCL, passa automaticamente a CPU
- **MediaPipe GenAI**: Usa `tasks-genai:0.10.22`

---

---

## FASE 2: Integrazione Gemma in Analytics Dashboard

### Situazione Attuale Analytics

**File principali:**
- `AnalyticsViewModel.kt` - ViewModel che carica dati
- `AnalyticsDashboard.kt` - UI con LazyColumn (scrollabile)
- `AnalyticsCards.kt` - Cards UI (PerformanceScore, DigitalTwin, Recovery, etc.)
- `WorkoutModelStateStore.kt` - Engine analytics (rule-based)

**Problema:** I "Suggerimenti AI" sono **rule-based**, non usano Gemma LLM.

### Piano di Integrazione Gemma

#### 1. Creare `GemmaAnalyticsHelper.kt`
```kotlin
// app/src/main/java/com/app/fityo/analytics/GemmaAnalyticsHelper.kt
class GemmaAnalyticsHelper(context: Context) {
    private val gemma = GemmaLlmHelper.getInstance(context)

    suspend fun generateWorkoutInsights(
        stats: SessionStats,
        muscleBalance: MuscleBalance,
        recoveryStatus: RecoveryStatus
    ): Result<GemmaWorkoutAnalysis>
}
```

#### 2. Aggiungere `GemmaInsightsCard` in AnalyticsCards.kt
- Card che mostra analisi generata da Gemma
- Loading state mentre Gemma elabora
- Chip per selezione engine (GPU/CPU)

#### 3. Modificare AnalyticsViewModel
- Aggiungere stato per Gemma insights
- Chiamare GemmaAnalyticsHelper dopo il caricamento dati

### Dati Usati nel Prompt ENHANCED

Il prompt invia a Gemma TUTTI questi dati:

**Da SchedeEntity:**
- `data` - Data allenamento
- `ora` - Orario (Mattina/Pomeriggio/Sera)
- `gruppoMuscolare`, `gruppiMuscolari` - Muscoli allenati
- `intesita` - Intensità (Alta/Media/Bassa)
- `avgHeartRate`, `maxHeartRate` - BPM
- `totalSteps` - Passi
- `completed`, `completedDate` - Stato completamento

**Da EsserciziEntity:**
- `nome` - Nome esercizio
- `nSerie`, `nRipetizione` - Serie x Reps
- `peso` - Peso in kg
- `attrezzo` - Attrezzatura
- `intervallo` - Riposo tra serie (secondi)
- `insometria` - Isometria

**Analisi Calcolate:**
- Volume per gruppo muscolare (questa vs scorsa settimana)
- Trend carichi (% variazione)
- Distribuzione muscolare (%)
- Giorni riposo medi
- Frequenza settimanale
- Progressione pesi per esercizio
- Tipo allenamento (Forza/Ipertrofia/Resistenza)

### Cards Generate da Gemma

1. **GemmaWeeklyMotivationCard** - Messaggio motivazionale personalizzato con numeri reali
2. **WeeklyStatsQuickCard** - Stats veloci settimana (sessioni, volume, muscoli)
3. **NextWorkoutFocusCard** - Prossimo allenamento consigliato con esercizi
4. **GemmaLiveSuggestionsCard** - 4-5 suggerimenti SPECIFICI con priorità
5. **LoadRecommendationCard** - Aumenta/Mantieni/Riduci carichi (es. "+5kg su panca")
6. **RestRecommendationCard** - Consiglio giorni riposo
7. **DeloadWarningCard** - Avviso settimana di scarico

### Cache Settimanale

Gli insights vengono cachati per 7 giorni:
- `SharedPreferences` con `last_update_epoch_day`
- Refresh manuale con pulsante
- Cambio engine NON invalida cache automaticamente

### Note Dashboard Scroll

La Dashboard Compose (`DashboardScreen.kt`) **GIÀ HA** `.verticalScroll(scrollState)` alla linea 67.
- Il file XML `fragment_dashboard.xml` è LEGACY (non usato)
- `DashFrag.kt` crea direttamente un `ComposeView`

Se lo scroll non funziona, verificare:
1. Emulatore vs device fisico
2. Conflitti con gesture navigation
3. Touch event consumption

---

## Prossimi Passi FASE 2

1. [ ] Copiare `GemmaAnalyticsHelper_NUOVO.txt` in `analytics/GemmaAnalyticsHelper.kt`
2. [ ] Integrare prompt ENHANCED da `GemmaAnalyticsHelper_ENHANCED.txt`
3. [ ] Copiare `GemmaInsightsCards_NUOVO.txt` in `analytics/ui/GemmaInsightsCards.kt`
4. [ ] Applicare modifiche da `AnalyticsViewModel_MODIFICHE.txt`
5. [ ] Testare con dati reali
6. [ ] Verificare cache settimanale

---

*Ultimo aggiornamento: Sessione corrente*
