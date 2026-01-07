# Sessione: Sistema Tutor con Feedback Graduato e Configurabile

## Data: 2026-01-06

---

## Obiettivo della Sessione
Implementare un sistema di feedback incoraggiante e configurabile per il Tutor, combinando MediaPipe con soglie definite in JSON.

---

## Problemi Risolti

### 1. Feedback Troppo Severo
**Prima:** Il sistema mostrava solo "errore" o "ok", scoraggiando gli utenti.
**Dopo:** Sistema graduato con 4 livelli:
- 🟢 **NONE** - Perfetto, nessun feedback negativo
- 🔵 **SUGGESTION** - Quasi perfetto, feedback incoraggiante (blu)
- 🟡 **WARNING** - Da migliorare
- 🟠 **ERROR** - Errore da correggere
- 🔴 **CRITICAL** - Errore critico

### 2. Soglie Hardcoded
**Prima:** Tutte le soglie erano nel codice Kotlin, richiedevano ricompilazione.
**Dopo:** Soglie in `assets/exercise_configs.json`, modificabili senza ricompilare.

### 3. Feedback Non Specifico
**Prima:** Messaggi generici ("Errore postura").
**Dopo:** Messaggi specifici e incoraggianti per ogni livello di soglia.

---

## File Creati

### 1. `assets/exercise_configs.json`
Configurazione completa per tutti gli esercizi:

```json
{
  "exercises": {
    "SQUAT": {
      "checkpoints": {
        "knee_angle": {
          "thresholds": {
            "perfect": { "max": 90, "severity": "NONE" },
            "good": { "max": 100, "severity": "SUGGESTION" },
            "acceptable": { "max": 110, "severity": "WARNING" },
            "insufficient": { "max": 120, "severity": "ERROR" },
            "critical": { "max": 180, "severity": "CRITICAL" }
          },
          "correctionHints": {
            "SUGGESTION": "Ottimo! Sei molto vicino...",
            "WARNING": "Buon inizio! Scendi ancora un po'...",
            ...
          }
        }
      }
    }
  }
}
```

### 2. `tutor/analysis/config/ExerciseConfig.kt`
Data classes per il parsing JSON:
- `ExerciseConfig` - Configurazione esercizio
- `CheckpointConfig` - Punto di verifica
- `ThresholdLevel` - Singola soglia
- `GlobalAnalysisSettings` - Impostazioni globali

### 3. `tutor/analysis/config/ExerciseConfigLoader.kt`
Loader singleton che:
- Carica JSON da assets
- Parsa in data classes
- Cache per performance
- Metodi per valutare checkpoint

### 4. `tutor/analysis/ConfigurableAnalyzer.kt`
Classe base astratta per analyzer configurabili:
- Usa JSON per soglie e messaggi
- Gestisce debouncing automatico
- Traccia min/max valori
- Calcola score

### 5. `tutor/analysis/ConfigurableSquatAnalyzer.kt`
Implementazione squat che usa ConfigurableAnalyzer:
- Estrae metriche da MediaPipe
- Mappa checkpoint -> ErrorType
- Definisce landmark coinvolti

---

## File Modificati

### 1. `dominio/ExerciseError.kt`
Aggiunto nuovo livello di severità:
```kotlin
enum class ErrorSeverity(..., val isEncouraging: Boolean = false) {
    SUGGESTION("Da migliorare", 0.25f, true),  // BLU - incoraggiante
    WARNING("Attenzione", 0.5f),
    ERROR("Errore", 0.75f),
    CRITICAL("Critico", 1.0f)
}
```

### 2. `ui/tutor/compose/PlaybackScreen.kt`
- Aggiunto colore blu per SUGGESTION
- Overlay real-time con suggerimento durante playback
- Icona diversa per feedback incoraggiante (✓ invece di ⚠)

### 3. `ui/tutor/compose/TutorResultScreen.kt`
- Supporto colore blu per SUGGESTION
- Icona stella per suggerimenti
- Messaggi di punteggio più incoraggianti

### 4. `tutor/analysis/SquatAnalyzer.kt`
- Soglie graduali (PERFECT/GOOD/ACCEPTABLE/ERROR/CRITICAL)
- Messaggi incoraggianti per ogni livello
- Penalità ridotta per SUGGESTION (2 punti invece di 5-25)

---

## Architettura Sistema Configurabile

