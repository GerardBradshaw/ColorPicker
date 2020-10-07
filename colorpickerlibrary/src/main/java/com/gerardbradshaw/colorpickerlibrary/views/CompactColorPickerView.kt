package com.gerardbradshaw.colorpickerlibrary.views

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.gerardbradshaw.colorpickerlibrary.R
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView

class CompactColorPickerView : AbstractColorPickerView {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context) {
    initView()
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    initView(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
    initView(attrs)
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
  private lateinit var slider: ColorSliderView
  private lateinit var preview: ImageView
  private var menuType = 0
  private var sliderType: SliderType = SliderType.COLOR



  // ------------------------ INITIALIZATION ------------------------

  init {
    View.inflate(context, R.layout.color_picker_library_view_picker_compact, this)
  }

  private fun initView(attrs: AttributeSet? = null) {
    if (attrs != null) saveCompactAttrs(attrs)
    initMenu()
    initSlider()
    initPreview()
  }

  private fun initMenu() {
    val menu: View =
      if (menuType == 0) findViewById(R.id.color_picker_library_compact_text_menu)
      else findViewById(R.id.color_picker_library_compact_image_menu)

    menu.visibility = View.VISIBLE

    menuButton = findViewById(R.id.color_picker_library_compact_menu_frame)

    menuButton.setOnClickListener {
      showPopupMenu(it)
    }
  }

  private fun initSlider() {
    slider = findViewById(R.id.compact_color_slider)

    slider.setOnProgressChangedListener(object : ColorSliderView.OnProgressChangedListener {
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
      it.menuInflater.inflate(R.menu.color_picker_library_color_options, it.menu)

      it.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
          val menuText = when (item?.itemId) {
            R.id.color_picker_library_option_color -> {
              sliderType =
                SliderType.COLOR
              resources.getString(R.string.color_picker_library_color)
            }
            R.id.color_picker_library_option_shade -> {
              sliderType =
                SliderType.SHADE
              resources.getString(R.string.color_picker_library_shade)
            }
            R.id.color_picker_library_option_tint -> {
              sliderType =
                SliderType.TINT
              resources.getString(R.string.color_picker_library_tint)
            }
            else -> return false
          }

          findViewById<TextView>(R.id.color_picker_library_compact_text_menu_text).text = menuText
          syncGradientBarWithSliderType()
          return true
        }
      })

      it.show()
    }
  }


  // ------------------------ HELPERS ------------------------

  private fun setSeekBarPosition() {
    val expectedRatio = when (sliderType) {
      SliderType.COLOR -> colorRatio
      SliderType.SHADE -> shadeRatio
      SliderType.TINT -> tintRatio
    }
    if (slider.getProgressRatio() != expectedRatio) slider.setProgressRatio(expectedRatio)
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

  override fun onColorRatioChanged() {
    setSeekBarPosition()
  }

  override fun onShadeRatioChanged() {
    setSeekBarPosition()
  }

  override fun onTintRatioChanged() {
    setSeekBarPosition()
  }

  // ------------------------ PUBLIC FUNCTIONS ------------------------

  fun setSliderPositionRatio(ratio: Double) {
    slider.setProgressRatio(ratio)
  }

  fun getSliderPositionRatio(): Double {
    return slider.getProgressRatio()
  }



  // ------------------------ INNER CLASSES ------------------------

  companion object {
    private const val TAG = "CompactColorPickerView"
  }

  private enum class SliderType {
    COLOR, SHADE, TINT
  }
}