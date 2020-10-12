package com.gerardbradshaw.colorpickerlibrary.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.views.AbstractColorPickerView

internal class CompactActivity : AppCompatActivity(), AbstractColorPickerView.ColorChangedListener {

  private lateinit var picker: AbstractColorPickerView
  private lateinit var listener: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.color_picker_library_activity_compact)

    picker = findViewById(R.id.color_picker_library_example_compact_picker)
    listener = findViewById(R.id.color_picker_library_example_listener)

    picker.setOnColorSelectedListener(this)

    updateListenerColor(picker.getCurrentColor())
  }

  override fun onColorChanged(color: Int) {
    updateListenerColor(color)
  }

  private fun updateListenerColor(color: Int) {
    listener.tag = color
    listener.setBackgroundColor(color)
  }
}