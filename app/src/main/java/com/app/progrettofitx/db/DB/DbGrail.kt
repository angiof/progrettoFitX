package com.app.progrettofitx.db.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.progrettofitx.db.EsserciziEntity
import com.app.progrettofitx.db.dao.DaoEssercissi

@Database(
    entities = [
        EsserciziEntity::class
    ],
    version = 1
)

abstract class DbFit : RoomDatabase() {

    abstract fun essercissiDao(): DaoEssercissi


    companion object {


        @Volatile
        private var INSTANCE: DbFit? = null

        fun getDatabase(context: Context): DbFit {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DbFit::class.java,
                    "dbFit"

                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
