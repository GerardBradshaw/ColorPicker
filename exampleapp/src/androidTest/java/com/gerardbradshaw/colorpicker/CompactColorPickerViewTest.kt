package com.gerardbradshaw.colorpicker

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CompactColorPickerViewTest {

  private lateinit var view: com.gerardbradshaw.library.CompactColorPickerView

  @Before
  fun setUp() {

  }


  @Test
  fun useAppContext() {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.gerardbradshaw.colorpicker", appContext.packageName)
  }
}