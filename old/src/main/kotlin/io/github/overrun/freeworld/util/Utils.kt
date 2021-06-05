/*
 * MIT License
 *
 * Copyright (c) 2021 OverRun Organization
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.overrun.freeworld.util

import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Useful functions and properties.
 *
 * @author squid233
 * @since 2021/03/18
 */
object Utils {
    /** `NULL` in C of type int. */
    const val NULL = 0

    /**
     * Read lines from specified file.
     *
     * @param name The name of file.
     * @return File content separated with `"\n"`.
     */
    @JvmStatic
    fun readLines(name: String?) =
        ClassLoader.getSystemResourceAsStream(name)!!.use {
            Scanner(it, StandardCharsets.UTF_8).use { sc ->
                val sb = StringBuilder()
                while (sc.hasNextLine())
                    sb.append(sc.nextLine()).append("\n")
                sb.toString()
            }
        }

    /**
     * Create an array with `1.0f` for GL.
     *
     * @param size       Size from `indices`.
     * @param multiplier Composites of color. For rgba, it's `4`.
     * @return The array with size `size`*`multiplier` fill with `1.0f`.
     */
    @JvmStatic
    @JvmOverloads
    fun makeColor1f(size: Int, multiplier: Int = 4): FloatArray {
        val arr = FloatArray(size * multiplier)
        arr.fill(1.0f)
        return arr
    }

    /**
     * Create an array with `0`.
     *
     * @param size Size of array.
     * @return The array fill with `0`.
     */
    fun intArrayOfSize(size: Int) = IntArray(size) { 0 }

    /** Function [use][kotlin.io.use] for LWJGL. */
    inline fun <T : AutoCloseable?, R> T.use(block: (T) -> R): R {
        @Suppress("ConvertTryFinallyToUseCall")
        try {
            return block(this)
        } finally {
            this?.close()
        }
    }
}