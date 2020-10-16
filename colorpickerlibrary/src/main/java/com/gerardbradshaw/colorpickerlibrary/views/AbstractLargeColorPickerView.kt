package com.gerardbradshaw.colorpickerlibrary.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView

/**
 * AbstractLargeColorPickerView was used to build the large RGB color picker [RadialColorPickerView]
 * and [SquareColorPickerView]. It's fiarly niche for these pickers, but may be useful for other,
 * large [AbstractColorPickerView]s.
 */
abstract class AbstractLargeColorPickerView : AbstractColorPickerView {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)



  // ------------------------ PROPERTIES ------------------------

  private lateinit var newColorPreview: ImageView
  private lateinit var oldColorPreview: ImageView

  /** The slider used to change 1 of the 3 ratios in UI. */
  protected lateinit var slider: ColorSliderView

  /** The window used to change 2 of the 3 ratios in UI. */
  protected lateinit var window: FrameLayout

  /** The thumb contained in the [window]. */
  protected lateinit var thumb: ImageView

  /** The color displayed in the old color preview . */
  protected var oldColor: Int = Color.RED



  // ------------------------ INITIALIZATION ------------------------

  /** Initiates the [ColorSliderView] found in the large layout by calling
   * [ColorSliderView.setUpGradientBar] and [ColorSliderView.setOnProgressChangedListener] using the
   * provided parameters.
   */
  protected fun initSlider(
    gradient: GradientDrawable,
    colorTag: Int,
    sliderTypeTag: ColorSliderView.SliderType,
    onProgressChangedListener: ColorSliderView.OnProgressChangedListener
  ) {
    slider = findViewById(R.id.color_picker_library_large_color_slider)
    slider.setUpGradientBar(gradient, colorTag, sliderTypeTag)
    slider.setOnProgressChangedListener(onProgressChangedListener)
  }

  /**
   * Sets the visibility of the previews depending on whether showPreview is true in the
   * AttributeSet.
   */
  protected fun initPreviews() {
    if (isPreviewEnabled) {
      oldColorPreview = findViewById(R.id.color_picker_library_large_preview_old)
      setOldPreviewColor(oldColor)

      newColorPreview = findViewById(R.id.color_picker_library_large_preview_new)
      updateNewPreviewColor(oldColor)
    } else {
      val previewContainer: LinearLayout =
        findViewById(R.id.color_picker_library_large_preview_container)

      previewContainer.visibility = View.GONE
    }
  }

  protected fun initListener() {
    listener?.onColorChanged(getCurrentColor())
  }

  @SuppressLint("ClickableViewAccessibility")
  protected fun initThumb(thumb: ImageView? = null) {
    this.thumb = thumb ?: findViewById(R.id.color_picker_library_large_thumb)

    window.setOnTouchListener { _, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
          moveThumb(event.x, event.y)
          true
        }

        else -> super.onTouchEvent(event)
      }
    }
  }



  // ------------------------ PREVIEWS ------------------------

  protected fun updateNewPreviewColor(color: Int) {
    if (isPreviewEnabled) {
      newColorPreview.setTag(R.id.color_picker_library_color_tag, color)
      newColorPreview.setColorFilter(color)
    } else {
      Log.d(TAG, "updateNewPreviewColor: preview not enabled")
    }
  }

  fun setOldPreviewColor(color: Int) {
    if (isPreviewEnabled) {
      oldColor = color
      oldColorPreview.setTag(R.id.color_picker_library_color_tag, color)
      oldColorPreview.setColorFilter(color)
    } else {
      Log.d(TAG, "setOldPreviewColor: preview not enabled")
    }
  }



  // ------------------------ THUMB ------------------------

  /**
   * Defines how the window thumb should move. The thumb will have different behaviour depending on
   * the window (for example, a circular window restricts the thumb to a circle while a square
   * window allows full movement within the window.
   */
  protected abstract fun moveThumb(x: Float, y: Float)

  /**
   * Called when the window thumb's position changes. This should be used to alert the listener of
   * color changes, etc.
   */
  protected abstract fun onThumbPositionChanged(x: Float, y: Float)



  // ------------------------ UTIL ------------------------

  companion object {
    private const val TAG = "AbstractLargeColorPicke"
  }
}