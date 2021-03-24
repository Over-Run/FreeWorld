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

import org.lwjgl.opengl.GL15.*
import java.io.Closeable

/**
 * @author squid233
 * @since 2021/03/23
 */
class Mesh(
    private val program: GlProgram,
    private val vertices: FloatArray,
    private val colors: FloatArray,
    private val texCoords: FloatArray?,
    indices: IntArray,
    private val dim: Int = 3,
    private val texture: Texture? = null,
    private val mode: Int = GL_TRIANGLES
) : Closeable {
    private val vertVbo = glGenBuffers()
    private val colorVbo: Int = glGenBuffers()
    private var texVbo = 0
    private val idxVbo: Int
    val vertexCount = indices.size

    init {
        if (texture != null) {
            texVbo = glGenBuffers()
        }
        // indices
        idxVbo = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVbo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE)
    }

    fun render() {
        if (texture != null) {
            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, texture.id)
        }
        processBuffer()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVbo)
        glDrawElements(mode, vertexCount, GL_UNSIGNED_INT, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE)
        glBindTexture(GL_TEXTURE_2D, GL_NONE)
    }

    fun processBuffer() {
        // vertices
        glBindBuffer(GL_ARRAY_BUFFER, vertVbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STREAM_DRAW)
        program.enableVertAttribArrPtr("vert", dim, GL_FLOAT, false, 0)
        // colors
        glBindBuffer(GL_ARRAY_BUFFER, colorVbo)
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STREAM_DRAW)
        program.enableVertAttribArrPtr("in_color", 4, GL_FLOAT, false, 0)
        if (texture != null) {
            // texture coordinates
            glBindBuffer(GL_ARRAY_BUFFER, texVbo)
            glBufferData(GL_ARRAY_BUFFER, texCoords!!, GL_STREAM_DRAW)
            program.enableVertAttribArrPtr("in_texCoord", 2, GL_FLOAT, false, 0)
        }
        glBindBuffer(GL_ARRAY_BUFFER, GL_NONE)
    }

    override fun close() {
        if (texture != null) {
            texture.close()
            program.disableVertexAttribArrays("in_texCoord")
        }
        program.disableVertexAttribArrays("vert", "in_color")
        glBindBuffer(GL_ARRAY_BUFFER, GL_NONE)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE)
        glDeleteBuffers(vertVbo)
        glDeleteBuffers(colorVbo)
        if (texture != null)
            glDeleteBuffers(texVbo)
        glDeleteBuffers(idxVbo)
    }
}