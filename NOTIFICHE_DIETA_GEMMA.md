# Feature: Notifiche Intelligenti Dieta con Gemma

## Obiettivo
Creare un sistema di notifiche giornaliere che analizza i dati nutrizionali dell'utente e genera commenti personalizzati usando Gemma, integrandoli con i dati delle schede di allenamento.

---

## Architettura Esistente

### Database Dieta
- `DailyNutritionEntity` - Totali giornalieri (kcal, proteine, carboidrati, grassi, ecc.)
- `DailyNutritionItemEntity` - Singoli alimenti consumati
- `DaoDailyNutrition` - Query per totali giornalieri
- `DaoDailyNutritionItem` - Query per singoli alimenti

### WorkManager
- `NotificationWorker` - Worker esistente per notifiche workout
- Canale notifiche: `reminderChannel`

### Gemma
- Profile `DIET` disponibile (temperature=0.5f)
- `GemmaLlmHelper` per inferenza

---

## Flusso Proposto

```
1. WorkManager triggera DietNotificationWorker (es. ore 9:00)
         ↓
2. Query DB: dati ieri + media ultimi 7 giorni + scheda oggi
         ↓
3. Calcola metriche (delta calorie, confronto media, ecc.)
         ↓
4. Costruisci prompt contestuale per Gemma
         ↓
5. Gemma genera commento personalizzato
         ↓
6. Mostra notifica con commento
         ↓
7. Salva in NotificationEntity per storico
```

---

## Dati da Raccogliere

### Dal DB Dieta
```kotlin
// Totali di ieri
val yesterdayTotals = daoDailyNutrition.getByDate(yesterday)

// Media ultimi 7 giorni
val weeklyData = daoDailyNutrition.getAll()
    .filter { it.date in lastWeekDates }
val weeklyAvgKcal = weeklyData.map { it.totalKcal }.average()

// Prodotto piu calorico di ieri
val yesterdayItems = daoDailyNutritionItem.getByDate(yesterday)
val highestCalItem = yesterdayItems.maxByOrNull { it.kcal }
```

### Dal DB Schede
```kotlin
// Scheda programmata per oggi
val todayWorkout = daoSchede.getSchedaByDate(today)

// Esercizi della scheda
val exercises = daoEssercizi.getEserciziBiIdScheda(schedaId)
```

---

## Metriche da Calcolare

| Metrica | Formula | Uso |
|---------|---------|-----|
| Delta vs media | `(ieri - media) / media * 100` | "Hai mangiato il 50% in piu" |
| Surplus calorico | `ieri - TDEE_stimato` | "Eccesso di X kcal" |
| Calorie da bruciare | `surplus / 7.7` | "~Y kcal da smaltire" |
| Stima esercizio | `calorie / MET * peso * tempo` | "30 min di cardio" |

---

## Prompt Gemma

```
Sei un coach fitness italiano. Analizza questi dati e genera UN SOLO commento motivazionale (max 200 caratteri) per una notifica.

DATI:
- Calorie ieri: {kcal_ieri}
- Media settimanale: {media_kcal}
- Variazione: {delta}%
- Prodotto piu calorico: {nome_prodotto} ({kcal_prodotto} kcal)
- Scheda oggi: {nome_scheda o "Nessuna"}
- Esercizi previsti: {lista_esercizi o "Nessuno"}

REGOLE:
- Se delta > 30%: suggerisci di compensare con allenamento
- Se delta < -20%: complimentati ma ricorda di non esagerare
- Se c'e scheda oggi: collega il suggerimento agli esercizi
- Se non c'e scheda: suggerisci attivita leggera (camminata, addominali)
- Tono: amichevole ma diretto, senza giri di parole
- NO emoji

RISPOSTA (solo il commento):
```

---

## Esempi Output Attesi

### Caso 1: Eccesso + Scheda presente
> "Ieri 2800 kcal, il 40% sopra la tua media. Oggi hai gambe in programma, spingi forte per compensare!"

### Caso 2: Eccesso + Nessuna scheda
> "Ieri hai esagerato con la pizza (850 kcal). Oggi niente scheda? Aggiungi 20 min di camminata veloce."

### Caso 3: Sotto media
> "Ieri solo 1400 kcal, sei al 25% sotto media. Non saltare i pasti, ti servono energie per allenarti bene."

### Caso 4: Nella norma
> "Ieri 2100 kcal, perfettamente in linea. Continua cosi e vedrai i risultati!"

---

## Implementazione

### 1. Nuovo Worker
```kotlin
// File: app/src/main/java/com/app/fityo/workMangerFit/DietNotificationWorker.kt

class DietNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = DbFit.getDatabase(applicationContext)
        val nutritionDao = db.dailyNutritionDao()
        val itemsDao = db.dailyNutritionItemDao()
        val schedeDao = db.schedeDao()

        // 1. Raccogli dati
        val metrics = collectNutritionMetrics(nutritionDao, itemsDao, schedeDao)

        // 2. Genera commento con Gemma
        val comment = generateDietComment(metrics)

        // 3. Mostra notifica
        showDietNotification(comment)

        // 4. Salva in storico
        saveToHistory(comment)

        return Result.success()
    }
}
```

### 2. Scheduling
```kotlin
// In Application o MainActivity
val dietWorkRequest = PeriodicWorkRequestBuilder<DietNotificationWorker>(
    1, TimeUnit.DAYS
)
    .setInitialDelay(calculateDelayUntil9AM(), TimeUnit.MILLISECONDS)
    .build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "diet_notification",
    ExistingPeriodicWorkPolicy.KEEP,
    dietWorkRequest
)
```

### 3. Nuovo Canale Notifiche
```kotlin
val dietChannel = NotificationChannel(
    "dietCoachChannel",
    "Coach Alimentare",
    NotificationManager.IMPORTANCE_DEFAULT
)
```

---

## Query DAO da Aggiungere

```kotlin
// In DaoDailyNutrition
@Query("SELECT * FROM daily_nutrition WHERE date >= :startDate ORDER BY date DESC")
suspend fun getFromDate(startDate: String): List<DailyNutritionEntity>

@Query("SELECT AVG(totalKcal) FROM daily_nutrition WHERE date >= :startDate")
suspend fun getAverageKcal(startDate: String): Float?
```

---

## File da Creare/Modificare

| File | Azione |
|------|--------|
| `DietNotificationWorker.kt` | Nuovo |
| `DietMetrics.kt` | Nuovo (data class) |
| `DietPromptBuilder.kt` | Nuovo |
| `DaoDailyNutrition.kt` | Aggiungere query |
| `Application.kt` | Scheduling WorkManager |
| `AndroidManifest.xml` | Registrare nuovo canale |

---

## Priorita Implementazione

1. **Query DB** - Aggiungere metodi DAO per media settimanale
2. **Data Class** - Creare `DietMetrics` per aggregare i dati
3. **Prompt Builder** - Costruire prompt dinamico
4. **Worker** - Implementare logica completa
5. **Scheduling** - Configurare esecuzione giornaliera
6. **Test** - Verificare generazione commenti

---

## Note

- Gemma potrebbe impiegare qualche secondo per generare la risposta
- Prevedere fallback se Gemma non e disponibile (commento statico)
- Considerare privacy: i dati nutrizionali restano sul device
- Il worker deve gestire casi edge (nessun dato, primo utilizzo)
