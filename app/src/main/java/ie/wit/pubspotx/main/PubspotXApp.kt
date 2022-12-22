package ie.wit.pubspotx.main

import android.app.Application
import timber.log.Timber

class PubspotXApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("PubspotX Application Started")
    }
}