package com.example.cinemaster.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Movie::class, User::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cinemaster.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .addCallback(SeedCallback())
                .build().also { INSTANCE = it }
            }

        /** Chạy khi cài mới (onCreate): seed tài khoản demo và phim mẫu */
        private class SeedCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                seedDemoUserAndMovies(db)
            }
        }

        /** Nâng cấp từ v1 → v2: tạo bảng users + seed user demo */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `users` (
                        `id`           INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name`         TEXT    NOT NULL,
                        `email`        TEXT    NOT NULL,
                        `passwordHash` TEXT    NOT NULL,
                        `createdAt`    INTEGER NOT NULL
                    )"""
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_users_email` ON `users` (`email`)"
                )
                seedDemoUserAndMovies(db)
            }
        }

        /** Nâng cấp từ v2 → v3: đổi bảng movies sang composite primary key (id, userId) để tách rời dữ liệu */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Tạo bảng tạm với cấu trúc mới
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `movies_new` (
                        `id`         INTEGER NOT NULL,
                        `userId`     INTEGER NOT NULL,
                        `title`      TEXT    NOT NULL,
                        `posterPath` TEXT,
                        `overview`   TEXT    NOT NULL,
                        `status`     TEXT    NOT NULL,
                        `rating`     REAL,
                        `note`       TEXT    NOT NULL,
                        `addedAt`    INTEGER NOT NULL,
                        PRIMARY KEY(`id`, `userId`)
                    )"""
                )

                // 2. Chuyển toàn bộ dữ liệu phim cũ sang gán cho tài khoản Khách (userId = -1)
                db.execSQL(
                    """INSERT INTO `movies_new` (`id`, `userId`, `title`, `posterPath`, `overview`, `status`, `rating`, `note`, `addedAt`)
                       SELECT `id`, -1, `title`, `posterPath`, `overview`, `status`, `rating`, `note`, `addedAt` FROM `movies`"""
                )

                // 3. Xóa bảng cũ và đổi tên bảng mới
                db.execSQL("DROP TABLE `movies`")
                db.execSQL("ALTER TABLE `movies_new` RENAME TO `movies`")
            }
        }

        private fun seedDemoUserAndMovies(db: SupportSQLiteDatabase) {
            val hash = AuthRepository.hashPassword("demo1234")
            db.execSQL(
                """INSERT OR IGNORE INTO `users` (`id`, `name`, `email`, `passwordHash`, `createdAt`)
                   VALUES (1, 'Người Dùng Mẫu', 'demo@cinemaster.app', '$hash', ${System.currentTimeMillis()})"""
            )
        }
    }
}
