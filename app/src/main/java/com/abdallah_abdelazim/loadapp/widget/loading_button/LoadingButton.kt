package com.abdallah_abdelazim.loadapp.widget.loading_button

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import com.abdallah_abdelazim.loadapp.R
import com.abdallah_abdelazim.loadapp.utils.dpToPx
import com.google.android.material.color.MaterialColors
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    @ColorInt
    var defaultBackgroundColor = 0
        private set

    @ColorInt
    var loadingBackgroundColor = 0
        private set

    var defaultText: String? = null
        private set

    var loadingText: String? = null
        private set

    private var defaultTextColor = Color.WHITE
    private var loadingTextColor = defaultTextColor
    private var loadingCircleColor = Color.RED

    var buttonState: ButtonState by Delegates.observable(ButtonState.Idle) { p, old, new ->
        when (new) {
            ButtonState.Idle -> if (old !is ButtonState.Idle) invalidate()
            ButtonState.Clicked -> startLoadingAnimation()
            ButtonState.Completed -> invalidate()
        }
    }

    private var widthSize = 0
    private var heightSize = 0

    private val cornerRadius = context.dpToPx(8f)

    private var loadingBackgroundValueAnimator = ValueAnimator()
    private var loadingCircleValueAnimator = ValueAnimator()

    private var progress = 0f
    private var angle = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = context.dpToPx(20f)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton, defStyleAttr, defStyleRes) {
            defaultBackgroundColor = getColor(R.styleable.LoadingButton_defaultBackgroundColor, 0)
            loadingBackgroundColor = getColor(R.styleable.LoadingButton_loadingBackgroundColor, 0)
            defaultText = getString(R.styleable.LoadingButton_defaultText)
            loadingText = getString(R.styleable.LoadingButton_loadingText) ?: defaultText
        }

        defaultTextColor =
            if (MaterialColors.isColorLight(defaultBackgroundColor)) Color.BLACK else Color.WHITE

        loadingTextColor =
            if (MaterialColors.isColorLight(loadingBackgroundColor)) Color.BLACK else Color.WHITE

        loadingCircleColor =
            MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondary)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        when (buttonState) {
            ButtonState.Clicked -> drawClickedState(canvas)
            ButtonState.Idle, ButtonState.Completed -> drawDefaultButton(canvas)
            // P.S. I do not differentiate in the UI between Idle & Completed. It's out of scope.
            // Maybe in a second iteration I may draw different UI for each.
        }

    }

    private fun drawClickedState(canvas: Canvas) {
        drawDefaultBackground(canvas)

        // Draw loading rect
        paint.color = loadingBackgroundColor
        canvas.drawRoundRect(
            0f,
            0f,
            progress,
            heightSize.toFloat(),
            cornerRadius,
            cornerRadius,
            paint
        )

        // Draw loading text
        paint.color = loadingTextColor
        canvas.drawText(
            loadingText ?: "",
            (widthSize / 2f),
            (heightSize / 2 - (paint.descent() + paint.ascent()) / 2),
            paint
        )

        // Draw loading circle
        paint.color = loadingCircleColor
        canvas.drawArc(
            widthSize.toFloat() - 150f,
            heightSize.toFloat() / 2 - 50f,
            widthSize.toFloat() - 50f,
            heightSize.toFloat() / 2 + 50f,
            0.0f,
            angle,
            true,
            paint
        )
    }

    private fun drawDefaultButton(canvas: Canvas) {
        drawDefaultBackground(canvas)

        // Draw default text
        paint.color = defaultTextColor
        canvas.drawText(
            defaultText ?: "",
            (widthSize / 2f),
            (heightSize / 2 - (paint.descent() + paint.ascent()) / 2),
            paint
        )
    }

    private fun drawDefaultBackground(canvas: Canvas) {
        paint.color = defaultBackgroundColor
        canvas.drawRoundRect(
            0f,
            0f,
            widthSize.toFloat(),
            heightSize.toFloat(),
            cornerRadius,
            cornerRadius,
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun startLoadingAnimation() {
        startLoadingBackgroundAnimator()
        startLoadingCircleAnimator()
    }

    private fun startLoadingBackgroundAnimator() {
        val width = measuredWidth
        loadingBackgroundValueAnimator = ValueAnimator.ofFloat(0f, width.toFloat()).apply {
            duration = 2500
            repeatCount = 0
            addUpdateListener { valueAnimator ->
                progress = valueAnimator.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun startLoadingCircleAnimator() {
        loadingCircleValueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { valueAnimator ->
                angle = valueAnimator.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

}