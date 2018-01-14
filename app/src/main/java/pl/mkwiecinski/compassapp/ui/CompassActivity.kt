package pl.mkwiecinski.compassapp.ui

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import pl.mkwiecinski.compassapp.R
import pl.mkwiecinski.compassapp.databinding.ActivityCompassBinding
import pl.mkwiecinski.compassapp.di.factories.CompassViewModelFactory
import pl.mkwiecinski.compassapp.models.TargetModel
import pl.mkwiecinski.compassapp.nav.ICompassNavigator
import pl.mkwiecinski.compassapp.providers.AzimuthProvider
import pl.mkwiecinski.compassapp.shared.*
import pl.mkwiecinski.compassapp.ui.dialogs.TargetPickerCallback
import pl.mkwiecinski.compassapp.vm.CompassViewModel
import javax.inject.Inject

class CompassActivity : AppCompatActivity(), TargetPickerCallback, HasSupportFragmentInjector {
    companion object {
        private const val LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private val disposeBag = CompositeDisposable()

    private lateinit var binding: ActivityCompassBinding
    private lateinit var viewModel: CompassViewModel
    @Inject lateinit var factory: CompassViewModelFactory

    private lateinit var rxPermissions: RxPermissions
    @Inject lateinit var azimuthProvider: AzimuthProvider
    @Inject lateinit var navigator: ICompassNavigator
    @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        initViewModel(savedInstanceState)
        lifecycle.apply {
            addObserver(azimuthProvider)
            addObserver(viewModel)
        }
        initView(savedInstanceState)
    }

    private fun initViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, factory)[CompassViewModel::class.java]
        viewModel.changeTargetCommand.success.flatMap {
            rxPermissions.requestEachCombined(LOCATION)
        }.subscribe {
            when {
                it.granted -> navigator.navigatePickTarget(viewModel.target.value)
                it.shouldShowRequestPermissionRationale -> showNoPermissionInfo()
                else -> showPermissionDeniedInfo()
            }
        }.addDisposable(disposeBag)
    }

    private fun showNoPermissionInfo() {
        Snackbar.make(binding.root,
                      R.string.missing_location_permission,
                      Snackbar.LENGTH_LONG).apply {
            setAction(R.string.target_retry) {
                viewModel.changeTargetCommand.execute()
            }
        }.show()
    }

    private fun showPermissionDeniedInfo() {
        Snackbar.make(binding.root,
                      R.string.missing_location_permission_disabled,
                      Snackbar.LENGTH_LONG).apply {
            setAction(R.string.permission_error_go_to_settings) {
                navigator.navigateAppPermissions()
            }
        }.show()
    }

    private fun initView(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compass)
        binding.model = viewModel
        rxPermissions = RxPermissions(this)
        setSupportActionBar(binding.toolbar)
    }

    override fun onTargetPicked(new: TargetModel) {
        viewModel.startNavigationCommand.execute(new)
    }

    override fun supportFragmentInjector() = fragmentInjector


    override fun onSaveInstanceState(outState: Bundle?) {
        val state = viewModel saveTo outState
        super.onSaveInstanceState(state)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        viewModel restoreFrom savedInstanceState
        if (viewModel.target.value != null && !rxPermissions.isGranted(LOCATION)) {
            viewModel.targetBearing.value = null
            Snackbar.make(binding.root,
                          R.string.missing_location_permission_disabled,
                          Snackbar.LENGTH_LONG).apply {
                setAction(R.string.target_retry) {
                    viewModel.changeTargetCommand.execute()
                }
            }.show()
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onDestroy() {
        disposeBag.dispose()
        super.onDestroy()
    }
}

private class CompassState(val azimuth: Float?, val target: TargetModel?, val targetBearing: Float?)

private const val STATE_KEY = ".state"

private infix fun CompassViewModel.saveTo(outState: Bundle?): Bundle {
    return (outState ?: Bundle()).apply {
        putString(STATE_KEY,
                  CompassState(azimuth.value, target.value, targetBearing.value).toJson())
    }
}

private infix fun CompassViewModel.restoreFrom(savedInstanceState: Bundle?) {
    savedInstanceState?.getString(STATE_KEY)?.fromJson<CompassState>()?.let {
        azimuth.value = it.azimuth
        target.value = it.target
        targetBearing.value = it.targetBearing
    }
}
