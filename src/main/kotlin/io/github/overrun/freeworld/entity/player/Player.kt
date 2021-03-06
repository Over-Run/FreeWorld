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

package io.github.overrun.freeworld.entity.player

import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author squid233
 * @since 2021/03/22
 */
object Player {
    var x = 2f
    var y = 3f
    var z = 2f
    var rotX = 0f
    var rotY = 0f
    var playing = false

    fun moveRelative(ox: Float, oy: Float, oz: Float) {
        if (ox != 0f) {
            x += sin(toRadians(rotY - 90.0)).toFloat() * -1.0f * ox
            z += cos(toRadians(rotY - 90.0)).toFloat() * ox
        }
        y += oy
        if (oz != 0f) {
            x += sin(toRadians(rotY.toDouble())).toFloat() * -1.0f * (-oz)
            z += cos(toRadians(rotY.toDouble())).toFloat() * (-oz)
        }
    }

    fun speed() = 0.3125f
}