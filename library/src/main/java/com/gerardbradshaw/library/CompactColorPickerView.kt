package com.gerardbradshaw.library

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.*

class CompactColorPickerView : AbstractColorPicker {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)


  // ------------------------ PROPERTIES ------------------------

  private lateinit var menuButton: View
  private lateinit var slider: ColorSlider
  private lateinit var preview: ImageView
  private var sliderType: SliderType = SliderType.COLOR


  // ------------------------ INITIALIZATION ------------------------

  init {
    View.inflate(context, R.layout.view_picker_compact, this)
    initView()
  }

  private fun initView() {
    initMenu()
    initSlider()
    initPreview()
  }

  private fun initMenu() {
    menuButton =
      if (menuType == 0) findViewById(R.id.compact_text_menu)
      else findViewById(R.id.compact_image_menu)

    menuButton.visibility = View.VISIBLE

    menuButton.setOnClickListener {
      showPopupMenu(it)
    }
  }

  private fun initSlider() {
    slider = findViewById(R.id.compact_color_slider)

    slider.setOnProgressChangedListener(object : ColorSlider.OnProgressChangedListener {
      override fun onProgressChanged(progress: Double) {
        when (sliderType) {
          SliderType.COLOR -> colorRatio = progress
          SliderType.SHADE -> shadeRatio = progress
          SliderType.TINT -> tintRatio = progress
        }
        onColorChanged()
      }
    })

    syncGradientBarWithSliderType()
  }

  private fun syncGradientBarWithSliderType() {
    val gradientDrawable = when (sliderType) {
      SliderType.COLOR -> getSpectrumGradient()
      SliderType.SHADE -> getShadeGradient()
      SliderType.TINT -> getTintGradient()
    }

    slider.setGradientBarDrawable(gradientDrawable)
    setSeekBarPosition()
  }

  private fun initPreview() {
    preview = findViewById(R.id.compact_preview)
    if (isPreviewEnabled) preview.setColorFilter(getCurrentColorHex())
    else preview.visibility = View.GONE
  }

  private fun showPopupMenu(view: View) {
    PopupMenu(context, view).also {
      it.menuInflater.inflate(R.menu.color_options, it.menu)

      it.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
          val menuText = when (item?.itemId) {
            R.id.option_color -> {
              sliderType =
                SliderType.COLOR
              resources.getString(R.string.color)
            }
            R.id.option_shade -> {
              sliderType =
                SliderType.SHADE
              resources.getString(R.string.shade)
            }
            R.id.option_tint -> {
              sliderType =
                SliderType.TINT
              resources.getString(R.string.tint)
            }
            else -> return false
          }

          findViewById<TextView>(R.id.compact_text_menu_text).text = menuText
          syncGradientBarWithSliderType()
          return true
        }
      })

      it.show()
    }
  }


  // ------------------------ HELPERS ------------------------

  private fun setSeekBarPosition() {
    val progress = when (sliderType) {
      SliderType.COLOR -> colorRatio
      SliderType.SHADE -> shadeRatio
      SliderType.TINT -> tintRatio
    }

    slider.setProgressRatio(progress)
  }

  override fun onColorChanged() {
    val colorHex = getCurrentColorHex()
    if (isPreviewEnabled) preview.setColorFilter(colorHex)
    listener?.onColorChanged(colorHex)
  }


  // ------------------------ INNER CLASSES ------------------------

  companion object {
    private const val TAG = "CompactColorPickerView"
  }

  private enum class SliderType {
    COLOR, SHADE, TINT
  }
}