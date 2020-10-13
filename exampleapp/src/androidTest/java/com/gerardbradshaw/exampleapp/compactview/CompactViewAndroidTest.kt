package com.gerardbradshaw.exampleapp.compactview

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.gerardbradshaw.exampleapp.CompactViewActivity
import com.gerardbradshaw.exampleapp.util.AndroidTestUtil.checkListenerColorIs
import com.gerardbradshaw.exampleapp.util.AndroidTestUtil.moveSeekBarTo
import com.gerardbradshaw.exampleapp.compactview.CompactViewAndroidTestUtil.SliderType
import com.gerardbradshaw.exampleapp.compactview.CompactViewAndroidTestUtil.changeSliderTypeTo
import com.gerardbradshaw.exampleapp.util.AndroidTestUtil.getParameterizedTestParams
import com.gerardbradshaw.exampleapp.util.ParamTestInput
import com.gerardbradshaw.exampleapp.util.ParamTestOutput
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

      checkListenerColorIs(expected.shadedAndTintedColor)
    }

    @Test
    fun should_notifyListener_when_onlyColorSliderPositionChanged() {
      moveSeekBarTo(inputs.colorProgress)
      checkListenerColorIs(expected.pureColor)
    }

    @Test
    fun should_notifyListener_when_colorAndShadeSliderProgressChanged() {
      moveSeekBarTo(inputs.colorProgress)

      changeSliderTypeTo(SliderType.SHADE)
      moveSeekBarTo(inputs.shadeProgress)

      checkListenerColorIs(expected.shadedColor)
    }

    @Test
    fun should_notifyListener_when_colorAndTintSliderProgressChanged() {
      moveSeekBarTo(inputs.colorProgress)

      changeSliderTypeTo(SliderType.TINT)
      moveSeekBarTo(inputs.tintProgress)

      checkListenerColorIs(expected.tintedColor)
    }


    companion object {
      @Parameterized.Parameters(name = "progress = {0}")
      @JvmStatic
      fun params(): Collection<Array<Any>> {
        return getParameterizedTestParams()
      }
    }
  }
}