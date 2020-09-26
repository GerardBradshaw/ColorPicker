package com.gerardbradshaw.library.views

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.gerardbradshaw.library.R
import com.gerardbradshaw.library.util.ColorSlider

class CompactColorPickerView :
  AbstractColorPickerView {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    if (attrs != null) saveCompactAttrs(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
    if (attrs != null) saveCompactAttrs(attrs)
  }

  private fun saveCompactAttrs(attrs: AttributeSet) {
    context.theme.obtainStyledAttributes(
      attrs, R.styleable.CompactColorPickerView, 0, 0).apply {
      try {
        menuType = getInteger(R.styleable.CompactColorPickerView_menuType, 0)

      } finally { recycle() }
    }
  }



  // ------------------------ PROPERTIES ------------------------

  private lateinit var menuButton: FrameLayout
  private lateinit var slider: ColorSlider
  private lateinit var preview: ImageView
  private var menuType = 0
  private var sliderType: SliderType =
    SliderType.COLOR


  // ------------------------ INITIALIZATION ------------------------

  init {
    View.inflate(context,
      R.layout.view_picker_compact, this)
    initView()
  }

  private fun initView() {
    initMenu()
    initSlider()
    initPreview()
  }

  private fun initMenu() {
    val menu: View =
      if (menuType == 0) findViewById(R.id.compact_text_menu)
      else findViewById(R.id.compact_image_menu)

    menu.visibility = View.VISIBLE

    menuButton = findViewById(R.id.compact_menu_frame)

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
    if (isPreviewEnabled) preview.setColor(getCurrentColor())
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
    val color = getCurrentColor()
    if (isPreviewEnabled) preview.setColor(color)
    listener?.onColorChanged(color)
  }

  private fun ImageView.setColor(color: Int) {
    this.setColorFilter(color)
    this.tag = color
  }


  // ------------------------ INNER CLASSES ------------------------

  companion object {
    private const val TAG = "CompactColorPickerView"
  }

  private enum class SliderType {
    COLOR, SHADE, TINT
  }
}