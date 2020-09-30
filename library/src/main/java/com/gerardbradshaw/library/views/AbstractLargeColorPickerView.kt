package com.gerardbradshaw.library.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.gerardbradshaw.library.R
import com.gerardbradshaw.library.util.ColorSlider

abstract class AbstractLargeColorPickerView :
  AbstractColorPickerView {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)



  // ------------------------ PROPERTIES ------------------------

  private lateinit var thumbOnDrawObserver: ViewTreeObserver.OnDrawListener
  private lateinit var newColorPreview: ImageView
  private lateinit var oldColorPreview: ImageView



  // ------------------------ INSTANCE PROPERTIES ------------------------

  protected lateinit var slider: ColorSlider
  protected lateinit var colorPicker: FrameLayout
  protected lateinit var thumb: ImageView
  protected var oldColor: Int = Color.RED



  // ------------------------ INSTANCE METHODS ------------------------

  protected fun initSlider(gradient: GradientDrawable,
                           onProgressChangedListener: ColorSlider.OnProgressChangedListener) {
    slider = findViewById(R.id.large_color_slider)
    slider.setGradientBarDrawable(gradient)
    slider.setOnProgressChangedListener(onProgressChangedListener)
  }

  protected fun initPreviews() {
    if (isPreviewEnabled) {
      oldColorPreview = findViewById(R.id.large_preview_old)
      setOldPreviewColor(oldColor)

      newColorPreview = findViewById(R.id.large_preview_new)
      updateNewPreviewColor(oldColor)
    }
    else {
      val previewContainer: LinearLayout = findViewById(R.id.large_preview_container)
      previewContainer.visibility = View.GONE
    }
  }

  protected fun updateNewPreviewColor(color: Int) {
    if (isPreviewEnabled) {
      newColorPreview.setColorFilter(color)
      newColorPreview.tag = color // tagged for testing purposes
    }
  }

  protected fun initListener() {
    listener?.onColorChanged(getCurrentColor())
  }

  protected abstract fun moveThumb(x: Float, y: Float)

  @SuppressLint("ClickableViewAccessibility")
  protected fun initThumb(thumbOnDrawObserver: ViewTreeObserver.OnDrawListener, thumb: ImageView? = null) {
    this.thumbOnDrawObserver = thumbOnDrawObserver
    this.thumb = thumb ?: findViewById(R.id.large_thumb)

    colorPicker.setOnTouchListener { _, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          startThumbOnDrawListener()
          moveThumb(event.x, event.y)
          true
        }

        MotionEvent.ACTION_MOVE -> {
          moveThumb(event.x, event.y)
          true
        }

        MotionEvent.ACTION_UP -> {
          removeThumbOnDrawListener()
          true
        }
        else -> super.onTouchEvent(event)
      }
    }
  }



  // ------------------------ PUBLIC METHODS ------------------------

  fun setOldPreviewColor(color: Int) {
    if (isPreviewEnabled) {
      oldColor = color
      oldColorPreview.setColorFilter(color)
      oldColorPreview.tag = color // tagged for testing purposes
    }
  }



  // ------------------------ HELPER METHODS ------------------------

  private fun startThumbOnDrawListener() {
    thumb.viewTreeObserver.addOnDrawListener(thumbOnDrawObserver)
  }

  private fun removeThumbOnDrawListener() {
    thumb.viewTreeObserver.removeOnDrawListener(thumbOnDrawObserver)
  }
}