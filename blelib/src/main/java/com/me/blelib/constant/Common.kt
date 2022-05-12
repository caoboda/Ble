package com.me.blelib.constant

import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
 object Common {
    fun now(pattern: String = "yyyy-MM-dd HH:mm:ss:SSS"): String {
        return TimeUtils.getNowString(SimpleDateFormat(pattern, Locale.getDefault()))
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    fun isMessyCode(strName: String): Boolean {
        val p = Pattern.compile("\\s*|t*|r*|n*")
        val m = p.matcher(strName)
        val after = m.replaceAll("")
        val temp = after.replace("\\p{P}".toRegex(), "")
        val ch = temp.trim { it <= ' ' }.toCharArray()
        val chLength = ch.size.toFloat()
        var count = 0f
        for (i in ch.indices) {
            val c = ch[i]
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count += 1
                }
            }
        }
        val result = count / chLength
        return result > 0.4
    }

    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    private fun isChinese(c: Char): Boolean {
        val ub = Character.UnicodeBlock.of(c)
        return (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
    }

    fun distinct(arr: IntArray): MutableList<Stack<Int>> {
        val stackList = mutableListOf<Stack<Int>>()
        var stack = Stack<Int>()
        stackList.add(stack)

        for (v in arr) {
            if (stack.isEmpty()) {
                stack.push(v)
            } else {
                if (v - 1 != stackList[stackList.size - 1].peek() && v - 2 != stackList[stackList.size - 1].peek()) {
                    stack = Stack()
                    stack.push(v)
                    stackList.add(stack)
                } else {
                    stackList[stackList.size - 1].push(v)
                }
            }
        }
        return stackList
    }
}