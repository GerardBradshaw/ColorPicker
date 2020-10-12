package com.gerardbradshaw.colorpickerlibrary.compactview

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.compactview.CompactViewUnitTestUtil.SliderType
import com.gerardbradshaw.colorpickerlibrary.compactview.CompactViewUnitTestUtil.changeSliderTypeTo
import com.gerardbradshaw.colorpickerlibrary.util.InputParams
import com.gerardbradshaw.colorpickerlibrary.util.OutputColors
import com.gerardbradshaw.colorpickerlibrary.util.UnitTestUtil.checkPreviewChangedColorTo
import com.gerardbradshaw.colorpickerlibrary.util.UnitTestUtil.checkSeekBarIsAtProgress
import com.gerardbradshaw.colorpickerlibrary.util.UnitTestUtil.moveSeekBarTo
import com.gerardbradshaw.colorpickerlibrary.util.UnitTestUtil.sliderMax
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.roundToInt


@RunWith(Enclosed::class)
class CompactViewUnitTests {

  // ---------------- SINGLE RUN TESTS ----------------

  @RunWith(RobolectricTestRunner::class)
  @Config(sdk = [28])
  class SingleTests {

    private lateinit var seekBar: SeekBar
    private lateinit var listener: View
    private lateinit var menu: FrameLayout

    @Before
    fun setUp() {
      val activityController = Robolectric.buildActivity(Activity::class.java)

      val layout = LayoutInflater
        .from(activityController.get())
        .inflate(R.layout.color_picker_library_activity_layout_with_compact_view, null) as LinearLayout

      seekBar = layout.findViewById(R.id.color_picker_library_slider_seek_bar)
      listener = layout.findViewById(R.id.color_picker_library_example_listener)
      menu = layout.findViewById(R.id.color_picker_library_compact_menu_frame)
    }

    @Test
    fun should_startColorSliderAtZeroProgress_when_firstEntering() {
      checkSeekBarIsAtProgress(0, seekBar)
    }

    @Test
    fun should_startShadeSliderAtZeroProgress_when_firstEntering() {
      moveSeekBarTo(seekBar.max / 2, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(seekBar.max / 2, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      checkSeekBarIsAtProgress(0, seekBar)
    }

    @Test
    fun should_startTintSliderAtZeroProgress_when_firstEntering() {
      moveSeekBarTo(seekBar.max / 2, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(seekBar.max / 2, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      checkSeekBarIsAtProgress(0, seekBar)
    }
  }



  // ---------------- PARAMETERIZED TESTS ----------------

  @RunWith(ParameterizedRobolectricTestRunner::class)
  @Config(sdk = [28])
  class ParameterizedTests(private val inputParams: InputParams,
                           private val expected: OutputColors) {

    private lateinit var seekBar: SeekBar
    private lateinit var menu: FrameLayout
    private lateinit var preview: ImageView

    @Before
    fun setUp() {
      val activityController = Robolectric.buildActivity(Activity::class.java)

      val layout = LayoutInflater
        .from(activityController.get())
        .inflate(R.layout.color_picker_library_activity_layout_with_compact_view, null) as LinearLayout

      seekBar = layout.findViewById(R.id.color_picker_library_slider_seek_bar)
      menu = layout.findViewById(R.id.color_picker_library_compact_menu_frame)
      preview = layout.findViewById(R.id.compact_preview)
    }


    // ---------------- PREVIEW ----------------

    @Test
    fun should_updatePreview_when_onlyColorSliderProgressChanged() {
      moveSeekBarTo(inputParams.colorProgress, seekBar)
      checkPreviewChangedColorTo(expected.pureColor, preview)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeSliderProgressChanged() {
      moveSeekBarTo(inputParams.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(inputParams.shadeProgress, seekBar)

      checkPreviewChangedColorTo(expected.shadedColor, preview)
    }

    @Test
    fun should_updatePreview_when_colorAndTintSliderProgressChanged() {
      moveSeekBarTo(inputParams.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(inputParams.tintProgress, seekBar)

      checkPreviewChangedColorTo(expected.tintedColor, preview)
    }

    @Test
    fun should_updatePreview_when_allSliderProgressChanged() {
      moveSeekBarTo(inputParams.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(inputParams.shadeProgress, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(inputParams.tintProgress, seekBar)

      checkPreviewChangedColorTo(expected.shadedAndTintedColor, preview)
    }



    // ---------------- SEEK BAR ----------------

    @Test
    fun should_startColorSliderAtPreviousProgress_when_returningFromShadeSlider() {
      moveSeekBarTo(inputParams.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.SHADE, menu)
      moveSeekBarTo(inputParams.shadeProgress, seekBar)

      changeSliderTypeTo(SliderType.COLOR, menu)
      checkSeekBarIsAtProgress(inputParams.colorProgress, seekBar)
    }

    @Test
    fun should_startColorSliderAtPreviousProgress_when_returningFromTintSlider() {
      moveSeekBarTo(inputParams.colorProgress, seekBar)

      changeSliderTypeTo(SliderType.TINT, menu)
      moveSeekBarTo(inputParams.tintProgress, seekBar)

      changeSliderTypeTo(SliderType.COLOR, menu)
      checkSeekBarIsAtProgress(inputParams.colorProgress, seekBar)
    }


    companion object {
      @JvmStatic
      @ParameterizedRobolectricTestRunner.Parameters(name = "progress: {0}")
      fun params(): Collection<Array<Any>> {
        val inputParams = Array<Any>(7) {
          val colorProgress = (it * sliderMax.toDouble() / 6.0).roundToInt()

          val shadeProgress = (sliderMax - colorProgress)

          val tintProgress =
            when (it) {
              0 -> 0
              6 -> ((sliderMax.toDouble() / 6.0)).roundToInt()
              else -> ((sliderMax.toDouble() / 6.0) + shadeProgress).roundToInt()
            }

          InputParams(colorProgress, shadeProgress, tintProgress)
        }

        val pureColors: Array<Int> = arrayOf(-65536, -256, -16711936, -16711681, -16776961, -65281, -65535)
        val shadedColors: Array<Int> = arrayOf(-16777216, - 13948160, - 16755456, - 16744320, - 16777046, - 2883372, - 65535)
        val tintedColors: Array<Int> = arrayOf(-65536, - 1, - 2752555, - 5570561, - 8355585, - 43521, - 54485)
        val shadedAndTintedColors: Array<Int> = arrayOf(-16777216, - 13948117, - 12102329, - 11173760, - 11184726, - 2865196, - 54485)

        val expectedOutputs = Array<Any>(7) {
          OutputColors(pureColors[it], shadedColors[it], tintedColors[it], shadedAndTintedColors[it])
        }

        return Array(7) {
          arrayOf(inputParams[it], expectedOutputs[it])
        }.asList()
      }
    }
  }
}