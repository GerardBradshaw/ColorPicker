package com.gerardbradshaw.colorpicker

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.*
import java.lang.String
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

class CompactColorPickerView : FrameLayout {

  // ------------------------ CONSTRUCTORS ------------------------

  constructor(context: Context) : super(context) {
    initView()
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    initWithAttrs(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
    initWithAttrs(attrs)
  }



  // ------------------------ PROPERTIES ------------------------

  private lateinit var menuButton: View
  private var menuType = 0

  private lateinit var seekBar: SeekBar
  private lateinit var gradientBar: FrameLayout

  private lateinit var preview: ImageView
  private var isPreviewEnabled = true

  private var currentSlider: SliderType = SliderType.COLOR

  private var colorRatio = 0.0
  private var shadeRatio = 0.0
  private var tintRatio = 0.0

  var listener: ColorChangedListener? = null



  // ------------------------ INITIALIZATION ------------------------

  init {
    View.inflate(context, R.layout.picker_compact, this)
  }

  private fun initView() {
    initMenu()
    initSlider()
    initPreview()
  }

  private fun initWithAttrs(attrs: AttributeSet?) {
    if (attrs == null) initView()

    context.theme.obtainStyledAttributes(attrs, R.styleable.CompactColorPickerView, 0, 0).apply {
      try {
        menuType = getInteger(R.styleable.CompactColorPickerView_menuType, 0)
        isPreviewEnabled = getBoolean(R.styleable.CompactColorPickerView_enablePreview, true)
        initView()

      } finally {
        recycle()
      }
    }
  }

  private fun initMenu() {
    menuButton = if (menuType == 0) findViewById(R.id.text_menu) else findViewById(R.id.image_menu)

    menuButton.visibility = View.VISIBLE

    menuButton.setOnClickListener {
      showPopupMenu(it)
    }
  }

  private fun initSlider() {
    initSeekBar()
    initGradientBar()
  }

  private fun initPreview() {
    preview = findViewById(R.id.preview)
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
              currentSlider = SliderType.COLOR
              resources.getString(R.string.color)
            }
            R.id.option_shade -> {
              currentSlider = SliderType.SHADE
              resources.getString(R.string.shade)
            }
            R.id.option_tint -> {
              currentSlider = SliderType.TINT
              resources.getString(R.string.tint)
            }
            else -> return false
          }

