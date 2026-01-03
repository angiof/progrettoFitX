package com.app.fityo.data_layer.db.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.app.fityo.data_layer.db.TutorSessionEntity;
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
public final class DaoTutorSession_Impl implements DaoTutorSession {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<TutorSessionEntity> __insertAdapterOfTutorSessionEntity;

  private final EntityDeleteOrUpdateAdapter<TutorSessionEntity> __deleteAdapterOfTutorSessionEntity;

  private final EntityDeleteOrUpdateAdapter<TutorSessionEntity> __updateAdapterOfTutorSessionEntity;

  public DaoTutorSession_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfTutorSessionEntity = new EntityInsertAdapter<TutorSessionEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `tutor_sessions` (`id`,`createdAt`,`exerciseType`,`videoPath`,`thumbnailPath`,`duration`,`totalErrors`,`overallScore`,`errorsJson`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final TutorSessionEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        statement.bindLong(2, entity.getCreatedAt());
        if (entity.getExerciseType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getExerciseType());
        }
        if (entity.getVideoPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getVideoPath());
        }
        if (entity.getThumbnailPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getThumbnailPath());
        }
        statement.bindLong(6, entity.getDuration());
        statement.bindLong(7, entity.getTotalErrors());
        statement.bindDouble(8, entity.getOverallScore());
        if (entity.getErrorsJson() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getErrorsJson());
        }
      }
    };
    this.__deleteAdapterOfTutorSessionEntity = new EntityDeleteOrUpdateAdapter<TutorSessionEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tutor_sessions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final TutorSessionEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfTutorSessionEntity = new EntityDeleteOrUpdateAdapter<TutorSessionEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tutor_sessions` SET `id` = ?,`createdAt` = ?,`exerciseType` = ?,`videoPath` = ?,`thumbnailPath` = ?,`duration` = ?,`totalErrors` = ?,`overallScore` = ?,`errorsJson` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final TutorSessionEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        statement.bindLong(2, entity.getCreatedAt());
        if (entity.getExerciseType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getExerciseType());
        }
        if (entity.getVideoPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getVideoPath());
        }
        if (entity.getThumbnailPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getThumbnailPath());
        }
        statement.bindLong(6, entity.getDuration());
        statement.bindLong(7, entity.getTotalErrors());
        statement.bindDouble(8, entity.getOverallScore());
        if (entity.getErrorsJson() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getErrorsJson());
        }
        if (entity.getId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getId());
        }
      }
    };
  }

  @Override
  public Object insert(final TutorSessionEntity session, final Continuation<? super Long> arg1) {
    if (session == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfTutorSessionEntity.insertAndReturnId(_connection, session);
    }, arg1);
  }

  @Override
  public Object delete(final TutorSessionEntity session, final Continuation<? super Unit> arg1) {
    if (session == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfTutorSessionEntity.handle(_connection, session);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Object update(final TutorSessionEntity session, final Continuation<? super Unit> arg1) {
    if (session == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfTutorSessionEntity.handle(_connection, session);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Flow<List<TutorSessionEntity>> getAllSessions() {
    final String _sql = "SELECT * FROM tutor_sessions ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"tutor_sessions"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfExerciseType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "exerciseType");
        final int _columnIndexOfVideoPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoPath");
        final int _columnIndexOfThumbnailPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnailPath");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfTotalErrors = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalErrors");
        final int _columnIndexOfOverallScore = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overallScore");
        final int _columnIndexOfErrorsJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "errorsJson");
        final List<TutorSessionEntity> _result = new ArrayList<TutorSessionEntity>();
        while (_stmt.step()) {
          final TutorSessionEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpExerciseType;
          if (_stmt.isNull(_columnIndexOfExerciseType)) {
            _tmpExerciseType = null;
          } else {
            _tmpExerciseType = _stmt.getText(_columnIndexOfExerciseType);
          }
          final String _tmpVideoPath;
          if (_stmt.isNull(_columnIndexOfVideoPath)) {
            _tmpVideoPath = null;
          } else {
            _tmpVideoPath = _stmt.getText(_columnIndexOfVideoPath);
          }
          final String _tmpThumbnailPath;
          if (_stmt.isNull(_columnIndexOfThumbnailPath)) {
            _tmpThumbnailPath = null;
          } else {
            _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final int _tmpTotalErrors;
          _tmpTotalErrors = (int) (_stmt.getLong(_columnIndexOfTotalErrors));
          final float _tmpOverallScore;
          _tmpOverallScore = (float) (_stmt.getDouble(_columnIndexOfOverallScore));
          final String _tmpErrorsJson;
          if (_stmt.isNull(_columnIndexOfErrorsJson)) {
            _tmpErrorsJson = null;
          } else {
            _tmpErrorsJson = _stmt.getText(_columnIndexOfErrorsJson);
          }
          _item = new TutorSessionEntity(_tmpId,_tmpCreatedAt,_tmpExerciseType,_tmpVideoPath,_tmpThumbnailPath,_tmpDuration,_tmpTotalErrors,_tmpOverallScore,_tmpErrorsJson);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getById(final int id, final Continuation<? super TutorSessionEntity> arg1) {
    final String _sql = "SELECT * FROM tutor_sessions WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfExerciseType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "exerciseType");
        final int _columnIndexOfVideoPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoPath");
        final int _columnIndexOfThumbnailPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnailPath");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfTotalErrors = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalErrors");
        final int _columnIndexOfOverallScore = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overallScore");
        final int _columnIndexOfErrorsJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "errorsJson");
        final TutorSessionEntity _result;
        if (_stmt.step()) {
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpExerciseType;
          if (_stmt.isNull(_columnIndexOfExerciseType)) {
            _tmpExerciseType = null;
          } else {
            _tmpExerciseType = _stmt.getText(_columnIndexOfExerciseType);
          }
          final String _tmpVideoPath;
          if (_stmt.isNull(_columnIndexOfVideoPath)) {
            _tmpVideoPath = null;
          } else {
            _tmpVideoPath = _stmt.getText(_columnIndexOfVideoPath);
          }
          final String _tmpThumbnailPath;
          if (_stmt.isNull(_columnIndexOfThumbnailPath)) {
            _tmpThumbnailPath = null;
          } else {
            _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final int _tmpTotalErrors;
          _tmpTotalErrors = (int) (_stmt.getLong(_columnIndexOfTotalErrors));
          final float _tmpOverallScore;
          _tmpOverallScore = (float) (_stmt.getDouble(_columnIndexOfOverallScore));
          final String _tmpErrorsJson;
          if (_stmt.isNull(_columnIndexOfErrorsJson)) {
            _tmpErrorsJson = null;
          } else {
            _tmpErrorsJson = _stmt.getText(_columnIndexOfErrorsJson);
          }
          _result = new TutorSessionEntity(_tmpId,_tmpCreatedAt,_tmpExerciseType,_tmpVideoPath,_tmpThumbnailPath,_tmpDuration,_tmpTotalErrors,_tmpOverallScore,_tmpErrorsJson);
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
  public Object getLatest(final Continuation<? super TutorSessionEntity> arg0) {
    final String _sql = "SELECT * FROM tutor_sessions ORDER BY createdAt DESC LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfExerciseType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "exerciseType");
        final int _columnIndexOfVideoPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoPath");
        final int _columnIndexOfThumbnailPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnailPath");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfTotalErrors = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalErrors");
        final int _columnIndexOfOverallScore = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overallScore");
        final int _columnIndexOfErrorsJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "errorsJson");
        final TutorSessionEntity _result;
        if (_stmt.step()) {
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpExerciseType;
          if (_stmt.isNull(_columnIndexOfExerciseType)) {
            _tmpExerciseType = null;
          } else {
            _tmpExerciseType = _stmt.getText(_columnIndexOfExerciseType);
          }
          final String _tmpVideoPath;
          if (_stmt.isNull(_columnIndexOfVideoPath)) {
            _tmpVideoPath = null;
          } else {
            _tmpVideoPath = _stmt.getText(_columnIndexOfVideoPath);
          }
          final String _tmpThumbnailPath;
          if (_stmt.isNull(_columnIndexOfThumbnailPath)) {
            _tmpThumbnailPath = null;
          } else {
            _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final int _tmpTotalErrors;
          _tmpTotalErrors = (int) (_stmt.getLong(_columnIndexOfTotalErrors));
          final float _tmpOverallScore;
          _tmpOverallScore = (float) (_stmt.getDouble(_columnIndexOfOverallScore));
          final String _tmpErrorsJson;
          if (_stmt.isNull(_columnIndexOfErrorsJson)) {
            _tmpErrorsJson = null;
          } else {
            _tmpErrorsJson = _stmt.getText(_columnIndexOfErrorsJson);
          }
          _result = new TutorSessionEntity(_tmpId,_tmpCreatedAt,_tmpExerciseType,_tmpVideoPath,_tmpThumbnailPath,_tmpDuration,_tmpTotalErrors,_tmpOverallScore,_tmpErrorsJson);
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
    final String _sql = "SELECT COUNT(*) FROM tutor_sessions";
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
  public Flow<List<TutorSessionEntity>> getSessionsByExerciseType(final String exerciseType) {
    final String _sql = "SELECT * FROM tutor_sessions WHERE exerciseType = ? ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"tutor_sessions"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (exerciseType == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, exerciseType);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfExerciseType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "exerciseType");
        final int _columnIndexOfVideoPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoPath");
        final int _columnIndexOfThumbnailPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnailPath");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfTotalErrors = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalErrors");
        final int _columnIndexOfOverallScore = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overallScore");
        final int _columnIndexOfErrorsJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "errorsJson");
        final List<TutorSessionEntity> _result = new ArrayList<TutorSessionEntity>();
        while (_stmt.step()) {
          final TutorSessionEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpExerciseType;
          if (_stmt.isNull(_columnIndexOfExerciseType)) {
            _tmpExerciseType = null;
          } else {
            _tmpExerciseType = _stmt.getText(_columnIndexOfExerciseType);
          }
          final String _tmpVideoPath;
          if (_stmt.isNull(_columnIndexOfVideoPath)) {
            _tmpVideoPath = null;
          } else {
            _tmpVideoPath = _stmt.getText(_columnIndexOfVideoPath);
          }
          final String _tmpThumbnailPath;
          if (_stmt.isNull(_columnIndexOfThumbnailPath)) {
            _tmpThumbnailPath = null;
          } else {
            _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final int _tmpTotalErrors;
          _tmpTotalErrors = (int) (_stmt.getLong(_columnIndexOfTotalErrors));
          final float _tmpOverallScore;
          _tmpOverallScore = (float) (_stmt.getDouble(_columnIndexOfOverallScore));
          final String _tmpErrorsJson;
          if (_stmt.isNull(_columnIndexOfErrorsJson)) {
            _tmpErrorsJson = null;
          } else {
            _tmpErrorsJson = _stmt.getText(_columnIndexOfErrorsJson);
          }
          _item = new TutorSessionEntity(_tmpId,_tmpCreatedAt,_tmpExerciseType,_tmpVideoPath,_tmpThumbnailPath,_tmpDuration,_tmpTotalErrors,_tmpOverallScore,_tmpErrorsJson);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<TutorSessionEntity>> getSessionsByDateRange(final long startDate,
      final long endDate) {
    final String _sql = "SELECT * FROM tutor_sessions WHERE createdAt BETWEEN ? AND ? ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"tutor_sessions"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, startDate);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, endDate);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfExerciseType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "exerciseType");
        final int _columnIndexOfVideoPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoPath");
        final int _columnIndexOfThumbnailPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnailPath");
        final int _columnIndexOfDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "duration");
        final int _columnIndexOfTotalErrors = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalErrors");
        final int _columnIndexOfOverallScore = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overallScore");
        final int _columnIndexOfErrorsJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "errorsJson");
        final List<TutorSessionEntity> _result = new ArrayList<TutorSessionEntity>();
        while (_stmt.step()) {
          final TutorSessionEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpExerciseType;
          if (_stmt.isNull(_columnIndexOfExerciseType)) {
            _tmpExerciseType = null;
          } else {
            _tmpExerciseType = _stmt.getText(_columnIndexOfExerciseType);
          }
          final String _tmpVideoPath;
          if (_stmt.isNull(_columnIndexOfVideoPath)) {
            _tmpVideoPath = null;
          } else {
            _tmpVideoPath = _stmt.getText(_columnIndexOfVideoPath);
          }
          final String _tmpThumbnailPath;
          if (_stmt.isNull(_columnIndexOfThumbnailPath)) {
            _tmpThumbnailPath = null;
          } else {
            _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath);
          }
          final long _tmpDuration;
          _tmpDuration = _stmt.getLong(_columnIndexOfDuration);
          final int _tmpTotalErrors;
          _tmpTotalErrors = (int) (_stmt.getLong(_columnIndexOfTotalErrors));
          final float _tmpOverallScore;
          _tmpOverallScore = (float) (_stmt.getDouble(_columnIndexOfOverallScore));
          final String _tmpErrorsJson;
          if (_stmt.isNull(_columnIndexOfErrorsJson)) {
            _tmpErrorsJson = null;
          } else {
            _tmpErrorsJson = _stmt.getText(_columnIndexOfErrorsJson);
          }
          _item = new TutorSessionEntity(_tmpId,_tmpCreatedAt,_tmpExerciseType,_tmpVideoPath,_tmpThumbnailPath,_tmpDuration,_tmpTotalErrors,_tmpOverallScore,_tmpErrorsJson);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getAverageScoreByExercise(final String exerciseType,
      final Continuation<? super Float> arg1) {
    final String _sql = "SELECT AVG(overallScore) FROM tutor_sessions WHERE exerciseType = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (exerciseType == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, exerciseType);
        }
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
    }, arg1);
  }

  @Override
  public Object getOverallAverageScore(final Continuation<? super Float> arg0) {
    final String _sql = "SELECT AVG(overallScore) FROM tutor_sessions";
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
  public Object getCountByExercise(final String exerciseType,
      final Continuation<? super Integer> arg1) {
    final String _sql = "SELECT COUNT(*) FROM tutor_sessions WHERE exerciseType = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (exerciseType == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, exerciseType);
        }
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
    }, arg1);
  }

  @Override
  public Object deleteById(final int id, final Continuation<? super Unit> arg1) {
    final String _sql = "DELETE FROM tutor_sessions WHERE id = ?";
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
    final String _sql = "DELETE FROM tutor_sessions";
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
