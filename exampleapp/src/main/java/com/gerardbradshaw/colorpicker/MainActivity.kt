package com.gerardbradshaw.colorpicker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  fun buttonClicked(view: View) {
    when (view.id) {
      R.id.round_dialog_example_button, R.id.square_dialog_example_button -> {
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show()
        return
      }
    }

    val activityToLaunch = when (view.id) {
      R.id.compact_example_button -> CompactViewActivity::class.java
      R.id.square_example_button -> SquareViewActivity::class.java
      R.id.round_example_button -> RadialViewActivity::class.java
      else -> return
    }

    startActivity(Intent(this, activityToLaunch))
  }

  companion object {
    private const val TAG = "MainActivity"
  }
}