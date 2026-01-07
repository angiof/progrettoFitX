# Sessione: Coach Mode + Dashboard Profile Filter

## Data: 2026-01-06

---

## Obiettivo della Sessione
Implementare il filtro profilo nella Dashboard e migliorare la navigazione nel Coach Mode.

---

## Modifiche Implementate

### 1. Selettore Profilo nella Dashboard

#### File Modificati:
- `app/src/main/res/layout/fragment_dashboard.xml` - Aggiunto bottone selettore profilo
- `app/src/main/res/values/strings.xml` - Aggiunte stringhe localizzate
- `app/src/main/res/drawable/ic_baseline_person_24.xml` - Creata icona persona (NUOVO)
- `app/src/main/java/com/app/fityo/ui/dashboard/DashViewModelFactory.kt` - Supporto DaoCoachProfile
- `app/src/main/java/com/app/fityo/ui/dashboard/DashViewModel.kt` - Logica filtro profilo
- `app/src/main/java/com/app/fityo/ui/dashboard/DashFrag.kt` - UI selezione profilo

#### Nuove Stringhe:
```xml
<string name="dashboard_profile_all">Tutti i dati</string>
<string name="dashboard_select_profile">Seleziona profilo</string>
<string name="dashboard_personal_data">Dati personali</string>
<string name="dashboard_showing_profile">Profilo: %s</string>
```

#### Funzionalità:
- Bottone chip in alto nella Dashboard per selezionare il profilo
- Dialog con lista profili coach + opzione "Tutti i dati"
- Tutti i grafici e statistiche si aggiornano in base al profilo selezionato:
  - Pie chart gruppi muscolari
  - Bar chart intensità media
  - Grafico frequenza settimanale
  - Card statistiche (totale schede, preferite, ultimo allenamento, ecc.)

---

### 2. Repository - Metodi Coach Profile

#### File Modificato:
- `app/src/main/java/com/app/fityo/data_layer/repository/SchedeRepository.kt`

#### Nuovi Metodi:
```kotlin
suspend fun getSchedeByCoachProfile(profileId: Int): List<SchedeEntity>
suspend fun getPersonalSchede(): List<SchedeEntity>
suspend fun countSchedeByCoachProfile(profileId: Int): Int
suspend fun getPercentualeByCoachProfile(profileId: Int): List<GruppoMuscolarePercentuale>
suspend fun getMediaIntensitaByCoachProfile(profileId: Int): List<GruppoMuscolareIntensitaMedia>
suspend fun getWorkoutCountByWeekdayForCoach(profileId: Int): List<WeekdayWorkoutCount>
suspend fun getLastWorkoutDateByCoach(profileId: Int): String?
suspend fun getMostTrainedMuscleGroupByCoach(profileId: Int): String?
suspend fun countFavoriteSchedeByCoach(profileId: Int): Int
```

---

### 3. Navigazione Dettaglio Scheda da Coach Mode

#### File Modificato:
- `app/src/main/java/com/app/fityo/ui/coach/CoachActivity.kt`

#### Modifica:
```kotlin
// Prima (non funzionante)
private fun navigateToViewScheda(schedaId: Int) {
    val intent = Intent(this, AcitivySheda::class.java).apply {
        putExtra("scheda_id", schedaId)
    }
}

// Dopo (funzionante)
private fun navigateToViewScheda(scheda: SchedeEntity) {
    val intent = Intent(this, AcitivySheda::class.java).apply {
        putExtra("f", scheda)
        putExtra("isNew", false)
        ActiveCoachSession.activeProfileId?.let {
            putExtra(EXTRA_COACH_PROFILE_ID, it)
        }
    }
}
```

---

## Architettura Filtro Dashboard

```
┌─────────────────────────────────────────────────────────────┐
│                      DashFrag.kt                            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  btn_select_profile.onClick -> showProfileDialog()   │   │
│  └──────────────────────────────────────────────────────┘   │
│                           │                                  │
│                           ▼                                  │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  viewModel.setSelectedProfile(id, name)              │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    DashViewModel.kt                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  _selectedProfileId: StateFlow<Int?>                 │   │
│  │  _selectedProfileName: LiveData<String?>             │   │
│  │  _coachProfiles: LiveData<List<CoachProfileEntity>>  │   │
│  └──────────────────────────────────────────────────────┘   │
│                           │                                  │
│       refreshAllDataForProfile() chiama:                    │
│       - loadPercentualiGruppiMuscolari()                    │
│       - loadMediaIntensitaAll()                             │
│       - loadWeekFrequencyAll()                              │
│       - refreshStats()                                       │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  SchedeRepository.kt                        │
│  if (profileId != null)                                     │
│      -> getPercentualeByCoachProfile(profileId)             │
│  else                                                       │
│      -> getPercentualePerGruppoMuscolare()                  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      DaoSchede.kt                           │
│  Query SQL filtrate per coachProfileId                      │
└─────────────────────────────────────────────────────────────┘
```

---

## Test Consigliati

1. **Seleziona profilo coach** → I grafici devono mostrare solo i dati di quel profilo
2. **Seleziona "Tutti i dati"** → I grafici devono tornare a mostrare tutto
3. **Crea scheda in Coach Mode** → La scheda deve avere coachProfileId corretto
4. **Click su scheda in Coach Mode** → Deve aprire AcitivySheda in modalità visualizzazione

---

## Note Tecniche

- Il filtro profilo usa `StateFlow` per reattività
- Il dialog usa `MaterialAlertDialogBuilder` per consistenza Material Design
- La Dashboard si aggiorna automaticamente quando il profilo cambia
- I dati vengono ricaricati anche in `onResume()` per sincronizzazione

---

## Build Status
✅ Build completato con successo
⚠️ Warning deprecation (non bloccanti)
