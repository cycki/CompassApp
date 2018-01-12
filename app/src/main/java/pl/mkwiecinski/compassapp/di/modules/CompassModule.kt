package pl.mkwiecinski.compassapp.di.modules

import com.patloew.rxlocation.RxLocation
import com.tbruyelle.rxpermissions.RxPermissions
import dagger.Module
import dagger.Provides
import pl.mkwiecinski.compassapp.di.ActivityScope
import pl.mkwiecinski.compassapp.providers.AzimuthProvider
import pl.mkwiecinski.compassapp.ui.CompassActivity

@Module class CompassModule {
    @ActivityScope @Provides fun azimuthProvider(activity: CompassActivity): AzimuthProvider {
        return AzimuthProvider(activity)
    }

    @Provides fun provideLocation(activity: CompassActivity): RxLocation = RxLocation(activity)
    @Provides fun providePermissions(activity: CompassActivity): RxPermissions = RxPermissions(
            activity)
}
