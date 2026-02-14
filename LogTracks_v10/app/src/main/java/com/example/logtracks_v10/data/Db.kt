package com.example.logtracks_v10.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [MetricEntity::class, DailyValueEntity::class, CategoryEntity::class],
    version = 2,
    exportSchema = true
)
abstract class TrackerDb : RoomDatabase() {
    abstract fun dao(): TrackerDao

    companion object {
        @Volatile private var INSTANCE: TrackerDb? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `CategoryEntity` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `order` INTEGER NOT NULL,
                `active` INTEGER NOT NULL
            )
        """.trimIndent())

                db.execSQL("ALTER TABLE `MetricEntity` ADD COLUMN `categoryId` INTEGER")

                // Catégories par défaut (optionnel mais utile)
                db.execSQL("INSERT INTO CategoryEntity(name, `order`, active) VALUES ('Décisions', 1, 1)")
                db.execSQL("INSERT INTO CategoryEntity(name, `order`, active) VALUES ('Finances', 2, 1)")
                db.execSQL("INSERT INTO CategoryEntity(name, `order`, active) VALUES ('Sport', 3, 1)")
            }
        }

        fun get(context: Context): TrackerDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TrackerDb::class.java,
                    "tracker.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
