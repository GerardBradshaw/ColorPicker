package com.gerardbradshaw.exampleapp.squareview

import android.app.Activity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView
import com.gerardbradshaw.colorpickerlibrary.views.SquareColorPickerView
import com.gerardbradshaw.exampleapp.R
import com.gerardbradshaw.exampleapp.util.RobolectricTestUtil.checkViewColorTagIsExactly
import com.gerardbradshaw.exampleapp.util.RobolectricTestUtil.checkSeekBarIsAtProgress
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(Enclosed::class)
class SquareViewUnitTests {

  @RunWith(RobolectricTestRunner::class)
  @Config(sdk = [28])
  class LaunchTests {
    private lateinit var picker: SquareColorPickerView

    @Before
    fun setUp() {
      val activityController = Robolectric.buildActivity(Activity::class.java)

      val layout = LayoutInflater
        .from(activityController.get())
        .inflate(R.layout.activity_example_square_picker, null) as LinearLayout

      picker = layout
        .findViewById(R.id.example_square_picker) as SquareColorPickerView
    }

    @Test
    fun should_startColorSliderAtZeroProgress_when_launched() {
      val colorBar: SeekBar = picker.findViewById(R.id.color_picker_library_slider_seek_bar)
      checkSeekBarIsAtProgress(0, colorBar)
    }

    @Test
    fun should_haveRedWindow_when_launched() {
      val square: FrameLayout = picker.findViewById(R.id.color_picker_library_large_window)
      checkViewColorTagIsExactly(-65536, square) // -65536 is pure red (Color.RED)
    }

    @Test
    fun should_haveRedPreview_when_launched() {
      val preview: ImageView = picker.findViewById(R.id.color_picker_library_large_preview_new)
      checkViewColorTagIsExactly(-65536, preview) // -65536 is pure red (Color.RED)
    }

    @Test
    fun should_haveColorGradient_when_launched() {
      val slider: ColorSliderView = picker.findViewById(R.id.color_picker_library_large_color_slider)
      assertThat(slider.sliderType, equalTo(ColorSliderView.SliderType.COLOR))
    }

    @Test
    fun should_updateOldPreviewColor_when_updateOldColorCalled() {
      picker.setOldPreviewColor(-8355585) // -8355585 is a random color

      val preview: ImageView = picker.findViewById(R.id.color_picker_library_large_preview_old)
      checkViewColorTagIsExactly(-8355585, preview)
    }
  }
}