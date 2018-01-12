package pl.mkwiecinski.compassapp.shared

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer
import pl.mkwiecinski.rxcommand.RxCommand


var <T> ObservableField<T>.value: T?
    get() = get()
    set(value) = set(value)

var ObservableBoolean.value: Boolean
    get() = get()
    set(value) = set(value)

fun <T : Disposable> T.addDisposable(disposeBag: DisposableContainer): T {
    disposeBag.add(this)
    return this
}

fun <T> RxCommand<Unit, T>.execute() {
    execute(Unit)
}