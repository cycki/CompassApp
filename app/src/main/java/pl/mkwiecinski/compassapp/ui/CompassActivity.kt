package pl.mkwiecinski.compassapp.ui

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import pl.mkwiecinski.compassapp.R
import pl.mkwiecinski.compassapp.databinding.ActivityCompassBinding
import pl.mkwiecinski.compassapp.di.factories.CompassViewModelFactory
import pl.mkwiecinski.compassapp.models.TargetModel
import pl.mkwiecinski.compassapp.providers.AzimuthProvider
import pl.mkwiecinski.compassapp.shared.addDisposable
import pl.mkwiecinski.compassapp.shared.execute
import pl.mkwiecinski.compassapp.vm.CompassViewModel
import javax.inject.Inject

class CompassActivity : AppCompatActivity() {

    private val disposeBag = CompositeDisposable()

    private lateinit var binding: ActivityCompassBinding
    private lateinit var viewModel: CompassViewModel
    private lateinit var rxPermissions: RxPermissions
    @Inject lateinit var factory: CompassViewModelFactory
    @Inject lateinit var azimuthProvider: AzimuthProvider

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
        viewModel.pickTargetCommand.success.flatMap {
            rxPermissions.requestEachCombined(Manifest.permission.ACCESS_FINE_LOCATION)
        }.subscribe {
            when {
                it.granted -> viewModel.startNavigationCommand.execute(TargetModel(1.0, 2.0))
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
                viewModel.pickTargetCommand.execute()
            }
        }.show()
    }

    private fun showPermissionDeniedInfo() {
        Snackbar.make(binding.root,
                      R.string.missing_location_permission_disabled,
                      Snackbar.LENGTH_LONG).apply {
            setAction(R.string.permission_error_go_to_settings) {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = Uri.fromParts("package", packageName, null)
                }.let { startActivity(it) }
            }
        }.show()
    }

    private fun initView(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compass)
        binding.model = viewModel
        rxPermissions = RxPermissions(this)
        setSupportActionBar(binding.toolbar)
    }


    override fun onDestroy() {
        disposeBag.dispose()
        super.onDestroy()
    }
}
