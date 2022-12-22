package ie.wit.pubspotx.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class PreferencesModel(
    @PrimaryKey var userid: String = "",
    var theme: Int = -1
) : Parcelable