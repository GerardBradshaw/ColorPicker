package com.gerardbradshaw.exampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gerardbradshaw.colorpickerlibrary.views.AbstractColorPickerView

class CompactViewActivity : AppCompatActivity(), AbstractColorPickerView.ColorChangedListener {

  private lateinit var picker: AbstractColorPickerView
  private lateinit var listener: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.color_picker_library_activity_compact_example)

    picker = findViewById(R.id.color_picker_library_example_compact_picker)
    listener = findViewById(R.id.color_picker_library_example_listener)

    picker.setOnColorSelectedListener(this)

    updateListenerColor(picker.getCurrentColor())
  }

  override fun onColorChanged(color: Int) {
    updateListenerColor(color)
  }

  private fun updateListenerColor(color: Int) {
    listener.setTag(R.id.color_picker_library_color_tag, color)
    listener.setBackgroundColor(color)
  }
}