package com.gerardbradshaw.colorpicker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  fun buttonClicked(view: View) {
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