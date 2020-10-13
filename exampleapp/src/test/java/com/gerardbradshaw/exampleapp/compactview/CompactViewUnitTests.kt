package com.gerardbradshaw.exampleapp.compactview

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView.SliderType
import com.gerardbradshaw.exampleapp.compactview.CompactViewUnitTestUtil.changeSliderTypeTo
import com.gerardbradshaw.exampleapp.compactview.CompactViewUnitTestUtil.getLayout
import com.gerardbradshaw.exampleapp.testutil.GlobalTestUtil.getParameterizedTestIO
import com.gerardbradshaw.exampleapp.testutil.ParamTestInput
import com.gerardbradshaw.exampleapp.testutil.ParamTestOutput
import com.gerardbradshaw.exampleapp.util.RobolectricTestUtil.checkViewColorTagIsExactly
import com.gerardbradshaw.exampleapp.util.RobolectricTestUtil.checkSeekBarIsAtProgress
import com.gerardbradshaw.exampleapp.util.RobolectricTestUtil.checkSeekBarTypeIs
import com.gerardbradshaw.exampleapp.util.RobolectricTestUtil.moveSeekBarTo
import com.gerardbradshaw.exampleapp.util.RobolectricTestUtil.sliderMax
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(Enclosed::class)
internal class CompactViewUnitTests {

  @RunWith(RobolectricTestRunner::class)
  @Config(sdk = [28])
  class InitializationTests {
    private lateinit var seekBar: SeekBar
    private lateinit var slider: ColorSliderView
    private lateinit var menu: FrameLayout

    @Before
    fun setUp() {
      val layout = getLayout()

      seekBar = layout.findViewById(R.id.color_picker_library_slider_seek_bar)
      slider = layout.findViewById(R.id.color_picker_library_compact_color_slider)
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
    fun should_openColorSlider_when_firstEntering() {
      checkSeekBarTypeIs(SliderType.COLOR, slider)
    }

  }


  @RunWith(RobolectricTestRunner::class)
  @Config(sdk = [28])
  class BasicSliderTests {

    private lateinit var seekBar: SeekBar
    private lateinit var slider: ColorSliderView
    private lateinit var menu: FrameLayout

    @Before
    fun setUp() {
      val layout = getLayout()

      seekBar = layout.findViewById(R.id.color_picker_library_slider_seek_bar)
      slider = layout.findViewById(R.id.color_picker_library_compact_color_slider)
      menu = layout.findViewById(R.id.color_picker_library_compact_menu_frame)
    }

    @Test
    fun should_openShadeSlider_when_shadeSliderSelected() {
      changeSliderTypeTo(SliderType.SHADE, menu)
      checkSeekBarTypeIs(SliderType.SHADE, slider)
    }

    @Test
    fun should_openTintSlider_when_tintSliderSelected() {
      changeSliderTypeTo(SliderType.TINT, menu)
      checkSeekBarTypeIs(SliderType.TINT, slider)
    }

    @Test
    fun should_openColorSlider_when_colorSliderSelected() {
      changeSliderTypeTo(SliderType.COLOR, menu)
      checkSeekBarTypeIs(SliderType.COLOR, slider)
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


  @RunWith(ParameterizedRobolectricTestRunner::class)
  @Config(sdk = [28])
  class PreviewTests(private val input: ParamTestInput, private val expected: ParamTestOutput) {
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

    @Test
    fun should_updatePreview_when_colorSliderProgressChanged() {
      moveSeekBarTo(input.colorProgress, seekBar)
      checkViewColorTagIsExactly(expected.pureColor, preview)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeSliderProgressChanged() {
      moveSeekBarTo(input.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(input.shadeProgress, seekBar)

      checkViewColorTagIsExactly(expected.shadedColor, preview)
    }

    @Test
    fun should_updatePreview_when_colorAndTintSliderProgressChanged() {
      moveSeekBarTo(input.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(input.tintProgress, seekBar)

      checkViewColorTagIsExactly(expected.tintedColor, preview)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeAndTintSliderProgressChanged() {
      moveSeekBarTo(input.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(input.shadeProgress, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(input.tintProgress, seekBar)

      checkViewColorTagIsExactly(expected.shadedAndTintedColor, preview)
    }

    companion object {
      @JvmStatic
      @ParameterizedRobolectricTestRunner.Parameters(name = "progress: {0}")
      fun params(): Collection<Array<Any>> {
        return getParameterizedTestIO()
      }
    }
  }


  @RunWith(ParameterizedRobolectricTestRunner::class)
  @Config(sdk = [28])
  class AdvancedSliderTests(private val input: ParamTestInput, private val expected: ParamTestOutput) {
    private lateinit var seekBar: SeekBar
    private lateinit var menu: FrameLayout
    private lateinit var slider: ColorSliderView
    private lateinit var preview: ImageView

    @Before
    fun setUp() {
      val layout = getLayout()

      seekBar = layout.findViewById(R.id.color_picker_library_slider_seek_bar)
      menu = layout.findViewById(R.id.color_picker_library_compact_menu_frame)
      slider = layout.findViewById(R.id.color_picker_library_compact_color_slider)
      preview = layout.findViewById(R.id.color_picker_library_compact_preview)
    }

    @Test
    fun should_updateColorTagOnShadeSlider_when_colorChanged() {
      moveSeekBarTo(input.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      checkViewColorTagIsExactly(expected.pureColor, slider)
    }

    @Test
    fun should_updateColorTagOnShadeSlider_when_colorAndTintChanged() {
      moveSeekBarTo(input.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(input.tintProgress, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      checkViewColorTagIsExactly(expected.tintedColor, slider)
    }

    @Test
    fun should_updateColorTagOnTintSlider_when_colorChanged() {
      moveSeekBarTo(input.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      checkViewColorTagIsExactly(expected.pureColor, slider)
    }

    @Test
    fun should_updateColorTagOnTintSlider_when_colorAndShadeChanged() {
      moveSeekBarTo(input.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(input.shadeProgress, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      checkViewColorTagIsExactly(expected.shadedColor, slider)
    }

    companion object {
      @JvmStatic
      @ParameterizedRobolectricTestRunner.Parameters(name = "progress: {0}")
      fun params(): Collection<Array<Any>> {
        return getParameterizedTestIO()
      }
    }
  }
}