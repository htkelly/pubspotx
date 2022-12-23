package ie.wit.pubspotx.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface PubStore {
    fun findAll(
        pubsList:
        MutableLiveData<List<PubModel>>
    )

    fun findAll(
        userid: String,
        pubsList:
        MutableLiveData<List<PubModel>>
    )

    fun findFiltered(
        query: String,
        pubsList:
        MutableLiveData<List<PubModel>>
    )

    fun findById(
        userid: String, pubid: String,
        pub: MutableLiveData<PubModel>
    )

    fun create(firebaseUser: MutableLiveData<FirebaseUser>, pub: PubModel)
    fun delete(userid: String, pubid: String)
    fun update(userid: String, pubid: String, pub: PubModel)
}