package com.gerardbradshaw.library

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.updateLayoutParams

class SquareColorPickerView : AbstractColorPicker {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)


  // ------------------------ PROPERTIES ------------------------

  private lateinit var slider: ColorSlider
  private lateinit var square: FrameLayout
  private lateinit var thumb: ImageView
  private lateinit var oldColorPreview: ImageView
  private lateinit var newColorPreview: ImageView
  private var oldColor: Int = Color.RED


  // ------------------------ INITIALIZATION ------------------------

  init {
    View.inflate(context, R.layout.view_picker_square, this)
    initView()
  }

  private fun initView() {
    initSlider()
    initPreviews()
    initSquare()
    initThumb()
  }

  private fun initSlider() {
    slider = findViewById(R.id.square_color_slider)

    slider.setGradientBarDrawable(getSpectrumGradient())

    slider.setOnProgressChangedListener(object : ColorSlider.OnProgressChangedListener {
      override fun onProgressChanged(progress: Double) {
        colorRatio = progress
        onColorChanged()
      }
    })
  }

  private fun initPreviews() {
    oldColorPreview = findViewById(R.id.square_preview_old)
    oldColorPreview.setColorFilter(oldColor)
    oldColorPreview.tag = oldColor // tagged for testing purposes

    newColorPreview = findViewById(R.id.square_preview_new)
    newColorPreview.setColorFilter(oldColor)
    newColorPreview.tag = oldColor // tagged for testing purposes
  }

  private fun initSquare(pureColor: Int = oldColor) {
    val background = getSquareBackgroundDrawable(pureColor)

    square = findViewById(R.id.square_square)
    square.background = background
    square.tag = pureColor // tagged for testing purposes
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun initThumb() {
    thumb = findViewById(R.id.square_thumb)

    thumb.viewTreeObserver.addOnDrawListener {
      tintRatio = 1 - ((thumb.x - square.x) / square.width.toDouble())
      shadeRatio = -(square.y - thumb.y) / square.height.toDouble()
      onColorChanged()
    }

    square.setOnTouchListener { _, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          moveThumb(event.x, event.y)
          true
        }

        MotionEvent.ACTION_MOVE -> {
          moveThumb(event.x, event.y)
          true
        }
        else -> super.onTouchEvent(event)
      }
    }
  }



  // ------------------------ INTERACTION ------------------------

  private fun moveThumb(x: Float, y: Float) {
    thumb.x =
      when {
        x < square.x -> square.x
        x > square.width.toFloat() -> square.width.toFloat()
        else -> x
      }

    thumb.y =
      when {
        y < square.y -> square.y
        y > square.height.toFloat() -> square.height.toFloat()
        else -> y
      }
  }

  override fun onColorChanged() {
    val color = getCurrentColor()

    newColorPreview.setColorFilter(color)
    newColorPreview.tag = color // tagged for testing purposes

    initSquare(getPureColor())
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