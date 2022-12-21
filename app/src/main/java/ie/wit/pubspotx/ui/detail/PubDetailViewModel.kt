package ie.wit.pubspotx.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.pubspotx.firebase.FirebaseDBManager
import ie.wit.pubspotx.models.PubModel
import timber.log.Timber

class PubDetailViewModel : ViewModel() {
    private val pub = MutableLiveData<PubModel>()

    var observablePub: LiveData<PubModel>
        get() = pub
        set(value) {
            pub.value = value.value
        }

    fun getPub(userid: String, id: String) {
        try {
            FirebaseDBManager.findById(userid, id, pub)
            Timber.i(
                "Detail getPub() Success : ${
                    pub.value.toString()
                }"
            )
        } catch (e: Exception) {
            Timber.i("Detail getPub() Error : $e.message")
        }
    }

    fun updatePub(userid: String, id: String, pub: PubModel) {
        try {
            FirebaseDBManager.update(userid, id, pub)
            Timber.i("Detail update() Success : $pub")
        } catch (e: Exception) {
            Timber.i("Detail update() Error : $e.message")
        }
    }
}

