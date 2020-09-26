package com.gerardbradshaw.library.views

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
import com.gerardbradshaw.library.R
import com.gerardbradshaw.library.util.ColorSlider

class SquareColorPickerView :
  AbstractLargeColorPickerView {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)



  // ------------------------ INITIALIZATION ------------------------

  init {
    View.inflate(context,
      R.layout.view_picker_large, this)
    initView()
  }

  private fun initView() {
    initSlider()
    super.initPreviews()
    initColorPicker()
    initThumb()
    initListener()
  }

  private fun initSlider() {
    val onSliderProgressChangedListener = object : ColorSlider.OnProgressChangedListener {
      override fun onProgressChanged(progress: Double) {
        colorRatio = progress
        onColorChanged()
      }
    }

    super.initSlider(getSpectrumGradient(), onSliderProgressChangedListener)
  }

  private fun initColorPicker(pureColor: Int = oldColor) {
    val background = getSquareBackgroundDrawable(pureColor)

    colorPicker = findViewById(R.id.large_color_window)
    colorPicker.background = background
    colorPicker.tag = pureColor // tagged for testing purposes
  }

  private fun initThumb() {
    val thumbOnDrawObserver = ViewTreeObserver.OnDrawListener {
      tintRatio = 1 - ((thumb.x - colorPicker.x) / colorPicker.width.toDouble())
      shadeRatio = -(colorPicker.y - thumb.y) / colorPicker.height.toDouble()
      onColorChanged()
    }

    super.initThumb(thumbOnDrawObserver, null)
  }



  // ------------------------ INTERACTION ------------------------

  override fun moveThumb(x: Float, y: Float) {
    Log.d(TAG, "moveThumb")
    thumb.x =
      when {
        x < colorPicker.x -> colorPicker.x
        x > colorPicker.width.toFloat() -> colorPicker.width.toFloat()
        else -> x
      }

    thumb.y =
      when {
        y < colorPicker.y -> colorPicker.y
        y > colorPicker.height.toFloat() -> colorPicker.height.toFloat()
        else -> y
      }
  }

  override fun onColorChanged() {
    val color = getCurrentColor()

    super.updateNewPreviewColor(color)

    initColorPicker(getPureColor())
    listener?.onColorChanged(color)
  }



  // ------------------------ UTIL ------------------------

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

  companion object {
    private const val TAG = "SquareColorPickerView"
  }
}