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
import com.app.fityo.data_layer.db.dao.DaoAvatar3D;
import com.app.fityo.data_layer.db.dao.DaoAvatar3D_Impl;
import com.app.fityo.data_layer.db.dao.DaoEssercissi;
import com.app.fityo.data_layer.db.dao.DaoEssercissi_Impl;
import com.app.fityo.data_layer.db.dao.DaoMuscleCompare;
import com.app.fityo.data_layer.db.dao.DaoMuscleCompare_Impl;
import com.app.fityo.data_layer.db.dao.DaoNotifications;
import com.app.fityo.data_layer.db.dao.DaoNotifications_Impl;
import com.app.fityo.data_layer.db.dao.DaoSchede;
import com.app.fityo.data_layer.db.dao.DaoSchede_Impl;
import com.app.fityo.data_layer.db.dao.DaoTutorSession;
import com.app.fityo.data_layer.db.dao.DaoTutorSession_Impl;
import com.app.fityo.data_layer.db.dao.DaoUserProfile;
import com.app.fityo.data_layer.db.dao.DaoUserProfile_Impl;
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

  private volatile DaoMuscleCompare _daoMuscleCompare;

  private volatile DaoTutorSession _daoTutorSession;

  private volatile DaoUserProfile _daoUserProfile;

  private volatile DaoAvatar3D _daoAvatar3D;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(9, "e0cd17566a2b2450e6b2e7bbf2133609", "6cdc31061a5dcaa25bb8083c9a61e553") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `essercissi` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `nome` TEXT NOT NULL, `attrezzo` TEXT NOT NULL, `nRipetizione` INTEGER NOT NULL, `nSerie` INTEGER NOT NULL, `insometria` INTEGER, `intervallo` INTEGER, `peso` REAL, `completed` INTEGER NOT NULL, `schedaId` INTEGER NOT NULL, FOREIGN KEY(`schedaId`) REFERENCES `schede`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_essercissi_schedaId` ON `essercissi` (`schedaId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `schede` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `gruppoMuscolare` TEXT NOT NULL, `gruppiMuscolari` TEXT, `intesita` TEXT NOT NULL, `titolo` TEXT NOT NULL, `data` TEXT NOT NULL, `notes` TEXT, `ora` TEXT, `favorite` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `completedDate` TEXT, `totalSteps` INTEGER, `avgHeartRate` INTEGER, `maxHeartRate` INTEGER)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `notifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `schedaId` INTEGER, `read` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `muscle_compare` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `createdAt` INTEGER NOT NULL, `photoAPath` TEXT NOT NULL, `photoBPath` TEXT NOT NULL, `armsVariation` REAL NOT NULL, `absVariation` REAL NOT NULL, `legsVariation` REAL NOT NULL, `glutesVariation` REAL NOT NULL, `notes` TEXT, `photoADate` TEXT, `photoBDate` TEXT, `scaleFactorA` REAL NOT NULL, `scaleFactorB` REAL NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `tutor_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `createdAt` INTEGER NOT NULL, `exerciseType` TEXT NOT NULL, `videoPath` TEXT NOT NULL, `thumbnailPath` TEXT, `duration` INTEGER NOT NULL, `totalErrors` INTEGER NOT NULL, `overallScore` REAL NOT NULL, `errorsJson` TEXT NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `user_profile` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `age` INTEGER NOT NULL, `heightCm` REAL NOT NULL, `weightKg` REAL NOT NULL, `sex` TEXT NOT NULL, `discipline` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `avatar_3d` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userId` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `meshDataPath` TEXT NOT NULL, `thumbnailPath` TEXT, `shapeParametersJson` TEXT NOT NULL, `zoneColorsJson` TEXT NOT NULL, `videoSourcePath` TEXT, `processingDurationMs` INTEGER NOT NULL, `framesAnalyzed` INTEGER NOT NULL, `confidence` REAL NOT NULL, FOREIGN KEY(`userId`) REFERENCES `user_profile`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_avatar_3d_userId` ON `avatar_3d` (`userId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e0cd17566a2b2450e6b2e7bbf2133609')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `essercissi`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `schede`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `notifications`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `muscle_compare`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `tutor_sessions`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `user_profile`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `avatar_3d`");
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
        final Map<String, TableInfo.Column> _columnsMuscleCompare = new HashMap<String, TableInfo.Column>(13);
        _columnsMuscleCompare.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("photoAPath", new TableInfo.Column("photoAPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("photoBPath", new TableInfo.Column("photoBPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("armsVariation", new TableInfo.Column("armsVariation", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("absVariation", new TableInfo.Column("absVariation", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("legsVariation", new TableInfo.Column("legsVariation", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("glutesVariation", new TableInfo.Column("glutesVariation", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("photoADate", new TableInfo.Column("photoADate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("photoBDate", new TableInfo.Column("photoBDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("scaleFactorA", new TableInfo.Column("scaleFactorA", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMuscleCompare.put("scaleFactorB", new TableInfo.Column("scaleFactorB", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysMuscleCompare = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesMuscleCompare = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMuscleCompare = new TableInfo("muscle_compare", _columnsMuscleCompare, _foreignKeysMuscleCompare, _indicesMuscleCompare);
        final TableInfo _existingMuscleCompare = TableInfo.read(connection, "muscle_compare");
        if (!_infoMuscleCompare.equals(_existingMuscleCompare)) {
          return new RoomOpenDelegate.ValidationResult(false, "muscle_compare(com.app.fityo.data_layer.db.MuscleCompareEntity).\n"
                  + " Expected:\n" + _infoMuscleCompare + "\n"
                  + " Found:\n" + _existingMuscleCompare);
        }
        final Map<String, TableInfo.Column> _columnsTutorSessions = new HashMap<String, TableInfo.Column>(9);
        _columnsTutorSessions.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorSessions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorSessions.put("exerciseType", new TableInfo.Column("exerciseType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorSessions.put("videoPath", new TableInfo.Column("videoPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorSessions.put("thumbnailPath", new TableInfo.Column("thumbnailPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorSessions.put("duration", new TableInfo.Column("duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorSessions.put("totalErrors", new TableInfo.Column("totalErrors", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorSessions.put("overallScore", new TableInfo.Column("overallScore", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorSessions.put("errorsJson", new TableInfo.Column("errorsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysTutorSessions = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesTutorSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTutorSessions = new TableInfo("tutor_sessions", _columnsTutorSessions, _foreignKeysTutorSessions, _indicesTutorSessions);
        final TableInfo _existingTutorSessions = TableInfo.read(connection, "tutor_sessions");
        if (!_infoTutorSessions.equals(_existingTutorSessions)) {
          return new RoomOpenDelegate.ValidationResult(false, "tutor_sessions(com.app.fityo.data_layer.db.TutorSessionEntity).\n"
                  + " Expected:\n" + _infoTutorSessions + "\n"
                  + " Found:\n" + _existingTutorSessions);
        }
        final Map<String, TableInfo.Column> _columnsUserProfile = new HashMap<String, TableInfo.Column>(9);
        _columnsUserProfile.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("age", new TableInfo.Column("age", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("heightCm", new TableInfo.Column("heightCm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("weightKg", new TableInfo.Column("weightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("sex", new TableInfo.Column("sex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("discipline", new TableInfo.Column("discipline", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysUserProfile = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesUserProfile = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserProfile = new TableInfo("user_profile", _columnsUserProfile, _foreignKeysUserProfile, _indicesUserProfile);
        final TableInfo _existingUserProfile = TableInfo.read(connection, "user_profile");
        if (!_infoUserProfile.equals(_existingUserProfile)) {
          return new RoomOpenDelegate.ValidationResult(false, "user_profile(com.app.fityo.data_layer.db.UserProfileEntity).\n"
                  + " Expected:\n" + _infoUserProfile + "\n"
                  + " Found:\n" + _existingUserProfile);
        }
        final Map<String, TableInfo.Column> _columnsAvatar3d = new HashMap<String, TableInfo.Column>(11);
        _columnsAvatar3d.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAvatar3d.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAvatar3d.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAvatar3d.put("meshDataPath", new TableInfo.Column("meshDataPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAvatar3d.put("thumbnailPath", new TableInfo.Column("thumbnailPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAvatar3d.put("shapeParametersJson", new TableInfo.Column("shapeParametersJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAvatar3d.put("zoneColorsJson", new TableInfo.Column("zoneColorsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAvatar3d.put("videoSourcePath", new TableInfo.Column("videoSourcePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAvatar3d.put("processingDurationMs", new TableInfo.Column("processingDurationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAvatar3d.put("framesAnalyzed", new TableInfo.Column("framesAnalyzed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAvatar3d.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysAvatar3d = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysAvatar3d.add(new TableInfo.ForeignKey("user_profile", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesAvatar3d = new HashSet<TableInfo.Index>(1);
        _indicesAvatar3d.add(new TableInfo.Index("index_avatar_3d_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoAvatar3d = new TableInfo("avatar_3d", _columnsAvatar3d, _foreignKeysAvatar3d, _indicesAvatar3d);
        final TableInfo _existingAvatar3d = TableInfo.read(connection, "avatar_3d");
        if (!_infoAvatar3d.equals(_existingAvatar3d)) {
          return new RoomOpenDelegate.ValidationResult(false, "avatar_3d(com.app.fityo.data_layer.db.Avatar3DEntity).\n"
                  + " Expected:\n" + _infoAvatar3d + "\n"
                  + " Found:\n" + _existingAvatar3d);
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
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "essercissi", "schede", "notifications", "muscle_compare", "tutor_sessions", "user_profile", "avatar_3d");
  }

  @Override
  public void clearAllTables() {
    super.performClear(true, "essercissi", "schede", "notifications", "muscle_compare", "tutor_sessions", "user_profile", "avatar_3d");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DaoEssercissi.class, DaoEssercissi_Impl.getRequiredConverters());
    _typeConvertersMap.put(DaoSchede.class, DaoSchede_Impl.getRequiredConverters());
    _typeConvertersMap.put(DaoNotifications.class, DaoNotifications_Impl.getRequiredConverters());
    _typeConvertersMap.put(DaoMuscleCompare.class, DaoMuscleCompare_Impl.getRequiredConverters());
    _typeConvertersMap.put(DaoTutorSession.class, DaoTutorSession_Impl.getRequiredConverters());
    _typeConvertersMap.put(DaoUserProfile.class, DaoUserProfile_Impl.getRequiredConverters());
    _typeConvertersMap.put(DaoAvatar3D.class, DaoAvatar3D_Impl.getRequiredConverters());
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

  @Override
  public DaoMuscleCompare muscleCompareDao() {
    if (_daoMuscleCompare != null) {
      return _daoMuscleCompare;
    } else {
      synchronized(this) {
        if(_daoMuscleCompare == null) {
          _daoMuscleCompare = new DaoMuscleCompare_Impl(this);
        }
        return _daoMuscleCompare;
      }
    }
  }

  @Override
  public DaoTutorSession tutorSessionDao() {
    if (_daoTutorSession != null) {
      return _daoTutorSession;
    } else {
      synchronized(this) {
        if(_daoTutorSession == null) {
          _daoTutorSession = new DaoTutorSession_Impl(this);
        }
        return _daoTutorSession;
      }
    }
  }

  @Override
  public DaoUserProfile userProfileDao() {
    if (_daoUserProfile != null) {
      return _daoUserProfile;
    } else {
      synchronized(this) {
        if(_daoUserProfile == null) {
          _daoUserProfile = new DaoUserProfile_Impl(this);
        }
        return _daoUserProfile;
      }
    }
  }

  @Override
  public DaoAvatar3D avatar3dDao() {
    if (_daoAvatar3D != null) {
      return _daoAvatar3D;
    } else {
      synchronized(this) {
        if(_daoAvatar3D == null) {
          _daoAvatar3D = new DaoAvatar3D_Impl(this);
        }
        return _daoAvatar3D;
      }
    }
  }
}
