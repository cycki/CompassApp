package pl.mkwiecinski.compassapp.di.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import pl.mkwiecinski.compassapp.vm.CompassViewModel
import javax.inject.Inject

class CompassViewModelFactory @Inject constructor(private val mainViewModel: CompassViewModel) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompassViewModel::class.java)) {
            return mainViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

