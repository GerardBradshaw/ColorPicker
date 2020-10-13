package com.gerardbradshaw.exampleapp.util

import android.view.View
import android.widget.SeekBar
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView
import com.gerardbradshaw.exampleapp.compactview.CompactViewUnitTestUtil
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isA
import org.junit.Assert.assertThat
import org.junit.Assert.fail

internal object RobolectricTestUtil {
  private const val TAG = "RobolectricTestUtil"
  const val sliderMax = 16777216

  fun moveSeekBarTo(progress: Int, seekBar: SeekBar) {
    seekBar.progress = progress
  }

  fun checkViewColorTagIsExactly(color: Int, view: View) {
    val tag = view.getTag(R.id.color_picker_library_color_tag) ?: fail("null tag")
    assertThat(tag as Int, equalTo(color))
  }

  fun checkSeekBarIsAtProgress(progress: Int, seekBar: SeekBar) {
    assertThat(seekBar.progress, equalTo(progress))
  }

  fun getHexString(color: Int): String {
    return String.format("#%06X", 0xFFFFFF and color)
  }

  fun checkSeekBarTagIs(tag: Int?, seekBar: SeekBar) {
    assertThat(seekBar.getTag(R.id.color_picker_library_color_tag) as Int?, equalTo(tag))
  }

  fun checkSeekBarTypeIs(type: ColorSliderView.SliderType, slider: ColorSliderView) {
    assertThat(slider.sliderType, equalTo(type))
  }
}