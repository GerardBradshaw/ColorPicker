package com.gerardbradshaw.colorpicker.compactview

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.gerardbradshaw.colorpicker.CompactViewActivity
import com.gerardbradshaw.colorpicker.InputParams
import com.gerardbradshaw.colorpicker.OutputColors
import com.gerardbradshaw.colorpicker.R
import com.gerardbradshaw.colorpicker.util.AndroidTestUtil.checkListenerChangedColorTo
import com.gerardbradshaw.colorpicker.util.AndroidTestUtil.moveSeekBarTo
import com.gerardbradshaw.colorpicker.compactview.CompactViewAndroidTestUtil.SliderType
import com.gerardbradshaw.colorpicker.compactview.CompactViewAndroidTestUtil.changeSliderTypeTo
import com.gerardbradshaw.colorpicker.util.AndroidTestUtil.getParamaterizedTestParams
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Enclosed::class)
class CompactViewAndroidTest {

  // ---------------- PARAMETERIZED TESTS ----------------

  @RunWith(Parameterized::class)
  class ParameterizedTests(private val inputs: InputParams, private val expected: OutputColors) {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<CompactViewActivity>(CompactViewActivity::class.java)


    // ---------------- LISTENER ----------------

    @Test
    fun should_notifyListener_when_allSliderProgressChanged() {
      moveSeekBarTo(inputs.colorProgress, R.id.color_picker_library_slider_seek_bar)

      changeSliderTypeTo(SliderType.SHADE, R.id.color_picker_library_compact_menu_frame)
      moveSeekBarTo(inputs.shadeProgress, R.id.color_picker_library_slider_seek_bar)

      changeSliderTypeTo(SliderType.TINT, R.id.color_picker_library_compact_menu_frame)
      moveSeekBarTo(inputs.tintProgress, R.id.color_picker_library_slider_seek_bar)

      checkListenerChangedColorTo(expected.shadedAndTintedColor, R.id.color_picker_library_ex_compact_listener)
    }

    @Test
    fun should_notifyListener_when_onlyColorSliderPositionChanged() {
      moveSeekBarTo(inputs.colorProgress, R.id.color_picker_library_slider_seek_bar)
      checkListenerChangedColorTo(expected.pureColor, R.id.color_picker_library_ex_compact_listener)
    }

    @Test
    fun should_notifyListener_when_colorAndShadeSliderProgressChanged() {
      moveSeekBarTo(inputs.colorProgress, R.id.color_picker_library_slider_seek_bar)

      changeSliderTypeTo(SliderType.SHADE, R.id.color_picker_library_compact_menu_frame)
      moveSeekBarTo(inputs.shadeProgress, R.id.color_picker_library_slider_seek_bar)

      checkListenerChangedColorTo(expected.shadedColor, R.id.color_picker_library_ex_compact_listener)
    }

    @Test
    fun should_notifyListener_when_colorAndTintSliderProgressChanged() {
      moveSeekBarTo(inputs.colorProgress, R.id.color_picker_library_slider_seek_bar)

      changeSliderTypeTo(SliderType.TINT, R.id.color_picker_library_compact_menu_frame)
      moveSeekBarTo(inputs.tintProgress, R.id.color_picker_library_slider_seek_bar)

      checkListenerChangedColorTo(expected.tintedColor, R.id.color_picker_library_ex_compact_listener)
    }


    companion object {
      @Parameterized.Parameters(name = "progress = {0}")
      @JvmStatic
      fun params(): Collection<Array<Any>> {
        return getParamaterizedTestParams()
      }
    }
  }
}