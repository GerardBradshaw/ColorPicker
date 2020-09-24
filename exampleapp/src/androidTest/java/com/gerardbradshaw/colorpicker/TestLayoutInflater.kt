package com.gerardbradshaw.colorpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


internal class TestLayoutInflater(context: Context) : LayoutInflater(context) {
  var resId = 0
    private set

  var root: ViewGroup? = null
    private set

  override fun inflate(resource: Int, root: ViewGroup?): View? {
    resId = resource
    this.root = root
    return null
  }

  override fun inflate(resource: Int, root: ViewGroup?, attachToRoot: Boolean): View? {
    resId = resource
    this.root = root
    return null
  }

  override fun cloneInContext(newContext: Context): LayoutInflater? {
    return null
  }
}