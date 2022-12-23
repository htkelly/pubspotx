package ie.wit.pubspotx.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ie.wit.pubspotx.models.PubModel
import ie.wit.pubspotx.models.PubStore
import timber.log.Timber

object FirebaseDBManager : PubStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(pubsList: MutableLiveData<List<PubModel>>) {
        database.child("pubs")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Pubspot error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<PubModel>()
                    val children = snapshot.children
                    children.forEach {
                        val pub = it.getValue(PubModel::class.java)
                        localList.add(pub!!)
                    }
                    database.child("pubs")
                        .removeEventListener(this)

                    pubsList.value = localList
                }
            })
    }

    override fun findFiltered(query: String, pubsList: MutableLiveData<List<PubModel>>) {
        database.child("pubs")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Pubspot error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<PubModel>()
                    val children = snapshot.children
                    children.forEach {
                        val pub = it.getValue(PubModel::class.java)
                        localList.add(pub!!)
                    }
                    database.child("pubs")
                        .removeEventListener(this)

                    pubsList.value = localList.filter { s -> s.name == query }
                }
            })
    }

    override fun findAll(userid: String, pubsList: MutableLiveData<List<PubModel>>) {

        database.child("user-pubs").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Pubspot error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<PubModel>()
                    val children = snapshot.children
                    children.forEach {
                        val pub = it.getValue(PubModel::class.java)
                        localList.add(pub!!)
                    }
                    database.child("user-pubs").child(userid)
                        .removeEventListener(this)

                    pubsList.value = localList
                }
            })
    }

    override fun findById(userid: String, pubid: String, pub: MutableLiveData<PubModel>) {

        database.child("user-pubs").child(userid)
            .child(pubid).get().addOnSuccessListener {
                pub.value = it.getValue(PubModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener {
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, pub: PubModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("pubs").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        pub.uid = key
        val pubValues = pub.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/pubs/$key"] = pubValues
        childAdd["/user-pubs/$uid/$key"] = pubValues

        database.updateChildren(childAdd)
    }

    override fun delete(userid: String, pubid: String) {

        val childDelete: MutableMap<String, Any?> = HashMap()
        childDelete["/pubs/$pubid"] = null
        childDelete["/user-pubs/$userid/$pubid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, pubid: String, pub: PubModel) {

        val pubValues = pub.toMap()

        val childUpdate: MutableMap<String, Any?> = HashMap()
        childUpdate["pubs/$pubid"] = pubValues
        childUpdate["user-pubs/$userid/$pubid"] = pubValues

        database.updateChildren(childUpdate)
    }

    fun updateImageRef(userid: String, imageUri: String) {

        val userPubs = database.child("user-pubs").child(userid)
        val allPubs = database.child("pubs")

        userPubs.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        //Update Users imageUri
                        it.ref.child("profilepic").setValue(imageUri)
                        //Update all pubs that match 'it'
                        val pub = it.getValue(PubModel::class.java)
                        allPubs.child(pub!!.uid!!)
                            .child("profilepic").setValue(imageUri)
                    }
                }
            })
    }
}