package com.gerardbradshaw.colorpickerlibrary.compactview

import android.view.Menu
import android.widget.FrameLayout
import com.gerardbradshaw.colorpickerlibrary.R
import org.robolectric.shadows.ShadowPopupMenu

object CompactViewUnitTestUtil {

  // -------------------- PUBLIC METHODS --------------------

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