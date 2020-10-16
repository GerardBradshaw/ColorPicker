package com.gerardbradshaw.colorpickerlibrary.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import com.gerardbradshaw.colorpickerlibrary.R
import kotlin.math.roundToInt

/**
 * ColorSliderView is designed to be used as simplified, customizable SeekBar for use with colors.
 * The bar's background can be set as a drawable (such as a color gradient), and functions for
 * getting/setting the progress ratio are provided for quick access.
 */
class ColorSliderView : FrameLayout {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
      super(context, attrs, defStyle)


  // ------------------------ PROPERTIES ------------------------

  val max = context.resources.getInteger(R.integer.color_picker_library_spectrum_color_count).toDouble()
  var sliderType: SliderType = SliderType.NOT_SET
  private lateinit var seekBar: SeekBar
  private lateinit var gradientBar: FrameLayout
  private var listener: OnProgressChangedListener? = null

  /**
   * The progress ratio given by progress / maxProgress. Note that the ratio is rounded to 1.0 or
   * 0.0 if it has been set greater than 1.0 or less than 0.0 respectively.
   */
  var progressRatio: Double = 0.0
    get() = (seekBar.progress).toDouble() / (seekBar.max).toDouble()
    set(value) {
      if (value > 1 || value < 0) Log.d(TAG, "setProgressRatio: invalid ratio ($value)")

      val safeValue = when {
        value > 1.0 -> 1.0
        value < 0.0 -> 0.0
        else -> value
      }
      field = safeValue
      seekBar.progress = (safeValue * max).roundToInt()
    }

  // ------------------------ INITIALIZATION ------------------------

  init {
    View.inflate(context, R.layout.color_picker_library_view_color_slider, this)
    initView()
  }

  private fun initView() {
    gradientBar = findViewById(R.id.color_picker_library_slider_gradient_bar)
    seekBar = findViewById(R.id.color_picker_library_slider_seek_bar)

    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onStartTrackingTouch(seekBar: SeekBar?) { /* Required empty */ }
      override fun onStopTrackingTouch(seekBar: SeekBar?) { /* Required empty */ }

      override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        listener?.onProgressChanged(progress.toDouble() / max)
      }
    })
  }


  // ------------------------ GRADIENT BAR ------------------------

  /** Set the bar drawable, optional color tag, and slider type. */
  fun setUpGradientBar(drawable: Drawable, colorTag: Int?, sliderType: SliderType) {
    gradientBar.background = drawable
    setTag(R.id.color_picker_library_color_tag, colorTag)
    this.sliderType = sliderType
  }

  /** Update the bar drawable and optional color tag without changing the slider type. */
  fun updateGradientDrawable(drawable: Drawable, colorTag: Int) {
    gradientBar.background = drawable
    setTag(R.id.color_picker_library_color_tag, colorTag)
  }

  fun setOnProgressChangedListener(listener: OnProgressChangedListener) {
    this.listener = listener
  }


  // ------------------------ HELPERS ------------------------

  interface OnProgressChangedListener {
    fun onProgressChanged(progress: Double)
  }

  companion object {
    private const val TAG = "ColorSliderView"
  }

  enum class SliderType(val value: String) {
    COLOR("Color"),
    SHADE("Shade"),
    TINT("Tint"),
    NOT_SET("NotSet")
  }
}