package com.app.fityo.data_layer.db.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.app.fityo.data_layer.db.Avatar3DEntity;
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
public final class DaoAvatar3D_Impl implements DaoAvatar3D {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Avatar3DEntity> __insertAdapterOfAvatar3DEntity;

  private final EntityDeleteOrUpdateAdapter<Avatar3DEntity> __deleteAdapterOfAvatar3DEntity;

  public DaoAvatar3D_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfAvatar3DEntity = new EntityInsertAdapter<Avatar3DEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `avatar_3d` (`id`,`userId`,`createdAt`,`meshDataPath`,`thumbnailPath`,`shapeParametersJson`,`zoneColorsJson`,`videoSourcePath`,`processingDurationMs`,`framesAnalyzed`,`confidence`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final Avatar3DEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        statement.bindLong(2, entity.getUserId());
        statement.bindLong(3, entity.getCreatedAt());
        if (entity.getMeshDataPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getMeshDataPath());
        }
        if (entity.getThumbnailPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getThumbnailPath());
        }
        if (entity.getShapeParametersJson() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getShapeParametersJson());
        }
        if (entity.getZoneColorsJson() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getZoneColorsJson());
        }
        if (entity.getVideoSourcePath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getVideoSourcePath());
        }
        statement.bindLong(9, entity.getProcessingDurationMs());
        statement.bindLong(10, entity.getFramesAnalyzed());
        statement.bindDouble(11, entity.getConfidence());
      }
    };
    this.__deleteAdapterOfAvatar3DEntity = new EntityDeleteOrUpdateAdapter<Avatar3DEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `avatar_3d` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final Avatar3DEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
  }

  @Override
  public Object insert(final Avatar3DEntity avatar, final Continuation<? super Long> $completion) {
    if (avatar == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfAvatar3DEntity.insertAndReturnId(_connection, avatar);
    }, $completion);
  }

  @Override
  public Object delete(final Avatar3DEntity avatar, final Continuation<? super Unit> $completion) {
    if (avatar == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfAvatar3DEntity.handle(_connection, avatar);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getById(final int id, final Continuation<? super Avatar3DEntity> $completion) {
    final String _sql = "SELECT * FROM avatar_3d WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfMeshDataPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "meshDataPath");
        final int _columnIndexOfThumbnailPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnailPath");
        final int _columnIndexOfShapeParametersJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shapeParametersJson");
        final int _columnIndexOfZoneColorsJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "zoneColorsJson");
        final int _columnIndexOfVideoSourcePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoSourcePath");
        final int _columnIndexOfProcessingDurationMs = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "processingDurationMs");
        final int _columnIndexOfFramesAnalyzed = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "framesAnalyzed");
        final int _columnIndexOfConfidence = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "confidence");
        final Avatar3DEntity _result;
        if (_stmt.step()) {
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpMeshDataPath;
          if (_stmt.isNull(_columnIndexOfMeshDataPath)) {
            _tmpMeshDataPath = null;
          } else {
            _tmpMeshDataPath = _stmt.getText(_columnIndexOfMeshDataPath);
          }
          final String _tmpThumbnailPath;
          if (_stmt.isNull(_columnIndexOfThumbnailPath)) {
            _tmpThumbnailPath = null;
          } else {
            _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath);
          }
          final String _tmpShapeParametersJson;
          if (_stmt.isNull(_columnIndexOfShapeParametersJson)) {
            _tmpShapeParametersJson = null;
          } else {
            _tmpShapeParametersJson = _stmt.getText(_columnIndexOfShapeParametersJson);
          }
          final String _tmpZoneColorsJson;
          if (_stmt.isNull(_columnIndexOfZoneColorsJson)) {
            _tmpZoneColorsJson = null;
          } else {
            _tmpZoneColorsJson = _stmt.getText(_columnIndexOfZoneColorsJson);
          }
          final String _tmpVideoSourcePath;
          if (_stmt.isNull(_columnIndexOfVideoSourcePath)) {
            _tmpVideoSourcePath = null;
          } else {
            _tmpVideoSourcePath = _stmt.getText(_columnIndexOfVideoSourcePath);
          }
          final long _tmpProcessingDurationMs;
          _tmpProcessingDurationMs = _stmt.getLong(_columnIndexOfProcessingDurationMs);
          final int _tmpFramesAnalyzed;
          _tmpFramesAnalyzed = (int) (_stmt.getLong(_columnIndexOfFramesAnalyzed));
          final float _tmpConfidence;
          _tmpConfidence = (float) (_stmt.getDouble(_columnIndexOfConfidence));
          _result = new Avatar3DEntity(_tmpId,_tmpUserId,_tmpCreatedAt,_tmpMeshDataPath,_tmpThumbnailPath,_tmpShapeParametersJson,_tmpZoneColorsJson,_tmpVideoSourcePath,_tmpProcessingDurationMs,_tmpFramesAnalyzed,_tmpConfidence);
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
  public Object getLatestByUserId(final int userId,
      final Continuation<? super Avatar3DEntity> $completion) {
    final String _sql = "SELECT * FROM avatar_3d WHERE userId = ? ORDER BY createdAt DESC LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfMeshDataPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "meshDataPath");
        final int _columnIndexOfThumbnailPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnailPath");
        final int _columnIndexOfShapeParametersJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shapeParametersJson");
        final int _columnIndexOfZoneColorsJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "zoneColorsJson");
        final int _columnIndexOfVideoSourcePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoSourcePath");
        final int _columnIndexOfProcessingDurationMs = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "processingDurationMs");
        final int _columnIndexOfFramesAnalyzed = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "framesAnalyzed");
        final int _columnIndexOfConfidence = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "confidence");
        final Avatar3DEntity _result;
        if (_stmt.step()) {
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpMeshDataPath;
          if (_stmt.isNull(_columnIndexOfMeshDataPath)) {
            _tmpMeshDataPath = null;
          } else {
            _tmpMeshDataPath = _stmt.getText(_columnIndexOfMeshDataPath);
          }
          final String _tmpThumbnailPath;
          if (_stmt.isNull(_columnIndexOfThumbnailPath)) {
            _tmpThumbnailPath = null;
          } else {
            _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath);
          }
          final String _tmpShapeParametersJson;
          if (_stmt.isNull(_columnIndexOfShapeParametersJson)) {
            _tmpShapeParametersJson = null;
          } else {
            _tmpShapeParametersJson = _stmt.getText(_columnIndexOfShapeParametersJson);
          }
          final String _tmpZoneColorsJson;
          if (_stmt.isNull(_columnIndexOfZoneColorsJson)) {
            _tmpZoneColorsJson = null;
          } else {
            _tmpZoneColorsJson = _stmt.getText(_columnIndexOfZoneColorsJson);
          }
          final String _tmpVideoSourcePath;
          if (_stmt.isNull(_columnIndexOfVideoSourcePath)) {
            _tmpVideoSourcePath = null;
          } else {
            _tmpVideoSourcePath = _stmt.getText(_columnIndexOfVideoSourcePath);
          }
          final long _tmpProcessingDurationMs;
          _tmpProcessingDurationMs = _stmt.getLong(_columnIndexOfProcessingDurationMs);
          final int _tmpFramesAnalyzed;
          _tmpFramesAnalyzed = (int) (_stmt.getLong(_columnIndexOfFramesAnalyzed));
          final float _tmpConfidence;
          _tmpConfidence = (float) (_stmt.getDouble(_columnIndexOfConfidence));
          _result = new Avatar3DEntity(_tmpId,_tmpUserId,_tmpCreatedAt,_tmpMeshDataPath,_tmpThumbnailPath,_tmpShapeParametersJson,_tmpZoneColorsJson,_tmpVideoSourcePath,_tmpProcessingDurationMs,_tmpFramesAnalyzed,_tmpConfidence);
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
  public Flow<List<Avatar3DEntity>> getAvatarsByUserId(final int userId) {
    final String _sql = "SELECT * FROM avatar_3d WHERE userId = ? ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"avatar_3d"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfMeshDataPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "meshDataPath");
        final int _columnIndexOfThumbnailPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnailPath");
        final int _columnIndexOfShapeParametersJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shapeParametersJson");
        final int _columnIndexOfZoneColorsJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "zoneColorsJson");
        final int _columnIndexOfVideoSourcePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoSourcePath");
        final int _columnIndexOfProcessingDurationMs = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "processingDurationMs");
        final int _columnIndexOfFramesAnalyzed = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "framesAnalyzed");
        final int _columnIndexOfConfidence = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "confidence");
        final List<Avatar3DEntity> _result = new ArrayList<Avatar3DEntity>();
        while (_stmt.step()) {
          final Avatar3DEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpMeshDataPath;
          if (_stmt.isNull(_columnIndexOfMeshDataPath)) {
            _tmpMeshDataPath = null;
          } else {
            _tmpMeshDataPath = _stmt.getText(_columnIndexOfMeshDataPath);
          }
          final String _tmpThumbnailPath;
          if (_stmt.isNull(_columnIndexOfThumbnailPath)) {
            _tmpThumbnailPath = null;
          } else {
            _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath);
          }
          final String _tmpShapeParametersJson;
          if (_stmt.isNull(_columnIndexOfShapeParametersJson)) {
            _tmpShapeParametersJson = null;
          } else {
            _tmpShapeParametersJson = _stmt.getText(_columnIndexOfShapeParametersJson);
          }
          final String _tmpZoneColorsJson;
          if (_stmt.isNull(_columnIndexOfZoneColorsJson)) {
            _tmpZoneColorsJson = null;
          } else {
            _tmpZoneColorsJson = _stmt.getText(_columnIndexOfZoneColorsJson);
          }
          final String _tmpVideoSourcePath;
          if (_stmt.isNull(_columnIndexOfVideoSourcePath)) {
            _tmpVideoSourcePath = null;
          } else {
            _tmpVideoSourcePath = _stmt.getText(_columnIndexOfVideoSourcePath);
          }
          final long _tmpProcessingDurationMs;
          _tmpProcessingDurationMs = _stmt.getLong(_columnIndexOfProcessingDurationMs);
          final int _tmpFramesAnalyzed;
          _tmpFramesAnalyzed = (int) (_stmt.getLong(_columnIndexOfFramesAnalyzed));
          final float _tmpConfidence;
          _tmpConfidence = (float) (_stmt.getDouble(_columnIndexOfConfidence));
          _item = new Avatar3DEntity(_tmpId,_tmpUserId,_tmpCreatedAt,_tmpMeshDataPath,_tmpThumbnailPath,_tmpShapeParametersJson,_tmpZoneColorsJson,_tmpVideoSourcePath,_tmpProcessingDurationMs,_tmpFramesAnalyzed,_tmpConfidence);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<Avatar3DEntity>> getAllAvatars() {
    final String _sql = "SELECT * FROM avatar_3d ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"avatar_3d"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfMeshDataPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "meshDataPath");
        final int _columnIndexOfThumbnailPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "thumbnailPath");
        final int _columnIndexOfShapeParametersJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shapeParametersJson");
        final int _columnIndexOfZoneColorsJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "zoneColorsJson");
        final int _columnIndexOfVideoSourcePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "videoSourcePath");
        final int _columnIndexOfProcessingDurationMs = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "processingDurationMs");
        final int _columnIndexOfFramesAnalyzed = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "framesAnalyzed");
        final int _columnIndexOfConfidence = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "confidence");
        final List<Avatar3DEntity> _result = new ArrayList<Avatar3DEntity>();
        while (_stmt.step()) {
          final Avatar3DEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final String _tmpMeshDataPath;
          if (_stmt.isNull(_columnIndexOfMeshDataPath)) {
            _tmpMeshDataPath = null;
          } else {
            _tmpMeshDataPath = _stmt.getText(_columnIndexOfMeshDataPath);
          }
          final String _tmpThumbnailPath;
          if (_stmt.isNull(_columnIndexOfThumbnailPath)) {
            _tmpThumbnailPath = null;
          } else {
            _tmpThumbnailPath = _stmt.getText(_columnIndexOfThumbnailPath);
          }
          final String _tmpShapeParametersJson;
          if (_stmt.isNull(_columnIndexOfShapeParametersJson)) {
            _tmpShapeParametersJson = null;
          } else {
            _tmpShapeParametersJson = _stmt.getText(_columnIndexOfShapeParametersJson);
          }
          final String _tmpZoneColorsJson;
          if (_stmt.isNull(_columnIndexOfZoneColorsJson)) {
            _tmpZoneColorsJson = null;
          } else {
            _tmpZoneColorsJson = _stmt.getText(_columnIndexOfZoneColorsJson);
          }
          final String _tmpVideoSourcePath;
          if (_stmt.isNull(_columnIndexOfVideoSourcePath)) {
            _tmpVideoSourcePath = null;
          } else {
            _tmpVideoSourcePath = _stmt.getText(_columnIndexOfVideoSourcePath);
          }
          final long _tmpProcessingDurationMs;
          _tmpProcessingDurationMs = _stmt.getLong(_columnIndexOfProcessingDurationMs);
          final int _tmpFramesAnalyzed;
          _tmpFramesAnalyzed = (int) (_stmt.getLong(_columnIndexOfFramesAnalyzed));
          final float _tmpConfidence;
          _tmpConfidence = (float) (_stmt.getDouble(_columnIndexOfConfidence));
          _item = new Avatar3DEntity(_tmpId,_tmpUserId,_tmpCreatedAt,_tmpMeshDataPath,_tmpThumbnailPath,_tmpShapeParametersJson,_tmpZoneColorsJson,_tmpVideoSourcePath,_tmpProcessingDurationMs,_tmpFramesAnalyzed,_tmpConfidence);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object countByUserId(final int userId, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM avatar_3d WHERE userId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
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
  public Object deleteById(final int id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM avatar_3d WHERE id = ?";
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

  @Override
  public Object deleteAllByUserId(final int userId, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM avatar_3d WHERE userId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM avatar_3d";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
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
