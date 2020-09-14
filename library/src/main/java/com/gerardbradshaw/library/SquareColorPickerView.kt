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
import android.widget.FrameLayout
import android.widget.ImageView

class SquareColorPickerView : com.gerardbradshaw.library.AbstractColorPicker {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)


  // ------------------------ PROPERTIES ------------------------

  private lateinit var slider: com.gerardbradshaw.library.ColorSlider
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

    slider.setOnProgressChangedListener(object : com.gerardbradshaw.library.ColorSlider.OnProgressChangedListener {
      override fun onProgressChanged(progress: Double) {
        colorRatio = progress
        onColorChanged()
      }
    })
  }

  private fun initPreviews() {
    val oldColorHex = colorToHex(oldColor)

    oldColorPreview = findViewById(R.id.square_preview_old)
    oldColorPreview.setColorFilter(oldColorHex)

    newColorPreview = findViewById(R.id.square_preview_new)
    newColorPreview.setColorFilter(oldColorHex)
  }

  private fun initSquare(pureColor: Int = oldColor) {
    val background = getSquareBackgroundDrawable(pureColor)

    square = findViewById(R.id.square_square)
    square.background = background
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun initThumb() {
    thumb = findViewById(R.id.square_thumb)

    square.setOnTouchListener { v, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          moveThumb(event.x, event.y)
          onColorChanged()
          true
        }

        MotionEvent.ACTION_MOVE -> {
          moveThumb(event.x, event.y)
          onColorChanged()
          true
        }

        else -> {
          Log.d(TAG, "initThumb: event was ${event.action}")
          super.onTouchEvent(event)
        }
      }
    }
  }

  private fun moveThumb(x: Float, y: Float) {
    thumb.x =
      when {
        x < square.x -> square.x
        x > square.width.toFloat() -> square.width.toFloat()
        else -> x
      }
    tintRatio = 1 - ((thumb.x - square.x) / square.width.toDouble())

    thumb.y =
      when {
        y < square.y -> square.y
        y > square.height.toFloat() -> square.height.toFloat()
        else -> y
      }
    shadeRatio = -(square.y - thumb.y) / square.height.toDouble()
  }


  override fun onColorChanged() {
    val colorHex = getCurrentColorHex()
    newColorPreview.setColorFilter(colorHex)
    initSquare(getPureColor())
    listener?.onColorChanged(colorHex)
  }

  // ------------------------ INITIALIZATION ------------------------

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