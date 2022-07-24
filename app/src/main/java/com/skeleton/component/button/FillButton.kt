package com.skeleton.component.button
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.lib.page.PageUI
import com.ironraft.pupping.bero.R
import com.ironraft.pupping.bero.databinding.UiFillButtonBinding
import com.lib.page.PageButtonType

open class FillButton : PageUI {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { initialize(context, attrs)}
    private lateinit var binding: UiFillButtonBinding
    private var btnType:PageButtonType = PageButtonType.Fill

    override fun onInit() {
        super.onInit()
        binding = UiFillButtonBinding.inflate(LayoutInflater.from(context), this, true)
    }
    @SuppressLint("CustomViewStyleable", "Recycle")
    final override fun initialize(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val style = context.obtainStyledAttributes(it, R.styleable.PageUIStyle)
            if (style.hasValue(R.styleable.PageUIStyle_android_title)) {
                this.text = style.getString(R.styleable.PageUIStyle_android_title)
            }
            val btnStyle = context.obtainStyledAttributes(it, R.styleable.PageButtonStyle)
            var type = PageButtonType.Fill
            if (btnStyle.hasValue(R.styleable.PageButtonStyle_buttonType)) {
                val resIdx = btnStyle.getInt(R.styleable.PageButtonStyle_buttonType, 0)
                type = PageButtonType.getType(resIdx)
            }
            setupButton(type, style)
            super.initialize(context, attrs)
        }
    }
    fun setupButton(type:PageButtonType, isSelected:Boolean = false):FillButton{
        setupButton(type)
        this.selected = isSelected
        return this
    }
    private fun setupButton(type:PageButtonType, style:TypedArray){

        this.btnType = type
        this.radius = context.resources.getDimension(R.dimen.radius_thin)
        this.defaultTextSize = context.resources.getDimension(R.dimen.font_light) / context.resources.displayMetrics.density
        val defaultColor = context.getColor( R.color.app_black )
        val bgColor = context.getColor( R.color.app_white )
        when(type){
            PageButtonType.Stroke -> {
                if (!style.hasValue(R.styleable.PageUIStyle_defaultStrokeColor)) {
                    this.defaultStrokeColor = defaultColor
                }
                if (!style.hasValue(R.styleable.PageUIStyle_defaultStrokeWidth)) {
                    this.defaultStroke = context.resources.getDimension(R.dimen.stroke_light)
                }
                if (!style.hasValue(R.styleable.PageUIStyle_defaultBgColor)) {
                    this.defaultBgColor = bgColor
                }
                if (!style.hasValue(R.styleable.PageUIStyle_defaultContentColor)) {
                    this.defaultContentColor = defaultColor
                }
            }
            else -> {
                if (!style.hasValue(R.styleable.PageUIStyle_defaultBgColor)) {
                    this.defaultBgColor = defaultColor
                }
                if (!style.hasValue(R.styleable.PageUIStyle_defaultContentColor)) {
                    this.defaultContentColor = bgColor
                }
            }
        }
    }

    fun getButton():Button{
        return binding.btn
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.btn.setOnClickListener(l)
    }

    @DrawableRes
    var defaultImageRes:Int? = null

    @DrawableRes
    var activeImageRes:Int? = null


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
            setBgColor(field)
            setOutline(field)
            defaultContentColor?.let { color ->
                if ( field == false) binding.text.setTextColor( color  )
                else binding.text.setTextColor( activeContentColor ?: color )
            }
            defaultTextSize?.let { size ->
                if ( field == false) binding.text.textSize = size
                else  binding.text.textSize = activeTextSize ?: size
            }
            val drawable: Drawable? = if (defaultImageRes != null) AppCompatResources.getDrawable(context,defaultImageRes!!) else defaultImage
            val drawableAc: Drawable? = if (activeImageRes != null) AppCompatResources.getDrawable(context,activeImageRes!!) else activeImage
            if ( drawable==null && drawableAc==null ) {
                binding.imageIcon.visibility = View.GONE
                return
            }
            binding.imageIcon.visibility = View.VISIBLE
            if ( field == false) {
                binding.imageIcon.setImageDrawable(drawable ?: drawableAc!!)
            }
            else {
                binding.imageIcon.setImageDrawable(drawableAc ?: drawable!!)
            }
            when(this.btnType){
                PageButtonType.Fill -> {
                    defaultContentColor?.let { color ->
                        if (field == false) {
                            binding.imageIcon.setColorFilter(color)
                        } else {
                            binding.imageIcon.setColorFilter(activeContentColor ?: color)
                        }
                    }
                }
                PageButtonType.Stroke -> {}
            }
        }
}