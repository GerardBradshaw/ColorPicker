package com.gerardbradshaw.library

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

abstract class AbstractColorPicker : FrameLayout {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    initWithAttrs(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
    initWithAttrs(attrs)
  }

  protected fun initWithAttrs(attrs: AttributeSet?) {
    if (attrs == null) return

    context.theme.obtainStyledAttributes(attrs, R.styleable.CompactColorPickerView, 0, 0).apply {
      try {
        menuType = getInteger(R.styleable.CompactColorPickerView_menuType, 0)
        isPreviewEnabled = getBoolean(R.styleable.CompactColorPickerView_enablePreview, true)

      } finally {
        recycle()
      }
    }
  }

  // ------------------------ PROPERTIES ------------------------

  var colorCount = context.resources.getInteger(R.integer.spectrum_color_count).toFloat()
  var colorRatio = 0.0
  var shadeRatio = 0.0
  var tintRatio = 0.0

  protected var menuType = 0
  protected var isPreviewEnabled = true
  var listener: ColorChangedListener? = null



  // ------------------------ INITIALIZATION ------------------------

  fun getSpectrumGradient(): GradientDrawable {
    return GradientDrawable(
      GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
      Color.parseColor("#FF0000"),
      Color.parseColor("#FFFF00"),
      Color.parseColor("#00FF00"),
      Color.parseColor("#00FFFF"),
      Color.parseColor("#0000FF"),
      Color.parseColor("#FF00FF"),
      Color.parseColor("#FF0000")
    ))
  }

  fun getShadeGradient(): GradientDrawable {
    return GradientDrawable(
      GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
      Color.parseColor(String.format("#%06X", 0xFFFFFF and getTintedColor(getPureColor()))),
      Color.parseColor("#000000")
    ))
  }

  fun getTintGradient(): GradientDrawable {
    return GradientDrawable(
      GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
      Color.parseColor(String.format("#%06X", 0xFFFFFF and getShadedColor(getPureColor()))),
      Color.parseColor(String.format("#%06X", 0xFFFFFF and getTintedColor(getShadedColor(getPureColor()), 1.0)))
    ))
  }



  // ------------------------ PUBLIC FUNCTIONS ------------------------

  /** Returns the current color including shade and tint as an Int. */
  fun getCurrentColor(): Int {
    return getTintedColor(getShadedColor(getPureColor()))
  }

  /** Returns the current color including shade and tint as a hexadecimal Int. */
  fun getCurrentColorHex(): Int {
    return colorToHex(getCurrentColor())
  }

  fun getPureColor(): Int {
    return getIthPureColor((colorCount * colorRatio).roundToInt())
  }

  fun getShadedColor(color: Int, shadeFactor: Double = 1.0 - shadeRatio): Int {
    val red = (Color.red(color) * shadeFactor).roundToInt()
    val green = (Color.green(color) * shadeFactor).roundToInt()
    val blue = (Color.blue(color) * shadeFactor).roundToInt()

    return Color.argb(255, red, green, blue)
  }

  fun getTintedColor(color: Int, tintRatio: Double = this.tintRatio): Int {
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)

    return when (max(red, max(green, blue))) {
      red -> {
        Color.argb(255,
          red,
          green + ((red - green).toFloat() * tintRatio).roundToInt(),
          blue + ((red - blue).toFloat() * tintRatio).roundToInt())
      }

      green -> {
        Color.argb(255,
          red + ((green - red).toFloat() * tintRatio).roundToInt(),
          green,
          blue + ((green - blue).toFloat() * tintRatio).roundToInt())
      }

      blue -> {
        Color.argb(255,
          red + ((blue - red).toFloat() * tintRatio).roundToInt(),
          green + ((blue - green).toFloat() * tintRatio).roundToInt(),
          blue)
      }

      else -> {
        Log.d(TAG, "getCurrentColor: unable to tint")
        Color.argb(255, red, green, blue)
      }
    }
  }

  fun setOnColorSelectedListener(listener: ColorChangedListener) {
    this.listener = listener
  }

  // ------------------------ HELPERS ------------------------

  private fun getIthPureColor(i: Int): Int {
    val gradientCount = 6.0
    val colorsPerGradient = colorCount / gradientCount

    val currentGradientNumber = floor(i.toDouble() / colorsPerGradient)
    val positionInGradient = (i.toDouble() - (currentGradientNumber * colorsPerGradient)) / (colorsPerGradient)

    val full = 255
    val fadeIn = (255.0 * positionInGradient).roundToInt()
    val fadeOut = (255.0 * (1.0 - positionInGradient)).roundToInt()
    val none = 0

    return when {
      currentGradientNumber < 1 -> Color.argb(full, full, fadeIn, none)
      currentGradientNumber < 2 -> Color.argb(full, fadeOut, full, none)
      currentGradientNumber < 3 -> Color.argb(full, none, full, fadeIn)
      currentGradientNumber < 4 -> Color.argb(full, none, fadeOut, full)
      currentGradientNumber < 5 -> Color.argb(full, fadeIn, none, full)
      currentGradientNumber <= 6 -> {
        if (i != colorCount.toInt()) Color.argb(full, full, none, fadeOut)
        else Color.argb(full, full, none, 1)
      }
      else -> {
        Log.d(TAG, "getIthColor: i too large. Returning white.")
        Color.argb(full, none, none, none)
      }
    }
  }

  protected abstract fun onColorChanged()

  protected fun colorToHex(color: Int): Int {
    val hexColor = String.format("#%06X", 0xFFFFFF and getCurrentColor())
    return Color.parseColor(hexColor)
  }


  // ------------------------ INNER CLASSES ------------------------

  companion object {
    private const val TAG = "AbstractColorPicker"
  }

  interface ColorChangedListener {
    fun onColorChanged(hexColor: Int)
  }
}