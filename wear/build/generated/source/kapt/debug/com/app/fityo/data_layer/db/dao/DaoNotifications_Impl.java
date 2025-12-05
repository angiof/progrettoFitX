package com.app.fityo.data_layer.db.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.app.fityo.data_layer.db.NotificationEntity;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class DaoNotifications_Impl implements DaoNotifications {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<NotificationEntity> __insertAdapterOfNotificationEntity;

  private final EntityDeleteOrUpdateAdapter<NotificationEntity> __deleteAdapterOfNotificationEntity;

  private final EntityDeleteOrUpdateAdapter<NotificationEntity> __updateAdapterOfNotificationEntity;

  public DaoNotifications_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfNotificationEntity = new EntityInsertAdapter<NotificationEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `notifications` (`id`,`title`,`message`,`timestamp`,`schedaId`,`read`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final NotificationEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getMessage() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getMessage());
        }
        statement.bindLong(4, entity.getTimestamp());
        if (entity.getSchedaId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getSchedaId());
        }
        final int _tmp = entity.getRead() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__deleteAdapterOfNotificationEntity = new EntityDeleteOrUpdateAdapter<NotificationEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `notifications` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final NotificationEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfNotificationEntity = new EntityDeleteOrUpdateAdapter<NotificationEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `notifications` SET `id` = ?,`title` = ?,`message` = ?,`timestamp` = ?,`schedaId` = ?,`read` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final NotificationEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getMessage() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getMessage());
        }
        statement.bindLong(4, entity.getTimestamp());
        if (entity.getSchedaId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getSchedaId());
        }
        final int _tmp = entity.getRead() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getId());
        }
      }
    };
  }

  @Override
  public Object insert(final NotificationEntity notification,
      final Continuation<? super Long> $completion) {
    if (notification == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfNotificationEntity.insertAndReturnId(_connection, notification);
    }, $completion);
  }

  @Override
  public Object delete(final NotificationEntity notification,
      final Continuation<? super Unit> $completion) {
    if (notification == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfNotificationEntity.handle(_connection, notification);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object update(final NotificationEntity notification,
      final Continuation<? super Unit> $completion) {
    if (notification == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfNotificationEntity.handle(_connection, notification);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public LiveData<List<NotificationEntity>> getAllNotifications() {
    final String _sql = "SELECT * FROM notifications ORDER BY timestamp DESC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"notifications"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final int _columnIndexOfSchedaId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "schedaId");
        final int _columnIndexOfRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "read");
        final List<NotificationEntity> _result = new ArrayList<NotificationEntity>();
        while (_stmt.step()) {
          final NotificationEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpMessage;
          if (_stmt.isNull(_columnIndexOfMessage)) {
            _tmpMessage = null;
          } else {
            _tmpMessage = _stmt.getText(_columnIndexOfMessage);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final Integer _tmpSchedaId;
          if (_stmt.isNull(_columnIndexOfSchedaId)) {
            _tmpSchedaId = null;
          } else {
            _tmpSchedaId = (int) (_stmt.getLong(_columnIndexOfSchedaId));
          }
          final boolean _tmpRead;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfRead));
          _tmpRead = _tmp != 0;
          _item = new NotificationEntity(_tmpId,_tmpTitle,_tmpMessage,_tmpTimestamp,_tmpSchedaId,_tmpRead);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<NotificationEntity>> getUnreadNotifications() {
    final String _sql = "SELECT * FROM notifications WHERE read = 0 ORDER BY timestamp DESC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"notifications"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final int _columnIndexOfSchedaId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "schedaId");
        final int _columnIndexOfRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "read");
        final List<NotificationEntity> _result = new ArrayList<NotificationEntity>();
        while (_stmt.step()) {
          final NotificationEntity _item;
          final Integer _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpMessage;
          if (_stmt.isNull(_columnIndexOfMessage)) {
            _tmpMessage = null;
          } else {
            _tmpMessage = _stmt.getText(_columnIndexOfMessage);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final Integer _tmpSchedaId;
          if (_stmt.isNull(_columnIndexOfSchedaId)) {
            _tmpSchedaId = null;
          } else {
            _tmpSchedaId = (int) (_stmt.getLong(_columnIndexOfSchedaId));
          }
          final boolean _tmpRead;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfRead));
          _tmpRead = _tmp != 0;
          _item = new NotificationEntity(_tmpId,_tmpTitle,_tmpMessage,_tmpTimestamp,_tmpSchedaId,_tmpRead);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<Integer> getUnreadCount() {
    final String _sql = "SELECT COUNT(*) FROM notifications WHERE read = 0";
    return __db.getInvalidationTracker().createLiveData(new String[] {"notifications"}, false, (_connection) -> {
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
    });
  }

  @Override
  public Object markAsRead(final int id, final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE notifications SET read = 1 WHERE id = ?";
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
  public Object deleteOlderThan(final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM notifications WHERE timestamp < ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
