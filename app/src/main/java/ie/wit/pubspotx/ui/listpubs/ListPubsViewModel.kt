package ie.wit.pubspotx.ui.listpubs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.pubspotx.firebase.FirebaseDBManager
import ie.wit.pubspotx.models.PubModel
import timber.log.Timber

class ListPubsViewModel : ViewModel() {

    private val pubsList =
        MutableLiveData<List<PubModel>>()

    val observablePubsList: LiveData<List<PubModel>>
        get() = pubsList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    var readOnly = MutableLiveData(false)

    init {
        load()
    }

    fun load() {
        try {
            readOnly.value = false
            FirebaseDBManager.findAll(liveFirebaseUser.value?.uid!!, pubsList)
            Timber.i("Pubs List Load Success : ${pubsList.value.toString()}")
        } catch (e: Exception) {
            Timber.i("Pubs List Load Error : $e.message")
        }
    }

    fun delete(userid: String, id: String) {
        try {
            FirebaseDBManager.delete(userid, id)
            Timber.i("Pubs List Delete Success")
        } catch (e: Exception) {
            Timber.i("Pubs List Delete Error : $e.message")
        }
    }

    fun loadAll() {
        try {
            readOnly.value = true
            FirebaseDBManager.findAll(pubsList)
            Timber.i("List pubs LoadAll Success : ${pubsList.value.toString()}")
        } catch (e: Exception) {
            Timber.i("List pubs LoadAll Error : $e.message")
        }
    }

    fun loadFiltered(query: String) {
        try {
            readOnly.value = true
            FirebaseDBManager.findFiltered(query, pubsList)
            Timber.i("List pubs LoadAll Success : ${pubsList.value.toString()}")
        } catch (e: Exception) {
            Timber.i("List pubs LoadAll Error : $e.message")
        }
    }
}


