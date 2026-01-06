package com.app.fityo.data_layer.db.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.app.fityo.data_layer.db.EsserciziEntity;
import java.lang.Class;
import java.lang.Float;
import java.lang.Integer;
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
public final class DaoEssercissi_Impl implements DaoEssercissi {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<EsserciziEntity> __insertAdapterOfEsserciziEntity;

  private final EntityDeleteOrUpdateAdapter<EsserciziEntity> __deleteAdapterOfEsserciziEntity;

  private final EntityDeleteOrUpdateAdapter<EsserciziEntity> __updateAdapterOfEsserciziEntity;

  public DaoEssercissi_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfEsserciziEntity = new EntityInsertAdapter<EsserciziEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `essercissi` (`id`,`nome`,`attrezzo`,`nRipetizione`,`nSerie`,`insometria`,`intervallo`,`peso`,`completed`,`schedaId`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final EsserciziEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getNome() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getNome());
        }
        if (entity.getAttrezzo() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getAttrezzo());
        }
        statement.bindLong(4, entity.getNRipetizione());
        statement.bindLong(5, entity.getNSerie());
        if (entity.getInsometria() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getInsometria());
        }
        if (entity.getIntervallo() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getIntervallo());
        }
        if (entity.getPeso() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getPeso());
        }
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getSchedaId());
      }
    };
    this.__deleteAdapterOfEsserciziEntity = new EntityDeleteOrUpdateAdapter<EsserciziEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `essercissi` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final EsserciziEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfEsserciziEntity = new EntityDeleteOrUpdateAdapter<EsserciziEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `essercissi` SET `id` = ?,`nome` = ?,`attrezzo` = ?,`nRipetizione` = ?,`nSerie` = ?,`insometria` = ?,`intervallo` = ?,`peso` = ?,`completed` = ?,`schedaId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final EsserciziEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getNome() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getNome());
        }
        if (entity.getAttrezzo() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getAttrezzo());
        }
        statement.bindLong(4, entity.getNRipetizione());
        statement.bindLong(5, entity.getNSerie());
        if (entity.getInsometria() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getInsometria());
        }
        if (entity.getIntervallo() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getIntervallo());
        }
        if (entity.getPeso() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getPeso());
        }
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getSchedaId());
        if (entity.getId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getId());
        }
      }
    };
  }

  @Override
  public Object insert(final EsserciziEntity essercizi,
      final Continuation<? super Unit> $completion) {
    if (essercizi == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfEsserciziEntity.insert(_connection, essercizi);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object delete(final EsserciziEntity essercizi,
      final Continuation<? super Unit> $completion) {
    if (essercizi == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfEsserciziEntity.handle(_connection, essercizi);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object update(final EsserciziEntity essercizi,
      final Continuation<? super Unit> $completion) {
    if (essercizi == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfEsserciziEntity.handle(_connection, essercizi);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object countEsserciziById(final int id, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM essercissi WHERE schedaId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
  public Object getEssercissiById(final int id,
      final Continuation<? super List<EsserciziEntity>> $completion) {
    final String _sql = "SELECT * FROM essercissi WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNome = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nome");
        final int _columnIndexOfAttrezzo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attrezzo");
        final int _columnIndexOfNRipetizione = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nRipetizione");
        final int _columnIndexOfNSerie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nSerie");
        final int _columnIndexOfInsometria = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "insometria");
        final int _columnIndexOfIntervallo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "intervallo");
        final int _columnIndexOfPeso = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "peso");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfSchedaId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "schedaId");
        final List<EsserciziEntity> _result = new ArrayList<EsserciziEntity>();
        while (_stmt.step()) {
          final EsserciziEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpNome;
          if (_stmt.isNull(_columnIndexOfNome)) {
            _tmpNome = null;
          } else {
            _tmpNome = _stmt.getText(_columnIndexOfNome);
          }
          final String _tmpAttrezzo;
          if (_stmt.isNull(_columnIndexOfAttrezzo)) {
            _tmpAttrezzo = null;
          } else {
            _tmpAttrezzo = _stmt.getText(_columnIndexOfAttrezzo);
          }
          final int _tmpNRipetizione;
          _tmpNRipetizione = (int) (_stmt.getLong(_columnIndexOfNRipetizione));
          final int _tmpNSerie;
          _tmpNSerie = (int) (_stmt.getLong(_columnIndexOfNSerie));
          final Integer _tmpInsometria;
          if (_stmt.isNull(_columnIndexOfInsometria)) {
            _tmpInsometria = null;
          } else {
            _tmpInsometria = (int) (_stmt.getLong(_columnIndexOfInsometria));
          }
          final Integer _tmpIntervallo;
          if (_stmt.isNull(_columnIndexOfIntervallo)) {
            _tmpIntervallo = null;
          } else {
            _tmpIntervallo = (int) (_stmt.getLong(_columnIndexOfIntervallo));
          }
          final Float _tmpPeso;
          if (_stmt.isNull(_columnIndexOfPeso)) {
            _tmpPeso = null;
          } else {
            _tmpPeso = (float) (_stmt.getDouble(_columnIndexOfPeso));
          }
          final boolean _tmpCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp != 0;
          final int _tmpSchedaId;
          _tmpSchedaId = (int) (_stmt.getLong(_columnIndexOfSchedaId));
          _item = new EsserciziEntity(_tmpId,_tmpNome,_tmpAttrezzo,_tmpNRipetizione,_tmpNSerie,_tmpInsometria,_tmpIntervallo,_tmpPeso,_tmpCompleted,_tmpSchedaId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public LiveData<List<EsserciziEntity>> getEssercissiBySchedaId(final int schedaId) {
    final String _sql = "SELECT * FROM essercissi WHERE schedaId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"essercissi"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, schedaId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNome = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nome");
        final int _columnIndexOfAttrezzo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attrezzo");
        final int _columnIndexOfNRipetizione = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nRipetizione");
        final int _columnIndexOfNSerie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nSerie");
        final int _columnIndexOfInsometria = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "insometria");
        final int _columnIndexOfIntervallo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "intervallo");
        final int _columnIndexOfPeso = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "peso");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfSchedaId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "schedaId");
        final List<EsserciziEntity> _result = new ArrayList<EsserciziEntity>();
        while (_stmt.step()) {
          final EsserciziEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpNome;
          if (_stmt.isNull(_columnIndexOfNome)) {
            _tmpNome = null;
          } else {
            _tmpNome = _stmt.getText(_columnIndexOfNome);
          }
          final String _tmpAttrezzo;
          if (_stmt.isNull(_columnIndexOfAttrezzo)) {
            _tmpAttrezzo = null;
          } else {
            _tmpAttrezzo = _stmt.getText(_columnIndexOfAttrezzo);
          }
          final int _tmpNRipetizione;
          _tmpNRipetizione = (int) (_stmt.getLong(_columnIndexOfNRipetizione));
          final int _tmpNSerie;
          _tmpNSerie = (int) (_stmt.getLong(_columnIndexOfNSerie));
          final Integer _tmpInsometria;
          if (_stmt.isNull(_columnIndexOfInsometria)) {
            _tmpInsometria = null;
          } else {
            _tmpInsometria = (int) (_stmt.getLong(_columnIndexOfInsometria));
          }
          final Integer _tmpIntervallo;
          if (_stmt.isNull(_columnIndexOfIntervallo)) {
            _tmpIntervallo = null;
          } else {
            _tmpIntervallo = (int) (_stmt.getLong(_columnIndexOfIntervallo));
          }
          final Float _tmpPeso;
          if (_stmt.isNull(_columnIndexOfPeso)) {
            _tmpPeso = null;
          } else {
            _tmpPeso = (float) (_stmt.getDouble(_columnIndexOfPeso));
          }
          final boolean _tmpCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp != 0;
          final int _tmpSchedaId;
          _tmpSchedaId = (int) (_stmt.getLong(_columnIndexOfSchedaId));
          _item = new EsserciziEntity(_tmpId,_tmpNome,_tmpAttrezzo,_tmpNRipetizione,_tmpNSerie,_tmpInsometria,_tmpIntervallo,_tmpPeso,_tmpCompleted,_tmpSchedaId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getEserciziByschedaIdSync(final int schedaId,
      final Continuation<? super List<EsserciziEntity>> $completion) {
    final String _sql = "SELECT * FROM essercissi WHERE schedaId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, schedaId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNome = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nome");
        final int _columnIndexOfAttrezzo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attrezzo");
        final int _columnIndexOfNRipetizione = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nRipetizione");
        final int _columnIndexOfNSerie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nSerie");
        final int _columnIndexOfInsometria = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "insometria");
        final int _columnIndexOfIntervallo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "intervallo");
        final int _columnIndexOfPeso = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "peso");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfSchedaId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "schedaId");
        final List<EsserciziEntity> _result = new ArrayList<EsserciziEntity>();
        while (_stmt.step()) {
          final EsserciziEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpNome;
          if (_stmt.isNull(_columnIndexOfNome)) {
            _tmpNome = null;
          } else {
            _tmpNome = _stmt.getText(_columnIndexOfNome);
          }
          final String _tmpAttrezzo;
          if (_stmt.isNull(_columnIndexOfAttrezzo)) {
            _tmpAttrezzo = null;
          } else {
            _tmpAttrezzo = _stmt.getText(_columnIndexOfAttrezzo);
          }
          final int _tmpNRipetizione;
          _tmpNRipetizione = (int) (_stmt.getLong(_columnIndexOfNRipetizione));
          final int _tmpNSerie;
          _tmpNSerie = (int) (_stmt.getLong(_columnIndexOfNSerie));
          final Integer _tmpInsometria;
          if (_stmt.isNull(_columnIndexOfInsometria)) {
            _tmpInsometria = null;
          } else {
            _tmpInsometria = (int) (_stmt.getLong(_columnIndexOfInsometria));
          }
          final Integer _tmpIntervallo;
          if (_stmt.isNull(_columnIndexOfIntervallo)) {
            _tmpIntervallo = null;
          } else {
            _tmpIntervallo = (int) (_stmt.getLong(_columnIndexOfIntervallo));
          }
          final Float _tmpPeso;
          if (_stmt.isNull(_columnIndexOfPeso)) {
            _tmpPeso = null;
          } else {
            _tmpPeso = (float) (_stmt.getDouble(_columnIndexOfPeso));
          }
          final boolean _tmpCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp != 0;
          final int _tmpSchedaId;
          _tmpSchedaId = (int) (_stmt.getLong(_columnIndexOfSchedaId));
          _item = new EsserciziEntity(_tmpId,_tmpNome,_tmpAttrezzo,_tmpNRipetizione,_tmpNSerie,_tmpInsometria,_tmpIntervallo,_tmpPeso,_tmpCompleted,_tmpSchedaId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getAllEsercizi(final Continuation<? super List<EsserciziEntity>> $completion) {
    final String _sql = "SELECT * FROM essercissi";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNome = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nome");
        final int _columnIndexOfAttrezzo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attrezzo");
        final int _columnIndexOfNRipetizione = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nRipetizione");
        final int _columnIndexOfNSerie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nSerie");
        final int _columnIndexOfInsometria = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "insometria");
        final int _columnIndexOfIntervallo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "intervallo");
        final int _columnIndexOfPeso = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "peso");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfSchedaId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "schedaId");
        final List<EsserciziEntity> _result = new ArrayList<EsserciziEntity>();
        while (_stmt.step()) {
          final EsserciziEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpNome;
          if (_stmt.isNull(_columnIndexOfNome)) {
            _tmpNome = null;
          } else {
            _tmpNome = _stmt.getText(_columnIndexOfNome);
          }
          final String _tmpAttrezzo;
          if (_stmt.isNull(_columnIndexOfAttrezzo)) {
            _tmpAttrezzo = null;
          } else {
            _tmpAttrezzo = _stmt.getText(_columnIndexOfAttrezzo);
          }
          final int _tmpNRipetizione;
          _tmpNRipetizione = (int) (_stmt.getLong(_columnIndexOfNRipetizione));
          final int _tmpNSerie;
          _tmpNSerie = (int) (_stmt.getLong(_columnIndexOfNSerie));
          final Integer _tmpInsometria;
          if (_stmt.isNull(_columnIndexOfInsometria)) {
            _tmpInsometria = null;
          } else {
            _tmpInsometria = (int) (_stmt.getLong(_columnIndexOfInsometria));
          }
          final Integer _tmpIntervallo;
          if (_stmt.isNull(_columnIndexOfIntervallo)) {
            _tmpIntervallo = null;
          } else {
            _tmpIntervallo = (int) (_stmt.getLong(_columnIndexOfIntervallo));
          }
          final Float _tmpPeso;
          if (_stmt.isNull(_columnIndexOfPeso)) {
            _tmpPeso = null;
          } else {
            _tmpPeso = (float) (_stmt.getDouble(_columnIndexOfPeso));
          }
          final boolean _tmpCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp != 0;
          final int _tmpSchedaId;
          _tmpSchedaId = (int) (_stmt.getLong(_columnIndexOfSchedaId));
          _item = new EsserciziEntity(_tmpId,_tmpNome,_tmpAttrezzo,_tmpNRipetizione,_tmpNSerie,_tmpInsometria,_tmpIntervallo,_tmpPeso,_tmpCompleted,_tmpSchedaId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public LiveData<List<EsserciziEntity>> getAllById(final int id) {
    final String _sql = "SELECT * FROM essercissi WHERE schedaId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"essercissi"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNome = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nome");
        final int _columnIndexOfAttrezzo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attrezzo");
        final int _columnIndexOfNRipetizione = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nRipetizione");
        final int _columnIndexOfNSerie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nSerie");
        final int _columnIndexOfInsometria = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "insometria");
        final int _columnIndexOfIntervallo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "intervallo");
        final int _columnIndexOfPeso = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "peso");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfSchedaId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "schedaId");
        final List<EsserciziEntity> _result = new ArrayList<EsserciziEntity>();
        while (_stmt.step()) {
          final EsserciziEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpNome;
          if (_stmt.isNull(_columnIndexOfNome)) {
            _tmpNome = null;
          } else {
            _tmpNome = _stmt.getText(_columnIndexOfNome);
          }
          final String _tmpAttrezzo;
          if (_stmt.isNull(_columnIndexOfAttrezzo)) {
            _tmpAttrezzo = null;
          } else {
            _tmpAttrezzo = _stmt.getText(_columnIndexOfAttrezzo);
          }
          final int _tmpNRipetizione;
          _tmpNRipetizione = (int) (_stmt.getLong(_columnIndexOfNRipetizione));
          final int _tmpNSerie;
          _tmpNSerie = (int) (_stmt.getLong(_columnIndexOfNSerie));
          final Integer _tmpInsometria;
          if (_stmt.isNull(_columnIndexOfInsometria)) {
            _tmpInsometria = null;
          } else {
            _tmpInsometria = (int) (_stmt.getLong(_columnIndexOfInsometria));
          }
          final Integer _tmpIntervallo;
          if (_stmt.isNull(_columnIndexOfIntervallo)) {
            _tmpIntervallo = null;
          } else {
            _tmpIntervallo = (int) (_stmt.getLong(_columnIndexOfIntervallo));
          }
          final Float _tmpPeso;
          if (_stmt.isNull(_columnIndexOfPeso)) {
            _tmpPeso = null;
          } else {
            _tmpPeso = (float) (_stmt.getDouble(_columnIndexOfPeso));
          }
          final boolean _tmpCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp != 0;
          final int _tmpSchedaId;
          _tmpSchedaId = (int) (_stmt.getLong(_columnIndexOfSchedaId));
          _item = new EsserciziEntity(_tmpId,_tmpNome,_tmpAttrezzo,_tmpNRipetizione,_tmpNSerie,_tmpInsometria,_tmpIntervallo,_tmpPeso,_tmpCompleted,_tmpSchedaId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object deleteFromId(final int id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM essercissi WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
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
