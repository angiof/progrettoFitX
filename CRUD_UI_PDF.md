# Fityo - CRUD, UI Fragments, Dialogs, PDF Export

## Core flow (schede + esercizi)
- Schede entity: SchedeEntity in Room (DbFit).
- Esercizi entity: EsserciziEntity linked by schedaId.
- CRUD is split between XML fragments (create flow) and Compose dialogs (detail flow).

## CRUD schede
### Create / Update
- Entry: AcitivySheda with NavHost and tabs (res/navigation/create_schedes_navigations.xml).
- FragCreateSchedeForm.kt builds SchedeEntity and insert/update via SchedeViewModel -> UsesCasesSheda -> SchedeRepository -> DaoSchede.
- Date picker: MaterialDatePicker, muscle groups multi-select dialog.
- After save, navigation to FragEssercissi with bundle.

### Read
- SchedeListViewModel.loadSchede uses DaoSchede.getAllSchede.
- XML list: ShedeFragments.kt + SchedaAdapter.kt.
- Compose list: SchedeListActivity.kt (LazyColumn + detail dialog).

### Delete
- ShedeFragments.kt shows AlertDialog confirm, then SchedeListViewModel.deleteScheda.
- SchedeListActivity.kt delete action in SchedaDetailDialog.

### Other state
- Favorite: DaoSchede.setFavorite via SchedeListViewModel.
- Completed: DaoSchede.setCompleted via SchedeListViewModel.

## CRUD esercizi
### Create / Update (XML flow)
- FragEssercissi.kt opens RipetizioniSheetFragment.kt (BottomSheetDialogFragment).
- RipetizioniSheetFragment builds EsserciziEntity and insert/update via EsserciziViewModel -> UsesCasesEssercissi -> EsserciziRepository -> DaoEssercissi.

### Create / Update (Compose flow)
- SchedeListActivity.kt uses ExerciseFormDialog in SchedaDetailDialog to add/edit exercises.

### Read
- DaoEssercissi.getEssercissiBySchedaId returns LiveData.
- Observed in FragEssercissi.kt and in SchedaDetailDialog (Compose).

### Delete
- Adapter delete buttons and swipe in FragEssercissi.kt.
- Delete action in SchedaDetailDialog (Compose).

## UI fragments / activities
- app/src/main/java/com/app/fityo/ui/MainActivity.kt: bottom nav host.
- app/src/main/java/com/app/fityo/ui/home/HomeFragment.kt: quick actions to create/open schede.
- app/src/main/java/com/app/fityo/ui/forms/AcitivySheda.kt: tabs + NavHost for CRUD flow.
- app/src/main/java/com/app/fityo/ui/shedeForms/FragCreateSchedeForm.kt: scheda form.
- app/src/main/java/com/app/fityo/ui/shedeForms/FragEssercissi.kt: exercise list + add/edit.
- app/src/main/java/com/app/fityo/ui/shedeForms/ui/FragmentRepielogo.kt: summary + optional time.
- app/src/main/java/com/app/fityo/fragments/filtro/ShedeFragments.kt: list + filters.
- app/src/main/java/com/app/fityo/ui/compose/SchedeListActivity.kt: Compose list + detail dialog + export/share.

## Dialogs used in CRUD
- FragCreateSchedeForm.kt:
  - MaterialDatePicker for date.
  - MaterialAlertDialogBuilder with res/layout/dialog_multi_select_groups.xml for multi-select groups.
- ShedeFragments.kt: AlertDialog confirm delete scheda.
- RipetizioniSheetFragment.kt: BottomSheetDialogFragment for add/edit; duration picker via MaterialAlertDialogBuilder and res/layout/dialog_duration_picker.xml.
- FragmentRepielogo.kt: TimePickerDialog for reminder time.
- SchedeListActivity.kt (Compose):
  - SchedaDetailDialog: full-screen dialog with actions.
  - ExerciseFormDialog: add/edit exercise.
  - MetadataDialog: metadata and PDF format selection.

## PDF export and share
### Flow
1) User triggers Export/Share in SchedaDetailDialog (SchedeListActivity.kt).
2) MetadataDialog collects title/coach/athlete/date range and pdf format.
3) PdfExporter.exportScheda builds a PdfDocument, draws header/footer, info, notes, exercises, writes to app files and copies to public Downloads.
4) Export shows toast; Share uses ShareUtils.sharePdf and FileProvider.

### Files involved
- app/src/main/java/com/app/fityo/utils/ExportMetadata.kt: metadata structure (pdfFormat = CLASSIC or MODERN).
- app/src/main/java/com/app/fityo/utils/PdfExporter.kt: PdfDocument builder + file copy.
- app/src/main/java/com/app/fityo/utils/ShareUtils.kt: ACTION_SEND intent.
- app/src/main/AndroidManifest.xml: FileProvider config.
- app/src/main/res/xml/file_paths.xml: shared file path.

### Note
- ExportMetadata.pdfFormat is collected in UI, but PdfExporter currently does not branch by format (output is fixed).
