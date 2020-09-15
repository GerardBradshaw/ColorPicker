package com.gerardbradshaw.colorpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gerardbradshaw.library.AbstractColorPicker
import com.gerardbradshaw.library.CompactColorPickerView

class CompactViewActivity : AppCompatActivity(), AbstractColorPicker.ColorChangedListener {

  private lateinit var picker: AbstractColorPicker
  private lateinit var listener: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_compact_view)

    picker = findViewById(R.id.ex_compact_picker)
    listener = findViewById(R.id.ex_compact_listener)

    picker.setOnColorSelectedListener(this)
  }

  override fun onColorChanged(hexColor: Int) {
    listener.setBackgroundColor(hexColor)
  }
}