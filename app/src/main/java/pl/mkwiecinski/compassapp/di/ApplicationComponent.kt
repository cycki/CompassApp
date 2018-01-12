package pl.mkwiecinski.compassapp.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import pl.mkwiecinski.compassapp.CompassApplication
import pl.mkwiecinski.compassapp.di.modules.ApplicationInjectors
import javax.inject.Singleton

@Singleton @Component(modules = [AndroidSupportInjectionModule::class, ApplicationInjectors::class]) interface ApplicationComponent :
        AndroidInjector<CompassApplication> {

    @Component.Builder abstract class Builder : AndroidInjector.Builder<CompassApplication>()
}