```
┌─────────────────────────────────────────────────────────────┐
│                 exercise_configs.json                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  {                                                   │   │
│  │    "SQUAT": {                                        │   │
│  │      "knee_angle": { thresholds, hints }            │   │
│  │      "back_angle": { thresholds, hints }            │   │
│  │    }                                                 │   │
│  │  }                                                   │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  ExerciseConfigLoader                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  - Carica JSON da assets                             │   │
│  │  - Parsa in data classes                             │   │
│  │  - Cache configurazione                              │   │
│  │  - evaluateCheckpoint(exercise, checkpoint, value)   │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  ConfigurableAnalyzer                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  abstract fun extractMetrics(): Map<String, Float?>  │   │
│  │  - Debouncing automatico                             │   │
│  │  - Tracking min/max                                  │   │
│  │  - Genera errori con severità da JSON               │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              ConfigurableSquatAnalyzer                       │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  extractMetrics():                                   │   │
│  │    - knee_angle: AngleCalculator.calculateKneeAngle  │   │
│  │    - back_angle: AngleCalculator.calculateBackAngle  │   │
│  │    - knee_valgus: ratio ginocchia/caviglie          │   │
│  │    - heels: isHeelRaised                            │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## Angoli Biomeccanici di Riferimento

### Squat
| Checkpoint | Perfect | Good | Warning | Error |
|------------|---------|------|---------|-------|
| Ginocchio (profondità) | ≤90° | ≤100° | ≤110° | ≤120° |
| Schiena (inclinazione) | ≤30° | ≤40° | ≤50° | ≤60° |
| Valgus (ratio) | ≥1.0 | ≥0.9 | ≥0.85 | <0.85 |

### Panca Piana
| Checkpoint | Perfect | Good | Warning | Error |
|------------|---------|------|---------|-------|
| ROM gomito | ≤90° | ≤100° | ≤110° | >110° |
| Flare gomiti | 45-75° | 75-85° | 85-90° | >90° |
| Lockout | ≥170° | ≥160° | ≥150° | <150° |

### Stacco
| Checkpoint | Perfect | Good | Warning | Critical |
|------------|---------|------|---------|----------|
| Curvatura schiena | ≤10° | ≤15° | ≤20° | >30° |
| Hip hinge | 80-110° | 70-120° | fuori range | - |

### Affondi
| Checkpoint | Perfect | Good | Warning |
|------------|---------|------|---------|
| Ginocchio ant. | 85-95° | 80-100° | 70-110° |
| Busto verticale | ≤10° | ≤20° | ≤30° |

---

## Come Modificare le Soglie

1. Apri `app/src/main/assets/exercise_configs.json`
2. Trova l'esercizio e il checkpoint
3. Modifica i valori `min`/`max` nelle soglie
4. Modifica i `correctionHints` se necessario
5. Riavvia l'app (no ricompilazione necessaria per release)

---

## Prossimi Step Suggeriti

1. **Completare gli analyzer configurabili** per:
   - Panca Piana
   - Stacco
   - Affondi
   - Curl Bicipiti
   - Shoulder Press

2. **Aggiungere supporto DTW** (Dynamic Time Warping):
   - Salvare sequenze "master" per ogni esercizio
   - Confrontare movimento utente vs master

3. **Aggiungere analisi tempo/velocità**:
   - Misurare durata fase eccentrica/concentrica
   - Feedback su tempo sotto tensione

4. **UI per modificare soglie**:
   - Permettere a utenti avanzati di personalizzare le soglie

---

## Test Consigliati

1. **Test Squat profondità:**
   - Video con squat profondi (≤90°) → Nessun errore
   - Video con squat medi (100-110°) → SUGGESTION blu
   - Video con squat alti (>120°) → ERROR

2. **Test Feedback overlay:**
   - Durante playback, verificare che appaia il suggerimento
   - Verificare colore blu per SUGGESTION

3. **Test modifica JSON:**
   - Cambiare soglia in JSON
   - Riavviare app
   - Verificare che nuova soglia sia applicata

---

## Note Tecniche

- Il JSON viene caricato una volta e cachato
- `reload()` disponibile per hot-reload in debug
- `consecutiveFramesThreshold` evita falsi positivi
- Penalità score: SUGGESTION=2, WARNING=5, ERROR=15, CRITICAL=25
