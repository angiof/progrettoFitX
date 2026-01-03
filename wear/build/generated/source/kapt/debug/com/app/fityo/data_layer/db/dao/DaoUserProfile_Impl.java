package com.app.fityo.data_layer.db.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.app.fityo.data_layer.db.UserProfileEntity;
import java.lang.Boolean;
import java.lang.Class;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class DaoUserProfile_Impl implements DaoUserProfile {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<UserProfileEntity> __insertAdapterOfUserProfileEntity;

  private final EntityDeleteOrUpdateAdapter<UserProfileEntity> __deleteAdapterOfUserProfileEntity;

  private final EntityDeleteOrUpdateAdapter<UserProfileEntity> __updateAdapterOfUserProfileEntity;

  public DaoUserProfile_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfUserProfileEntity = new EntityInsertAdapter<UserProfileEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_profile` (`id`,`name`,`age`,`heightCm`,`weightKg`,`sex`,`discipline`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        statement.bindLong(3, entity.getAge());
        statement.bindDouble(4, entity.getHeightCm());
        statement.bindDouble(5, entity.getWeightKg());
        if (entity.getSex() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getSex());
        }
        if (entity.getDiscipline() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getDiscipline());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
      }
    };
    this.__deleteAdapterOfUserProfileEntity = new EntityDeleteOrUpdateAdapter<UserProfileEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `user_profile` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfUserProfileEntity = new EntityDeleteOrUpdateAdapter<UserProfileEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user_profile` SET `id` = ?,`name` = ?,`age` = ?,`heightCm` = ?,`weightKg` = ?,`sex` = ?,`discipline` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        statement.bindLong(3, entity.getAge());
        statement.bindDouble(4, entity.getHeightCm());
        statement.bindDouble(5, entity.getWeightKg());
        if (entity.getSex() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getSex());
        }
        if (entity.getDiscipline() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getDiscipline());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
        if (entity.getId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getId());
        }
      }
    };
  }

  @Override
  public Object insert(final UserProfileEntity profile, final Continuation<? super Long> arg1) {
    if (profile == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfUserProfileEntity.insertAndReturnId(_connection, profile);
    }, arg1);
  }

  @Override
  public Object delete(final UserProfileEntity profile, final Continuation<? super Unit> arg1) {
    if (profile == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfUserProfileEntity.handle(_connection, profile);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Object update(final UserProfileEntity profile, final Continuation<? super Unit> arg1) {
    if (profile == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfUserProfileEntity.handle(_connection, profile);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Object getActiveProfile(final Continuation<? super UserProfileEntity> arg0) {
    final String _sql = "SELECT * FROM user_profile ORDER BY updatedAt DESC LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfAge = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "age");
        final int _columnIndexOfHeightCm = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "heightCm");
        final int _columnIndexOfWeightKg = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weightKg");
        final int _columnIndexOfSex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sex");
        final int _columnIndexOfDiscipline = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "discipline");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final UserProfileEntity _result;
        if (_stmt.step()) {
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final int _tmpAge;
          _tmpAge = (int) (_stmt.getLong(_columnIndexOfAge));
          final float _tmpHeightCm;
          _tmpHeightCm = (float) (_stmt.getDouble(_columnIndexOfHeightCm));
          final float _tmpWeightKg;
          _tmpWeightKg = (float) (_stmt.getDouble(_columnIndexOfWeightKg));
          final String _tmpSex;
          if (_stmt.isNull(_columnIndexOfSex)) {
            _tmpSex = null;
          } else {
            _tmpSex = _stmt.getText(_columnIndexOfSex);
          }
          final String _tmpDiscipline;
          if (_stmt.isNull(_columnIndexOfDiscipline)) {
            _tmpDiscipline = null;
          } else {
            _tmpDiscipline = _stmt.getText(_columnIndexOfDiscipline);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          _result = new UserProfileEntity(_tmpId,_tmpName,_tmpAge,_tmpHeightCm,_tmpWeightKg,_tmpSex,_tmpDiscipline,_tmpCreatedAt,_tmpUpdatedAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, arg0);
  }

  @Override
  public Flow<UserProfileEntity> getActiveProfileFlow() {
    final String _sql = "SELECT * FROM user_profile ORDER BY updatedAt DESC LIMIT 1";
    return FlowUtil.createFlow(__db, false, new String[] {"user_profile"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfAge = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "age");
        final int _columnIndexOfHeightCm = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "heightCm");
        final int _columnIndexOfWeightKg = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weightKg");
        final int _columnIndexOfSex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sex");
        final int _columnIndexOfDiscipline = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "discipline");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final UserProfileEntity _result;
        if (_stmt.step()) {
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final int _tmpAge;
          _tmpAge = (int) (_stmt.getLong(_columnIndexOfAge));
          final float _tmpHeightCm;
          _tmpHeightCm = (float) (_stmt.getDouble(_columnIndexOfHeightCm));
          final float _tmpWeightKg;
          _tmpWeightKg = (float) (_stmt.getDouble(_columnIndexOfWeightKg));
          final String _tmpSex;
          if (_stmt.isNull(_columnIndexOfSex)) {
            _tmpSex = null;
          } else {
            _tmpSex = _stmt.getText(_columnIndexOfSex);
          }
          final String _tmpDiscipline;
          if (_stmt.isNull(_columnIndexOfDiscipline)) {
            _tmpDiscipline = null;
          } else {
            _tmpDiscipline = _stmt.getText(_columnIndexOfDiscipline);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          _result = new UserProfileEntity(_tmpId,_tmpName,_tmpAge,_tmpHeightCm,_tmpWeightKg,_tmpSex,_tmpDiscipline,_tmpCreatedAt,_tmpUpdatedAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getById(final int id, final Continuation<? super UserProfileEntity> arg1) {
    final String _sql = "SELECT * FROM user_profile WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfAge = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "age");
        final int _columnIndexOfHeightCm = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "heightCm");
        final int _columnIndexOfWeightKg = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weightKg");
        final int _columnIndexOfSex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sex");
        final int _columnIndexOfDiscipline = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "discipline");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final UserProfileEntity _result;
        if (_stmt.step()) {
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final int _tmpAge;
          _tmpAge = (int) (_stmt.getLong(_columnIndexOfAge));
          final float _tmpHeightCm;
          _tmpHeightCm = (float) (_stmt.getDouble(_columnIndexOfHeightCm));
          final float _tmpWeightKg;
          _tmpWeightKg = (float) (_stmt.getDouble(_columnIndexOfWeightKg));
          final String _tmpSex;
          if (_stmt.isNull(_columnIndexOfSex)) {
            _tmpSex = null;
          } else {
            _tmpSex = _stmt.getText(_columnIndexOfSex);
          }
          final String _tmpDiscipline;
          if (_stmt.isNull(_columnIndexOfDiscipline)) {
            _tmpDiscipline = null;
          } else {
            _tmpDiscipline = _stmt.getText(_columnIndexOfDiscipline);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          _result = new UserProfileEntity(_tmpId,_tmpName,_tmpAge,_tmpHeightCm,_tmpWeightKg,_tmpSex,_tmpDiscipline,_tmpCreatedAt,_tmpUpdatedAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, arg1);
  }

  @Override
  public Flow<List<UserProfileEntity>> getAllProfiles() {
    final String _sql = "SELECT * FROM user_profile ORDER BY updatedAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"user_profile"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfAge = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "age");
        final int _columnIndexOfHeightCm = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "heightCm");
        final int _columnIndexOfWeightKg = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weightKg");
        final int _columnIndexOfSex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sex");
        final int _columnIndexOfDiscipline = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "discipline");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final List<UserProfileEntity> _result = new ArrayList<UserProfileEntity>();
        while (_stmt.step()) {
          final UserProfileEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final int _tmpAge;
          _tmpAge = (int) (_stmt.getLong(_columnIndexOfAge));
          final float _tmpHeightCm;
          _tmpHeightCm = (float) (_stmt.getDouble(_columnIndexOfHeightCm));
          final float _tmpWeightKg;
          _tmpWeightKg = (float) (_stmt.getDouble(_columnIndexOfWeightKg));
          final String _tmpSex;
          if (_stmt.isNull(_columnIndexOfSex)) {
            _tmpSex = null;
          } else {
            _tmpSex = _stmt.getText(_columnIndexOfSex);
          }
          final String _tmpDiscipline;
          if (_stmt.isNull(_columnIndexOfDiscipline)) {
            _tmpDiscipline = null;
          } else {
            _tmpDiscipline = _stmt.getText(_columnIndexOfDiscipline);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          _item = new UserProfileEntity(_tmpId,_tmpName,_tmpAge,_tmpHeightCm,_tmpWeightKg,_tmpSex,_tmpDiscipline,_tmpCreatedAt,_tmpUpdatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object count(final Continuation<? super Integer> arg0) {
    final String _sql = "SELECT COUNT(*) FROM user_profile";
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
    }, arg0);
  }

  @Override
  public Object hasProfile(final Continuation<? super Boolean> arg0) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM user_profile LIMIT 1)";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Boolean _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp == null ? null : _tmp != 0;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, arg0);
  }

  @Override
  public Object deleteById(final int id, final Continuation<? super Unit> arg1) {
    final String _sql = "DELETE FROM user_profile WHERE id = ?";
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
    }, arg1);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> arg0) {
    final String _sql = "DELETE FROM user_profile";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, arg0);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
