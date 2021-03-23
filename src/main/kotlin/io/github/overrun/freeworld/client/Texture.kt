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

import org.lwjgl.opengl.GL12.*
import java.io.Closeable
import javax.imageio.ImageIO

/**
 * @author squid233
 * @since 2021/03/23
 */
class Texture(name: String) : Closeable {
    companion object {
        fun load(name: String): Int =
            ClassLoader.getSystemResourceAsStream(name)!!.use {
                val img = ImageIO.read(it)
                val w = img.width
                val h = img.height
                val id = glGenTextures()
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
                    GL_BGRA,
                    GL_UNSIGNED_BYTE,
                    img.getRGB(0, 0, w, h, null, 0, w)
                )
                return id
            }
    }

    val id = load(name)

    override fun close() =
        glDeleteTextures(id)
}