@file:Suppress("unused")

package com.me.blelib.ext

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * 设置View的高度
 */
fun View.cHeight(height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    params.height = height
    layoutParams = params
    return this
}

/**
 * 设置View高度，限制在min和max范围之内
 * @param h
 * @param min 最小高度
 * @param max 最大高度
 */
fun View.limitHeight(h: Int, min: Int, max: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    when {
        h < min -> params.height = min
        h > max -> params.height = max
        else -> params.height = h
    }
    layoutParams = params
    return this
}

/**
 * 设置View的宽度
 */
fun View.cWidth(width: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    params.width = width
    layoutParams = params
    return this
}

/**
 * 设置View宽度，限制在min和max范围之内
 * @param w
 * @param min 最小宽度
 * @param max 最大宽度
 */
fun View.limitWidth(w: Int, min: Int, max: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    when {
        w < min -> params.width = min
        w > max -> params.width = max
        else -> params.width = w
    }
    layoutParams = params
    return this
}

/**
 * 设置View的宽度和高度
 * @param width 要设置的宽度
 * @param height 要设置的高度
 */
fun View.widthAndHeight(width: Int, height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    params.width = width
    params.height = height
    layoutParams = params
    return this
}

/**
 * 设置View的margin
 * @param leftMargin 默认保留原来的
 * @param topMargin 默认是保留原来的
 * @param rightMargin 默认是保留原来的
 * @param bottomMargin 默认是保留原来的
 */
fun View.margin(leftMargin: Int = Int.MAX_VALUE, topMargin: Int = Int.MAX_VALUE, rightMargin: Int = Int.MAX_VALUE, bottomMargin: Int = Int.MAX_VALUE): View {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    if (leftMargin != Int.MAX_VALUE)
        params.leftMargin = leftMargin
    if (topMargin != Int.MAX_VALUE)
        params.topMargin = topMargin
    if (rightMargin != Int.MAX_VALUE)
        params.rightMargin = rightMargin
    if (bottomMargin != Int.MAX_VALUE)
        params.bottomMargin = bottomMargin
    layoutParams = params
    return this
}

/**
 * 设置宽度，带有过渡动画
 * @param targetValue 目标宽度
 * @param duration 时长
 * @param action 可选行为
 */
