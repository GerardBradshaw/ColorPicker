package com.gerardbradshaw.exampleapp.squareview

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import com.gerardbradshaw.colorpickerlibrary.views.SquareColorPickerView
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.exampleapp.squareview.SquareViewUnitTestUtil.checkSquareGradientColorIs
import com.gerardbradshaw.exampleapp.squareview.SquareViewUnitTestUtil.checkThumbPositionIs
import com.gerardbradshaw.exampleapp.squareview.SquareViewUnitTestUtil.getLayout
import com.gerardbradshaw.exampleapp.squareview.SquareViewUnitTestUtil.moveThumbToPosition
import com.gerardbradshaw.exampleapp.squareview.SquareViewUnitTestUtil.setOldPreviewColorTo
import com.gerardbradshaw.exampleapp.util.ParamTestInput
import com.gerardbradshaw.exampleapp.util.ParamTestOutput
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.checkPreviewColorIs
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.checkSeekBarIsAtProgress
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.getParamaterizedTestParams
import com.gerardbradshaw.exampleapp.util.UnitTestUtil.moveSeekBarTo
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(Enclosed::class)
internal class SquareViewUnitTests {

  // ---------------- SINGLE RUN TESTS ----------------

  // Done!!
  @RunWith(RobolectricTestRunner::class)
  @Config(sdk = [28])
  class SingleTests {

    private lateinit var picker: SquareColorPickerView

    @Before
    fun setUp() {
      val layout = getLayout()

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
      checkSquareGradientColorIs(-65536, square) // -65536 is pure red (Color.RED)
    }

    @Test
    fun should_haveRedPreview_when_firstEntering() {
      val preview: ImageView = picker.findViewById(R.id.color_picker_library_large_preview_new)
      checkPreviewColorIs(-65536, preview) // -65536 is pure red (Color.RED)
    }

    @Test
    fun should_updateOldPreviewColor_when_updateOldColorCalled() {
      setOldPreviewColorTo(-8355585, picker) // -8355585 is a random color

      val preview: ImageView = picker.findViewById(R.id.color_picker_library_large_preview_old)
      checkPreviewColorIs(-8355585, preview)
    }
  }



  // ---------------- PARAMETERIZED TESTS ----------------

  @RunWith(ParameterizedRobolectricTestRunner::class)
  @Config(sdk = [28])
  class ParameterizedTests(
    private val inputParams: ParamTestInput,
    private val expected: ParamTestOutput
  ) {
    private lateinit var picker: SquareColorPickerView
    private lateinit var square: FrameLayout
    private lateinit var colorSliderSeekBar: SeekBar
    private lateinit var preview: ImageView

    @Before
    fun setUp() {
      val layout = getLayout()

      picker = layout
        .findViewById(R.id.color_picker_library_example_square_picker) as SquareColorPickerView

      colorSliderSeekBar = layout.findViewById(R.id.color_picker_library_slider_seek_bar)
      square = layout.findViewById(R.id.color_picker_library_large_window)
      preview = layout.findViewById(R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_colorChanged() {
      moveSeekBarTo(inputParams.colorProgress, colorSliderSeekBar)
      checkPreviewColorIs(expected.pureColor, preview)
    }

    @Test
    fun should_updateSquareColor_when_colorChanged() {
      moveSeekBarTo(inputParams.colorProgress, colorSliderSeekBar)
      checkSquareGradientColorIs(expected.pureColor, square)
    }

    @Deprecated("Moved to AndroidTest")
    @Test
    fun should_updatePreview_when_colorAndShadeChanged() {
      moveSeekBarTo(inputParams.colorProgress, colorSliderSeekBar)
      moveThumbToPosition(0.0, inputParams.shadeRatio, square)
      checkPreviewColorIs(expected.shadedColor, preview)
    }

    @Test
    fun should_updatePreview_when_colorAndTintChanged() {
      moveSeekBarTo(inputParams.colorProgress, colorSliderSeekBar)
      moveThumbToPosition(inputParams.tintRatio, 0.0, square)
      checkPreviewColorIs(expected.tintedColor, preview)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeAndTintChanged() {
      moveSeekBarTo(inputParams.colorProgress, colorSliderSeekBar)
      moveThumbToPosition(inputParams.tintRatio, inputParams.shadeRatio, square)
      checkPreviewColorIs(expected.shadedAndTintedColor, preview)
    }

    @Test
    fun should_moveThumbToTappedPosition_when_squareTapped() {
      moveThumbToPosition(inputParams.tintRatio, inputParams.shadeRatio, square)
      checkThumbPositionIs(inputParams.tintRatio, inputParams.shadeRatio, picker)
    }

    companion object {
      @JvmStatic
      @ParameterizedRobolectricTestRunner.Parameters(name = "progress: {0}")
      fun params(): Collection<Array<Any>> {
        return getParamaterizedTestParams()
      }
    }
  }
}