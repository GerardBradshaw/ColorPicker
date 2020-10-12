package com.gerardbradshaw.colorpickerlibrary.squareview

import android.app.Activity
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.views.SquareColorPickerView
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.robolectric.Robolectric

object SquareViewUnitTestUtil {

  fun getLayout(): LinearLayout {
    val activityController = Robolectric.buildActivity(Activity::class.java)

    val layout = LayoutInflater
      .from(activityController.get())
      .inflate(R.layout.color_picker_library_example_activity, null) as LinearLayout

//    layout.measure(1080, 1920)
//    layout.layout(0, 0, 1080, 1920)

    val picker = SquareColorPickerView(layout.context)
//    picker.measure(1080, 1920)
//    picker.layout(0, 0, 1080, 1920)

    layout.addView(picker)

    return layout
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

    assertThat(thumb.x, equalTo(x))
    assertThat(thumb.y, equalTo(y))
  }

  fun checkSquareGradientColorIs(color: Int, square: FrameLayout) {
    assertThat(square.getTag(R.id.color_picker_library_color_tag) as Int, equalTo(color))
  }

  fun setOldPreviewColorTo(color: Int, picker: SquareColorPickerView) {
    picker.setOldPreviewColor(color)
  }
}