package com.gerardbradshaw.colorpicker

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import kotlin.math.roundToInt

class ColorSlider : FrameLayout {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)


  // ------------------------ PROPERTIES ------------------------

  val max = 16777216.0
  private lateinit var seekBar: SeekBar
  private lateinit var gradientBar: FrameLayout
  private var listener: OnProgressChangedListener? = null


  // ------------------------ INITIALIZATION ------------------------

  init {
    View.inflate(context, R.layout.view_color_slider, this)
    initView()
  }

  private fun initView() {
    gradientBar = findViewById(R.id.slider_gradient_bar)
    seekBar = findViewById(R.id.slider_seek_bar)

    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onStartTrackingTouch(seekBar: SeekBar?) { /* Required empty */ }
      override fun onStopTrackingTouch(seekBar: SeekBar?) { /* Required empty */ }

      override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        listener?.onProgressChanged(progress.toDouble() / max)
      }
    })
  }


  // ------------------------ PUBLIC METHODS ------------------------

  fun setProgress(progress: Double) {
    seekBar.progress = (progress * max).roundToInt()
  }

  fun setGradientBarDrawable(drawable: Drawable) {
    gradientBar.background = drawable
  }

  fun setOnProgressChangedListener(listener: OnProgressChangedListener) {
    this.listener = listener
  }


  // ------------------------ HELPERS ------------------------

  interface OnProgressChangedListener {
    fun onProgressChanged(progress: Double)
  }

  companion object {
    private const val TAG = "ColorSlider"
  }

}