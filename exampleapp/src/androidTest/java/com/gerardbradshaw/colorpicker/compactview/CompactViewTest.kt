package com.gerardbradshaw.colorpicker.compactview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gerardbradshaw.colorpicker.CompactViewActivity
import com.gerardbradshaw.colorpicker.MainActivity
import com.gerardbradshaw.colorpicker.R
import com.gerardbradshaw.colorpicker.TestLayoutInflater
import com.gerardbradshaw.colorpicker.TestUtil.checkListenerChangedColorTo
import com.gerardbradshaw.colorpicker.TestUtil.checkPreviewChangedColorTo
import com.gerardbradshaw.colorpicker.TestUtil.checkSeekBarIsAtProgress
import com.gerardbradshaw.colorpicker.TestUtil.getShadedColor
import com.gerardbradshaw.colorpicker.TestUtil.getTintedAndShadedColor
import com.gerardbradshaw.colorpicker.TestUtil.getTintedColor
import com.gerardbradshaw.colorpicker.TestUtil.moveSeekBarTo
import com.gerardbradshaw.colorpicker.TestUtil.sliderMax
import com.gerardbradshaw.colorpicker.compactview.CompactViewTestUtil.SliderType
import com.gerardbradshaw.colorpicker.compactview.CompactViewTestUtil.changeSliderTypeTo
import com.gerardbradshaw.library.CompactColorPickerView
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import kotlin.math.roundToInt


@RunWith(Enclosed::class)
class CompactViewTest {

  // ---------------- SINGLE RUN TESTS ----------------

  @RunWith(AndroidJUnit4::class)
  class SingleTests {
    @Rule
    @JvmField
    val asr = ActivityScenarioRule<CompactViewActivity>(CompactViewActivity::class.java)

    @Test
    fun should_startColorSliderAtZeroProgress_when_firstEntering() {
      checkSeekBarIsAtProgress(0, R.id.slider_seek_bar)
    }

    @Test
    fun should_startShadeSliderAtZeroProgress_when_firstEntering() {
      moveSeekBarTo(sliderMax / 2, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.TINT, R.id.compact_menu_frame)
      moveSeekBarTo(sliderMax / 2, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.SHADE, R.id.compact_menu_frame)
      checkSeekBarIsAtProgress(0, R.id.slider_seek_bar)
    }

    @Test
    fun should_startTintSliderAtZeroProgress_when_firstEntering() {
      moveSeekBarTo(sliderMax / 2, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.SHADE, R.id.compact_menu_frame)
      moveSeekBarTo(sliderMax / 2, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.TINT, R.id.compact_menu_frame)
      checkSeekBarIsAtProgress(0, R.id.slider_seek_bar)
    }
  }

  @RunWith(AndroidJUnit4::class)
  class MockitoTestTest {
//    @Rule
//    @JvmField
//    val asr = ActivityScenarioRule<CompactViewActivity>(CompactViewActivity::class.java)

    @Test
    fun should_showMenuAsText_when_attributeSet() {
      val context = mock(Context::class.java)

      val typedArray = mock(TypedArray::class.java)

      val layoutInflater = TestLayoutInflater(context)

      initMockAttributes(context, typedArray, layoutInflater, 0, true)

      val view = CompactColorPickerView(context, mock(AttributeSet::class.java)) as ViewGroup

      assertEquals(layoutInflater.root, view);
    }



    private fun initMockAttributes(context: Context,
                                   typedArray: TypedArray,
                                   layoutInflater: TestLayoutInflater,
                                   menuType: Int,
                                   enablePreview: Boolean) {
      `when`<TypedArray>(context.obtainStyledAttributes(any(AttributeSet::class.java), eq(R.styleable.CompactColorPickerView)))
        .thenReturn(typedArray)

      `when`(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
        .thenReturn(layoutInflater)

      `when`(typedArray.getInteger(R.styleable.CompactColorPickerView_menuType, 0))
        .thenReturn(menuType)

      `when`(typedArray.getBoolean(R.styleable.CompactColorPickerView_enablePreview, true))
        .thenReturn(enablePreview)
    }
  }



  // ---------------- PARAMETERIZED TESTS ----------------

  @RunWith(Parameterized::class)
  class ParameterizedTests(private val inputProgress: Int, private val expectedPureColor: Int) {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<CompactViewActivity>(CompactViewActivity::class.java)

