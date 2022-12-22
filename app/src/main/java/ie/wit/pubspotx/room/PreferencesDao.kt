package ie.wit.pubspotx.room

import androidx.room.*
import ie.wit.pubspotx.models.PreferencesModel

@Dao
interface PreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(preferences: PreferencesModel)

    @Query("SELECT * FROM PreferencesModel")
    suspend fun findAll(): List<PreferencesModel>

    @Query("select * from PreferencesModel where userid = :userid")
    suspend fun findByUserId(userid: String): PreferencesModel

    @Update
    suspend fun update(preferences: PreferencesModel)

    @Delete
    suspend fun deletePreferences(preferences: PreferencesModel)
}