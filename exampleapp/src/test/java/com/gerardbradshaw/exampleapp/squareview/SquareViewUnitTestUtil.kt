package com.gerardbradshaw.exampleapp.squareview

import android.app.Activity
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.views.SquareColorPickerView
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.robolectric.Robolectric

internal object SquareViewUnitTestUtil {

  fun getLayout(): LinearLayout {
    val activityController = Robolectric.buildActivity(Activity::class.java)

    return LayoutInflater
      .from(activityController.get())
      .inflate(R.layout.color_picker_library_activity_square_example, null) as LinearLayout
  }

  fun moveThumbToPosition(xRatio: Double, yRatio: Double, square: FrameLayout) {
    val touchX = (xRatio * square.width).toFloat()
    val touchY = (yRatio * square.height).toFloat()

    val motionEvent = MotionEvent.obtain(
      SystemClock.uptimeMillis(),
      SystemClock.uptimeMillis() + 100,
      MotionEvent.ACTION_UP,
      touchX,
      touchY,
      0)

    square.dispatchTouchEvent(motionEvent)
  }

  fun checkThumbPositionIs(xRatio: Double, yRatio: Double, picker: SquareColorPickerView) {
    val square: FrameLayout = picker.findViewById(R.id.color_picker_library_large_color_window)

    val x = ((square.width * xRatio) + square.x).toFloat()
    val y = ((square.height * yRatio) + square.y).toFloat()

    val thumb: ImageView = picker.findViewById(R.id.color_picker_library_large_thumb)

    MatcherAssert.assertThat(thumb.x, Matchers.equalTo(x))
    MatcherAssert.assertThat(thumb.y, Matchers.equalTo(y))
  }

  fun checkSquareGradientColorIs(color: Int, square: FrameLayout) {
    MatcherAssert.assertThat(
      square.getTag(R.id.color_picker_library_color_tag) as Int,
      Matchers.equalTo(color)
    )
  }

  fun setOldPreviewColorTo(color: Int, picker: SquareColorPickerView) {
    picker.setOldPreviewColor(color)
  }
}