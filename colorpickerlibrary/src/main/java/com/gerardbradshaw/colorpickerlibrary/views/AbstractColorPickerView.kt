package com.gerardbradshaw.colorpickerlibrary.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import com.gerardbradshaw.colorpickerlibrary.R
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * AbstractColorPickerView can be used to build RGB color picker views. It provides functions for
 * calculating pure, shaded, and tinted colors based on ratios.
 */
abstract class AbstractColorPickerView : FrameLayout {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    if (attrs != null) saveAttrs(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
    if (attrs != null) saveAttrs(attrs)
  }



  // ------------------------ PROPERTIES ------------------------

  /** Color ratio that does not alert the UI when changed. */
  protected var internalColorRatio = 0.0

  /** Shade ratio that does not alert the UI when changed. */
  protected var internalShadeRatio = 0.0

  /** Tint ratio that does not alert the UI when changed. */
  protected var internalTintRatio = 0.0

  /**
   * The color ratio between 0-1 defines the pure/base color (no tint or shade). A ratio of 0.0
   * represents RED, and increasing to 1.0 moves across the spectrum (through R-O-Y-G-B-I-V) back to
   * RED.
   */
  var colorRatio = 0.0
    get() = internalColorRatio
    set(value) {
      internalColorRatio = getSafeRatio(value)
      field = internalColorRatio
      onColorChanged()
      updateUIOnColorRatioChange()
    }

  /**
   * The shade ratio between 0-1 defines the darkness of the color. A shade ratio of 0.0 has no
   * darkness, while a ratio of 1.0 is pure BLACK.
   */
  var shadeRatio = 0.0
    get() = internalShadeRatio
    set(value) {
      internalShadeRatio = getSafeRatio(value)
      field = internalShadeRatio
      onColorChanged()
      updateUIOnShadeRatioChange()
    }

  /**
   * The tint ratio between 0-1 defines the whiteness of the color. A tint ratio of 0.0 has no
   * whiteness, while a ratio of 1.0 is pure WHITE.
   */
  var tintRatio = 0.0
    get() = internalTintRatio
    set(value) {
      internalTintRatio = getSafeRatio(value)
      field = internalTintRatio
      onColorChanged()
      updateUIOnTintRatioChange()
    }

  protected var isPreviewEnabled = true
  var listener: ColorChangedListener? = null



  // ------------------------ INITIALIZATION ------------------------

  private fun saveAttrs(attrs: AttributeSet) {
    context.theme.obtainStyledAttributes(
      attrs, R.styleable.AbstractColorPickerView, 0, 0).apply {
      try {
        isPreviewEnabled = getBoolean(R.styleable.AbstractColorPickerView_enablePreview, true)

      } finally { recycle() }
    }
  }

  /**
   * Returns a [GradientDrawable] of the color spectrum from RED through ORANGE, YELLOW, GREEN,
   * BLUE, INDIGO, and VIOLET.
   */
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

  /**
   * Returns a [GradientDrawable] of the current tinted color through to BLACK. The current tinted
   * color is calculated using the [colorRatio] and [tintRatio].
   */
  fun getShadeGradient(): GradientDrawable {
    return GradientDrawable(
      GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
      Color.parseColor(String.format("#%06X", 0xFFFFFF and getTintedColor(getPureColor()))),
      Color.parseColor("#000000")
    ))
  }

  /**
   * Returns a [GradientDrawable] of the current shaded color through to WHITE. The current shaded
   * color is calculated using the [colorRatio] and [shadeRatio].
   */
  fun getTintGradient(): GradientDrawable {
    return GradientDrawable(
      GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
      Color.parseColor(String.format("#%06X", 0xFFFFFF and getShadedColor(getPureColor()))),
      Color.parseColor(String.format("#%06X", 0xFFFFFF and getTintedColor(getShadedColor(getPureColor()), 1.0)))
    ))
  }



  // ------------------------ PUBLIC FUNCTIONS ------------------------

  /**
   * Gets the current tinted and shaded color. This is calculated using the [colorRatio],
   * [shadeRatio], and [tintRatio].
   */
  fun getCurrentColor(): Int {
    return getTintedColor(getShadedColor(getPureColor()))
  }

  /** Gets the current pure color. This is calculated using the [colorRatio] only. */
  fun getPureColor(): Int {
    return getIthPureColor(
      (colorCount * internalColorRatio).roundToInt()
    )
  }

  /**
   * Gets the current shaded color. This is calculated using the [colorRatio] and [shadeRatio] only.
   */
  fun getShadedColor(color: Int, shadeFactor: Double = 1.0 - internalShadeRatio): Int {
    val red = (Color.red(color) * shadeFactor).roundToInt()
    val green = (Color.green(color) * shadeFactor).roundToInt()
    val blue = (Color.blue(color) * shadeFactor).roundToInt()

    return Color.argb(255, red, green, blue)
  }

  /**
   * Gets the current tinted color. This is calculated using the [colorRatio] and [tintRatio] only.
   */
  fun getTintedColor(color: Int, tintRatio: Double = this.internalTintRatio): Int {
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

  /**
   * Called when one of the ratios is updated. Also see [updateUIOnColorRatioChange],
   * [updateUIOnShadeRatioChange], and [updateUIOnTintRatioChange].
   */
  protected abstract fun onColorChanged()

  /**
   * Called when [colorRatio] is updated programmatically. This should be used to update UI element
   * used to represent the ratio.
   * typically called after [onColorChanged].
   */
  protected abstract fun updateUIOnColorRatioChange()

  /**
   * Called when [shadeRatio] is updated programmatically. This should be used to update UI element
   * used to represent the ratio.
   */
  protected abstract fun updateUIOnShadeRatioChange()

  /**
   * Called when [tintRatio] is updated programmatically. This should be used to update UI element
   * used to represent the ratio.
   */
  protected abstract fun updateUIOnTintRatioChange()

  private fun getSafeRatio(ratio: Double): Double {
    return when {
      ratio < 0.0 -> 0.0
      ratio > 1.0 -> 1.0
      else -> ratio
    }
  }


  // ------------------------ INNER CLASSES ------------------------

  companion object {
    private const val TAG = "AbstractColorPickerView"
    private const val colorCount = 16777216

    fun getIthPureColor(i: Int): Int {
      val sectionCount = 6.0
      val colorsPerSection = colorCount / sectionCount

      val currentSectionNumber = floor(i.toDouble() / colorsPerSection)
      val positionInSection =
        (i.toDouble() - (currentSectionNumber * colorsPerSection)) / (colorsPerSection)

      val full = 255
      val fadeIn = (255.0 * positionInSection).roundToInt()
      val fadeOut = (255.0 * (1.0 - positionInSection)).roundToInt()
      val none = 0

      return when {
        currentSectionNumber < 1 -> Color.argb(full, full, fadeIn, none)
        currentSectionNumber < 2 -> Color.argb(full, fadeOut, full, none)
        currentSectionNumber < 3 -> Color.argb(full, none, full, fadeIn)
        currentSectionNumber < 4 -> Color.argb(full, none, fadeOut, full)
        currentSectionNumber < 5 -> Color.argb(full, fadeIn, none, full)
        currentSectionNumber <= 6 -> {
          if (i != colorCount) Color.argb(full, full, none, fadeOut)
          else Color.argb(full, full, none, 1)
        }
        else -> {
          Log.d(TAG, "getIthColor: i too large. Returning white.")
          Color.argb(full, none, none, none)
        }
      }
    }
  }

  interface ColorChangedListener {
    fun onColorChanged(color: Int)
  }
}