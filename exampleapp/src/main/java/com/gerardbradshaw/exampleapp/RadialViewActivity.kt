package com.gerardbradshaw.exampleapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gerardbradshaw.colorpickerlibrary.views.AbstractColorPickerView

class RadialViewActivity : AppCompatActivity(), AbstractColorPickerView.ColorChangedListener {

  private lateinit var picker: AbstractColorPickerView
  private lateinit var listener: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_example_radial_picker)

    picker = findViewById(R.id.example_radial_picker)
    listener = findViewById(R.id.example_listener)

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