    private val tintAndShadeProgress = sliderMax - inputProgress


    // ---------------- PREVIEW ----------------

    @Test
    fun should_updatePreview_when_onlyColorSliderProgressChanged() {
      moveSeekBarTo(inputProgress, R.id.slider_seek_bar)
      checkPreviewChangedColorTo(expectedPureColor, R.id.compact_preview)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeSliderProgressChanged() {
      moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.SHADE, R.id.compact_menu_frame)
      moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

      checkPreviewChangedColorTo(
        getShadedColor(expectedPureColor, tintAndShadeProgress),
        R.id.compact_preview)
    }

    @Test
    fun should_updatePreview_when_colorAndTintSliderProgressChanged() {
      moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.TINT, R.id.compact_menu_frame)
      moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

      checkPreviewChangedColorTo(
        getTintedColor(expectedPureColor, tintAndShadeProgress),
        R.id.compact_preview)
    }

    @Test
    fun should_updatePreview_when_allSliderProgressChanged() {
      moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.SHADE, R.id.compact_menu_frame)
      moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.TINT, R.id.compact_menu_frame)
      moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

      checkPreviewChangedColorTo(
        getTintedAndShadedColor(expectedPureColor, tintAndShadeProgress, tintAndShadeProgress),
        R.id.compact_preview)
    }


    // ---------------- LISTENER ----------------

    @Test
    fun should_notifyListener_when_allSliderProgressChanged() {
      moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.SHADE, R.id.compact_menu_frame)
      moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.TINT, R.id.compact_menu_frame)
      moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

      checkListenerChangedColorTo(
        getTintedAndShadedColor(expectedPureColor, tintAndShadeProgress, tintAndShadeProgress),
        R.id.ex_compact_listener)
    }

    @Test
    fun should_notifyListener_when_onlyColorSliderPositionChanged() {
      moveSeekBarTo(inputProgress, R.id.slider_seek_bar)
      checkListenerChangedColorTo(expectedPureColor, R.id.ex_compact_listener)
    }

    @Test
    fun should_notifyListener_when_colorAndShadeSliderProgressChanged() {
      moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.SHADE, R.id.compact_menu_frame)
      moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

      checkListenerChangedColorTo(
        getShadedColor(expectedPureColor, tintAndShadeProgress),
        R.id.ex_compact_listener)
    }

    @Test
    fun should_notifyListener_when_colorAndTintSliderProgressChanged() {
      moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.TINT, R.id.compact_menu_frame)
      moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

      checkListenerChangedColorTo(
        getTintedColor(expectedPureColor, tintAndShadeProgress),
        R.id.ex_compact_listener)
    }


    // ---------------- SEEK BAR ----------------

    @Test
    fun should_startColorSliderAtPreviousProgress_when_returningFromShadeSlider() {
      moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.SHADE, R.id.compact_menu_frame)
      moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.COLOR, R.id.compact_menu_frame)
      checkSeekBarIsAtProgress(inputProgress, R.id.slider_seek_bar)
    }

    @Test
    fun should_startColorSliderAtPreviousProgress_when_returningFromTintSlider() {
      moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.TINT, R.id.compact_menu_frame)
      moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

      changeSliderTypeTo(SliderType.COLOR, R.id.compact_menu_frame)
      checkSeekBarIsAtProgress(inputProgress, R.id.slider_seek_bar)
    }


    companion object {
      @Parameterized.Parameters
      @JvmStatic
      fun data(): Collection<Array<Any>> {
        val progressInputs = Array<Any>(7) {
          (it * sliderMax.toDouble() / 6.0).roundToInt()
        }

        val expectedPureColorOutputs: Array<Any> = arrayOf(
          Color.argb(255, 255, 0, 0),
          Color.argb(255, 255, 255, 0),
          Color.argb(255, 0, 255, 0),
          Color.argb(255, 0, 255, 255),
          Color.argb(255, 0, 0, 255),
          Color.argb(255, 255, 0, 255),
          Color.argb(255, 255, 0, 1)
        )

        return Array(7) {
          arrayOf(progressInputs[it], expectedPureColorOutputs[it])
        }.asList()
      }
    }
  }
}