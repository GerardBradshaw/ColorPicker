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

class ColorSliderView : FrameLayout {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
      super(context, attrs, defStyle)


  // ------------------------ PROPERTIES ------------------------

  val max = context.resources.getInteger(R.integer.color_picker_library_spectrum_color_count).toDouble()
  private lateinit var seekBar: SeekBar
  private lateinit var gradientBar: FrameLayout
  private var listener: OnProgressChangedListener? = null
  var sliderType: SliderType = SliderType.NOT_SET


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


  // ------------------------ PUBLIC METHODS ------------------------

  fun getProgressRatio(): Double {
    return (seekBar.progress).toDouble() / (seekBar.max).toDouble()
  }

  fun setProgressRatio(progressRatio: Double) {
    if (progressRatio > 1 || progressRatio < 0) {
      Log.d(TAG, "setProgressRatio: invalid progress ratio ($progressRatio)")
    }
    seekBar.progress = (progressRatio * max).roundToInt()
  }

  fun setUpGradientBar(
    drawable: Drawable,
    colorTag: Int,
    sliderType: SliderType
  ) {
    gradientBar.background = drawable
    setTag(R.id.color_picker_library_color_tag, colorTag)
    this.sliderType = sliderType
  }

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