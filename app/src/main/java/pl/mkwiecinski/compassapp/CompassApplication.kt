package pl.mkwiecinski.compassapp

import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import pl.mkwiecinski.compassapp.di.DaggerApplicationComponent
import timber.log.Timber

class CompassApplication : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    override fun applicationInjector(): AndroidInjector<CompassApplication>? {
        return DaggerApplicationComponent.builder().create(this)
    }
}