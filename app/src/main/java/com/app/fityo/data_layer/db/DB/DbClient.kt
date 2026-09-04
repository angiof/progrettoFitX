package com.app.fityo.data_layer.db.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.fityo.data_layer.db.Avatar3DEntity
import com.app.fityo.data_layer.db.ChatMessageEntity
import com.app.fityo.data_layer.db.CustomValueEntity
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.NotificationEntity
import com.app.fityo.data_layer.db.MuscleCompareEntity
import com.app.fityo.data_layer.db.TutorSessionEntity
import com.app.fityo.data_layer.db.UserProfileEntity
import com.app.fityo.data_layer.db.CoachProfileEntity
import com.app.fityo.data_layer.db.CoachAppointmentEntity
import com.app.fityo.data_layer.db.DailyNutritionEntity
import com.app.fityo.data_layer.db.DailyNutritionItemEntity
import com.app.fityo.data_layer.db.converters.Converters
import com.app.fityo.data_layer.db.dao.DaoAvatar3D
import com.app.fityo.data_layer.db.dao.DaoChat
import com.app.fityo.data_layer.db.dao.DaoCustomValue
import com.app.fityo.data_layer.db.dao.DaoEssercissi
import com.app.fityo.data_layer.db.dao.DaoSchede
import com.app.fityo.data_layer.db.dao.DaoNotifications
import com.app.fityo.data_layer.db.dao.DaoMuscleCompare
import com.app.fityo.data_layer.db.dao.DaoTutorSession
import com.app.fityo.data_layer.db.dao.DaoUserProfile
import com.app.fityo.data_layer.db.dao.DaoCoachProfile
import com.app.fityo.data_layer.db.dao.DaoCoachAppointment
import com.app.fityo.data_layer.db.dao.DaoDailyNutrition
import com.app.fityo.data_layer.db.dao.DaoDailyNutritionItem

@Database(
    entities = [
        EsserciziEntity::class,
        SchedeEntity::class,
        NotificationEntity::class,
        MuscleCompareEntity::class,
        TutorSessionEntity::class,
        UserProfileEntity::class,
        Avatar3DEntity::class,
        CoachProfileEntity::class,
        CoachAppointmentEntity::class,
        DailyNutritionEntity::class,
        DailyNutritionItemEntity::class,
        ChatMessageEntity::class,
        CustomValueEntity::class
    ],
    version = 18
)
@TypeConverters(Converters::class)

abstract class DbFit : RoomDatabase() {

    abstract fun essercissiDao(): DaoEssercissi
    abstract fun schedeDao(): DaoSchede
    abstract fun notificationsDao(): DaoNotifications
    abstract fun muscleCompareDao(): DaoMuscleCompare
    abstract fun tutorSessionDao(): DaoTutorSession
    abstract fun userProfileDao(): DaoUserProfile
    abstract fun avatar3dDao(): DaoAvatar3D
    abstract fun coachProfileDao(): DaoCoachProfile
    abstract fun coachAppointmentDao(): DaoCoachAppointment
    abstract fun chatDao(): DaoChat
    abstract fun dailyNutritionDao(): DaoDailyNutrition
    abstract fun dailyNutritionItemDao(): DaoDailyNutritionItem
    abstract fun customValueDao(): DaoCustomValue


