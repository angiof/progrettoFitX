package com.app.fityo.data_layer.db.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.fityo.data_layer.db.EsserciziEntity
import com.app.fityo.data_layer.db.SchedeEntity
import com.app.fityo.data_layer.db.NotificationEntity
import com.app.fityo.data_layer.db.converters.Converters
import com.app.fityo.data_layer.db.dao.DaoEssercissi
import com.app.fityo.data_layer.db.dao.DaoSchede
import com.app.fityo.data_layer.db.dao.DaoNotifications

@Database(
    entities = [
        EsserciziEntity::class,
        SchedeEntity::class,
        NotificationEntity::class
    ],
    version = 5
)
@TypeConverters(Converters::class)

abstract class DbFit : RoomDatabase() {

    abstract fun essercissiDao(): DaoEssercissi
    abstract fun schedeDao(): DaoSchede
    abstract fun notificationsDao(): DaoNotifications


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

        fun getDatabase(context: Context): DbFit {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DbFit::class.java,
                    "dbFit"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}

