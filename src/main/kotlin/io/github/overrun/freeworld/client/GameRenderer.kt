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
import org.joml.Matrix4f
import org.lwjgl.opengl.GL15.*
import java.io.Closeable

/**
 * @author squid233
 * @since 2021/03/18
 */
class GameRenderer : Closeable {
    private val transformation = Transformation()
    private val viewMatrix = Matrix4f()
    private lateinit var program: GlProgram
    private lateinit var guiProgram: GlProgram
    private lateinit var mesh: Mesh
    private lateinit var crossHair: Mesh

    fun makeColor1f(size: Int, multiper: Int = 3): FloatArray {
        val arr = FloatArray(size * multiper)
        arr.fill(1.0f)
        return arr
    }

    fun init() {
        program = GlProgram()
        program.createVsh(readShaderLines("shader/core/block.vsh"))
        program.createFsh(readShaderLines("shader/core/block.fsh"))
        program.link()
        guiProgram = GlProgram()
        guiProgram.createVsh(readShaderLines("shader/core/block.vsh"))
        guiProgram.createFsh(readShaderLines("shader/core/block.fsh"))
        guiProgram.link()
        val vertices = floatArrayOf(
            // front
            // V0
            -0.5f, 0.5f, 0.0f,
            // V1
            -0.5f, -0.5f, 0.0f,
            // V2
            0.5f, -0.5f, 0.0f,
            // V3
            0.5f, 0.5f, 0.0f,
            // right
            // V3
            0.5f, 0.5f, 0.0f,
            // V2
            0.5f, -0.5f, 0.0f,
            // V6
            0.5f, -0.5f, -1.0f,
            // V7
            0.5f, 0.5f, -1.0f,
            // top
            // V4
            -0.5f, 0.5f, -1.0f,
            // V0
            -0.5f, 0.5f, 0.0f,
            // V3
            0.5f, 0.5f, 0.0f,
            // V7
            0.5f, 0.5f, -1.0f,
            // left
            // V4
            -0.5f, 0.5f, -1.0f,
            // V5
            -0.5f, -0.5f, -1.0f,
            // V1
            -0.5f, -0.5f, 0.0f,
            // V0
            -0.5f, 0.5f, 0.0f,
            // back
            // V7
            0.5f, 0.5f, -1.0f,
            // V6
            0.5f, -0.5f, -1.0f,
            // V5
            -0.5f, -0.5f, -1.0f,
            // V4
            -0.5f, 0.5f, -1.0f,
            // bottom
            // V1
            -0.5f, -0.5f, 0.0f,
            // V5
            -0.5f, -0.5f, -1.0f,
            // V6
            0.5f, -0.5f, -1.0f,
            // V2
            0.5f, -0.5f, 0.0f
        )
        val texCoords = floatArrayOf(
            // front
            0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.0f,
            // right
            0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.0f,
            // top
            0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f,
            // left
            0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.0f,
            // back
            0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.0f,
            // bottom
            0.0f, 0.5f, 0.0f, 1.0f, 0.5f, 1.0f, 0.5f, 0.5f
        )
        val indices = intArrayOf(
            // front
            0, 1, 3, 3, 2, 1,
            // right
            4, 5, 7, 7, 6, 5,
            // top
            8, 9, 11, 11, 10, 9,
            // left
            12, 13, 15, 15, 14, 13,
            // back
            16, 17, 19, 19, 18, 17,
            // bottom
            20, 21, 23, 23, 22, 21
        )
        mesh = Mesh(
            program,
            vertices,
            makeColor1f(indices.size),
            texCoords,
            indices,
            Texture("assets.freeworld/textures/block/grass_block.png")
        )
        crossHair = Mesh(
            guiProgram,
            floatArrayOf(
                -18f, 8f, 0f,
                -18f, 9f, 0f,
                18f, 9f, 0f,
                18f, 8f, 0f,
                8f, -18f, 0f,
                8f, 18f, 0f,
                9f, 18f, 0f,
                9f, -18f, 0f
            ),
            makeColor1f(12),
            floatArrayOf(),
            intArrayOf(
                0, 1, 3, 3, 2, 1,
                4, 5, 7, 7, 6, 5
            ),
            null
        )
    }

    fun render(window: Window) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        if (window.resized) {
            glViewport(0, 0, window.width, window.height)
            window.resized = false
        }
        program.bind()
        program.setUniform("texSampler", 0)
        program.setUniform(
            "projectionMatrix",
            transformation.getProjectionMatrix(window.width.toFloat(), window.height.toFloat())
        )
        program.setUniform(
            "modelViewMatrix",
            transformation.getModelViewMatrix(transformation.getViewMatrix())
        )
        mesh.render()
        program.unbind()
        renderGui(window)
    }

    private fun renderGui(window: Window) {
        guiProgram.bind()
        guiProgram.setUniform("texSampler", 0)
        guiProgram.setUniform(
            "projectionMatrix",
            transformation.getOrthoMatrix(window.width.toFloat(), window.height.toFloat()))
        guiProgram.setUniform(
            "modelViewMatrix",
            transformation.getModelViewMatrix(viewMatrix.identity())
        )
        crossHair.render()
        guiProgram.unbind()
    }

    override fun close() {
        mesh.close()
        program.close()
        guiProgram.close()
    }
}