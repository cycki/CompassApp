package pl.mkwiecinski.compassapp.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import pl.mkwiecinski.compassapp.databinding.WidgetCompassBinding


class CompassView @JvmOverloads constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyleAttr: Int = 0) : RelativeLayout(context,
                                                                                    attrs,
                                                                                    defStyleAttr) {
    private val binding = WidgetCompassBinding.inflate(LayoutInflater.from(context), this, true)

    var azimuth: Float?
        get() = binding.azimuth
        set(value) {
            binding.azimuth = value
        }

    var target: Float?
        get() = binding.target
        set(value) {
            binding.target = value
        }

}