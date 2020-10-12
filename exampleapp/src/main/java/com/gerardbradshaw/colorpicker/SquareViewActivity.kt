package com.gerardbradshaw.colorpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gerardbradshaw.colorpickerlibrary.views.AbstractColorPickerView

class SquareViewActivity : AppCompatActivity(), AbstractColorPickerView.ColorChangedListener {

  private lateinit var picker: AbstractColorPickerView
  private lateinit var listener: View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_square_view)

    picker = findViewById(R.id.example_square_picker)
    listener = findViewById(R.id.example_listener)

    picker.setOnColorSelectedListener(this)
  }

  override fun onColorChanged(color: Int) {
    listener.setBackgroundColor(color)
  }
}