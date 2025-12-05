package com.app.fityo.data_layer.db.DB;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import com.app.fityo.data_layer.db.dao.DaoEssercissi;
import com.app.fityo.data_layer.db.dao.DaoEssercissi_Impl;
import com.app.fityo.data_layer.db.dao.DaoNotifications;
import com.app.fityo.data_layer.db.dao.DaoNotifications_Impl;
import com.app.fityo.data_layer.db.dao.DaoSchede;
import com.app.fityo.data_layer.db.dao.DaoSchede_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class DbFit_Impl extends DbFit {
  private volatile DaoEssercissi _daoEssercissi;

  private volatile DaoSchede _daoSchede;

  private volatile DaoNotifications _daoNotifications;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(5, "d6af434cd5687b3f563e67b5f46fa0b1", "5d0ee82dd1f9864a7310f80004b5738c") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `essercissi` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `nome` TEXT NOT NULL, `attrezzo` TEXT NOT NULL, `nRipetizione` INTEGER NOT NULL, `nSerie` INTEGER NOT NULL, `insometria` INTEGER, `intervallo` INTEGER, `peso` REAL, `completed` INTEGER NOT NULL, `schedaId` INTEGER NOT NULL, FOREIGN KEY(`schedaId`) REFERENCES `schede`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_essercissi_schedaId` ON `essercissi` (`schedaId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `schede` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `gruppoMuscolare` TEXT NOT NULL, `gruppiMuscolari` TEXT, `intesita` TEXT NOT NULL, `titolo` TEXT NOT NULL, `data` TEXT NOT NULL, `notes` TEXT, `ora` TEXT, `favorite` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `completedDate` TEXT, `totalSteps` INTEGER, `avgHeartRate` INTEGER, `maxHeartRate` INTEGER)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `notifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `schedaId` INTEGER, `read` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd6af434cd5687b3f563e67b5f46fa0b1')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `essercissi`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `schede`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `notifications`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsEssercissi = new HashMap<String, TableInfo.Column>(10);
        _columnsEssercissi.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEssercissi.put("nome", new TableInfo.Column("nome", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEssercissi.put("attrezzo", new TableInfo.Column("attrezzo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEssercissi.put("nRipetizione", new TableInfo.Column("nRipetizione", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEssercissi.put("nSerie", new TableInfo.Column("nSerie", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEssercissi.put("insometria", new TableInfo.Column("insometria", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEssercissi.put("intervallo", new TableInfo.Column("intervallo", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEssercissi.put("peso", new TableInfo.Column("peso", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEssercissi.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEssercissi.put("schedaId", new TableInfo.Column("schedaId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysEssercissi = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysEssercissi.add(new TableInfo.ForeignKey("schede", "CASCADE", "NO ACTION", Arrays.asList("schedaId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesEssercissi = new HashSet<TableInfo.Index>(1);
        _indicesEssercissi.add(new TableInfo.Index("index_essercissi_schedaId", false, Arrays.asList("schedaId"), Arrays.asList("ASC")));
        final TableInfo _infoEssercissi = new TableInfo("essercissi", _columnsEssercissi, _foreignKeysEssercissi, _indicesEssercissi);
        final TableInfo _existingEssercissi = TableInfo.read(connection, "essercissi");
        if (!_infoEssercissi.equals(_existingEssercissi)) {
          return new RoomOpenDelegate.ValidationResult(false, "essercissi(com.app.fityo.data_layer.db.EsserciziEntity).\n"
                  + " Expected:\n" + _infoEssercissi + "\n"
                  + " Found:\n" + _existingEssercissi);
        }
        final Map<String, TableInfo.Column> _columnsSchede = new HashMap<String, TableInfo.Column>(14);
        _columnsSchede.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("gruppoMuscolare", new TableInfo.Column("gruppoMuscolare", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("gruppiMuscolari", new TableInfo.Column("gruppiMuscolari", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("intesita", new TableInfo.Column("intesita", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("titolo", new TableInfo.Column("titolo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("data", new TableInfo.Column("data", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("ora", new TableInfo.Column("ora", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("favorite", new TableInfo.Column("favorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("completedDate", new TableInfo.Column("completedDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("totalSteps", new TableInfo.Column("totalSteps", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("avgHeartRate", new TableInfo.Column("avgHeartRate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchede.put("maxHeartRate", new TableInfo.Column("maxHeartRate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysSchede = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesSchede = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSchede = new TableInfo("schede", _columnsSchede, _foreignKeysSchede, _indicesSchede);
        final TableInfo _existingSchede = TableInfo.read(connection, "schede");
        if (!_infoSchede.equals(_existingSchede)) {
          return new RoomOpenDelegate.ValidationResult(false, "schede(com.app.fityo.data_layer.db.SchedeEntity).\n"
                  + " Expected:\n" + _infoSchede + "\n"
                  + " Found:\n" + _existingSchede);
        }
        final Map<String, TableInfo.Column> _columnsNotifications = new HashMap<String, TableInfo.Column>(6);
        _columnsNotifications.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("schedaId", new TableInfo.Column("schedaId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("read", new TableInfo.Column("read", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysNotifications = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesNotifications = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotifications = new TableInfo("notifications", _columnsNotifications, _foreignKeysNotifications, _indicesNotifications);
        final TableInfo _existingNotifications = TableInfo.read(connection, "notifications");
        if (!_infoNotifications.equals(_existingNotifications)) {
          return new RoomOpenDelegate.ValidationResult(false, "notifications(com.app.fityo.data_layer.db.NotificationEntity).\n"
                  + " Expected:\n" + _infoNotifications + "\n"
                  + " Found:\n" + _existingNotifications);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "essercissi", "schede", "notifications");
  }

  @Override
  public void clearAllTables() {
    super.performClear(true, "essercissi", "schede", "notifications");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DaoEssercissi.class, DaoEssercissi_Impl.getRequiredConverters());
    _typeConvertersMap.put(DaoSchede.class, DaoSchede_Impl.getRequiredConverters());
    _typeConvertersMap.put(DaoNotifications.class, DaoNotifications_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DaoEssercissi essercissiDao() {
    if (_daoEssercissi != null) {
      return _daoEssercissi;
    } else {
      synchronized(this) {
        if(_daoEssercissi == null) {
          _daoEssercissi = new DaoEssercissi_Impl(this);
        }
        return _daoEssercissi;
      }
    }
  }

  @Override
  public DaoSchede schedeDao() {
    if (_daoSchede != null) {
      return _daoSchede;
    } else {
      synchronized(this) {
        if(_daoSchede == null) {
          _daoSchede = new DaoSchede_Impl(this);
        }
        return _daoSchede;
      }
    }
  }

  @Override
  public DaoNotifications notificationsDao() {
    if (_daoNotifications != null) {
      return _daoNotifications;
    } else {
      synchronized(this) {
        if(_daoNotifications == null) {
          _daoNotifications = new DaoNotifications_Impl(this);
        }
        return _daoNotifications;
      }
    }
  }
}
