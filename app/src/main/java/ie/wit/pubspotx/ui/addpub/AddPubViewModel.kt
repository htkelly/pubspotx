package ie.wit.pubspotx.ui.addpub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.pubspotx.firebase.FirebaseDBManager
import ie.wit.pubspotx.firebase.FirebaseImageManager
import ie.wit.pubspotx.models.PubModel

class AddPubViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addPub(
        firebaseUser: MutableLiveData<FirebaseUser>,
        pub: PubModel
    ) {
        status.value = try {
            pub.profilepic = FirebaseImageManager.imageUri.value.toString()
            FirebaseDBManager.create(firebaseUser, pub)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}