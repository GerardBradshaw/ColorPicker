package com.gerardbradshaw.colorpickerlibrary.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView
import kotlin.math.*

class RadialColorPickerView :
  AbstractLargeColorPickerView {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context) {
    initView()
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    initView(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
    initView(attrs)
  }

  private fun saveRadialAttrs(attrs: AttributeSet) {
    context.theme.obtainStyledAttributes(
      attrs, R.styleable.RadialColorPickerView, 0, 0).apply {
      try {
        radialSnapToCentre = getBoolean(R.styleable.RadialColorPickerView_snapThumbToCentre, true)

      } finally { recycle() }
    }
  }



  // ------------------------ PROPERTIES ------------------------

  private var circleDiameter = 0
  private var radialSnapToCentre = true



  // ------------------------ INITIALIZATION ------------------------

  init {
    View.inflate(context, R.layout.color_picker_library_view_picker_large, this)
  }

  private fun initView(attrs: AttributeSet? = null) {
    if (attrs != null) saveRadialAttrs(attrs)
    initSlider()
    super.initPreviews()
    initColorPicker()
    initThumb()
    initListener()
  }

  private fun initSlider() {
    val onSliderProgressChangedListener = object : ColorSliderView.OnProgressChangedListener {
      override fun onProgressChanged(progress: Double) {
        shadeRatio = progress
        onColorChanged()
      }
    }

    super.initSlider(getShadeGradient(), onSliderProgressChangedListener)
  }

  private fun initColorPicker() {
    colorPicker = findViewById(R.id.color_picker_library_large_color_window)

    colorPicker.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
      override fun onGlobalLayout() {
        if (colorPicker.height > 0) {
          circleDiameter = min(colorPicker.width, colorPicker.height)

          if (colorPicker.width != colorPicker.height) {
            val params = colorPicker.layoutParams
            params.height = circleDiameter
            params.width = circleDiameter
            colorPicker.layoutParams = params
          }

          colorPicker.viewTreeObserver.removeOnGlobalLayoutListener(this)
          val background = getGradientCircle(circleDiameter)

          colorPicker = findViewById(R.id.color_picker_library_large_color_window)

          colorPicker.background = background
          initThumb()
        }
      }
    })
  }

  private fun initThumb() {
    thumb = findViewById(R.id.color_picker_library_large_thumb)

    thumb.y = circleDiameter / 2f

    val thumbOnDrawObserver = ViewTreeObserver.OnDrawListener {
      tintRatio = 1.0 - getRadialPositionRatio(thumb.x, thumb.y)
      colorRatio = 1.0 - getCircumferentialPositionRatio(thumb.x, thumb.y)
      onColorChanged()
    }

    super.initThumb(thumbOnDrawObserver, thumb)
  }



  // ------------------------ INTERACTION ------------------------

  override fun moveThumb(x: Float, y: Float) {
    val radialPositionRatio = getRadialPositionRatio(x, y)
    val radius = circleDiameter / 2.0
    val snapRange = (0.9 * radius)..(1.1 * radius)

    if (radialSnapToCentre && x in snapRange && y in snapRange) {
      thumb.x = radius.toFloat()
      thumb.y = radius.toFloat()
      return
    }

    thumb.x =
      if (radialPositionRatio in 0.0..1.0) x
      else (radius + (x - radius) / radialPositionRatio).toFloat()

    thumb.y =
      if (radialPositionRatio in 0.0..1.0) y
      else (radius + (y - radius) / radialPositionRatio).toFloat()
  }

  override fun onColorChanged() {
    val color = getCurrentColor()

    super.updateNewPreviewColor(color)

    slider.setGradientBarDrawable(getShadeGradient())
    listener?.onColorChanged(color)
  }



  // ------------------------ UTIL ------------------------

  override fun onShadeRatioChanged() {
    slider.setProgressRatio(shadeRatio)
  }

  override fun onTintRatioChanged() {
    // TODO
  }

  override fun onColorRatioChanged() {
    // TODO
  }

  private fun getRadialPositionRatio(x: Float, y: Float): Double {
    val xRatio = getXPositionRatio(x)
    val yRatio = getYPositionRatio(y)

    return sqrt(xRatio.pow(2) + yRatio.pow(2))
  }

  private fun getXPositionRatio(x: Float): Double {
    val radius = circleDiameter / 2.0
    val distanceFromCentre = x - radius
    return distanceFromCentre / radius
  }

  private fun getYPositionRatio(y: Float): Double {
    val radius = circleDiameter / 2.0
    val distanceFromCentre = radius - y
    return distanceFromCentre / radius
  }

  private fun getCircumferentialPositionRatio(x: Float, y: Float): Double {
    val xRatio = getXPositionRatio(x)
    val yRatio = getYPositionRatio(y)

    var angle =
      if (xRatio != 0.0 && yRatio != 0.0) atan(yRatio / xRatio)
      else 0.0

    angle += when {
      xRatio > 0.0 && yRatio > 0.0 -> 0.0
      xRatio < 0.0 -> Math.PI
      else -> 2.0 * Math.PI
    }

    return (angle) / (2.0 * Math.PI)
  }

  private fun radToDeg(rads: Double): Int {
    return (rads * 360.0 / (2.0 * Math.PI)).toInt()
  }

  private fun getGradientCircle(diameter: Int): Drawable {
    val spectrumDrawable = GradientDrawable(
      GradientDrawable.Orientation.BL_TR, intArrayOf(
        Color.parseColor("#FF0000"),
        Color.parseColor("#FFFF00"),
        Color.parseColor("#00FF00"),
        Color.parseColor("#00FFFF"),
        Color.parseColor("#0000FF"),
        Color.parseColor("#FF00FF"),
        Color.parseColor("#FF0000")))

    spectrumDrawable.gradientType = GradientDrawable.SWEEP_GRADIENT

    val roundGradientDrawable =
      RoundedBitmapDrawableFactory
        .create(resources, spectrumDrawable.toBitmap(diameter, diameter, null))

    roundGradientDrawable.cornerRadius = diameter / 2f

    val tintDrawable = GradientDrawable(
      GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
        Color.WHITE,
        Color.TRANSPARENT))

    tintDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
    tintDrawable.gradientRadius = diameter / 2f

    return LayerDrawable(arrayOf(roundGradientDrawable, tintDrawable))
  }

  companion object {
    private const val TAG = "SquareColorPickerView"
  }
}