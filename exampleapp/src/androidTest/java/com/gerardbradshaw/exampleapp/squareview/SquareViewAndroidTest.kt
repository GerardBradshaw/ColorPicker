package com.gerardbradshaw.exampleapp.squareview

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gerardbradshaw.exampleapp.R
import com.gerardbradshaw.exampleapp.SquareViewActivity
import com.gerardbradshaw.exampleapp.squareview.SquareViewAndroidTestUtil.checkThumbIsAtRatioPosition
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.checkViewColorTagIsExactly
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.moveSeekBarTo
import com.gerardbradshaw.exampleapp.squareview.SquareViewAndroidTestUtil.moveThumbTo
import com.gerardbradshaw.exampleapp.testutil.GlobalTestUtil.getParameterizedTestIO
import com.gerardbradshaw.exampleapp.testutil.ParamTestInput
import com.gerardbradshaw.exampleapp.testutil.ParamTestOutput
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.checkPickerRatiosAre
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.checkSeekBarIsAtProgress
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.setPickerRatios
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Enclosed::class)
internal class SquareViewAndroidTest {

  @RunWith(AndroidJUnit4::class)
  class LaunchTests {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<SquareViewActivity>(SquareViewActivity::class.java)

    @Test
    fun should_startThumbInTopRightCornerOfSquare_when_launched() {
      checkThumbIsAtRatioPosition(0.0, 0.0)
    }
  }


  @RunWith(Parameterized::class)
  class PreviewTests(private val input: ParamTestInput, private val expected: ParamTestOutput) {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<SquareViewActivity>(SquareViewActivity::class.java)

    @Test
    fun should_updatePreview_when_colorChangedInUI() {
      moveSeekBarTo(input.colorProgress)
      checkViewColorTagIsExactly(expected.pureColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeChangedInUI() {
      moveSeekBarTo(input.colorProgress)
      moveThumbTo(0.0, input.shadeRatio)
      checkViewColorTagIsExactly(expected.shadedColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_colorAndTintChangedInUI() {
      moveSeekBarTo(input.colorProgress)
      moveThumbTo(input.tintRatio, 0.0)
      checkViewColorTagIsExactly(expected.tintedColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeAndTintChangedInUI() {
      moveSeekBarTo(input.colorProgress)
      moveThumbTo(input.tintRatio, input.shadeRatio)
      checkViewColorTagIsExactly(expected.shadedAndTintedColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_ratiosChangedProgrammatically() {
      setPickerRatios(
        input.colorRatio,
        input.shadeRatio,
        input.tintRatio,
        R.id.example_square_picker)

      checkViewColorTagIsExactly(
        expected.shadedAndTintedColor,
        R.id.color_picker_library_large_preview_new)
    }

    companion object {
      @Parameterized.Parameters(name = "{0}")
      @JvmStatic
      fun data(): Collection<Array<Any>> {
        return getParameterizedTestIO()
      }
    }
  }


  @RunWith(Parameterized::class)
  class ListenerTests(private val input: ParamTestInput, private val expected: ParamTestOutput) {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<SquareViewActivity>(SquareViewActivity::class.java)

    @Test
    fun should_updateListener_when_colorChangedInUI() {
      moveSeekBarTo(input.colorProgress)
      checkViewColorTagIsExactly(expected.pureColor, R.id.example_listener)
    }

    @Test
    fun should_updateListener_when_colorAndShadeChangedInUI() {
      moveSeekBarTo(input.colorProgress)
      moveThumbTo(0.0, input.shadeRatio)
      checkViewColorTagIsExactly(expected.shadedColor, R.id.example_listener)
    }

    @Test
    fun should_updateListener_when_colorAndTintChangedInUI() {
      moveSeekBarTo(input.colorProgress)
      moveThumbTo(input.tintRatio, 0.0)
      checkViewColorTagIsExactly(expected.tintedColor, R.id.example_listener)
    }

    @Test
    fun should_updateListener_when_colorAndShadeAndTintChangedInUI() {
      moveSeekBarTo(input.colorProgress)
      moveThumbTo(input.tintRatio, input.shadeRatio)
      checkViewColorTagIsExactly(expected.shadedAndTintedColor, R.id.example_listener)
    }

    @Test
    fun should_updateListener_when_ratiosChangedProgrammatically() {
      setPickerRatios(
        input.colorRatio,
        input.shadeRatio,
        input.tintRatio,
        R.id.example_square_picker)

      checkViewColorTagIsExactly(
        expected.shadedAndTintedColor,
        R.id.example_listener)
    }

    companion object {
      @Parameterized.Parameters(name = "{0}")
      @JvmStatic
      fun data(): Collection<Array<Any>> {
        return getParameterizedTestIO()
      }
    }
  }


  @RunWith(Parameterized::class)
  class ThumbAndWindowTests(private val input: ParamTestInput, private val expected: ParamTestOutput) {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<SquareViewActivity>(SquareViewActivity::class.java)

    @Test
    fun should_updateWindowColor_when_colorChangedInUI() {
      moveSeekBarTo(input.colorProgress)
      checkViewColorTagIsExactly(expected.pureColor, R.id.color_picker_library_large_window)
    }

    @Test
    fun should_moveThumbToTappedPosition_when_squareTapped() {
      moveThumbTo(input.tintRatio, input.shadeRatio)
      checkThumbIsAtRatioPosition(input.tintRatio, input.shadeRatio)
    }

    @Test
    fun should_updateRatios_when_squareTapped() {
      moveSeekBarTo(input.colorProgress)
      moveThumbTo(input.tintRatio, input.shadeRatio)

      checkPickerRatiosAre(
        input.colorRatio,
        input.shadeRatio,
        input.tintRatio,
        R.id.example_square_picker)
    }

    @Test
    fun should_moveThumbToCorrectPosition_when_ratiosSetProgrammatically() {
      setPickerRatios(
        input.colorRatio,
        input.shadeRatio,
        input.tintRatio,
        R.id.example_square_picker)

      checkThumbIsAtRatioPosition(input.tintRatio, input.shadeRatio)
    }

    @Test
    fun should_moveSliderToCorrectPosition_when_colorRatioSetProgrammatically() {
      setPickerRatios(
        input.colorRatio,
        input.shadeRatio,
        input.tintRatio,
        R.id.example_square_picker)

      checkSeekBarIsAtProgress(input.colorProgress)
    }

    companion object {
      @Parameterized.Parameters(name = "{0}")
      @JvmStatic
      fun data(): Collection<Array<Any>> {
        return getParameterizedTestIO()
      }
    }
  }
}
