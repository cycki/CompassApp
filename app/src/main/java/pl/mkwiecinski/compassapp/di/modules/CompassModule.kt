package pl.mkwiecinski.compassapp.di.modules

import android.content.Context
import com.patloew.rxlocation.RxLocation
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.Module
import dagger.Provides
import pl.mkwiecinski.compassapp.di.ActivityScope
import pl.mkwiecinski.compassapp.providers.AzimuthProvider
import pl.mkwiecinski.compassapp.ui.CompassActivity

@Module class CompassModule {
    @ActivityScope @Provides fun azimuthProvider(context: Context): AzimuthProvider {
        return AzimuthProvider(context)
    }

    @Provides fun provideLocation(context: Context): RxLocation = RxLocation(context)
    @Provides fun providePermissions(activity: CompassActivity): RxPermissions = RxPermissions(
            activity)
}
