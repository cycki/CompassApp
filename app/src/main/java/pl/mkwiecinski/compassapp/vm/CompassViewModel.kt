package pl.mkwiecinski.compassapp.vm

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import com.tbruyelle.rxpermissions.RxPermissions
import dagger.Lazy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pl.mkwiecinski.compassapp.providers.AzimuthProvider
import pl.mkwiecinski.compassapp.shared.addDisposable
import pl.mkwiecinski.compassapp.shared.value
import javax.inject.Inject


class CompassViewModel @Inject constructor(private val azimuthProvider: AzimuthProvider,
                                           private val locationProvider: RxLocation,
                                           private val permissions: Lazy<RxPermissions>) : ViewModel(),
        LifecycleObserver {
    private val disposeBag = CompositeDisposable()
    private val stoppableUpdates = CompositeDisposable()

    val azimuth = ObservableField<Float?>()

    @OnLifecycleEvent(Lifecycle.Event.ON_START) fun requestUpdates() {
        azimuthProvider.radiansValue.subscribeOn(Schedulers.io()).map { radiansToUsableDegrees(it) }.retry().subscribe {
            azimuth.value = it
        }.addDisposable(stoppableUpdates)

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
        }

//        locationProvider.location().updates(locationRequest).map {
//            it.bearing
//        }
    }

    private fun radiansToUsableDegrees(it: Float): Float {
        return Math.toDegrees(-it.toDouble()).toFloat().let {
            if (it < 0) {
                it + 360
            } else {
                it
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP) fun stopUpdates() {
        stoppableUpdates.clear()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.dispose()
    }
}

private fun Float.toDegrees(): Float {
    return Math.toDegrees(toDouble()).toFloat()
}
