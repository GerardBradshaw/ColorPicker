package com.gerardbradshaw.library

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView

abstract class AbstractLargeColorPicker : AbstractColorPicker {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)



  // ------------------------ PROPERTIES ------------------------

  private lateinit var thumbOnDrawObserver: ViewTreeObserver.OnDrawListener
  protected lateinit var slider: ColorSlider



  // ------------------------ INSTANCE PROPERTIES ------------------------

  protected lateinit var colorPicker: FrameLayout
  protected lateinit var thumb: ImageView
  protected lateinit var newColorPreview: ImageView
  protected var oldColor: Int = Color.RED



  // ------------------------ INSTANCE METHODS ------------------------

  protected fun initSlider(gradient: GradientDrawable,
                          onProgressChangedListener: ColorSlider.OnProgressChangedListener) {
    slider = findViewById(R.id.large_color_slider)
    slider.setGradientBarDrawable(gradient)
    slider.setOnProgressChangedListener(onProgressChangedListener)
  }

  protected fun initPreviews() {
    val oldColorPreview: ImageView = findViewById(R.id.large_preview_old)
    oldColorPreview.setColorFilter(oldColor)
    oldColorPreview.tag = oldColor // tagged for testing purposes

    newColorPreview = findViewById(R.id.large_preview_new)
    newColorPreview.setColorFilter(oldColor)
    newColorPreview.tag = oldColor // tagged for testing purposes
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



  // ------------------------ HELPER METHODS ------------------------

  private fun startThumbOnDrawListener() {
    thumb.viewTreeObserver.addOnDrawListener(thumbOnDrawObserver)
  }

  private fun removeThumbOnDrawListener() {
    thumb.viewTreeObserver.removeOnDrawListener(thumbOnDrawObserver)
  }
}