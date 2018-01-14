package pl.mkwiecinski.compassapp.shared

import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableFloat
import android.support.v4.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

operator inline fun ObservableBoolean.plusAssign(crossinline action: (Boolean) -> Unit) {
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

val gson: Gson by lazy {
    GsonBuilder().create()
}

inline fun <reified T> String?.fromJson(): T? {
    return try {
        gson.fromJson(this, T::class.java)
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T> T?.toJson(): String {
    return gson.toJson(this, T::class.java)
}


val Any.TAG: String
    get() = javaClass.name

inline fun <reified T> findCallback(fragment: Fragment): T? {
    var parent = fragment.parentFragment
    while (parent != null) {
        parent = (parent as? T)?.let { return it } ?: parent.parentFragment
    }
    (fragment.activity as? T)?.let {
        return it
    }
    (fragment.activity?.application as? T)?.let {
        return it
    }

    return null
}