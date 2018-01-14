package pl.mkwiecinski.compassapp.vm

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.mkwiecinski.compassapp.BuildConfig
import pl.mkwiecinski.compassapp.models.TargetModel
import pl.mkwiecinski.compassapp.providers.AzimuthProvider
import pl.mkwiecinski.compassapp.shared.addDisposable
import pl.mkwiecinski.compassapp.shared.plusAssign
import pl.mkwiecinski.compassapp.shared.value
import pl.mkwiecinski.compassapp.shared.vm.BaseViewModel
import pl.mkwiecinski.rxcommand.RxCommand
import javax.inject.Inject


class CompassViewModel @Inject constructor(private val azimuthProvider: AzimuthProvider,
                                           private val locationProvider: RxLocation) : BaseViewModel(),
        LifecycleObserver {

    val northRotation = ObservableField<Float>()

    val target = ObservableField<TargetModel>()
    internal val targetBearing = ObservableField<Float>()
    val targetRelativeAngle = ObservableField<Float>()
    val isWaitingForLocationUpdate = ObservableBoolean(false)

    val changeTargetCommand = RxCommand(this::changeTarget)
    val startNavigationCommand = RxCommand(this::startNavigationTo)

    init {
        northRotation += this::updateTargetAngle
        targetBearing += this::updateTargetAngle
        target += {
            isWaitingForLocationUpdate.value = it != null
        }
    }

    private fun updateTargetAngle(param: Float?) {
        targetRelativeAngle.value = targetBearing.value?.plus(northRotation.value ?: 0f)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START) fun requestUpdates() {
        azimuthProvider.radiansValue.subscribeOn(Schedulers.io()).map { radiansToUsableDegrees(it) }.retry().subscribe {
            northRotation.value = it
        }.addDisposable(disposeBag)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP) fun stopUpdates() {
        disposeBag.clear()
    }

    @SuppressLint("MissingPermission") private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 200
        }

        locationProvider.location().updates(locationRequest).doOnNext {
            isWaitingForLocationUpdate.value = false
        }.map {
            val current = target.value?.let {
                Location(BuildConfig.APPLICATION_ID).apply {
                    latitude = it.latitude
                    longitude = it.longitude
                    bearing = northRotation.value ?: 0f
                }
            }
            it.bearingTo(current)
        }.subscribe {
            targetBearing.value = it
        }.addDisposable(disposeBag)

    }

    private fun startNavigationTo(param: TargetModel): Single<Unit> {
        return Single.just(param).doOnSuccess {
            target.value = it
        }.doOnSuccess {
            requestLocationUpdates()
        }.map {}
    }

    private fun changeTarget(param: Unit) = Single.just(Unit)

    private fun radiansToUsableDegrees(it: Float): Float {
        return Math.toDegrees(-it.toDouble()).toFloat().let {
            var result = it
            while (result < 0.0f) {
                result += 360.0f
            }
            while (result >= 360.0f) {
                result -= 360.0f
            }
            result
        }
    }
}
