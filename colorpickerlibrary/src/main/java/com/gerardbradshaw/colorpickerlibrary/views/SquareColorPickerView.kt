package com.gerardbradshaw.colorpickerlibrary.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView

class SquareColorPickerView :
  AbstractLargeColorPickerView {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context) {
    initView()
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    initView(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
      super(context, attrs, defStyle)
  {
    initView(attrs)
  }


  // ------------------------ INITIALIZATION ------------------------

  init {
    View.inflate(context, R.layout.color_picker_library_view_picker_large, this)
  }

  private fun initView(attrs: AttributeSet? = null) {
    initSlider()
    super.initPreviews()
    initColorPicker()
    initThumb()
    initListener()
  }

  private fun initSlider() {
    val onSliderProgressChangedListener = object : ColorSliderView.OnProgressChangedListener {
      override fun onProgressChanged(progress: Double) {
        internalColorRatio = progress
        onColorChanged()
      }
    }

    super.initSlider(getSpectrumGradient(), onSliderProgressChangedListener)
  }

  private fun initColorPicker(pureColor: Int = oldColor) {
    val background = getSquareBackgroundDrawable(pureColor)

    colorPicker = findViewById(R.id.color_picker_library_large_color_window)
    colorPicker.background = background
    colorPicker.tag = pureColor // tagged for testing purposes
  }

  private fun initThumb() {
    super.initThumb(null)
  }



  // ------------------------ SQUARE ------------------------

  private fun getSquareBackgroundDrawable(pureColor: Int): Drawable {
    val sf: ShapeDrawable.ShaderFactory = object : ShapeDrawable.ShaderFactory() {
      override fun resize(width: Int, height: Int): Shader {
        val grad1 = LinearGradient(0f,0f, 0f, height.toFloat(),
          Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP)

        val grad2 = LinearGradient(0f,0f, width.toFloat(), 0f,
          Color.WHITE, pureColor, Shader.TileMode.CLAMP)

        return ComposeShader(grad1, grad2, PorterDuff.Mode.MULTIPLY)
      }
    }

    val paintDrawable = PaintDrawable()
    paintDrawable.shape = RectShape()
    paintDrawable.shaderFactory = sf

    return paintDrawable
  }

  override fun onColorChanged() {
    val color = getCurrentColor()

    super.updateNewPreviewColor(color)

    initColorPicker(getPureColor())
    listener?.onColorChanged(color)
  }



  // ------------------------ THUMB ------------------------

  override fun moveThumb(x: Float, y: Float) {
    val thumbX = when {
      x < colorPicker.x -> colorPicker.x
      x > colorPicker.width.toFloat() -> colorPicker.width.toFloat()
      else -> x
    }

    val thumbY = when {
      y < colorPicker.y -> colorPicker.y
      y > colorPicker.height.toFloat() -> colorPicker.height.toFloat()
      else -> y
    }

    thumb.x = thumbX
    thumb.y = thumbY

    onThumbPositionChanged(thumbX, thumbY)
  }

  override fun onThumbPositionChanged(x: Float, y: Float) {
    internalTintRatio = 1 - ((thumb.x - colorPicker.x) / colorPicker.width.toDouble())
    internalShadeRatio = -(colorPicker.y - thumb.y) / colorPicker.height.toDouble()
    onColorChanged()
  }

  override fun updateThumbOnColorRatioChange() {
    when {
      internalColorRatio > 1.0 -> internalColorRatio = 1.0
      internalColorRatio < 0.0 -> internalColorRatio = 0.0
    }

    slider.setProgressRatio(internalColorRatio)
  }

  override fun updateThumbOnShadeRatioChange() {
    when {
      internalShadeRatio > 1.0 -> internalShadeRatio = 1.0
      internalShadeRatio < 0.0 -> internalShadeRatio = 0.0
    }

    moveThumb(thumb.x, (internalShadeRatio * colorPicker.height).toFloat())
  }

  override fun updateThumbOnTintRatioChange() {
    when {
      internalTintRatio > 1.0 -> internalTintRatio = 1.0
      internalTintRatio < 0.0 -> internalTintRatio = 0.0
    }

    moveThumb(((1.0 - internalTintRatio) * colorPicker.width).toFloat(), thumb.y)
  }



  // ------------------------ UTIL ------------------------

  companion object {
    private const val TAG = "SquareColorPickerView"
  }
}