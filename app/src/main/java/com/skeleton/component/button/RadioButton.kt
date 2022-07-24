package com.skeleton.component.button

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.lib.page.PageUI
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.databinding.UiRadioButtonBinding

open class RadioButton : PageUI {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { initialize(context, attrs)}
    private lateinit var binding: UiRadioButtonBinding
    @SuppressLint("CustomViewStyleable", "Recycle")
    final override fun initialize(context: Context, attrs: AttributeSet?) {
        attrs?.let {

            val style = context.obtainStyledAttributes(it, R.styleable.PageUIStyle)
            if (style.hasValue(R.styleable.PageUIStyle_android_title)) {
                this.text = style.getString(R.styleable.PageUIStyle_android_title)
            }
            if (!style.hasValue(R.styleable.PageUIStyle_defaultContentColor)) {
                this.defaultContentColor = context.getColor(R.color.app_grey200)
            }
            if (!style.hasValue(R.styleable.PageUIStyle_activeContentColor)) {
                this.activeContentColor =  context.getColor(R.color.brand_secondary)
            }
            super.initialize(context, attrs)
        }
    }
    override fun onInit() {
        super.onInit()
        binding = UiRadioButtonBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.btn.setOnClickListener(l)
    }

    var text:String? = null
        set(value) {
            field = value
            if (field == null) binding.text.visibility = View.GONE
            else {
                binding.text.visibility = View.VISIBLE
                binding.text.text = field
            }
        }

    override var selected:Boolean? = null
        set(value) {
            field = value
            val context = context ?: return
            var dfColor:Int? = null
            var acColor:Int? = null
            defaultContentColor?.let { color ->
                dfColor = color
                activeContentColor?.let {  acColor = it }
            }
            dfColor?.let { color ->
                if ( field == false) binding.text.setTextColor( color  )
                else binding.text.setTextColor( context.getColor(R.color.app_grey500) )
            }

            dfColor?.let { color ->
                if ( field == false) {
                    binding.imageIcon.setColorFilter(color)
                }
                else {
                    binding.imageIcon.setColorFilter(acColor ?: color)
                }
            }
        }
}