          findViewById<TextView>(R.id.menu_text).text = menuText
          initGradientBar()
          return true
        }
      })

      it.show()
    }
  }

  private fun initSeekBar() {
    seekBar = findViewById(R.id.seek_bar)

    setSeekBarPosition()

    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onStartTrackingTouch(seekBar: SeekBar?) { /* Required empty */ }
      override fun onStopTrackingTouch(seekBar: SeekBar?) { /* Required empty */ }

      override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (currentSlider) {
          SliderType.COLOR -> colorRatio = progress.toDouble() / seekBar.max.toDouble()
          SliderType.SHADE -> shadeRatio = progress.toDouble() / seekBar.max.toDouble()
          SliderType.TINT -> tintRatio = progress.toDouble() / seekBar.max.toDouble()
        }
        onColorChanged()
      }
    })
  }

  private fun initGradientBar() {
    val gradientDrawable = when (currentSlider) {
      SliderType.COLOR -> {
        GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
          Color.parseColor("#FF0000"),
          Color.parseColor("#FFFF00"),
          Color.parseColor("#00FF00"),
          Color.parseColor("#00FFFF"),
          Color.parseColor("#0000FF"),
          Color.parseColor("#FF00FF"),
          Color.parseColor("#FF0000")
        ))
      }

      SliderType.SHADE -> {
        GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
          Color.parseColor(String.format("#%06X", 0xFFFFFF and getTintedColor(getPureColor()))),
          Color.parseColor("#000000")
        ))
      }

      SliderType.TINT -> {
        GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
          Color.parseColor(String.format("#%06X", 0xFFFFFF and getCurrentColor())),
          Color.parseColor(String.format("#%06X", 0xFFFFFF and getTintedColor(getShadedColor(getPureColor()), 1.0)))
        ))
      }
    }

    gradientBar = findViewById(R.id.seek_bar_line)
    gradientBar.background = gradientDrawable
    setSeekBarPosition()
  }



  // ------------------------ COLOR GETTERS ------------------------

  private fun getIthColor(i: Int): Int {
    val colorCount = 16777216.0
    val gradientCount = 6.0
    val colorsPerGradient = colorCount / gradientCount

    val currentGradientNumber = floor(i.toDouble() / colorsPerGradient)
    val positionInGradient = (i.toDouble() - (currentGradientNumber * colorsPerGradient)) / (colorsPerGradient)

    val full = 255
    val fadeIn = (255.0 * positionInGradient).roundToInt()
    val fadeOut = (255.0 * (1.0 - positionInGradient)).roundToInt()
    val none = 0

    return when {
      currentGradientNumber < 1 -> Color.argb(full, full, fadeIn, none)
      currentGradientNumber < 2 -> Color.argb(full, fadeOut, full, none)
      currentGradientNumber < 3 -> Color.argb(full, none, full, fadeIn)
      currentGradientNumber < 4 -> Color.argb(full, none, fadeOut, full)
      currentGradientNumber < 5 -> Color.argb(full, fadeIn, none, full)
      currentGradientNumber <= 6 -> {
        if (i != colorCount.toInt()) Color.argb(full, full, none, fadeOut)
        else Color.argb(full, full, none, 1)
      }
      else -> {
        Log.d(TAG, "getIthColor: i too large. Returning white.")
        Color.argb(full, none, none, none)
      }
    }
  }

  private fun getPureColor(): Int {
    return getIthColor((seekBar.max.toDouble() * colorRatio).roundToInt())
  }

  private fun getShadedColor(color: Int, shadeFactor: Double = 1.0 - shadeRatio): Int {
    val red = (Color.red(color) * shadeFactor).roundToInt()
    val green = (Color.green(color) * shadeFactor).roundToInt()
    val blue = (Color.blue(color) * shadeFactor).roundToInt()

    return Color.argb(255, red, green, blue)
  }

  private fun getTintedColor(color: Int, tintRatio: Double = this.tintRatio): Int {
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)

    return when (max(red, max(green, blue))) {
      red -> {
        Color.argb(255,
          red,
          green + ((red - green).toFloat() * tintRatio).roundToInt(),
          blue + ((red - blue).toFloat() * tintRatio).roundToInt())
      }

      green -> {
        Color.argb(255,
          red + ((green - red).toFloat() * tintRatio).roundToInt(),
          green,
          blue + ((green - blue).toFloat() * tintRatio).roundToInt())
      }

      blue -> {
        Color.argb(255,
          red + ((blue - red).toFloat() * tintRatio).roundToInt(),
          green + ((blue - green).toFloat() * tintRatio).roundToInt(),
          blue)
      }

      else -> {
        Log.d(TAG, "getCurrentColor: unable to tint")
        Color.argb(255, red, green, blue)
      }
    }
  }

  private fun onColorChanged() {
    val colorHex = getCurrentColorHex()
    if (isPreviewEnabled) preview.setColorFilter(colorHex)
    listener?.onColorChanged(colorHex)
  }



  // ------------------------ PUBLIC FUNCTIONS ------------------------

  /** Returns the current color including shade and tint as an Int. */
  fun getCurrentColor(): Int {
    return getTintedColor(getShadedColor(getPureColor()))
  }

  /** Returns the current color including shade and tint as a hexadecimal Int. */
  fun getCurrentColorHex(): Int {
    val hexColor = String.format("#%06X", 0xFFFFFF and getCurrentColor())
    return Color.parseColor(hexColor)
  }

  fun setOnColorSelectedListener(listener: ColorChangedListener) {
    this.listener = listener
  }



  // ------------------------ HELPERS ------------------------

  private fun dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      dp.toFloat(),
      resources.displayMetrics).roundToInt()
  }

  private fun setSeekBarPosition() {
    seekBar.progress = when (currentSlider) {
      SliderType.COLOR -> (colorRatio * seekBar.max).roundToInt()
      SliderType.SHADE -> (shadeRatio * seekBar.max).roundToInt()
      SliderType.TINT -> (tintRatio * seekBar.max).roundToInt()
    }
  }



  // ------------------------ INNER CLASSES ------------------------

  companion object {
    private const val TAG = "CompactColorPickerView"
  }

  interface ColorChangedListener {
    fun onColorChanged(hexColor: Int)
  }

  private enum class SliderType {
    COLOR, SHADE, TINT
  }
}