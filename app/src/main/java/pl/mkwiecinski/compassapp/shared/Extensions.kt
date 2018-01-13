package pl.mkwiecinski.compassapp.shared

import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableFloat
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer
import pl.mkwiecinski.rxcommand.RxCommand


var <T> ObservableField<T>.value: T?
    get() = get()
    set(value) = set(value)

var ObservableBoolean.value: Boolean
    get() = get()
    set(value) = set(value)
var ObservableFloat.value: Float
    get() = get()
    set(value) = set(value)

fun <T : Disposable> T.addDisposable(disposeBag: DisposableContainer): T {
    disposeBag.add(this)
    return this
}

fun <T> RxCommand<Unit, T>.execute() {
    execute(Unit)
}

operator inline fun ObservableFloat.plusAssign(crossinline action: (Float) -> Unit) {
    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            action(get())
        }
    })
}

operator inline fun <T> ObservableField<T>.plusAssign(crossinline action: (T?) -> Unit) {
    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            action(get())
        }
    })
}
