package ie.wit.pubspotx.room

import android.content.Context
import androidx.room.Room
import ie.wit.pubspotx.models.PreferencesModel
import ie.wit.pubspotx.models.PreferencesStore
import timber.log.Timber

class PreferencesStoreRoom(val context: Context) : PreferencesStore {

    var dao: PreferencesDao

    init {
        val database =
            Room.databaseBuilder(context, Database::class.java, "pubspotx_preferences.db")
                .fallbackToDestructiveMigration()
                .build()
        dao = database.preferencesDao()
    }

    override suspend fun findAll(): List<PreferencesModel> {
        return dao.findAll()
    }

    override suspend fun findByUserId(userid: String): PreferencesModel? {
        return dao.findByUserId(userid)
    }

    override suspend fun create(preferences: PreferencesModel) {
        dao.create(preferences)
        Timber.i("Created $preferences")
    }

    override suspend fun delete(preferences: PreferencesModel) {
        dao.deletePreferences(preferences)
    }

    override suspend fun update(preferences: PreferencesModel) {
        dao.update(preferences)
        Timber.i("Updated $preferences")
    }
}