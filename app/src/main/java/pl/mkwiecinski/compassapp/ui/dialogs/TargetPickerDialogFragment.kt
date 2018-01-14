package pl.mkwiecinski.compassapp.ui.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import pl.mkwiecinski.compassapp.R
import pl.mkwiecinski.compassapp.databinding.DialogTargetPickerBinding
import pl.mkwiecinski.compassapp.models.TargetModel
import pl.mkwiecinski.compassapp.shared.*
import pl.mkwiecinski.compassapp.shared.validation.ValidationKey
import pl.mkwiecinski.compassapp.vm.TargetPickerViewModel
import javax.inject.Inject


class TargetPickerDialogFragment : DialogFragment() {
    companion object {
        private const val DATA_KEY = ".data"
        fun newInstance(old: TargetModel?) = TargetPickerDialogFragment().apply {
            data = old
        }
    }

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var viewModel: TargetPickerViewModel
    private lateinit var binding: DialogTargetPickerBinding
    private val callback: TargetPickerCallback?
        get() = findCallback(this)

    private var data: TargetModel?
        get() = arguments?.getString(DATA_KEY).fromJson()
        set(value) {
            arguments = (arguments ?: Bundle()).apply {
                putString(DATA_KEY, value.toJson())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel.validateCommand.success.subscribe {
            dismiss()
            callback?.onTargetPicked(it)
        }.addDisposable(disposeBag)
        viewModel.validationErrors += {
            binding.lblLatitude.error = it?.contains(ValidationKey.Latitude)?.takeIf { it }?.let {
                TargetPickerViewModel.LATITUDE_RANGE.let {
                    getString(R.string.pick_target_error_latitude, it.first, it.second)
                }
            }
            binding.lblLongitude.error = it?.contains(ValidationKey.Longitude)?.takeIf { it }?.let {
                TargetPickerViewModel.LONGITUDE_RANGE.let {
                    getString(R.string.pick_target_error_longitude, it.first, it.second)
                }
            }
        }

        if (savedInstanceState == null) {
            data?.let {
                viewModel.initCommand.execute(it)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogTargetPickerBinding.inflate(inflater, container, false)
        binding.model = viewModel

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val state = viewModel saveTo outState
        super.onSaveInstanceState(state)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        viewModel restoreFrom savedInstanceState
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroy() {
        disposeBag.clear()
        super.onDestroy()
    }
}

private const val STATE_LAT = ".latitude"
private const val STATE_LNG = ".longitude"

private infix fun TargetPickerViewModel.saveTo(outState: Bundle): Bundle {
    return outState.apply {
        putString(STATE_LAT, latitude.value)
        putString(STATE_LNG, longitude.value)
    }
}

private infix fun TargetPickerViewModel.restoreFrom(savedInstanceState: Bundle?) {
    savedInstanceState?.let {
        latitude.value = it.getString(STATE_LAT)
        longitude.value = it.getString(STATE_LNG)
    }
}

interface TargetPickerCallback {
    fun onTargetPicked(new: TargetModel)
}