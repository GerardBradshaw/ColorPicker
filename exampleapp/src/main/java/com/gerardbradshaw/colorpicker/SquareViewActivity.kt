package com.gerardbradshaw.colorpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gerardbradshaw.library.AbstractColorPicker

class SquareViewActivity : AppCompatActivity(), AbstractColorPicker.ColorChangedListener {

  private lateinit var picker: AbstractColorPicker
  private lateinit var listener: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_square_view)

    picker = findViewById(R.id.ex_square_picker)
    listener = findViewById(R.id.ex_square_listener)

    picker.setOnColorSelectedListener(this)
  }

  override fun onColorChanged(color: Int) {
    listener.setBackgroundColor(color)
  }
}