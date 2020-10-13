package com.gerardbradshaw.exampleapp.compactview

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.exampleapp.compactview.CompactViewUnitTestUtil.changeSliderTypeTo
import com.gerardbradshaw.exampleapp.compactview.CompactViewUnitTestUtil.getLayout
import com.gerardbradshaw.exampleapp.compactview.CompactViewUnitTestUtil.SliderType
import com.gerardbradshaw.exampleapp.util.ParamTestInput
import com.gerardbradshaw.exampleapp.util.ParamTestOutput
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.checkViewColorTagIsExactly
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.checkSeekBarIsAtProgress
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.getParameterizedTestParams
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.moveSeekBarTo
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.sliderMax
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.roundToInt

@RunWith(Enclosed::class)
internal class CompactViewUnitTests {

  // ---------------- SINGLE RUN TESTS ----------------

  @RunWith(RobolectricTestRunner::class)
  @Config(sdk = [28])
  class SingleTests {

    private lateinit var seekBar: SeekBar
    private lateinit var menu: FrameLayout

    @Before
    fun setUp() {
      val layout = getLayout()

      seekBar = layout.findViewById(R.id.color_picker_library_slider_seek_bar)
      menu = layout.findViewById(R.id.color_picker_library_compact_menu_frame)
    }

    @Test
    fun should_startColorSliderAtZero_when_firstEntering() {
      checkSeekBarIsAtProgress(0, seekBar)
    }

    @Test
    fun should_startShadeSliderAtZero_when_firstEntering() {
      moveSeekBarTo(seekBar.max / 2, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(seekBar.max / 2, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      checkSeekBarIsAtProgress(0, seekBar)
    }

    @Test
    fun should_startTintSliderAtZero_when_firstEntering() {
      moveSeekBarTo(seekBar.max / 2, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(seekBar.max / 2, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      checkSeekBarIsAtProgress(0, seekBar)
    }

    @Test
    fun should_startColorSliderAtPreviousProgress_when_returningFromShadeSlider() {
      moveSeekBarTo(sliderMax / 2, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(sliderMax, seekBar)

      changeSliderTypeTo(SliderType.COLOR, menu)
      checkSeekBarIsAtProgress(sliderMax / 2, seekBar)
    }

    @Test
    fun should_startColorSliderAtPreviousProgress_when_returningFromTintSlider() {
      moveSeekBarTo(sliderMax / 2, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(sliderMax, seekBar)

      changeSliderTypeTo(SliderType.COLOR, menu)
      checkSeekBarIsAtProgress(sliderMax / 2, seekBar)
    }

    @Test
    fun should_startShadeSliderAtPreviousProgress_when_returningFromColorSlider() {
      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(sliderMax / 2, seekBar)

      changeSliderTypeTo(SliderType.COLOR, menu)
      moveSeekBarTo(sliderMax, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      checkSeekBarIsAtProgress(sliderMax / 2, seekBar)
    }

    @Test
    fun should_startShadeSliderAtPreviousProgress_when_returningFromTintSlider() {
      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(sliderMax / 2, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(sliderMax, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      checkSeekBarIsAtProgress(sliderMax / 2, seekBar)
    }

    @Test
    fun should_startTintSliderAtPreviousProgress_when_returningFromColorSlider() {
      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(sliderMax / 2, seekBar)

      changeSliderTypeTo(SliderType.COLOR, menu)
      moveSeekBarTo(sliderMax, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      checkSeekBarIsAtProgress(sliderMax / 2, seekBar)
    }

    @Test
    fun should_startTintSliderAtPreviousProgress_when_returningFromShadeSlider() {
      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(sliderMax / 2, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(sliderMax, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      checkSeekBarIsAtProgress(sliderMax / 2, seekBar)
    }
  }



  // ---------------- PARAMETERIZED TESTS ----------------

  @RunWith(ParameterizedRobolectricTestRunner::class)
  @Config(sdk = [28])
  class ParameterizedTests(
    private val inputParams: ParamTestInput,
    private val expected: ParamTestOutput
  ) {
    private lateinit var seekBar: SeekBar
    private lateinit var menu: FrameLayout
    private lateinit var preview: ImageView

    @Before
    fun setUp() {
      val layout = getLayout()

      seekBar = layout.findViewById(R.id.color_picker_library_slider_seek_bar)
      menu = layout.findViewById(R.id.color_picker_library_compact_menu_frame)
      preview = layout.findViewById(R.id.color_picker_library_compact_preview)
    }


    // ---------------- COLOR PREVIEW ----------------

    @Test
    fun should_updatePreview_when_colorSliderProgressChanged() {
      moveSeekBarTo(inputParams.colorProgress, seekBar)
      checkViewColorTagIsExactly(expected.pureColor, preview)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeSliderProgressChanged() {
      moveSeekBarTo(inputParams.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(inputParams.shadeProgress, seekBar)

      checkViewColorTagIsExactly(expected.shadedColor, preview)
    }

    @Test
    fun should_updatePreview_when_colorAndTintSliderProgressChanged() {
      moveSeekBarTo(inputParams.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(inputParams.tintProgress, seekBar)

      checkViewColorTagIsExactly(expected.tintedColor, preview)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeAndTintSliderProgressChanged() {
      moveSeekBarTo(inputParams.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(inputParams.shadeProgress, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(inputParams.tintProgress, seekBar)

      checkViewColorTagIsExactly(expected.shadedAndTintedColor, preview)
    }


    companion object {
      @JvmStatic
      @ParameterizedRobolectricTestRunner.Parameters(name = "progress: {0}")
      fun params(): Collection<Array<Any>> {
        return getParameterizedTestParams()
      }
    }
  }
}