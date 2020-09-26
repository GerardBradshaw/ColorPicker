package com.gerardbradshaw.colorpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gerardbradshaw.library.views.AbstractColorPickerView

class CompactViewActivity : AppCompatActivity(), AbstractColorPickerView.ColorChangedListener {

  private lateinit var picker: AbstractColorPickerView
  private lateinit var listener: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_compact_view)

    picker = findViewById(R.id.ex_compact_picker)
    listener = findViewById(R.id.ex_compact_listener)

    picker.setOnColorSelectedListener(this)

    onColorChanged(picker.getCurrentColor())
  }

  override fun onColorChanged(color: Int) {
    listener.setBackgroundColor(color)
  }
}