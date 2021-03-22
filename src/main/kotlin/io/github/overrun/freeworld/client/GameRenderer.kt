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

import io.github.overrun.freeworld.util.Utils.readShaderLines
import org.lwjgl.opengl.GL15.*
import java.io.Closeable

/**
 * @author squid233
 * @since 2021/03/18
 */
class GameRenderer : Closeable {
    private val vboList = ArrayList<Int>()
    private val transformation = Transformation()
    private lateinit var program: GlProgram

    fun init() {
        program = GlProgram()
        program.createVsh(readShaderLines("shader/core/block.vsh"))
        program.createFsh(readShaderLines("shader/core/block.fsh"))
        program.link()
        val vertices = floatArrayOf(
            -0.5f, 0.5f, -1.05f,
            -0.5f, -0.5f, -1.05f,
            0.5f, -0.5f, -1.05f,
            0.5f, 0.5f, -1.05f
        )
        val colors = floatArrayOf(
            1f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 1f,
            0f, 1f, 0f
        )
        val indices = intArrayOf(
            0, 1, 3, 3, 2, 1
        )
        // vertices
        val vertVbo = glGenBuffers()
        vboList.add(vertVbo)
        glBindBuffer(GL_ARRAY_BUFFER, vertVbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
        program.enableVertexAttribArray("vert")
        program.vertexAttribPointer("vert", 3, GL_FLOAT, false, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        // colors
        val colorVbo = glGenBuffers()
        vboList.add(colorVbo)
        glBindBuffer(GL_ARRAY_BUFFER, colorVbo)
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW)
        program.enableVertexAttribArray("in_color")
        program.vertexAttribPointer("in_color", 3, GL_FLOAT, false, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        // indices
        val idxVbo = glGenBuffers()
        vboList.add(idxVbo)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVbo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun render(window: Window) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        if (window.resized) {
            glViewport(0, 0, window.width, window.height)
            window.resized = false
        }
        program.bind()
        program.setUniform("projectionMatrix",
            transformation.getProjectionMatrix(window.width.toFloat(), window.height.toFloat()))
        program.setUniform("worldMatrix",
            transformation.getWorldMatrix())
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)
        program.unbind()
    }

    override fun close() {
        program.disableVertexAttribArrays("vert", "in_color")
        program.close()
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        for (vbo in vboList) {
            glDeleteBuffers(vbo)
        }
    }
}