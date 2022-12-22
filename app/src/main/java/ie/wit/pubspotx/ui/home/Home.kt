package ie.wit.pubspotx.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseUser
import ie.wit.pubspotx.R
import ie.wit.pubspotx.databinding.HomeBinding
import ie.wit.pubspotx.databinding.NavHeaderBinding
import ie.wit.pubspotx.dialogs.SetThemeDialog
import ie.wit.pubspotx.firebase.FirebaseImageManager
import ie.wit.pubspotx.models.PreferencesModel
import ie.wit.pubspotx.models.PreferencesStore
import ie.wit.pubspotx.room.PreferencesStoreRoom
import ie.wit.pubspotx.ui.auth.LoggedInViewModel
import ie.wit.pubspotx.ui.auth.Login
import ie.wit.pubspotx.utils.readImageUri
import ie.wit.pubspotx.utils.showImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.i

class Home : AppCompatActivity(), SetThemeDialog.SetThemeDialogListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeBinding: HomeBinding
    private lateinit var navHeaderBinding: NavHeaderBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loggedInViewModel: LoggedInViewModel
    private lateinit var headerView: View
    private lateinit var intentLauncher: ActivityResultLauncher<Intent>
    private lateinit var preferences: PreferencesStore
    private var loggedInUserPreferences: PreferencesModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = PreferencesStoreRoom(applicationContext)

        homeBinding = HomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        drawerLayout = homeBinding.drawerLayout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.addPubFragment, R.id.listPubsFragment, R.id.aboutFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView = homeBinding.navView
        navView.setupWithNavController(navController)
        initNavHeader()
    }

    public override fun onStart() {
        super.onStart()
        loggedInViewModel = ViewModelProvider(this).get(LoggedInViewModel::class.java)
        loggedInViewModel.liveFirebaseUser.observe(this, Observer { firebaseUser ->
            if (firebaseUser != null) {
                updateNavHeader(firebaseUser)
                GlobalScope.launch(Dispatchers.Main) {
                    loggedInUserPreferences = getUserPreferences(firebaseUser)
                    if (loggedInUserPreferences != null) {
                        i("Found user preferences")
                        when (loggedInUserPreferences!!.theme) {
                            MODE_NIGHT_NO -> setDefaultNightMode(MODE_NIGHT_NO)
                            MODE_NIGHT_YES -> setDefaultNightMode(MODE_NIGHT_YES)
                            else -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                        }
                    } else {
                        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                        i("Could not find user preferences -- creating a db entry for this user's preferences")
                        createLoggedInUserPreferences(
                            PreferencesModel(
                                userid = firebaseUser.uid,
                                theme = MODE_NIGHT_FOLLOW_SYSTEM
                            )
                        )
                    }
                }
            }
        })

        loggedInViewModel.loggedOut.observe(this, Observer { loggedout ->
            if (loggedout) {
                startActivity(Intent(this, Login::class.java))
            }
        })
        registerImagePickerCallback()
    }

    private fun initNavHeader() {
        Timber.i("DX Init Nav Header")
        headerView = homeBinding.navView.getHeaderView(0)
        navHeaderBinding = NavHeaderBinding.bind(headerView)
        navHeaderBinding.navHeaderImage.setOnClickListener {
            showImagePicker(intentLauncher)
        }
    }

    private fun updateNavHeader(currentUser: FirebaseUser) {
        FirebaseImageManager.imageUri.observe(this) { result ->
            if (result == Uri.EMPTY) {
                Timber.i("DX NO Existing imageUri")
                if (currentUser.photoUrl != null) {
                    //if you're a google user
                    FirebaseImageManager.updateUserImage(
                        currentUser.uid,
                        currentUser.photoUrl,
                        navHeaderBinding.navHeaderImage,
                        false
                    )
                } else {
                    Timber.i("DX Loading Existing Default imageUri")
                    FirebaseImageManager.updateDefaultImage(
                        currentUser.uid,
                        R.drawable.ic_launcher_homer,
                        navHeaderBinding.navHeaderImage
                    )
                }
            } else // load existing image from firebase
            {
                Timber.i("DX Loading Existing imageUri")
                FirebaseImageManager.updateUserImage(
                    currentUser.uid,
                    FirebaseImageManager.imageUri.value,
                    navHeaderBinding.navHeaderImage, false
                )
            }
        }
        navHeaderBinding.navHeaderEmail.text = currentUser.email
        if (currentUser.displayName != null)
            navHeaderBinding.navHeaderName.text = currentUser.displayName
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun signOut(item: MenuItem) {
        loggedInViewModel.logOut()
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun registerImagePickerCallback() {
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i(
                                "DX registerPickerCallback() ${
                                    readImageUri(
                                        result.resultCode,
                                        result.data
                                    ).toString()
                                }"
                            )
                            FirebaseImageManager
                                .updateUserImage(
                                    loggedInViewModel.liveFirebaseUser.value!!.uid,
                                    readImageUri(result.resultCode, result.data),
                                    navHeaderBinding.navHeaderImage,
                                    true
                                )
                        } // end of if
                    }
                    RESULT_CANCELED -> {}
                    else -> {}
                }
            }
    }

    fun showSetThemeDialog(item: MenuItem) {
        val dialog = SetThemeDialog()
        dialog.show(this.supportFragmentManager, "SetThemeDialogFragment")
    }

    private suspend fun getUserPreferences(firebaseUser: FirebaseUser): PreferencesModel? {
        i("Getting preferences for uid %s", firebaseUser.uid)
        i(preferences.findByUserId(firebaseUser.uid).toString())
        return preferences.findByUserId(firebaseUser.uid)
    }

    private suspend fun createLoggedInUserPreferences(userPreferences: PreferencesModel) {
        preferences.create(userPreferences)
    }

    private suspend fun updateCurrentUserThemePreference(theme: Int) {
        val loggedInUserId = loggedInViewModel.liveFirebaseUser.value?.uid
        if (loggedInUserId != null) preferences.update(
            PreferencesModel(
                userid = loggedInUserId,
                theme = theme
            )
        )
    }

    override fun onSelectLightMode(dialog: DialogFragment) {
        setDefaultNightMode(MODE_NIGHT_NO)
        GlobalScope.launch(Dispatchers.Main) {
            updateCurrentUserThemePreference(MODE_NIGHT_NO)
        }
    }

    override fun onSelectDarkMode(dialog: DialogFragment) {
        i("Setting dark mode")
        setDefaultNightMode(MODE_NIGHT_YES)
        GlobalScope.launch(Dispatchers.Main) {
            updateCurrentUserThemePreference(MODE_NIGHT_YES)
        }
    }

    override fun onSelectSystemDefault(dialog: DialogFragment) {
        i("Setting system default theme")
        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        GlobalScope.launch(Dispatchers.Main) {
            updateCurrentUserThemePreference(MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}