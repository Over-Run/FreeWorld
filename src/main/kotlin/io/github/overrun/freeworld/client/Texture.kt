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

package io.github.overrun.freeworld.client

import io.github.overrun.freeworld.util.Images
import org.lwjgl.opengl.GL12.*
import java.io.Closeable

/**
 * @author squid233
 * @since 2021/03/23
 */
class Texture
    @JvmOverloads constructor(
        name: String,
        gl: Boolean = true,
        toRgba: Boolean = false
    ) : Closeable {
    companion object {
        @JvmStatic
        fun load(
            name: String,
            texture: Texture,
            gl: Boolean = true,
            toRgba: Boolean = false
        ): Int {
            var id = 0
            if (gl) id = glGenTextures()
            var w: Int
            var h: Int
            var data: IntArray
            try {
                val img = Images.read(name)
                w = img.width
                h = img.height
                // Get the pixels of format alpha_BGR.
                data = img.getRGB(0, 0, w, h, null, 0, w)
            } catch (e: Exception) {
                e.printStackTrace()
                w = 4
                h = 4
                data = intArrayOf(
                    0xfff800f8.toInt(), 0xff000000.toInt(), 0xfff800f8.toInt(), 0xff000000.toInt(),
                    0xff000000.toInt(), 0xfff800f8.toInt(), 0xff000000.toInt(), 0xfff800f8.toInt(),
                    0xfff800f8.toInt(), 0xff000000.toInt(), 0xfff800f8.toInt(), 0xff000000.toInt(),
                    0xff000000.toInt(), 0xfff800f8.toInt(), 0xff000000.toInt(), 0xfff800f8.toInt()
                )
            }
            if (toRgba) {
                for (i in data.indices) {
                    val a = data[i].shr(24).and(255)
                    val r = data[i].shr(16).and(255)
                    val g = data[i].shr(8).and(255)
                    val b = data[i].and(255)
                    data[i] = a.shl(24).or(b.shl(16)).or(g.shl(8)).or(r)
                }
            }
            if (gl) {
                glBindTexture(GL_TEXTURE_2D, id)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                glTexImage2D(
                    GL_TEXTURE_2D,
                    0,
                    GL_RGBA,
                    w,
                    h,
                    0,
                    if (toRgba) GL_RGBA else GL_BGRA,
                    GL_UNSIGNED_BYTE,
                    data
                )
            }
            texture.id = id
            texture.w = w
            texture.h = h
            texture.pixels = data
            return id
        }
    }

    var id = 0
        private set
    var w = 0
    var h = 0
    lateinit var pixels: IntArray

    init {
        load(name, this, gl, toRgba)
    }

    override fun close() =
        glDeleteTextures(id)
}