fun View.animateWidth(targetValue: Int, duration: Long = 400, listener: Animator.AnimatorListener? = null, action: ((Float) -> Unit)? = null) {
    post {
        ValueAnimator.ofInt(width, targetValue).apply {
            addUpdateListener {
                cWidth(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if(listener!=null)addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

/**
 * 设置高度，带有过渡动画
 * @param targetValue 目标高度
 * @param duration 时长
 * @param action 可选行为
 */
fun View.animateHeight(targetValue: Int, duration: Long = 400, listener: Animator.AnimatorListener? = null, action: ((Float) -> Unit)? = null) {
    post {
        ValueAnimator.ofInt(height, targetValue).apply {
            addUpdateListener {
                cHeight(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if(listener!=null)addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

/**
 * 设置宽度和高度，带有过渡动画
 * @param targetWidth 目标宽度
 * @param targetHeight 目标高度
 * @param duration 时长
 * @param action 可选行为
 */
fun View.animateWidthAndHeight(targetWidth: Int, targetHeight: Int, duration: Long = 400, listener: Animator.AnimatorListener? = null, action: ((Float) -> Unit)? = null) {
    post {
        val startHeight = height
        val evaluator = IntEvaluator()
        ValueAnimator.ofInt(width, targetWidth).apply {
            addUpdateListener {
                widthAndHeight(it.animatedValue as Int, evaluator.evaluate(it.animatedFraction, startHeight, targetHeight))
                action?.invoke((it.animatedFraction))
            }
            if(listener!=null)addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

/**
 * 设置点击监听, 并实现事件节流
 */
var ViewClickFlag = false
var ClickRunnable = Runnable { ViewClickFlag = false }
fun View.click(action: (view: View) -> Unit) {
    setOnClickListener {
        if (!ViewClickFlag) {
            ViewClickFlag = true
            action(it)
        }
        removeCallbacks(ClickRunnable)
        postDelayed(ClickRunnable, 350)
    }
}

/**
 * 设置长按监听
 */
fun View.longClick(action: (view: View) -> Boolean) {
    setOnLongClickListener {
        action(it)
    }
}

/*** 可见性相关 ****/
fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

val View.isGone: Boolean
    get() {
        return visibility == View.GONE
    }

val View.isVisible: Boolean
    get() {
        return visibility == View.VISIBLE
    }

val View.isInvisible: Boolean
    get() {
        return visibility == View.INVISIBLE
    }

/**
 * 切换View的可见性
 */
fun View.toggleVisibility() {
    visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
}

/**
 * 获取View的截图, 支持获取整个RecyclerView列表的长截图
 * 注意：调用该方法时，请确保View已经测量完毕，如果宽高为0，则将抛出异常
 */
fun View?.toBitmap(bgColor: Int = Color.WHITE): Bitmap? {
    if (this == null) {
        return this
    }
    if (measuredWidth == 0 || measuredHeight == 0) {
        throw RuntimeException("调用该方法时，请确保View已经测量完毕，如果宽高为0，则抛出异常以提醒！")
    }
    return when (this) {
        is RecyclerView -> {
            adapter?.let { adapter ->
                val size = adapter.itemCount
                var height = 0
                val paint = Paint()
                var iHeight = 0f
                val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

                // Use 1/8th of the available memory for this memory cache.
                val cacheSize = maxMemory / 8
                val bitmapCache: LruCache<String, Bitmap> = LruCache(cacheSize)
                for (i in 0 until size) {
                    val holder = adapter.createViewHolder(this, adapter.getItemViewType(i))
                    adapter.onBindViewHolder(holder, i)
                    holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    holder.itemView.layout(
                        0, 0, holder.itemView.measuredWidth,
                        holder.itemView.measuredHeight
                    )
                    bitmapCache.put(i.toString(), holder.itemView.toBitmap(bgColor))
//                    holder.itemView.isDrawingCacheEnabled = true
//                    holder.itemView.buildDrawingCache()
//                    val drawingCache = holder.itemView.drawingCache
//                    if (drawingCache != null) {
//                        bitmapCache.put(i.toString(), drawingCache)
//                    }
                    height += holder.itemView.measuredHeight
                }
                val bigBitmap = Bitmap.createBitmap(measuredWidth, height, Bitmap.Config.ARGB_8888)
                bigBitmap?.let {
                    val bigCanvas = Canvas(it)
                    val lBackground = background
                    if (lBackground is ColorDrawable) {
                        val lColor = lBackground.color
                        bigCanvas.drawColor(lColor)
                    }
                    for (i in 0 until size) {
                        val bitmap: Bitmap? = bitmapCache.get(i.toString())
                        bitmap?.let {
                            bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint)
                            iHeight += bitmap.height
                            bitmap.recycle()
                        }
                    }
                    return it
                } ?: return Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
            } ?: return Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
//            this.scrollToPosition(0)
//            this.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
//
//            val bmp = Bitmap.createBitmap(width, measuredHeight, Bitmap.Config.ARGB_8888)
//            val canvas = Canvas(bmp)
//
//            //draw default bg, otherwise will be black
//            if (background != null) {
//                background.setBounds(0, 0, width, measuredHeight)
//                background.draw(canvas)
//            } else {
//                canvas.drawColor(Color.WHITE)
//            }
//            this.draw(canvas)
//            //恢复高度
//            this.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST))
//            bmp //return
        }
        is NestedScrollView -> {
            var h = 0
            // 获取ScrollView实际高度
            for (i in 0 until childCount) {
                h += getChildAt(i).height
            }
            // 创建对应大小的bitmap
            val bitmap = Bitmap.createBitmap(getWidth(), h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            draw(canvas)
            return bitmap
        }
        else -> {
            val screenshot = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            if (background != null) {
                background.setBounds(0, 0, width, measuredHeight)
                background.draw(canvas)
            } else {
                canvas.drawColor(bgColor)
            }
            draw(canvas)// 将 view 画到画布上
            screenshot //return
        }
    }
}

// 所有子View
inline val ViewGroup.children
    get() = (0 until childCount).map { getChildAt(it) }

/**
 * 设置View不可用
 */
fun View.disable() {
    isEnabled = false
    alpha = 0.5f
}

/**
 * 设置View不可用
 */
fun View.enable() {
    isEnabled = true
    alpha = 1f
}

fun View.enabled(isEnabled: Boolean) {
    this.isEnabled = isEnabled
    alpha = if (isEnabled) 1f else 0.5f
}

@ColorInt
fun View.color(@ColorRes color: Int) = checkNotNull(ContextCompat.getColor(this.context, color))

fun View.drawable(@DrawableRes res: Int) = checkNotNull(ContextCompat.getDrawable(this.context, res))

@Px
fun View.dimen(@DimenRes res: Int) = resources.getDimensionPixelSize(res)

fun View.stringArray(@ArrayRes res: Int): Array<String> = resources.getStringArray(res)

fun View.showSoftInput() {
    val imm = this.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideSoftInput() {
    val imm = this.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
}