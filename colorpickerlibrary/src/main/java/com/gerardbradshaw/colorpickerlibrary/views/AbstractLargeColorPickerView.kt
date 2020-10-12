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

abstract class AbstractLargeColorPickerView : AbstractColorPickerView {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)



  // ------------------------ PROPERTIES ------------------------

  private lateinit var newColorPreview: ImageView
  private lateinit var oldColorPreview: ImageView

  protected lateinit var slider: ColorSliderView
  protected lateinit var colorPicker: FrameLayout
  protected lateinit var thumb: ImageView
  protected var oldColor: Int = Color.RED



  // ------------------------ INITIALIZATION ------------------------

  protected fun initSlider(
    gradient: GradientDrawable,
    onProgressChangedListener: ColorSliderView.OnProgressChangedListener
  ) {
    slider = findViewById(R.id.color_picker_library_large_color_slider)
    slider.setGradientBarDrawable(gradient)
    slider.setOnProgressChangedListener(onProgressChangedListener)
  }

  protected fun initPreviews() {
    if (isPreviewEnabled) {
      oldColorPreview = findViewById(R.id.color_picker_library_large_preview_old)
      setOldPreviewColor(oldColor)

      newColorPreview = findViewById(R.id.color_picker_library_large_preview_new)
      updateNewPreviewColor(oldColor)
    }
    else {
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

    colorPicker.setOnTouchListener { _, event ->
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
      newColorPreview.setColorFilter(color)
      newColorPreview.setTag(R.id.color_picker_library_color_tag, color) // tagged for testing purposes
    }
  }

  fun setOldPreviewColor(color: Int) {
    if (isPreviewEnabled) {
      oldColor = color
      oldColorPreview.setColorFilter(color)
      oldColorPreview.setTag(R.id.color_picker_library_color_tag, color) // tagged for testing purposes
    } else {
      Log.d(TAG, "setOldPreviewColor: preview not enabled")
    }
  }



  // ------------------------ THUMB ------------------------

  protected abstract fun moveThumb(x: Float, y: Float)

  protected abstract fun onThumbPositionChanged(x: Float, y: Float)



  // ------------------------ UTIL ------------------------

  companion object {
    private const val TAG = "AbstractLargeColorPicke"
  }
}