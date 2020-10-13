package com.gerardbradshaw.exampleapp.squareview

import android.app.Activity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import com.gerardbradshaw.colorpickerlibrary.views.SquareColorPickerView
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.checkViewColorTagIsExactly
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.checkSeekBarIsAtProgress
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SquareViewUnitTests {

  private lateinit var picker: SquareColorPickerView

  @Before
  fun setUp() {
    val activityController = Robolectric.buildActivity(Activity::class.java)

    val layout = LayoutInflater
      .from(activityController.get())
      .inflate(R.layout.color_picker_library_activity_square_example, null) as LinearLayout

    picker = layout
      .findViewById(R.id.color_picker_library_example_square_picker) as SquareColorPickerView
  }

  @Test
  fun should_startColorSliderAtZeroProgress_when_firstEntering() {
    val colorBar: SeekBar = picker.findViewById(R.id.color_picker_library_slider_seek_bar)
    checkSeekBarIsAtProgress(0, colorBar)
  }

  @Test
  fun should_haveRedGradientSquare_when_firstEntering() {
    val square: FrameLayout = picker.findViewById(R.id.color_picker_library_large_window)
    checkViewColorTagIsExactly(-65536, square) // -65536 is pure red (Color.RED)
  }

  @Test
  fun should_haveRedPreview_when_firstEntering() {
    val preview: ImageView = picker.findViewById(R.id.color_picker_library_large_preview_new)
    checkViewColorTagIsExactly(-65536, preview) // -65536 is pure red (Color.RED)
  }

  @Test
  fun should_updateOldPreviewColor_when_updateOldColorCalled() {
    picker.setOldPreviewColor(-8355585) // -8355585 is a random color

    val preview: ImageView = picker.findViewById(R.id.color_picker_library_large_preview_old)
    checkViewColorTagIsExactly(-8355585, preview)
  }
}