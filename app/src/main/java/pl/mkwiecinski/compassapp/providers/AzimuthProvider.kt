package pl.mkwiecinski.compassapp.providers

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject


class AzimuthProvider(context: Context) : LifecycleObserver, SensorEventListener {
    companion object {
        private const val ALPHA = 0.5f

    }

    private val disposeBag = CompositeDisposable()

    private val azimuthSubject = PublishSubject.create<Float>()
    val radiansValue: Observable<Float> = azimuthSubject.hide()

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null

    private val rotationMatrix = FloatArray(9)
    private val rotationMatrixResult = FloatArray(3)
    @OnLifecycleEvent(Lifecycle.Event.ON_START) fun requestUpdates() {
        sensorManager?.let {
            it.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            it.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP) fun stopUpdates() {
        sensorManager?.unregisterListener(this)
        disposeBag.clear()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun applyLowPassFilter(input: FloatArray, output: FloatArray?): FloatArray {
        if (output == null) return input

        for (i in input.indices) {
            output[i] = output[i] + ALPHA * (input[i] - output[i])
        }
        return output
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                gravity = applyLowPassFilter(event.values.clone(), gravity)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                geomagnetic = applyLowPassFilter(event.values.clone(), geomagnetic)
            }
        }
        updateAzimuth()
    }

    private fun updateAzimuth() {
        gravity ?: return
        geomagnetic ?: return
        if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
            SensorManager.getOrientation(rotationMatrix, rotationMatrixResult)
            azimuthSubject.onNext(rotationMatrixResult.first())
        }
    }

    fun isCompatibleWithDevice() = when (null) {
        sensorManager, accelerometer, magnetometer -> false
        else -> true
    }

}
