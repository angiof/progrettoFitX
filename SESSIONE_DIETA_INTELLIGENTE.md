# Sessione Dieta Intelligente (Gemma + Open Food Facts)

## Obiettivo
Fornire un feedback immediato sulla qualita nutrizionale di cio che l utente mangia, in relazione all allenamento del giorno. Non e un semplice conteggio calorico, ma una lettura di efficienza metabolica per supportare performance e recupero.

## Flusso utente
1) Acquisizione alimento tramite barcode (EAN-13 o EAN-8) con Google Code Scanner.
2) Se lo scanner fallisce o il prodotto non ha barcode: ricerca testuale o OCR della tabella nutrizionale.
3) I macro vengono salvati nella data odierna e mostrati nello storico della sessione.
4) Gemma genera un consiglio motivante e tecnico basato su macro assunti e tipo di allenamento del giorno.

## Dati e database (Room)
- DailyNutrition: aggrega i macro della giornata (proteine, carboidrati, grassi, kcal) per data.
- DailyNutritionItem: singoli elementi consumati con nome, grammi, macro e sorgente (barcode, ricerca, OCR, manuale).
- La relazione e per data (stringa gg/MM/yyyy) e gli elementi compongono il totale giornaliero.

## Open Food Facts
- Chiamata senza API key.
- Endpoint: https://world.openfoodfacts.org/api/v2/product/{BARCODE}.json
- Fields richiesti: product_name, nutriments, image_url.
- La ricerca testuale usa il search endpoint per prendere il primo prodotto con nutriments disponibili.
l f
## Analisi Gemma
Gemma riceve un prompt con:
- Allenamento del giorno (ricavato dalle schede salvate in data odierna).
- Macro totali assunti (proteine e carboidrati in particolare).
Output atteso: risposta tecnica ma motivante, massimo 30 parole.

## Gestione edge cases
- Prodotto non trovato: avvia OCR o inserimento manuale.
- Offline: Gemma continua a funzionare; i dati restano in cache o inseriti manualmente.
- API lenta: shimmer effect mentre Gemma prepara il consiglio.

## UI e navigazione
- Nuova card in Home per aprire la Sessione Dieta Intelligente.
- Schermata dedicata con:
  - Scanner barcode, ricerca testuale, OCR.
  - Dettaglio prodotto con macro per 100g.
  - Aggiunta ai macro giornalieri con quantita in grammi.
  - Storico pasti della giornata.
  - Consiglio Gemma con stato di caricamento.

## Stato implementazione
- Implementazione attuale in XML + Activity (non Compose).
- Serve porting in Compose se il progetto e totalmente Compose.

## Note su OCR e manuale
- I valori nutrizionali inseriti manualmente o via OCR sono interpretati come per 100g.
- Se viene indicata una quantita in grammi, i macro sono scalati in base al rapporto grammi/100.

## File principali (implementazione corrente)
- app/src/main/java/com/app/fityo/ui/diet/DietSessionActivity.kt
- app/src/main/java/com/app/fityo/ui/diet/DietSessionViewModel.kt
- app/src/main/java/com/app/fityo/data_layer/repository/NutritionRepository.kt
- app/src/main/java/com/app/fityo/data_layer/db/DailyNutritionEntity.kt
- app/src/main/java/com/app/fityo/data_layer/db/DailyNutritionItemEntity.kt
- app/src/main/java/com/app/fityo/data_layer/db/dao/DaoDailyNutrition.kt
- app/src/main/java/com/app/fityo/data_layer/db/dao/DaoDailyNutritionItem.kt
- app/src/main/java/com/app/fityo/data_layer/network/OpenFoodFactsService.kt
- app/src/main/res/layout/activity_diet_session.xml

