package com.app.fityo.data_layer.db.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.app.fityo.data_layer.db.MuscleCompareEntity;
import java.lang.Class;
import java.lang.Float;
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
public final class DaoMuscleCompare_Impl implements DaoMuscleCompare {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<MuscleCompareEntity> __insertAdapterOfMuscleCompareEntity;

  private final EntityDeleteOrUpdateAdapter<MuscleCompareEntity> __deleteAdapterOfMuscleCompareEntity;

  private final EntityDeleteOrUpdateAdapter<MuscleCompareEntity> __updateAdapterOfMuscleCompareEntity;

  public DaoMuscleCompare_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfMuscleCompareEntity = new EntityInsertAdapter<MuscleCompareEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `muscle_compare` (`id`,`createdAt`,`photoAPath`,`photoBPath`,`armsVariation`,`absVariation`,`legsVariation`,`glutesVariation`,`notes`,`photoADate`,`photoBDate`,`scaleFactorA`,`scaleFactorB`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MuscleCompareEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        statement.bindLong(2, entity.getCreatedAt());
        if (entity.getPhotoAPath() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getPhotoAPath());
        }
        if (entity.getPhotoBPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getPhotoBPath());
        }
        statement.bindDouble(5, entity.getArmsVariation());
        statement.bindDouble(6, entity.getAbsVariation());
        statement.bindDouble(7, entity.getLegsVariation());
        statement.bindDouble(8, entity.getGlutesVariation());
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getNotes());
        }
        if (entity.getPhotoADate() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getPhotoADate());
        }
        if (entity.getPhotoBDate() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getPhotoBDate());
        }
        statement.bindDouble(12, entity.getScaleFactorA());
        statement.bindDouble(13, entity.getScaleFactorB());
      }
    };
    this.__deleteAdapterOfMuscleCompareEntity = new EntityDeleteOrUpdateAdapter<MuscleCompareEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `muscle_compare` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MuscleCompareEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfMuscleCompareEntity = new EntityDeleteOrUpdateAdapter<MuscleCompareEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `muscle_compare` SET `id` = ?,`createdAt` = ?,`photoAPath` = ?,`photoBPath` = ?,`armsVariation` = ?,`absVariation` = ?,`legsVariation` = ?,`glutesVariation` = ?,`notes` = ?,`photoADate` = ?,`photoBDate` = ?,`scaleFactorA` = ?,`scaleFactorB` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MuscleCompareEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        statement.bindLong(2, entity.getCreatedAt());
        if (entity.getPhotoAPath() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getPhotoAPath());
        }
        if (entity.getPhotoBPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getPhotoBPath());
        }
        statement.bindDouble(5, entity.getArmsVariation());
        statement.bindDouble(6, entity.getAbsVariation());
        statement.bindDouble(7, entity.getLegsVariation());
        statement.bindDouble(8, entity.getGlutesVariation());
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getNotes());
        }
        if (entity.getPhotoADate() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getPhotoADate());
        }
        if (entity.getPhotoBDate() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getPhotoBDate());
        }
        statement.bindDouble(12, entity.getScaleFactorA());
        statement.bindDouble(13, entity.getScaleFactorB());
        if (entity.getId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getId());
        }
      }
    };
  }

  @Override
  public Object insert(final MuscleCompareEntity compare, final Continuation<? super Long> arg1) {
    if (compare == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfMuscleCompareEntity.insertAndReturnId(_connection, compare);
    }, arg1);
  }

  @Override
  public Object delete(final MuscleCompareEntity compare, final Continuation<? super Unit> arg1) {
    if (compare == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfMuscleCompareEntity.handle(_connection, compare);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Object update(final MuscleCompareEntity compare, final Continuation<? super Unit> arg1) {
    if (compare == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfMuscleCompareEntity.handle(_connection, compare);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Flow<List<MuscleCompareEntity>> getAllCompares() {
    final String _sql = "SELECT * FROM muscle_compare ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"muscle_compare"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfPhotoAPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoAPath");
        final int _columnIndexOfPhotoBPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoBPath");
        final int _columnIndexOfArmsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "armsVariation");
        final int _columnIndexOfAbsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "absVariation");
        final int _columnIndexOfLegsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "legsVariation");
        final int _columnIndexOfGlutesVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "glutesVariation");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfPhotoADate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoADate");
        final int _columnIndexOfPhotoBDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoBDate");
        final int _columnIndexOfScaleFactorA = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "scaleFactorA");
        final int _columnIndexOfScaleFactorB = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "scaleFactorB");
        final List<MuscleCompareEntity> _result = new ArrayList<MuscleCompareEntity>();
        while (_stmt.step()) {
          final MuscleCompareEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpPhotoAPath;
          if (_stmt.isNull(_columnIndexOfPhotoAPath)) {
            _tmpPhotoAPath = null;
          } else {
            _tmpPhotoAPath = _stmt.getText(_columnIndexOfPhotoAPath);
          }
          final String _tmpPhotoBPath;
          if (_stmt.isNull(_columnIndexOfPhotoBPath)) {
            _tmpPhotoBPath = null;
          } else {
            _tmpPhotoBPath = _stmt.getText(_columnIndexOfPhotoBPath);
          }
          final float _tmpArmsVariation;
          _tmpArmsVariation = (float) (_stmt.getDouble(_columnIndexOfArmsVariation));
          final float _tmpAbsVariation;
          _tmpAbsVariation = (float) (_stmt.getDouble(_columnIndexOfAbsVariation));
          final float _tmpLegsVariation;
          _tmpLegsVariation = (float) (_stmt.getDouble(_columnIndexOfLegsVariation));
          final float _tmpGlutesVariation;
          _tmpGlutesVariation = (float) (_stmt.getDouble(_columnIndexOfGlutesVariation));
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final String _tmpPhotoADate;
          if (_stmt.isNull(_columnIndexOfPhotoADate)) {
            _tmpPhotoADate = null;
          } else {
            _tmpPhotoADate = _stmt.getText(_columnIndexOfPhotoADate);
          }
          final String _tmpPhotoBDate;
          if (_stmt.isNull(_columnIndexOfPhotoBDate)) {
            _tmpPhotoBDate = null;
          } else {
            _tmpPhotoBDate = _stmt.getText(_columnIndexOfPhotoBDate);
          }
          final float _tmpScaleFactorA;
          _tmpScaleFactorA = (float) (_stmt.getDouble(_columnIndexOfScaleFactorA));
          final float _tmpScaleFactorB;
          _tmpScaleFactorB = (float) (_stmt.getDouble(_columnIndexOfScaleFactorB));
          _item = new MuscleCompareEntity(_tmpId,_tmpCreatedAt,_tmpPhotoAPath,_tmpPhotoBPath,_tmpArmsVariation,_tmpAbsVariation,_tmpLegsVariation,_tmpGlutesVariation,_tmpNotes,_tmpPhotoADate,_tmpPhotoBDate,_tmpScaleFactorA,_tmpScaleFactorB);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getById(final int id, final Continuation<? super MuscleCompareEntity> arg1) {
    final String _sql = "SELECT * FROM muscle_compare WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfPhotoAPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoAPath");
        final int _columnIndexOfPhotoBPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoBPath");
        final int _columnIndexOfArmsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "armsVariation");
        final int _columnIndexOfAbsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "absVariation");
        final int _columnIndexOfLegsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "legsVariation");
        final int _columnIndexOfGlutesVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "glutesVariation");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfPhotoADate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoADate");
        final int _columnIndexOfPhotoBDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoBDate");
        final int _columnIndexOfScaleFactorA = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "scaleFactorA");
        final int _columnIndexOfScaleFactorB = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "scaleFactorB");
        final MuscleCompareEntity _result;
        if (_stmt.step()) {
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpPhotoAPath;
          if (_stmt.isNull(_columnIndexOfPhotoAPath)) {
            _tmpPhotoAPath = null;
          } else {
            _tmpPhotoAPath = _stmt.getText(_columnIndexOfPhotoAPath);
          }
          final String _tmpPhotoBPath;
          if (_stmt.isNull(_columnIndexOfPhotoBPath)) {
            _tmpPhotoBPath = null;
          } else {
            _tmpPhotoBPath = _stmt.getText(_columnIndexOfPhotoBPath);
          }
          final float _tmpArmsVariation;
          _tmpArmsVariation = (float) (_stmt.getDouble(_columnIndexOfArmsVariation));
          final float _tmpAbsVariation;
          _tmpAbsVariation = (float) (_stmt.getDouble(_columnIndexOfAbsVariation));
          final float _tmpLegsVariation;
          _tmpLegsVariation = (float) (_stmt.getDouble(_columnIndexOfLegsVariation));
          final float _tmpGlutesVariation;
          _tmpGlutesVariation = (float) (_stmt.getDouble(_columnIndexOfGlutesVariation));
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final String _tmpPhotoADate;
          if (_stmt.isNull(_columnIndexOfPhotoADate)) {
            _tmpPhotoADate = null;
          } else {
            _tmpPhotoADate = _stmt.getText(_columnIndexOfPhotoADate);
          }
          final String _tmpPhotoBDate;
          if (_stmt.isNull(_columnIndexOfPhotoBDate)) {
            _tmpPhotoBDate = null;
          } else {
            _tmpPhotoBDate = _stmt.getText(_columnIndexOfPhotoBDate);
          }
          final float _tmpScaleFactorA;
          _tmpScaleFactorA = (float) (_stmt.getDouble(_columnIndexOfScaleFactorA));
          final float _tmpScaleFactorB;
          _tmpScaleFactorB = (float) (_stmt.getDouble(_columnIndexOfScaleFactorB));
          _result = new MuscleCompareEntity(_tmpId,_tmpCreatedAt,_tmpPhotoAPath,_tmpPhotoBPath,_tmpArmsVariation,_tmpAbsVariation,_tmpLegsVariation,_tmpGlutesVariation,_tmpNotes,_tmpPhotoADate,_tmpPhotoBDate,_tmpScaleFactorA,_tmpScaleFactorB);
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
  public Object getLatest(final Continuation<? super MuscleCompareEntity> arg0) {
    final String _sql = "SELECT * FROM muscle_compare ORDER BY createdAt DESC LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfPhotoAPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoAPath");
        final int _columnIndexOfPhotoBPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoBPath");
        final int _columnIndexOfArmsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "armsVariation");
        final int _columnIndexOfAbsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "absVariation");
        final int _columnIndexOfLegsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "legsVariation");
        final int _columnIndexOfGlutesVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "glutesVariation");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfPhotoADate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoADate");
        final int _columnIndexOfPhotoBDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoBDate");
        final int _columnIndexOfScaleFactorA = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "scaleFactorA");
        final int _columnIndexOfScaleFactorB = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "scaleFactorB");
        final MuscleCompareEntity _result;
        if (_stmt.step()) {
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpPhotoAPath;
          if (_stmt.isNull(_columnIndexOfPhotoAPath)) {
            _tmpPhotoAPath = null;
          } else {
            _tmpPhotoAPath = _stmt.getText(_columnIndexOfPhotoAPath);
          }
          final String _tmpPhotoBPath;
          if (_stmt.isNull(_columnIndexOfPhotoBPath)) {
            _tmpPhotoBPath = null;
          } else {
            _tmpPhotoBPath = _stmt.getText(_columnIndexOfPhotoBPath);
          }
          final float _tmpArmsVariation;
          _tmpArmsVariation = (float) (_stmt.getDouble(_columnIndexOfArmsVariation));
          final float _tmpAbsVariation;
          _tmpAbsVariation = (float) (_stmt.getDouble(_columnIndexOfAbsVariation));
          final float _tmpLegsVariation;
          _tmpLegsVariation = (float) (_stmt.getDouble(_columnIndexOfLegsVariation));
          final float _tmpGlutesVariation;
          _tmpGlutesVariation = (float) (_stmt.getDouble(_columnIndexOfGlutesVariation));
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final String _tmpPhotoADate;
          if (_stmt.isNull(_columnIndexOfPhotoADate)) {
            _tmpPhotoADate = null;
          } else {
            _tmpPhotoADate = _stmt.getText(_columnIndexOfPhotoADate);
          }
          final String _tmpPhotoBDate;
          if (_stmt.isNull(_columnIndexOfPhotoBDate)) {
            _tmpPhotoBDate = null;
          } else {
            _tmpPhotoBDate = _stmt.getText(_columnIndexOfPhotoBDate);
          }
          final float _tmpScaleFactorA;
          _tmpScaleFactorA = (float) (_stmt.getDouble(_columnIndexOfScaleFactorA));
          final float _tmpScaleFactorB;
          _tmpScaleFactorB = (float) (_stmt.getDouble(_columnIndexOfScaleFactorB));
          _result = new MuscleCompareEntity(_tmpId,_tmpCreatedAt,_tmpPhotoAPath,_tmpPhotoBPath,_tmpArmsVariation,_tmpAbsVariation,_tmpLegsVariation,_tmpGlutesVariation,_tmpNotes,_tmpPhotoADate,_tmpPhotoBDate,_tmpScaleFactorA,_tmpScaleFactorB);
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
  public Object getCount(final Continuation<? super Integer> arg0) {
    final String _sql = "SELECT COUNT(*) FROM muscle_compare";
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
  public Flow<List<MuscleCompareEntity>> getComparesByDateRange(final long startDate,
      final long endDate) {
    final String _sql = "SELECT * FROM muscle_compare WHERE createdAt BETWEEN ? AND ? ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"muscle_compare"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, startDate);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, endDate);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfPhotoAPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoAPath");
        final int _columnIndexOfPhotoBPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoBPath");
        final int _columnIndexOfArmsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "armsVariation");
        final int _columnIndexOfAbsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "absVariation");
        final int _columnIndexOfLegsVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "legsVariation");
        final int _columnIndexOfGlutesVariation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "glutesVariation");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfPhotoADate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoADate");
        final int _columnIndexOfPhotoBDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "photoBDate");
        final int _columnIndexOfScaleFactorA = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "scaleFactorA");
        final int _columnIndexOfScaleFactorB = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "scaleFactorB");
        final List<MuscleCompareEntity> _result = new ArrayList<MuscleCompareEntity>();
        while (_stmt.step()) {
          final MuscleCompareEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpPhotoAPath;
          if (_stmt.isNull(_columnIndexOfPhotoAPath)) {
            _tmpPhotoAPath = null;
          } else {
            _tmpPhotoAPath = _stmt.getText(_columnIndexOfPhotoAPath);
          }
          final String _tmpPhotoBPath;
          if (_stmt.isNull(_columnIndexOfPhotoBPath)) {
            _tmpPhotoBPath = null;
          } else {
            _tmpPhotoBPath = _stmt.getText(_columnIndexOfPhotoBPath);
          }
          final float _tmpArmsVariation;
          _tmpArmsVariation = (float) (_stmt.getDouble(_columnIndexOfArmsVariation));
          final float _tmpAbsVariation;
          _tmpAbsVariation = (float) (_stmt.getDouble(_columnIndexOfAbsVariation));
          final float _tmpLegsVariation;
          _tmpLegsVariation = (float) (_stmt.getDouble(_columnIndexOfLegsVariation));
          final float _tmpGlutesVariation;
          _tmpGlutesVariation = (float) (_stmt.getDouble(_columnIndexOfGlutesVariation));
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final String _tmpPhotoADate;
          if (_stmt.isNull(_columnIndexOfPhotoADate)) {
            _tmpPhotoADate = null;
          } else {
            _tmpPhotoADate = _stmt.getText(_columnIndexOfPhotoADate);
          }
          final String _tmpPhotoBDate;
          if (_stmt.isNull(_columnIndexOfPhotoBDate)) {
            _tmpPhotoBDate = null;
          } else {
            _tmpPhotoBDate = _stmt.getText(_columnIndexOfPhotoBDate);
          }
          final float _tmpScaleFactorA;
          _tmpScaleFactorA = (float) (_stmt.getDouble(_columnIndexOfScaleFactorA));
          final float _tmpScaleFactorB;
          _tmpScaleFactorB = (float) (_stmt.getDouble(_columnIndexOfScaleFactorB));
          _item = new MuscleCompareEntity(_tmpId,_tmpCreatedAt,_tmpPhotoAPath,_tmpPhotoBPath,_tmpArmsVariation,_tmpAbsVariation,_tmpLegsVariation,_tmpGlutesVariation,_tmpNotes,_tmpPhotoADate,_tmpPhotoBDate,_tmpScaleFactorA,_tmpScaleFactorB);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getAverageArmsVariation(final Continuation<? super Float> arg0) {
    final String _sql = "SELECT AVG(armsVariation) FROM muscle_compare";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Float _result;
        if (_stmt.step()) {
          final Float _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (float) (_stmt.getDouble(0));
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
  public Object getAverageAbsVariation(final Continuation<? super Float> arg0) {
    final String _sql = "SELECT AVG(absVariation) FROM muscle_compare";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Float _result;
        if (_stmt.step()) {
          final Float _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (float) (_stmt.getDouble(0));
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
  public Object getAverageLegsVariation(final Continuation<? super Float> arg0) {
    final String _sql = "SELECT AVG(legsVariation) FROM muscle_compare";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Float _result;
        if (_stmt.step()) {
          final Float _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (float) (_stmt.getDouble(0));
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
  public Object getAverageGlutesVariation(final Continuation<? super Float> arg0) {
    final String _sql = "SELECT AVG(glutesVariation) FROM muscle_compare";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Float _result;
        if (_stmt.step()) {
          final Float _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (float) (_stmt.getDouble(0));
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
  public Object deleteById(final int id, final Continuation<? super Unit> arg1) {
    final String _sql = "DELETE FROM muscle_compare WHERE id = ?";
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
    final String _sql = "DELETE FROM muscle_compare";
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
