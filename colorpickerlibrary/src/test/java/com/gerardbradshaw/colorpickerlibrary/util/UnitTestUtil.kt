package com.gerardbradshaw.colorpickerlibrary.util

import android.widget.ImageView
import android.widget.SeekBar
import com.gerardbradshaw.colorpickerlibrary.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Assert.fail

object UnitTestUtil {

  private const val TAG = "UnitTestUtil"
  const val sliderMax = 16777216

  // ---------------- PUBLIC METHODS ----------------

  fun moveSeekBarTo(progress: Int, seekBar: SeekBar) {
    seekBar.progress = progress
  }

  fun checkPreviewColorIs(color: Int, preview: ImageView) {
    val tag = preview.getTag(R.id.color_picker_library_color_tag) ?: fail("null tag")
    assertThat(tag as Int, equalTo(color))
  }

  fun checkSeekBarIsAtProgress(progress: Int, seekBar: SeekBar) {
    assertThat(seekBar.progress, equalTo(progress))
  }

  fun getHexString(color: Int): String {
    return String.format("#%06X", 0xFFFFFF and color)
  }
}