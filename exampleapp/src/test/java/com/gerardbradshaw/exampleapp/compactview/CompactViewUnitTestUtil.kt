package com.gerardbradshaw.exampleapp.compactview

import android.app.Activity
import android.view.LayoutInflater
import android.view.Menu
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.gerardbradshaw.colorpickerlibrary.R
import org.robolectric.Robolectric
import org.robolectric.shadows.ShadowPopupMenu

internal object CompactViewUnitTestUtil {

  // -------------------- PUBLIC METHODS --------------------

  fun getLayout(): LinearLayout {
    val activityController = Robolectric.buildActivity(Activity::class.java)

    return LayoutInflater
      .from(activityController.get())
      .inflate(R.layout.color_picker_library_activity_compact_example, null) as LinearLayout
  }

  fun changeSliderTypeTo(slider: SliderType, menuFrame: FrameLayout) {
    menuFrame.performClick()

    val menuItem = when (slider) {
      SliderType.COLOR -> R.id.color_picker_library_option_color
      SliderType.SHADE -> R.id.color_picker_library_option_shade
      SliderType.TINT -> R.id.color_picker_library_option_tint
    }

    ShadowPopupMenu.getLatestPopupMenu().menu
      .performIdentifierAction(menuItem, Menu.FLAG_ALWAYS_PERFORM_CLOSE)
  }



  // -------------------- HELPERS --------------------

  enum class SliderType(val value: String) {
    COLOR("Color"),
    SHADE("Shade"),
    TINT("Tint")
  }
}