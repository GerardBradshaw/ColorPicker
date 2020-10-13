package com.gerardbradshaw.exampleapp.compactview

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView.SliderType
import com.gerardbradshaw.exampleapp.CompactViewActivity
import com.gerardbradshaw.exampleapp.R
import com.gerardbradshaw.exampleapp.compactview.CompactViewAndroidTestUtil.changeSliderTypeTo
import com.gerardbradshaw.exampleapp.testutil.GlobalTestUtil.getParameterizedTestIO
import com.gerardbradshaw.exampleapp.testutil.ParamTestInput
import com.gerardbradshaw.exampleapp.testutil.ParamTestOutput
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.checkViewColorTagIsApprox
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.moveSeekBarTo
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Enclosed::class)
internal class CompactViewAndroidTest {

  // ---------------- PARAMETERIZED TESTS ----------------

  @RunWith(Parameterized::class)
  class ParameterizedTests(
    private val inputs: ParamTestInput,
    private val expected: ParamTestOutput
  ) {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<CompactViewActivity>(CompactViewActivity::class.java)


    // ---------------- LISTENER ----------------

    @Test
    fun should_notifyListener_when_allSliderProgressChanged() {
      moveSeekBarTo(inputs.colorProgress)

      changeSliderTypeTo(SliderType.SHADE)
      moveSeekBarTo(inputs.shadeProgress)

      changeSliderTypeTo(SliderType.TINT)
      moveSeekBarTo(inputs.tintProgress)

      checkViewColorTagIsApprox(expected.shadedAndTintedColor, R.id.example_listener)
    }

    @Test
    fun should_notifyListener_when_onlyColorSliderPositionChanged() {
      moveSeekBarTo(inputs.colorProgress)
      checkViewColorTagIsApprox(expected.pureColor, R.id.example_listener)
    }

    @Test
    fun should_notifyListener_when_colorAndShadeSliderProgressChanged() {
      moveSeekBarTo(inputs.colorProgress)

      changeSliderTypeTo(SliderType.SHADE)
      moveSeekBarTo(inputs.shadeProgress)

      checkViewColorTagIsApprox(expected.shadedColor, R.id.example_listener)
    }

    @Test
    fun should_notifyListener_when_colorAndTintSliderProgressChanged() {
      moveSeekBarTo(inputs.colorProgress)

      changeSliderTypeTo(SliderType.TINT)
      moveSeekBarTo(inputs.tintProgress)

      checkViewColorTagIsApprox(expected.tintedColor, R.id.example_listener)
    }


    companion object {
      @Parameterized.Parameters(name = "progress = {0}")
      @JvmStatic
      fun params(): Collection<Array<Any>> {
        return getParameterizedTestIO()
      }
    }
  }
}