    companion object {

        @Volatile
        private var INSTANCE: DbFit? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Aggiungi colonna peso a essercissi
                database.execSQL("ALTER TABLE essercissi ADD COLUMN peso REAL DEFAULT NULL")
                // Aggiungi colonna gruppiMuscolari a schede
                database.execSQL("ALTER TABLE schede ADD COLUMN gruppiMuscolari TEXT DEFAULT NULL")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crea tabella notifiche
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS notifications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        message TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        schedaId INTEGER,
                        read INTEGER NOT NULL DEFAULT 0
                    )
                """)
                // Crea indice per schedaId in essercissi per migliorare performance con foreign key
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_essercissi_schedaId
                    ON essercissi(schedaId)
                """)
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Aggiungi campo completed agli esercizi
                database.execSQL("ALTER TABLE essercissi ADD COLUMN completed INTEGER NOT NULL DEFAULT 0")
                // Aggiungi campi completed e completedDate alle schede
                database.execSQL("ALTER TABLE schede ADD COLUMN completed INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE schede ADD COLUMN completedDate TEXT DEFAULT NULL")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Aggiungi campi fitness tracking alle schede
                database.execSQL("ALTER TABLE schede ADD COLUMN totalSteps INTEGER DEFAULT NULL")
                database.execSQL("ALTER TABLE schede ADD COLUMN avgHeartRate INTEGER DEFAULT NULL")
                database.execSQL("ALTER TABLE schede ADD COLUMN maxHeartRate INTEGER DEFAULT NULL")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crea tabella confronti muscolari
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS muscle_compare (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        createdAt INTEGER NOT NULL,
                        photoAPath TEXT NOT NULL,
                        photoBPath TEXT NOT NULL,
                        armsVariation REAL NOT NULL,
                        absVariation REAL NOT NULL,
                        legsVariation REAL NOT NULL,
                        glutesVariation REAL NOT NULL,
                        notes TEXT,
                        photoADate TEXT,
                        photoBDate TEXT,
                        scaleFactorA REAL NOT NULL DEFAULT 1.0,
                        scaleFactorB REAL NOT NULL DEFAULT 1.0
                    )
                """)
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crea tabella sessioni Tutor per analisi esercizi
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS tutor_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        createdAt INTEGER NOT NULL,
                        exerciseType TEXT NOT NULL,
                        videoPath TEXT NOT NULL,
                        thumbnailPath TEXT,
                        duration INTEGER NOT NULL,
                        totalErrors INTEGER NOT NULL,
                        overallScore REAL NOT NULL,
                        errorsJson TEXT NOT NULL
                    )
                """)
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crea tabella profilo utente per Body Intelligence
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_profile (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        age INTEGER NOT NULL,
                        heightCm REAL NOT NULL,
                        weightKg REAL NOT NULL,
                        sex TEXT NOT NULL,
                        discipline TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crea tabella avatar 3D per Body Intelligence
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS avatar_3d (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        userId INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        meshDataPath TEXT NOT NULL,
                        thumbnailPath TEXT,
                        shapeParametersJson TEXT NOT NULL,
                        zoneColorsJson TEXT NOT NULL,
                        videoSourcePath TEXT,
                        processingDurationMs INTEGER NOT NULL,
                        framesAnalyzed INTEGER NOT NULL,
                        confidence REAL NOT NULL,
                        FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE
                    )
                """)
                // Crea indice per userId
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_avatar_3d_userId
                    ON avatar_3d(userId)
                """)
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crea tabella profili coach
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS coach_profiles (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        avatarColor INTEGER NOT NULL,
                        notes TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                // Aggiungi colonna coachProfileId a schede (nullable FK)
                database.execSQL(
                    "ALTER TABLE schede ADD COLUMN coachProfileId INTEGER DEFAULT NULL"
                )
                // Crea indice per performance
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_schede_coachProfileId
                    ON schede(coachProfileId)
                """)
            }
        }

        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crea tabella appuntamenti coach
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS coach_appointments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        profileId INTEGER NOT NULL,
                        date TEXT NOT NULL,
                        time TEXT,
                        title TEXT NOT NULL,
                        notes TEXT,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        schedeId INTEGER,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY (profileId) REFERENCES coach_profiles(id) ON DELETE CASCADE
                    )
                """)
                // Crea indice per profileId
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_coach_appointments_profileId
                    ON coach_appointments(profileId)
                """)
            }
        }

        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Aggiungi campo notes agli esercizi per note specifiche
                database.execSQL("ALTER TABLE essercissi ADD COLUMN notes TEXT DEFAULT NULL")
            }
        }

        private val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crea tabella chat messages per Body Intelligence Chatbot
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS chat_messages (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        text TEXT NOT NULL,
                        isFromUser INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL,
                        intentType TEXT,
                        profileId INTEGER,
                        sessionId TEXT
                    )
                """)
            }
        }

        private val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crea tabella nutrizione giornaliera
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_nutrition (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        date TEXT NOT NULL,
                        totalProteins REAL NOT NULL,
                        totalCarbs REAL NOT NULL,
                        totalFats REAL NOT NULL,
                        totalKcal REAL NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                database.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_daily_nutrition_date
                    ON daily_nutrition(date)
                """)
            }
        }

        private val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crea tabella elementi nutrizionali giornalieri
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_nutrition_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        date TEXT NOT NULL,
                        name TEXT NOT NULL,
                        grams REAL NOT NULL,
                        proteins REAL NOT NULL,
                        carbs REAL NOT NULL,
                        fats REAL NOT NULL,
                        kcal REAL NOT NULL,
                        source TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """)
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_daily_nutrition_items_date
                    ON daily_nutrition_items(date)
                """)
            }
        }

        private val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE daily_nutrition ADD COLUMN totalFibers REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE daily_nutrition ADD COLUMN totalSugars REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE daily_nutrition ADD COLUMN totalSaturatedFats REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE daily_nutrition ADD COLUMN totalSalt REAL NOT NULL DEFAULT 0")

                database.execSQL("ALTER TABLE daily_nutrition_items ADD COLUMN fibers REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE daily_nutrition_items ADD COLUMN sugars REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE daily_nutrition_items ADD COLUMN saturatedFats REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE daily_nutrition_items ADD COLUMN salt REAL NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE essercissi ADD COLUMN wgerId INTEGER")
            }
        }


        private val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS custom_values (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        type TEXT NOT NULL,
                        value TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """)
                database.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_custom_values_type_value
                    ON custom_values(type, value)
                """)
            }
        }

        fun getDatabase(context: Context): DbFit {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DbFit::class.java,
                    "dbFit"
                )
                    // Le migration precedenti non sono mai state registrate: aggiungiamo almeno
                    // questa cosi chi e gia alla 17 non perde le schede passando alla 18.
                    .addMigrations(MIGRATION_17_18)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}

