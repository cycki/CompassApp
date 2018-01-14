package pl.mkwiecinski.compassapp.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.mkwiecinski.compassapp.di.FragmentScope
import pl.mkwiecinski.compassapp.ui.dialogs.TargetPickerDialogFragment

@Module abstract class CompassInjectors {

    @FragmentScope @ContributesAndroidInjector abstract fun target(): TargetPickerDialogFragment
}
