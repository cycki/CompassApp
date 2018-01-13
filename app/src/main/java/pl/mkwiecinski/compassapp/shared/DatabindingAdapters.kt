package pl.mkwiecinski.compassapp.shared

import android.databinding.BindingAdapter
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator
import io.reactivex.android.schedulers.AndroidSchedulers
import pl.mkwiecinski.rxcommand.CommandState
import pl.mkwiecinski.rxcommand.RxCommand

@BindingAdapter("android:onClick") fun <T> bindOnClick(view: Button, command: RxCommand<Unit, T>?) {
    view.setOnClickListener {
        command?.execute()
    }
}

@BindingAdapter("android:onClick") fun <T> bindOnClick(view: ImageButton,
                                                       command: RxCommand<Unit, T>?) {
    view.setOnClickListener {
        command?.execute()
    }
}

@BindingAdapter("android:visibility") fun <T> bindVisibility(view: View, isVisible: Boolean?) {
    view.visibility = if (isVisible == true) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("android:visibility") fun <T> bindVisibility(view: View,
                                                             command: RxCommand<Unit, T>?) {
    command ?: return
    command.executing.observeOn(AndroidSchedulers.mainThread()).subscribe {
        view.visibility = when (it) {
            CommandState.Finished -> View.VISIBLE
            else -> View.GONE
        }
    }
}

@BindingAdapter("rotationAnimated") fun animateRotation(view: View, value: Float?) {
    value?.let {
        AdditiveAnimator.animate(view).rotation(it).start()
    }
}