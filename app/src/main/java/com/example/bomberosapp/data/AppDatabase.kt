package com.example.bomberosapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bomberosapp.data.dao.PersonalDao
import com.example.bomberosapp.data.model.Personal

@Database(entities = [Personal::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personalDao(): PersonalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bomberos_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
