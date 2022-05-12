@file:Suppress("unused")

package com.me.blelib.ext
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.*
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.me.blelib.constant.Common
import com.me.blelib.constant.Config
import java.util.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
/**
 * 倒转间隔
 * @param hasSpace 是否有空格
 */
 fun String.reversalEvery2Charts(hasSpace: Boolean = false): String {
    val hex = this.addSpaceEvery2Charts()
    return hex.split(" ").reversed().joinToString(if (hasSpace) " " else "")
}

/**
 * 增加空格间隔
 */
 fun String.addSpaceEvery2Charts(): String {
    val hex = this.replace(" ", "")
    val sb = StringBuilder()
    for (i in 0 until hex.length / 2) {
        sb.append(hex.substring(i * 2, i * 2 + 2))
        sb.append(" ")
    }
    return sb.toString().trim()
}

/**
 * 16进制文本转字节数组
 */
 fun String.hex2ByteArray(): ByteArray {
    val s = this.replace(" ", "")
    val bs = ByteArray(s.length / 2)
    for (i in 0 until s.length / 2) {
        bs[i] = s.substring(i * 2, i * 2 + 2).toInt(16).toByte()
    }
    return bs
}

/**
 * ASCII转字节数组
 * @param hasSpace 是否保留空格
 */
 fun String.ascii2ByteArray(hasSpace: Boolean = false): ByteArray {
    val s = if (hasSpace) this else this.replace(" ", "")
    return s.toByteArray(charset("US-ASCII"))
}

/**
 * 插入文本
 * @param s 要插入内容
 */
 fun String.addFirst(s: String) = "$s$this"

/**
 * 添加文本
 * @param s 要添加内容
 */
 fun String.addLast(s: String) = "$this$s"

/**
 * 格式化文本
 * @param format 要格式化的规则
 */
 fun String.formatString(format: String): String {
    return String.format(Locale.getDefault(), format, this)
}

/**
 * 格式化文本
 * @param format 要格式化的规则
 * @param second 格式化填充的第二个值
 */
 fun String.formatString(format: String, second: String): String {
    return String.format(Locale.getDefault(), format, this, second)
}

/**
 * 字符串过滤
 */
@Throws(PatternSyntaxException::class)
 fun String.filterRule(): String {
    // 只允许字母、数字和-_
    val regEx = "[^a-zA-Z0-9_\\-. ]"
    val p = Pattern.compile(regEx)
    val m = p.matcher(this)
    return m.replaceAll("").trim { it <= ' ' }
}

/**
 * 是否ASCII字符串
 */
@Throws(PatternSyntaxException::class)
 fun String.isAscii(): Boolean {
    val regEx = "[^a-zA-Z0-9_.-]"
    val p = Pattern.compile(regEx)
    val m = p.matcher(this)
    return !m.matches()
}

/**
 * 将一段文字中指定range的文字改变大小
 * @param range 要改变大小的文字的范围
 * @param scale 缩放值，大于1，则比其他文字大；小于1，则比其他文字小；默认是1.5
 */
 fun CharSequence.toSizeSpan(range: IntRange, scale: Float = 1.5f): CharSequence {
    return SpannableString(this).apply {
        setSpan(RelativeSizeSpan(scale), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 将一段文字中指定range的文字改变前景色
 * @param range 要改变前景色的文字的范围
 * @param color 要改变的颜色，默认是红色
 */
 fun CharSequence.toColorSpan(range: IntRange, color: Int = Color.RED): CharSequence {
    return SpannableString(this).apply {
        setSpan(ForegroundColorSpan(color), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 将一段文字中指定range的文字改变背景色
 * @param range 要改变背景色的文字的范围
 * @param color 要改变的颜色，默认是红色
 */
 fun CharSequence.toBackgroundColorSpan(range: IntRange, color: Int = Color.RED): CharSequence {
    return SpannableString(this).apply {
        setSpan(BackgroundColorSpan(color), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 将一段文字中指定range的文字添加删除线
 * @param range 要添加删除线的文字的范围
 */
 fun CharSequence.toStrikeThroughSpan(range: IntRange): CharSequence {
    return SpannableString(this).apply {
        setSpan(StrikethroughSpan(), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 将一段文字中指定range的文字添加颜色和点击事件
 * @param range 目标文字的范围
 */
 fun CharSequence.toClickSpan(range: IntRange, color: Int = Color.RED, isUnderlineText: Boolean = false, clickAction: ()->Unit): CharSequence {
    return SpannableString(this).apply {
        val clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                clickAction()
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.color = color
                ds.isUnderlineText = isUnderlineText
            }
        }
        setSpan(clickableSpan, range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 将一段文字中指定range的文字添加style效果
 * @param range 要添加删除线的文字的范围
 */
 fun CharSequence.toStyleSpan(style: Int = Typeface.BOLD, range: IntRange): CharSequence {
    return SpannableString(this).apply {
        setSpan(StyleSpan(style), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

 fun String.logA() {
    if (Config.isLogEnabled) {
        LogUtils.a("${Common.now()} $this")
    }
}

 fun String.logD() {
    if (Config.isLogEnabled) {
        LogUtils.d("${Common.now()} $this")
    }
}

 fun String.logE() {
    if (Config.isLogEnabled) {
       // LogUtils.e("${Common.now()} $this")
        Log.e("TAG","${Common.now()} $this")
    }
}

 fun String.logI() {
    if (Config.isLogEnabled) {
        LogUtils.i("${Common.now()} $this")
    }
}

 fun String.logV() {
    if (Config.isLogEnabled) {
        LogUtils.v("${Common.now()} $this")
    }
}

 fun String.logW() {
    if (Config.isLogEnabled) {
        LogUtils.w("${Common.now()} $this")
    }
}

 fun CharSequence.logA() {
    if (Config.isLogEnabled) {
        LogUtils.a("${Common.now()} $this")
    }
}

 fun CharSequence.logD() {
    if (Config.isLogEnabled) {
        LogUtils.d("${Common.now()} $this")
    }
}

 fun CharSequence.logE() {
    if (Config.isLogEnabled) {
        LogUtils.e("${Common.now()} $this")
    }
}

 fun CharSequence.logI() {
    if (Config.isLogEnabled) {
        LogUtils.i("${Common.now()} $this")
    }
}

 fun CharSequence.logV() {
    if (Config.isLogEnabled) {
        LogUtils.v("${Common.now()} $this")
    }
}

 fun CharSequence.logW() {
    if (Config.isLogEnabled) {
        LogUtils.w("${Common.now()} $this")
    }
}


