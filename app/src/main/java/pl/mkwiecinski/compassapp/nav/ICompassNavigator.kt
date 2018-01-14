package pl.mkwiecinski.compassapp.nav

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import pl.mkwiecinski.compassapp.models.TargetModel
import pl.mkwiecinski.compassapp.shared.TAG
import pl.mkwiecinski.compassapp.ui.CompassActivity
import pl.mkwiecinski.compassapp.ui.dialogs.TargetPickerDialogFragment
import javax.inject.Inject

interface ICompassNavigator {
    fun navigateAppPermissions()
    fun navigatePickTarget(value: TargetModel?)
}

class CompassNavigator @Inject constructor(private val activity: CompassActivity) :
        ICompassNavigator {
    override fun navigatePickTarget(value: TargetModel?) {
        TargetPickerDialogFragment.newInstance(value).let {
            it.show(activity.supportFragmentManager, it.TAG)
        }
    }

    override fun navigateAppPermissions() {
        with(activity) {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = Uri.fromParts("package", packageName, null)
            }.let { startActivity(it) }
        }
    }

}
