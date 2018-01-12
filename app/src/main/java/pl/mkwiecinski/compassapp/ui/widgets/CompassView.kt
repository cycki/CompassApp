package pl.mkwiecinski.compassapp.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator
import pl.mkwiecinski.compassapp.databinding.WidgetCompassBinding


class CompassView @JvmOverloads constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyleAttr: Int = 0) : RelativeLayout(context,
                                                                                    attrs,
                                                                                    defStyleAttr) {
    private val binding = WidgetCompassBinding.inflate(LayoutInflater.from(context), this, true)

    private val animator = AdditiveAnimator.animate(binding.compass)
    var azimuth: Float? = null
        get() = field
        set(value) {
            field = value
            value?.let {
                animator.rotation(it).start()
            }
        }

    init {

    }
}