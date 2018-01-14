package pl.mkwiecinski.compassapp

import android.location.Location
import com.nhaarman.mockito_kotlin.*
import com.patloew.rxlocation.FusedLocation
import com.patloew.rxlocation.RxLocation
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import pl.mkwiecinski.compassapp.models.TargetModel
import pl.mkwiecinski.compassapp.providers.AzimuthProvider
import pl.mkwiecinski.compassapp.shared.value
import pl.mkwiecinski.compassapp.vm.CompassViewModel
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class) class CompassViewModelTest {
    @Before fun setupRxSchedulers() {
        val immediate = Schedulers.trampoline()
        RxJavaPlugins.setInitIoSchedulerHandler { immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
    }


    @Test fun requestUpdates_azimuthSubscribed() {
        val azimuthProvider = mock<AzimuthProvider> {
            on { radiansValue } doReturn Observable.just((Math.PI / 2).toFloat())
        }
        val locationProvider = mock<RxLocation>()
        val tested = CompassViewModel(azimuthProvider, locationProvider)

        tested.requestUpdates()

        verify(azimuthProvider, times(1)).radiansValue
    }


    @Test fun startNavigationCommand_relativeAngleUpdated() {
        val location = Location("tests").apply {
            latitude = 20.0
            longitude = 20.0
        }
        val fusedLocation = mock<FusedLocation> {
            on { updates(any()) } doReturn Observable.just(location)
        }
        val locationProvider = mock<RxLocation>() {
            on { location() } doReturn fusedLocation
        }
        val tested = CompassViewModel(mock(), locationProvider)
        tested.northRotation.value = 13f
        val param = TargetModel(10.0, 20.0)
        val test = tested.startNavigationCommand.success.test()

        tested.startNavigationCommand.execute(param)

        test.awaitCount(1)
        Assert.assertEquals(193f, tested.targetRelativeAngle.value ?: Float.MIN_VALUE, .0000001f)
    }
}
