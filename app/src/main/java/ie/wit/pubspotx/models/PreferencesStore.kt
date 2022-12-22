package ie.wit.pubspotx.models

interface PreferencesStore {
    suspend fun findAll(): List<PreferencesModel>
    suspend fun findByUserId(userid: String): PreferencesModel?
    suspend fun create(preferences: PreferencesModel)
    suspend fun delete(preferences: PreferencesModel)
    suspend fun update(preferences: PreferencesModel)
}