package pl.mkwiecinski.compassapp.di.modules

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.mkwiecinski.compassapp.CompassApplication
import pl.mkwiecinski.compassapp.di.ActivityScope
import pl.mkwiecinski.compassapp.ui.CompassActivity

@Module abstract class ApplicationInjectors {
    @Binds abstract fun context(app: CompassApplication): Context
    @ActivityScope @ContributesAndroidInjector(modules = [CompassModule::class]) abstract fun main(): CompassActivity
}