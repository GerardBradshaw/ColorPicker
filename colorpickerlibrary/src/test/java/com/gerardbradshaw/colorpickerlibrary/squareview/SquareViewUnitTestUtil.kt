package com.gerardbradshaw.colorpickerlibrary.squareview

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.views.SquareColorPickerView
import org.robolectric.Robolectric

object SquareViewUnitTestUtil {

  fun getLayout(): LinearLayout {
    val activityController = Robolectric.buildActivity(Activity::class.java)

    val layout = LayoutInflater
      .from(activityController.get())
      .inflate(R.layout.color_picker_library_example_activity, null) as LinearLayout

    val picker = SquareColorPickerView(layout.context)
    picker.layoutParams = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT)

    layout.addView(picker)

    return layout
  }

  fun moveThumbToMatchShadeRatio(ratio: Double, picker: SquareColorPickerView) {
    TODO()
  }

  fun moveThumbToMatchTintRatio(ratio: Double, picker: SquareColorPickerView) {
    TODO()
  }

  fun checkThumbPositionIs(shadeRatio: Double, tintRatio: Double, picker: SquareColorPickerView) {
    TODO()
  }

  fun checkSquareGradientColorIs(color: Int, square: FrameLayout) {
    TODO()
  }

  fun setOldPreviewColorTo(color: Int, picker: SquareColorPickerView) {
    TODO()
  }
}