package pl.mkwiecinski.compassapp.shared.vm

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {
    protected val disposeBag = CompositeDisposable()


    override fun onCleared() {
        super.onCleared()
        disposeBag.dispose()
    }
}
