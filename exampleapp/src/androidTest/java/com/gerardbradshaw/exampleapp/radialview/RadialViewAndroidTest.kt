package com.gerardbradshaw.exampleapp.radialview

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gerardbradshaw.exampleapp.R
import com.gerardbradshaw.exampleapp.RadialViewActivity
import com.gerardbradshaw.exampleapp.radialview.RadialViewAndroidTestUtil.checkThumbIsAtRatioPosition
import com.gerardbradshaw.exampleapp.radialview.RadialViewAndroidTestUtil.moveThumbTo
import com.gerardbradshaw.exampleapp.testutil.GlobalTestUtil
import com.gerardbradshaw.exampleapp.testutil.ParamTestInput
import com.gerardbradshaw.exampleapp.testutil.ParamTestOutput
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.checkPickerRatiosAre
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.checkSeekBarIsAtProgress
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.checkViewColorTagIsApprox
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.moveSeekBarTo
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.setPickerRatios
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Enclosed::class)
internal class RadialViewAndroidTest {

  @RunWith(AndroidJUnit4::class)
  class LaunchTests {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<RadialViewActivity>(RadialViewActivity::class.java)

    @Test
    fun should_startThumbAtRightmostEdge_when_launched() {
      checkThumbIsAtRatioPosition(0.0, 0.0)
    }
  }


  @RunWith(Parameterized::class)
  class PreviewTests(private val input: ParamTestInput, private val expected: ParamTestOutput) {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<RadialViewActivity>(RadialViewActivity::class.java)

    @Test
    fun should_updatePreview_when_colorChangedInUI() {
      moveThumbTo(0.0, input.colorRatio)
      checkViewColorTagIsApprox(expected.pureColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeChangedInUI() {
      moveSeekBarTo(input.shadeProgress)
      moveThumbTo(0.0, input.colorRatio)
      checkViewColorTagIsApprox(expected.shadedColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_colorAndTintChangedInUI() {
      moveThumbTo(input.tintRatio, input.colorRatio)
      checkViewColorTagIsApprox(expected.tintedColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeAndTintChangedInUI() {
      moveSeekBarTo(input.shadeProgress)
      moveThumbTo(input.tintRatio, input.colorRatio)
      checkViewColorTagIsApprox(
        expected.shadedAndTintedColor,
        R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_ratiosChangedProgrammatically() {
      setPickerRatios(
        input.colorRatio,
        input.shadeRatio,
        input.tintRatio,
        R.id.example_radial_picker)

      checkViewColorTagIsApprox(
        expected.shadedAndTintedColor,
        R.id.color_picker_library_large_preview_new)
    }

    companion object {
      @Parameterized.Parameters(name = "{0}")
      @JvmStatic
      fun data(): Collection<Array<Any>> {
        return GlobalTestUtil.getParameterizedTestIO()
      }
    }
  }


  @RunWith(Parameterized::class)
  class ListenerTests(private val input: ParamTestInput, private val expected: ParamTestOutput) {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<RadialViewActivity>(RadialViewActivity::class.java)

    @Test
    fun should_updateListener_when_colorChangedInUI() {
      moveThumbTo(0.0, input.colorRatio)
      checkViewColorTagIsApprox(expected.pureColor, R.id.example_listener)
    }

    @Test
    fun should_updateListener_when_colorAndShadeChangedInUI() {
      moveSeekBarTo(input.shadeProgress)
      moveThumbTo(0.0, input.colorRatio)
      checkViewColorTagIsApprox(expected.shadedColor, R.id.example_listener)
    }

    @Test
    fun should_updateListener_when_colorAndTintChangedInUI() {
      moveThumbTo(input.tintRatio, input.colorRatio)
      checkViewColorTagIsApprox(expected.tintedColor, R.id.example_listener)
    }

    @Test
    fun should_updateListener_when_colorAndShadeAndTintChangedInUI() {
      moveSeekBarTo(input.shadeProgress)
      moveThumbTo(input.tintRatio, input.colorRatio)
      checkViewColorTagIsApprox(expected.shadedAndTintedColor, R.id.example_listener)
    }

    @Test
    fun should_updateListener_when_ratiosChangedProgrammatically() {
      setPickerRatios(
        input.colorRatio,
        input.shadeRatio,
        input.tintRatio,
        R.id.example_radial_picker
      )

      checkViewColorTagIsApprox(expected.shadedAndTintedColor, R.id.example_listener)
    }

    companion object {
      @Parameterized.Parameters(name = "{0}")
      @JvmStatic
      fun data(): Collection<Array<Any>> {
        return GlobalTestUtil.getParameterizedTestIO()
      }
    }
  }


  @RunWith(Parameterized::class)
  class ThumbAndSliderTests(private val input: ParamTestInput, private val expected: ParamTestOutput) {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<RadialViewActivity>(RadialViewActivity::class.java)

    @Test
    fun should_updateSliderColor_when_colorChangedInUI() {
      moveThumbTo(0.0, input.colorRatio)
      checkViewColorTagIsApprox(expected.pureColor, R.id.color_picker_library_large_color_slider)
    }

    @Test
    fun should_moveThumbToTappedPosition_when_windowTapped() {
      moveThumbTo(input.tintRatio, input.colorRatio)
      checkThumbIsAtRatioPosition(input.tintRatio, input.colorRatio)
    }

    @Test
    fun should_updateRatios_when_windowTapped() {
      moveSeekBarTo(input.shadeProgress)
      moveThumbTo(input.tintRatio, input.colorRatio)

      checkPickerRatiosAre(
        input.colorRatio,
        input.shadeRatio,
        input.tintRatio,
        R.id.example_radial_picker)
    }

    @Test
    fun should_moveThumbToCorrectPosition_when_ratiosSetProgrammatically() {
      setPickerRatios(
        input.colorRatio,
        input.shadeRatio,
        input.tintRatio,
        R.id.example_radial_picker)

      checkThumbIsAtRatioPosition(input.tintRatio, input.colorRatio)
    }

    @Test
    fun should_moveSliderToCorrectPosition_when_colorRatioSetProgrammatically() {
      setPickerRatios(
        input.colorRatio,
        input.shadeRatio,
        input.tintRatio,
        R.id.example_radial_picker)

      checkSeekBarIsAtProgress(input.shadeProgress)
    }

    companion object {
      @Parameterized.Parameters(name = "{0}")
      @JvmStatic
      fun data(): Collection<Array<Any>> {
        return GlobalTestUtil.getParameterizedTestIO()
      }
    }
  }
}