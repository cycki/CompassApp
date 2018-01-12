package pl.mkwiecinski.compassapp.ui

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import pl.mkwiecinski.compassapp.R
import pl.mkwiecinski.compassapp.databinding.ActivityCompassBinding
import pl.mkwiecinski.compassapp.di.factories.CompassViewModelFactory
import pl.mkwiecinski.compassapp.vm.CompassViewModel
import javax.inject.Inject

class CompassActivity : AppCompatActivity() {

    private val disposeBag = CompositeDisposable()
    private lateinit var binding: ActivityCompassBinding
    private lateinit var viewModel: CompassViewModel
    @Inject lateinit var factory: CompassViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        initViewModel(savedInstanceState)
        initView(savedInstanceState)
    }

    private fun initViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, factory)[CompassViewModel::class.java]
    }

    private fun initView(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compass)
        binding.model = viewModel
        setSupportActionBar(binding.toolbar)
    }


    override fun onDestroy() {
        disposeBag.dispose()
        super.onDestroy()
    }
}
