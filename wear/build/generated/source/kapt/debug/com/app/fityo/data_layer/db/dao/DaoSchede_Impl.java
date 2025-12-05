package com.app.fityo.data_layer.db.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.app.fityo.data_layer.db.SchedeEntity;
import com.app.fityo.data_layer.db.converters.Converters;
import com.app.fityo.dominio.GruppoMuscolarePercentuale;
import com.app.fityo.dominio.WeekdayWorkoutCount;
import java.lang.Class;
import java.lang.Double;
import java.lang.Integer;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class DaoSchede_Impl implements DaoSchede {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<SchedeEntity> __insertAdapterOfSchedeEntity;

  private final Converters __converters = new Converters();

  private final EntityDeleteOrUpdateAdapter<SchedeEntity> __deleteAdapterOfSchedeEntity;

  private final EntityDeleteOrUpdateAdapter<SchedeEntity> __updateAdapterOfSchedeEntity;

  public DaoSchede_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfSchedeEntity = new EntityInsertAdapter<SchedeEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `schede` (`id`,`gruppoMuscolare`,`gruppiMuscolari`,`intesita`,`titolo`,`data`,`notes`,`ora`,`favorite`,`completed`,`completedDate`,`totalSteps`,`avgHeartRate`,`maxHeartRate`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final SchedeEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getGruppoMuscolare() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getGruppoMuscolare());
        }
        final String _tmp = __converters.fromStringList(entity.getGruppiMuscolari());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, _tmp);
        }
        if (entity.getIntesita() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getIntesita());
        }
        if (entity.getTitolo() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getTitolo());
        }
        if (entity.getData() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getData());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getNotes());
        }
        if (entity.getOra() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getOra());
        }
        final int _tmp_1 = entity.getFavorite() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        final int _tmp_2 = entity.getCompleted() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        if (entity.getCompletedDate() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getCompletedDate());
        }
        if (entity.getTotalSteps() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getTotalSteps());
        }
        if (entity.getAvgHeartRate() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getAvgHeartRate());
        }
        if (entity.getMaxHeartRate() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getMaxHeartRate());
        }
      }
    };
    this.__deleteAdapterOfSchedeEntity = new EntityDeleteOrUpdateAdapter<SchedeEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `schede` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final SchedeEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfSchedeEntity = new EntityDeleteOrUpdateAdapter<SchedeEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `schede` SET `id` = ?,`gruppoMuscolare` = ?,`gruppiMuscolari` = ?,`intesita` = ?,`titolo` = ?,`data` = ?,`notes` = ?,`ora` = ?,`favorite` = ?,`completed` = ?,`completedDate` = ?,`totalSteps` = ?,`avgHeartRate` = ?,`maxHeartRate` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final SchedeEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getGruppoMuscolare() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getGruppoMuscolare());
        }
        final String _tmp = __converters.fromStringList(entity.getGruppiMuscolari());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, _tmp);
        }
        if (entity.getIntesita() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getIntesita());
        }
        if (entity.getTitolo() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getTitolo());
        }
        if (entity.getData() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getData());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getNotes());
        }
        if (entity.getOra() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getOra());
        }
        final int _tmp_1 = entity.getFavorite() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        final int _tmp_2 = entity.getCompleted() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        if (entity.getCompletedDate() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getCompletedDate());
        }
        if (entity.getTotalSteps() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getTotalSteps());
        }
        if (entity.getAvgHeartRate() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getAvgHeartRate());
        }
        if (entity.getMaxHeartRate() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getMaxHeartRate());
        }
        if (entity.getId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getId());
        }
      }
    };
  }

  @Override
  public Object insert(final SchedeEntity schede, final Continuation<? super Long> $completion) {
    if (schede == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfSchedeEntity.insertAndReturnId(_connection, schede);
    }, $completion);
  }

  @Override
  public Object delete(final SchedeEntity scheda, final Continuation<? super Unit> $completion) {
    if (scheda == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfSchedeEntity.handle(_connection, scheda);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object update(final SchedeEntity scheda, final Continuation<? super Unit> $completion) {
    if (scheda == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfSchedeEntity.handle(_connection, scheda);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getAllSchede(final Continuation<? super List<SchedeEntity>> $completion) {
    final String _sql = "SELECT * FROM schede";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfGruppoMuscolare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gruppoMuscolare");
        final int _columnIndexOfGruppiMuscolari = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gruppiMuscolari");
        final int _columnIndexOfIntesita = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "intesita");
        final int _columnIndexOfTitolo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "titolo");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfOra = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ora");
        final int _columnIndexOfFavorite = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "favorite");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfCompletedDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completedDate");
        final int _columnIndexOfTotalSteps = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalSteps");
        final int _columnIndexOfAvgHeartRate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "avgHeartRate");
        final int _columnIndexOfMaxHeartRate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maxHeartRate");
        final List<SchedeEntity> _result = new ArrayList<SchedeEntity>();
        while (_stmt.step()) {
          final SchedeEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpGruppoMuscolare;
          if (_stmt.isNull(_columnIndexOfGruppoMuscolare)) {
            _tmpGruppoMuscolare = null;
          } else {
            _tmpGruppoMuscolare = _stmt.getText(_columnIndexOfGruppoMuscolare);
          }
          final List<String> _tmpGruppiMuscolari;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfGruppiMuscolari)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfGruppiMuscolari);
          }
          _tmpGruppiMuscolari = __converters.toStringList(_tmp);
          final String _tmpIntesita;
          if (_stmt.isNull(_columnIndexOfIntesita)) {
            _tmpIntesita = null;
          } else {
            _tmpIntesita = _stmt.getText(_columnIndexOfIntesita);
          }
          final String _tmpTitolo;
          if (_stmt.isNull(_columnIndexOfTitolo)) {
            _tmpTitolo = null;
          } else {
            _tmpTitolo = _stmt.getText(_columnIndexOfTitolo);
          }
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final String _tmpOra;
          if (_stmt.isNull(_columnIndexOfOra)) {
            _tmpOra = null;
          } else {
            _tmpOra = _stmt.getText(_columnIndexOfOra);
          }
          final boolean _tmpFavorite;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfFavorite));
          _tmpFavorite = _tmp_1 != 0;
          final boolean _tmpCompleted;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp_2 != 0;
          final String _tmpCompletedDate;
          if (_stmt.isNull(_columnIndexOfCompletedDate)) {
            _tmpCompletedDate = null;
          } else {
            _tmpCompletedDate = _stmt.getText(_columnIndexOfCompletedDate);
          }
          final Integer _tmpTotalSteps;
          if (_stmt.isNull(_columnIndexOfTotalSteps)) {
            _tmpTotalSteps = null;
          } else {
            _tmpTotalSteps = (int) (_stmt.getLong(_columnIndexOfTotalSteps));
          }
          final Integer _tmpAvgHeartRate;
          if (_stmt.isNull(_columnIndexOfAvgHeartRate)) {
            _tmpAvgHeartRate = null;
          } else {
            _tmpAvgHeartRate = (int) (_stmt.getLong(_columnIndexOfAvgHeartRate));
          }
          final Integer _tmpMaxHeartRate;
          if (_stmt.isNull(_columnIndexOfMaxHeartRate)) {
            _tmpMaxHeartRate = null;
          } else {
            _tmpMaxHeartRate = (int) (_stmt.getLong(_columnIndexOfMaxHeartRate));
          }
          _item = new SchedeEntity(_tmpId,_tmpGruppoMuscolare,_tmpGruppiMuscolari,_tmpIntesita,_tmpTitolo,_tmpData,_tmpNotes,_tmpOra,_tmpFavorite,_tmpCompleted,_tmpCompletedDate,_tmpTotalSteps,_tmpAvgHeartRate,_tmpMaxHeartRate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getSchedeById(final int id, final Continuation<? super SchedeEntity> $completion) {
    final String _sql = "SELECT * FROM schede WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfGruppoMuscolare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gruppoMuscolare");
        final int _columnIndexOfGruppiMuscolari = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gruppiMuscolari");
        final int _columnIndexOfIntesita = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "intesita");
        final int _columnIndexOfTitolo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "titolo");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfOra = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ora");
        final int _columnIndexOfFavorite = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "favorite");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfCompletedDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completedDate");
        final int _columnIndexOfTotalSteps = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalSteps");
        final int _columnIndexOfAvgHeartRate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "avgHeartRate");
        final int _columnIndexOfMaxHeartRate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maxHeartRate");
        final SchedeEntity _result;
        if (_stmt.step()) {
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpGruppoMuscolare;
          if (_stmt.isNull(_columnIndexOfGruppoMuscolare)) {
            _tmpGruppoMuscolare = null;
          } else {
            _tmpGruppoMuscolare = _stmt.getText(_columnIndexOfGruppoMuscolare);
          }
          final List<String> _tmpGruppiMuscolari;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfGruppiMuscolari)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfGruppiMuscolari);
          }
          _tmpGruppiMuscolari = __converters.toStringList(_tmp);
          final String _tmpIntesita;
          if (_stmt.isNull(_columnIndexOfIntesita)) {
            _tmpIntesita = null;
          } else {
            _tmpIntesita = _stmt.getText(_columnIndexOfIntesita);
          }
          final String _tmpTitolo;
          if (_stmt.isNull(_columnIndexOfTitolo)) {
            _tmpTitolo = null;
          } else {
            _tmpTitolo = _stmt.getText(_columnIndexOfTitolo);
          }
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final String _tmpOra;
          if (_stmt.isNull(_columnIndexOfOra)) {
            _tmpOra = null;
          } else {
            _tmpOra = _stmt.getText(_columnIndexOfOra);
          }
          final boolean _tmpFavorite;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfFavorite));
          _tmpFavorite = _tmp_1 != 0;
          final boolean _tmpCompleted;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp_2 != 0;
          final String _tmpCompletedDate;
          if (_stmt.isNull(_columnIndexOfCompletedDate)) {
            _tmpCompletedDate = null;
          } else {
            _tmpCompletedDate = _stmt.getText(_columnIndexOfCompletedDate);
          }
          final Integer _tmpTotalSteps;
          if (_stmt.isNull(_columnIndexOfTotalSteps)) {
            _tmpTotalSteps = null;
          } else {
            _tmpTotalSteps = (int) (_stmt.getLong(_columnIndexOfTotalSteps));
          }
          final Integer _tmpAvgHeartRate;
          if (_stmt.isNull(_columnIndexOfAvgHeartRate)) {
            _tmpAvgHeartRate = null;
          } else {
            _tmpAvgHeartRate = (int) (_stmt.getLong(_columnIndexOfAvgHeartRate));
          }
          final Integer _tmpMaxHeartRate;
          if (_stmt.isNull(_columnIndexOfMaxHeartRate)) {
            _tmpMaxHeartRate = null;
          } else {
            _tmpMaxHeartRate = (int) (_stmt.getLong(_columnIndexOfMaxHeartRate));
          }
          _result = new SchedeEntity(_tmpId,_tmpGruppoMuscolare,_tmpGruppiMuscolari,_tmpIntesita,_tmpTitolo,_tmpData,_tmpNotes,_tmpOra,_tmpFavorite,_tmpCompleted,_tmpCompletedDate,_tmpTotalSteps,_tmpAvgHeartRate,_tmpMaxHeartRate);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getSchedeByGruppoMuscolare(final String gruppoMuscolare,
      final Continuation<? super List<SchedeEntity>> $completion) {
    final String _sql = "SELECT * FROM schede WHERE gruppoMuscolare = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (gruppoMuscolare == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, gruppoMuscolare);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfGruppoMuscolare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gruppoMuscolare");
        final int _columnIndexOfGruppiMuscolari = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gruppiMuscolari");
        final int _columnIndexOfIntesita = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "intesita");
        final int _columnIndexOfTitolo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "titolo");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfOra = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ora");
        final int _columnIndexOfFavorite = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "favorite");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfCompletedDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completedDate");
        final int _columnIndexOfTotalSteps = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalSteps");
        final int _columnIndexOfAvgHeartRate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "avgHeartRate");
        final int _columnIndexOfMaxHeartRate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maxHeartRate");
        final List<SchedeEntity> _result = new ArrayList<SchedeEntity>();
        while (_stmt.step()) {
          final SchedeEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpGruppoMuscolare;
          if (_stmt.isNull(_columnIndexOfGruppoMuscolare)) {
            _tmpGruppoMuscolare = null;
          } else {
            _tmpGruppoMuscolare = _stmt.getText(_columnIndexOfGruppoMuscolare);
          }
          final List<String> _tmpGruppiMuscolari;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfGruppiMuscolari)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfGruppiMuscolari);
          }
          _tmpGruppiMuscolari = __converters.toStringList(_tmp);
          final String _tmpIntesita;
          if (_stmt.isNull(_columnIndexOfIntesita)) {
            _tmpIntesita = null;
          } else {
            _tmpIntesita = _stmt.getText(_columnIndexOfIntesita);
          }
          final String _tmpTitolo;
          if (_stmt.isNull(_columnIndexOfTitolo)) {
            _tmpTitolo = null;
          } else {
            _tmpTitolo = _stmt.getText(_columnIndexOfTitolo);
          }
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final String _tmpOra;
          if (_stmt.isNull(_columnIndexOfOra)) {
            _tmpOra = null;
          } else {
            _tmpOra = _stmt.getText(_columnIndexOfOra);
          }
          final boolean _tmpFavorite;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfFavorite));
          _tmpFavorite = _tmp_1 != 0;
          final boolean _tmpCompleted;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp_2 != 0;
          final String _tmpCompletedDate;
          if (_stmt.isNull(_columnIndexOfCompletedDate)) {
            _tmpCompletedDate = null;
          } else {
            _tmpCompletedDate = _stmt.getText(_columnIndexOfCompletedDate);
          }
          final Integer _tmpTotalSteps;
          if (_stmt.isNull(_columnIndexOfTotalSteps)) {
            _tmpTotalSteps = null;
          } else {
            _tmpTotalSteps = (int) (_stmt.getLong(_columnIndexOfTotalSteps));
          }
          final Integer _tmpAvgHeartRate;
          if (_stmt.isNull(_columnIndexOfAvgHeartRate)) {
            _tmpAvgHeartRate = null;
          } else {
            _tmpAvgHeartRate = (int) (_stmt.getLong(_columnIndexOfAvgHeartRate));
          }
          final Integer _tmpMaxHeartRate;
          if (_stmt.isNull(_columnIndexOfMaxHeartRate)) {
            _tmpMaxHeartRate = null;
          } else {
            _tmpMaxHeartRate = (int) (_stmt.getLong(_columnIndexOfMaxHeartRate));
          }
          _item = new SchedeEntity(_tmpId,_tmpGruppoMuscolare,_tmpGruppiMuscolari,_tmpIntesita,_tmpTitolo,_tmpData,_tmpNotes,_tmpOra,_tmpFavorite,_tmpCompleted,_tmpCompletedDate,_tmpTotalSteps,_tmpAvgHeartRate,_tmpMaxHeartRate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getSchedeWithTime(final Continuation<? super List<SchedeEntity>> $completion) {
    final String _sql = "SELECT * FROM schede WHERE ora IS NOT NULL";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfGruppoMuscolare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gruppoMuscolare");
        final int _columnIndexOfGruppiMuscolari = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gruppiMuscolari");
        final int _columnIndexOfIntesita = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "intesita");
        final int _columnIndexOfTitolo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "titolo");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfOra = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ora");
        final int _columnIndexOfFavorite = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "favorite");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfCompletedDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completedDate");
        final int _columnIndexOfTotalSteps = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalSteps");
        final int _columnIndexOfAvgHeartRate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "avgHeartRate");
        final int _columnIndexOfMaxHeartRate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maxHeartRate");
        final List<SchedeEntity> _result = new ArrayList<SchedeEntity>();
        while (_stmt.step()) {
          final SchedeEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpGruppoMuscolare;
          if (_stmt.isNull(_columnIndexOfGruppoMuscolare)) {
            _tmpGruppoMuscolare = null;
          } else {
            _tmpGruppoMuscolare = _stmt.getText(_columnIndexOfGruppoMuscolare);
          }
          final List<String> _tmpGruppiMuscolari;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfGruppiMuscolari)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfGruppiMuscolari);
          }
          _tmpGruppiMuscolari = __converters.toStringList(_tmp);
          final String _tmpIntesita;
          if (_stmt.isNull(_columnIndexOfIntesita)) {
            _tmpIntesita = null;
          } else {
            _tmpIntesita = _stmt.getText(_columnIndexOfIntesita);
          }
          final String _tmpTitolo;
          if (_stmt.isNull(_columnIndexOfTitolo)) {
            _tmpTitolo = null;
          } else {
            _tmpTitolo = _stmt.getText(_columnIndexOfTitolo);
          }
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final String _tmpOra;
          if (_stmt.isNull(_columnIndexOfOra)) {
            _tmpOra = null;
          } else {
            _tmpOra = _stmt.getText(_columnIndexOfOra);
          }
          final boolean _tmpFavorite;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfFavorite));
          _tmpFavorite = _tmp_1 != 0;
          final boolean _tmpCompleted;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp_2 != 0;
          final String _tmpCompletedDate;
          if (_stmt.isNull(_columnIndexOfCompletedDate)) {
            _tmpCompletedDate = null;
          } else {
            _tmpCompletedDate = _stmt.getText(_columnIndexOfCompletedDate);
          }
          final Integer _tmpTotalSteps;
          if (_stmt.isNull(_columnIndexOfTotalSteps)) {
            _tmpTotalSteps = null;
          } else {
            _tmpTotalSteps = (int) (_stmt.getLong(_columnIndexOfTotalSteps));
          }
          final Integer _tmpAvgHeartRate;
          if (_stmt.isNull(_columnIndexOfAvgHeartRate)) {
            _tmpAvgHeartRate = null;
          } else {
            _tmpAvgHeartRate = (int) (_stmt.getLong(_columnIndexOfAvgHeartRate));
          }
          final Integer _tmpMaxHeartRate;
          if (_stmt.isNull(_columnIndexOfMaxHeartRate)) {
            _tmpMaxHeartRate = null;
          } else {
            _tmpMaxHeartRate = (int) (_stmt.getLong(_columnIndexOfMaxHeartRate));
          }
          _item = new SchedeEntity(_tmpId,_tmpGruppoMuscolare,_tmpGruppiMuscolari,_tmpIntesita,_tmpTitolo,_tmpData,_tmpNotes,_tmpOra,_tmpFavorite,_tmpCompleted,_tmpCompletedDate,_tmpTotalSteps,_tmpAvgHeartRate,_tmpMaxHeartRate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getPercentualePerGruppoMuscolare(
      final Continuation<? super List<GruppoMuscolarePercentuale>> $completion) {
    final String _sql = "\n"
            + "        SELECT gruppoMuscolare, COUNT(*) * 100.0 / (SELECT COUNT(*) FROM schede) as percentuale \n"
            + "        FROM schede \n"
            + "        GROUP BY gruppoMuscolare\n"
            + "    ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfGruppoMuscolare = 0;
        final int _columnIndexOfPercentuale = 1;
        final List<GruppoMuscolarePercentuale> _result = new ArrayList<GruppoMuscolarePercentuale>();
        while (_stmt.step()) {
          final GruppoMuscolarePercentuale _item;
          final String _tmpGruppoMuscolare;
          if (_stmt.isNull(_columnIndexOfGruppoMuscolare)) {
            _tmpGruppoMuscolare = null;
          } else {
            _tmpGruppoMuscolare = _stmt.getText(_columnIndexOfGruppoMuscolare);
          }
          final float _tmpPercentuale;
          _tmpPercentuale = (float) (_stmt.getDouble(_columnIndexOfPercentuale));
          _item = new GruppoMuscolarePercentuale(_tmpGruppoMuscolare,_tmpPercentuale);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getPercentualePerGruppoMuscolareInDateRange(final String startDate,
      final String endDate,
      final Continuation<? super List<GruppoMuscolarePercentuale>> $completion) {
    final String _sql = "\n"
            + "    SELECT gruppoMuscolare, COUNT(*) * 100.0 / (\n"
            + "        SELECT COUNT(*) \n"
            + "        FROM schede \n"
            + "        WHERE data BETWEEN ? AND ?\n"
            + "    ) as percentuale \n"
            + "    FROM schede \n"
            + "    WHERE data BETWEEN ? AND ?\n"
            + "    GROUP BY gruppoMuscolare\n";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (startDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, startDate);
        }
        _argIndex = 2;
        if (endDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, endDate);
        }
        _argIndex = 3;
        if (startDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, startDate);
        }
        _argIndex = 4;
        if (endDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, endDate);
        }
        final int _columnIndexOfGruppoMuscolare = 0;
        final int _columnIndexOfPercentuale = 1;
        final List<GruppoMuscolarePercentuale> _result = new ArrayList<GruppoMuscolarePercentuale>();
        while (_stmt.step()) {
          final GruppoMuscolarePercentuale _item;
          final String _tmpGruppoMuscolare;
          if (_stmt.isNull(_columnIndexOfGruppoMuscolare)) {
            _tmpGruppoMuscolare = null;
          } else {
            _tmpGruppoMuscolare = _stmt.getText(_columnIndexOfGruppoMuscolare);
          }
          final float _tmpPercentuale;
          _tmpPercentuale = (float) (_stmt.getDouble(_columnIndexOfPercentuale));
          _item = new GruppoMuscolarePercentuale(_tmpGruppoMuscolare,_tmpPercentuale);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getMediaIntensitaPerGruppoMuscolareDateRange(final String startDate,
      final String endDate,
      final Continuation<? super List<GruppoMuscolareIntensitaMedia>> $completion) {
    final String _sql = "\n"
            + "    SELECT gruppoMuscolare, AVG(\n"
            + "        CASE intesita\n"
            + "            WHEN 'Bassa' THEN 5\n"
            + "            WHEN 'Media' THEN 10\n"
            + "            WHEN 'Alta' THEN 15\n"
            + "            ELSE 0 \n"
            + "        END\n"
            + "    ) AS mediaIntensita\n"
            + "    FROM schede\n"
            + "    WHERE data BETWEEN ? AND ?\n"
            + "    GROUP BY gruppoMuscolare\n";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (startDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, startDate);
        }
        _argIndex = 2;
        if (endDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, endDate);
        }
        final int _columnIndexOfGruppoMuscolare = 0;
        final int _columnIndexOfMediaIntensita = 1;
        final List<GruppoMuscolareIntensitaMedia> _result = new ArrayList<GruppoMuscolareIntensitaMedia>();
        while (_stmt.step()) {
          final GruppoMuscolareIntensitaMedia _item;
          final String _tmpGruppoMuscolare;
          if (_stmt.isNull(_columnIndexOfGruppoMuscolare)) {
            _tmpGruppoMuscolare = null;
          } else {
            _tmpGruppoMuscolare = _stmt.getText(_columnIndexOfGruppoMuscolare);
          }
          final float _tmpMediaIntensita;
          _tmpMediaIntensita = (float) (_stmt.getDouble(_columnIndexOfMediaIntensita));
          _item = new GruppoMuscolareIntensitaMedia(_tmpGruppoMuscolare,_tmpMediaIntensita);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getSchedeInDateRange(final String start, final String end,
      final Continuation<? super List<SchedeEntity>> $completion) {
    final String _sql = "\n"
            + "    SELECT * FROM schede\n"
            + "    WHERE date(data) BETWEEN date(?) AND date(?)\n"
            + "    ORDER BY date(data) DESC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (start == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, start);
        }
        _argIndex = 2;
        if (end == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, end);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfGruppoMuscolare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gruppoMuscolare");
        final int _columnIndexOfGruppiMuscolari = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gruppiMuscolari");
        final int _columnIndexOfIntesita = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "intesita");
        final int _columnIndexOfTitolo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "titolo");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfOra = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ora");
        final int _columnIndexOfFavorite = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "favorite");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfCompletedDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completedDate");
        final int _columnIndexOfTotalSteps = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalSteps");
        final int _columnIndexOfAvgHeartRate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "avgHeartRate");
        final int _columnIndexOfMaxHeartRate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maxHeartRate");
        final List<SchedeEntity> _result = new ArrayList<SchedeEntity>();
        while (_stmt.step()) {
          final SchedeEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpGruppoMuscolare;
          if (_stmt.isNull(_columnIndexOfGruppoMuscolare)) {
            _tmpGruppoMuscolare = null;
          } else {
            _tmpGruppoMuscolare = _stmt.getText(_columnIndexOfGruppoMuscolare);
          }
          final List<String> _tmpGruppiMuscolari;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfGruppiMuscolari)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfGruppiMuscolari);
          }
          _tmpGruppiMuscolari = __converters.toStringList(_tmp);
          final String _tmpIntesita;
          if (_stmt.isNull(_columnIndexOfIntesita)) {
            _tmpIntesita = null;
          } else {
            _tmpIntesita = _stmt.getText(_columnIndexOfIntesita);
          }
          final String _tmpTitolo;
          if (_stmt.isNull(_columnIndexOfTitolo)) {
            _tmpTitolo = null;
          } else {
            _tmpTitolo = _stmt.getText(_columnIndexOfTitolo);
          }
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final String _tmpOra;
          if (_stmt.isNull(_columnIndexOfOra)) {
            _tmpOra = null;
          } else {
            _tmpOra = _stmt.getText(_columnIndexOfOra);
          }
          final boolean _tmpFavorite;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfFavorite));
          _tmpFavorite = _tmp_1 != 0;
          final boolean _tmpCompleted;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp_2 != 0;
          final String _tmpCompletedDate;
          if (_stmt.isNull(_columnIndexOfCompletedDate)) {
            _tmpCompletedDate = null;
          } else {
            _tmpCompletedDate = _stmt.getText(_columnIndexOfCompletedDate);
          }
          final Integer _tmpTotalSteps;
          if (_stmt.isNull(_columnIndexOfTotalSteps)) {
            _tmpTotalSteps = null;
          } else {
            _tmpTotalSteps = (int) (_stmt.getLong(_columnIndexOfTotalSteps));
          }
          final Integer _tmpAvgHeartRate;
          if (_stmt.isNull(_columnIndexOfAvgHeartRate)) {
            _tmpAvgHeartRate = null;
          } else {
            _tmpAvgHeartRate = (int) (_stmt.getLong(_columnIndexOfAvgHeartRate));
          }
          final Integer _tmpMaxHeartRate;
          if (_stmt.isNull(_columnIndexOfMaxHeartRate)) {
            _tmpMaxHeartRate = null;
          } else {
            _tmpMaxHeartRate = (int) (_stmt.getLong(_columnIndexOfMaxHeartRate));
          }
          _item = new SchedeEntity(_tmpId,_tmpGruppoMuscolare,_tmpGruppiMuscolari,_tmpIntesita,_tmpTitolo,_tmpData,_tmpNotes,_tmpOra,_tmpFavorite,_tmpCompleted,_tmpCompletedDate,_tmpTotalSteps,_tmpAvgHeartRate,_tmpMaxHeartRate);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object countSchede(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM schede";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object countFavoriteSchede(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM schede WHERE favorite = 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object countTotalExercises(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM essercissi";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getLastWorkoutDate(final Continuation<? super String> $completion) {
    final String _sql = "SELECT data FROM schede ORDER BY date(data) DESC LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final String _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = _stmt.getText(0);
          }
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getMediaIntensitaPerGruppoMuscolareAll(
      final Continuation<? super List<GruppoMuscolareIntensitaMedia>> $completion) {
    final String _sql = "\n"
            + "        SELECT gruppoMuscolare, AVG(\n"
            + "            CASE intesita\n"
            + "                WHEN 'Bassa' THEN 5\n"
            + "                WHEN 'Media' THEN 10\n"
            + "                WHEN 'Alta' THEN 15\n"
            + "                ELSE 0 \n"
            + "            END\n"
            + "        ) AS mediaIntensita\n"
            + "        FROM schede\n"
            + "        GROUP BY gruppoMuscolare\n"
            + "    ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfGruppoMuscolare = 0;
        final int _columnIndexOfMediaIntensita = 1;
        final List<GruppoMuscolareIntensitaMedia> _result = new ArrayList<GruppoMuscolareIntensitaMedia>();
        while (_stmt.step()) {
          final GruppoMuscolareIntensitaMedia _item;
          final String _tmpGruppoMuscolare;
          if (_stmt.isNull(_columnIndexOfGruppoMuscolare)) {
            _tmpGruppoMuscolare = null;
          } else {
            _tmpGruppoMuscolare = _stmt.getText(_columnIndexOfGruppoMuscolare);
          }
          final float _tmpMediaIntensita;
          _tmpMediaIntensita = (float) (_stmt.getDouble(_columnIndexOfMediaIntensita));
          _item = new GruppoMuscolareIntensitaMedia(_tmpGruppoMuscolare,_tmpMediaIntensita);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getWorkoutCountByWeekdayAll(
      final Continuation<? super List<WeekdayWorkoutCount>> $completion) {
    final String _sql = "\n"
            + "        SELECT strftime('%w', data) AS dayOfWeek, COUNT(*) AS count\n"
            + "        FROM schede\n"
            + "        GROUP BY dayOfWeek\n"
            + "    ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfDayOfWeek = 0;
        final int _columnIndexOfCount = 1;
        final List<WeekdayWorkoutCount> _result = new ArrayList<WeekdayWorkoutCount>();
        while (_stmt.step()) {
          final WeekdayWorkoutCount _item;
          final int _tmpDayOfWeek;
          _tmpDayOfWeek = (int) (_stmt.getLong(_columnIndexOfDayOfWeek));
          final int _tmpCount;
          _tmpCount = (int) (_stmt.getLong(_columnIndexOfCount));
          _item = new WeekdayWorkoutCount(_tmpDayOfWeek,_tmpCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getWorkoutCountByWeekday(final String startDate, final String endDate,
      final Continuation<? super List<WeekdayWorkoutCount>> $completion) {
    final String _sql = "\n"
            + "        SELECT strftime('%w', data) AS dayOfWeek, COUNT(*) AS count\n"
            + "        FROM schede\n"
            + "        WHERE date(data) BETWEEN date(?) AND date(?)\n"
            + "        GROUP BY dayOfWeek\n"
            + "    ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (startDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, startDate);
        }
        _argIndex = 2;
        if (endDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, endDate);
        }
        final int _columnIndexOfDayOfWeek = 0;
        final int _columnIndexOfCount = 1;
        final List<WeekdayWorkoutCount> _result = new ArrayList<WeekdayWorkoutCount>();
        while (_stmt.step()) {
          final WeekdayWorkoutCount _item;
          final int _tmpDayOfWeek;
          _tmpDayOfWeek = (int) (_stmt.getLong(_columnIndexOfDayOfWeek));
          final int _tmpCount;
          _tmpCount = (int) (_stmt.getLong(_columnIndexOfCount));
          _item = new WeekdayWorkoutCount(_tmpDayOfWeek,_tmpCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getDaysSinceLastWorkout(final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT CAST(JULIANDAY('now') - JULIANDAY(MAX(data)) AS INTEGER) as daysSinceLastWorkout\n"
            + "        FROM schede\n"
            + "    ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getMostTrainedMuscleGroup(final Continuation<? super String> $completion) {
    final String _sql = "\n"
            + "        SELECT gruppoMuscolare\n"
            + "        FROM schede\n"
            + "        GROUP BY gruppoMuscolare\n"
            + "        ORDER BY COUNT(*) DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final String _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = _stmt.getText(0);
          }
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getAverageWorkoutsPerWeek(final Continuation<? super Double> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) * 1.0 /\n"
            + "        (SELECT (JULIANDAY(MAX(data)) - JULIANDAY(MIN(data))) / 7.0 FROM schede)\n"
            + "        as avgPerWeek\n"
            + "        FROM schede\n"
            + "        WHERE (SELECT COUNT(*) FROM schede) > 1\n"
            + "    ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Double _result;
        if (_stmt.step()) {
          final Double _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getDouble(0);
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateTime(final int id, final String time,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE schede SET ora = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (time == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, time);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object setFavorite(final int id, final boolean isFav,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE schede SET favorite = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = isFav ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object setCompleted(final int id, final boolean isCompleted, final String completedDate,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE schede SET completed = ?, completedDate = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = isCompleted ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        if (completedDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, completedDate);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
