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

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
      super(context, attrs, defStyle)
  {
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
        internalShadeRatio = progress
        onColorChanged()
      }
    }

    super.initSlider(getShadeGradient(), onSliderProgressChangedListener)
  }

  private fun initColorPicker() {
    window = findViewById(R.id.color_picker_library_large_window)

    window.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
      override fun onGlobalLayout() {
        if (window.height > 0) {
          circleDiameter = min(window.width, window.height)

          if (window.width != window.height) {
            val params = window.layoutParams
            params.height = circleDiameter
            params.width = circleDiameter
            window.layoutParams = params
          }

          window.viewTreeObserver.removeOnGlobalLayoutListener(this)
          val background = getGradientCircle(circleDiameter)

          window = findViewById(R.id.color_picker_library_large_window)

          window.background = background
          initThumb()
        }
      }
    })
  }

  private fun initThumb() {
    thumb = findViewById(R.id.color_picker_library_large_thumb)
    thumb.y = circleDiameter / 2f
    super.initThumb(thumb)
  }



  // ------------------------ CIRCLE ------------------------

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

  override fun onColorChanged() {
    val color = getCurrentColor()

    super.updateNewPreviewColor(color)

    slider.setGradientBarDrawable(getShadeGradient())
    listener?.onColorChanged(color)
  }



  // ------------------------ THUMB ------------------------

  override fun moveThumb(x: Float, y: Float) {
    val radialPositionRatio = getRadialPositionRatio(x, y)
    val radius = circleDiameter / 2.0
    val snapRange = (0.9 * radius)..(1.1 * radius)
    val snapToCenter = radialSnapToCentre && x in snapRange && y in snapRange

    val thumbX = when {
      snapToCenter -> radius.toFloat()
      radialPositionRatio in 0.0..1.0 -> x
      else -> (radius + (x - radius) / radialPositionRatio).toFloat()
    }

    val thumbY = when {
      snapToCenter -> radius.toFloat()
      radialPositionRatio in 0.0..1.0 -> y
      else -> (radius + (y - radius) / radialPositionRatio).toFloat()
    }

    thumb.x = thumbX
    thumb.y = thumbY

    onThumbPositionChanged(thumbX, thumbY)
  }

  override fun onThumbPositionChanged(x: Float, y: Float) {
    internalTintRatio = 1.0 - getRadialPositionRatio(x, y)
    internalColorRatio = 1.0 - getCircumferentialPositionRatio(x, y)
    onColorChanged()
  }

  override fun updateThumbOnColorRatioChange() {
    val acuteAngle = getAcuteAngleUsingColorRatio()

    val thumbX = getXPositionUsingRatios(acuteAngle)
    val thumbY = getYPositionUsingRatios(acuteAngle)

    if (thumb.x != thumbX) thumb.x = thumbX
    if (thumb.y != thumbY) thumb.y = thumbY
  }

  override fun updateThumbOnShadeRatioChange() {
    if (slider.getProgressRatio() != internalShadeRatio) {
      slider.setProgressRatio(internalShadeRatio)
    }
  }

  override fun updateThumbOnTintRatioChange() {
    val acuteAngle = getAcuteAngleUsingColorRatio()

    val thumbX = getXPositionUsingRatios(acuteAngle)
    val thumbY = getYPositionUsingRatios(acuteAngle)

    if (thumb.x != thumbX) thumb.x = thumbX
    if (thumb.y != thumbY) thumb.y = thumbY
  }

  private fun getAcuteAngleUsingColorRatio(): Double {
    val acuteRatio = when {
      internalColorRatio < 0.25 -> internalColorRatio
      internalColorRatio < 0.5 -> 0.5 - internalColorRatio
      internalColorRatio < 0.75 -> internalColorRatio - 0.5
      else -> 1.0 - internalColorRatio
    }

    return acuteRatio * (2.0 * Math.PI)
  }

  private fun getCenterOffsetUsingTintRatio(): Double {
    return (circleDiameter / 2.0) * (1.0 - internalTintRatio)
  }

  private fun getXPositionUsingRatios(acuteAngle: Double): Float {
    val radius = circleDiameter / 2.0

    val horizontalOffset = getCenterOffsetUsingTintRatio() * cos(acuteAngle)

    return when {
      internalColorRatio < 0.25 || internalColorRatio > 0.75 -> (radius + horizontalOffset).toFloat()
      else -> (radius - horizontalOffset).toFloat()
    }
  }

  private fun getYPositionUsingRatios(acuteAngle: Double): Float {
    val radius = circleDiameter / 2.0

    val verticalOffset = getCenterOffsetUsingTintRatio() * sin(acuteAngle)

    return when {
      internalColorRatio > 0.5 -> (radius - verticalOffset).toFloat()
      else -> (radius + verticalOffset).toFloat()
    }
  }

  private fun getTintRatioFromThumbPosition(): Double {
    val radius = circleDiameter / 2.0

    val adjustedThumbX = when {
      thumb.x < radius -> radius - thumb.x
      else -> thumb.x - radius
    }

    val adjustedThumbY = when {
      thumb.y < radius -> radius - thumb.y
      else -> thumb.y - radius
    }

    return 1f - (sqrt(adjustedThumbX.pow(2) + adjustedThumbY.pow(2))) / radius
  }



  // ------------------------ UTIL ------------------------

  private fun radToDeg(rads: Double): Int {
    return (rads * 360.0 / (2.0 * Math.PI)).toInt()
  }

  companion object {
    private const val TAG = "RadialColorPickerView"
  }
}