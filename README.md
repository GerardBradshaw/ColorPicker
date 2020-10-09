# ColorPicker
ColorPicker is an Android library of lightweight custom views that allow a user to input colors. There are 3 predefined views:
- CompactColorPicker - a compact view that fits nicely in any layout
- SquareColorPicker - a large view with a square gradient allowing fine control of tint and shade. Useful for dialogues and fragments.
- RadialColorPicker - a large view with the standard RGB circle. Useful for dialogues and fragments.

In addition to these, the AbstractColorPicker class can be used to create your own color picker without the need worry about the underlying logic.

## Dependency
To use the library, add the following to your gradle build file:
```groovy
repositories {
  jcenter()
}

dependencies {
  implementation 'com.gerardbradshaw:colorpicker:2.0.1'
}
```

Older versions are available [here](https://bintray.com/gerardb/Android/ColorPickerView).

## Views
There are 3 predefined color pickers in this library.
### CompactColorPickerView
The compact picker fits nicely in any layout. The menu appearance and presence of a preview are customizable in code or XML. The menu provides sliders of pure RGB color, shade, and tint.
<img src=https://i.imgur.com/9kin7eH.png>

### SquareColorPickerView
The square picker is a familiar implementation of the common RGB picker. A slider provides the pure RGB color selection, and a prominent square allows users to slider or tap to define the shade and tint. The previous color can be easily defined for a preview of the difference, and the preview itself can be toggled in code or XML.
<img src=https://i.imgur.com/z4oaff6.png>

### RadialColorPickerView
The radial picker displays a large, appealing color wheel for a user to select a color. The wheel defines the pure color and tint, while a separate slider allows full control of the shade level. The previous color can be easily defined for a preview of the difference, and the preview itself can be toggled in code or XML.
<img src=https://i.imgur.com/nDnQU8D.png>

## AbstractColorPicker
The AbstractColorPicker class can be extended so you can make your own color picker! This class provides nifty methods for setting the color specturm ratio, shade ratio, and tint ratio, and then returning the color produced by these combinations. See the documentation for full details!

## Examples
Implementations can be found by launching the example app included in this repo.

## Compatibility
This library has been tested with SDK 21 and up.

## License
TouchImageView is available under the Apache Licence 2.0. See the LICENSE file for more info.