package pl.mkwiecinski.compassapp.vm

import android.databinding.ObservableField
import io.reactivex.Single
import pl.mkwiecinski.compassapp.models.TargetModel
import pl.mkwiecinski.compassapp.shared.plusAssign
import pl.mkwiecinski.compassapp.shared.validation.ValidationException
import pl.mkwiecinski.compassapp.shared.validation.ValidationKey
import pl.mkwiecinski.compassapp.shared.value
import pl.mkwiecinski.compassapp.shared.vm.BaseViewModel
import pl.mkwiecinski.rxcommand.RxCommand
import javax.inject.Inject

class TargetPickerViewModel @Inject constructor() : BaseViewModel() {
    companion object {
        val LATITUDE_RANGE = Pair(-90, 90)
        val LONGITUDE_RANGE = Pair(-180, 180)
    }

    val latitude = ObservableField<String>()
    val longitude = ObservableField<String>()
    val validationErrors = ObservableField<List<ValidationKey>>()

    val initCommand = RxCommand(this::initialize)
    val validateCommand = RxCommand(this::validate)

    init {
        latitude += {
            validationErrors.value = validationErrors.value?.filterNot { it == ValidationKey.Latitude }
        }
        longitude += {
            validationErrors.value = validationErrors.value?.filterNot { it == ValidationKey.Longitude }
        }
    }

    private fun initialize(param: TargetModel): Single<Unit> {
        return Single.just(param).doOnSuccess {
            latitude.value = it.latitude.toString()
            longitude.value = it.longitude.toString()
        }.map { }
    }

    private fun validate(param: Unit): Single<TargetModel> {
        return Single.create<TargetModel> { emitter ->
            val lat = latitude.value?.trim()?.toDoubleOrNull()?.takeIf {
                it.inRange(LATITUDE_RANGE)
            }
            val lng = longitude.value?.trim()?.toDoubleOrNull()?.takeIf {
                it.inRange(LONGITUDE_RANGE)
            }

            if (lat == null || lng == null) {
                val errors = mutableListOf<ValidationKey>()
                if (lat == null) {
                    errors.add(ValidationKey.Latitude)
                }
                if (lng == null) {
                    errors.add(ValidationKey.Longitude)
                }
                emitter.onError(ValidationException(errors))
            } else {
                emitter.onSuccess(TargetModel(latitude = lat, longitude = lng))
            }
        }.doOnError {
            when (it) {
                is ValidationException -> validationErrors.value = it.errors
            }
        }
    }
}

private fun Double.inRange(range: Pair<Int, Int>): Boolean {
    return range.first <= this && range.second >= this
}



