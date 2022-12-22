package ie.wit.pubspotx.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ie.wit.pubspotx.models.PreferencesModel

@Database(entities = arrayOf(PreferencesModel::class), version = 1, exportSchema = false)
abstract class Database: RoomDatabase() {
    abstract fun preferencesDao(): PreferencesDao
}