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

import io.github.overrun.freeworld.block.Block
import io.github.overrun.freeworld.client.gui.CrossHair
import io.github.overrun.freeworld.world.World
import org.lwjgl.opengl.GL15.*
import java.io.Closeable

/**
 * @author squid233
 * @since 2021/03/18
 */
class GameRenderer : Closeable {
    private val transformation = Transformation()
    private lateinit var world: World
    private lateinit var program: GlProgram
    private lateinit var nTexGuiProgram: GlProgram
    private lateinit var block: Block
    private lateinit var crossHair: CrossHair

    fun makeColor1f(size: Int, multiplier: Int = 4): FloatArray {
        val arr = FloatArray(size * multiplier)
        arr.fill(1.0f)
        return arr
    }

    fun init() {
        program = GlProgram()
        program.createSh("shader/core/block")
        val vertices = floatArrayOf(
            // front
            // V0
            0.0f, 1.0f, 0.0f,
            // V1
            0.0f, 0.0f, 0.0f,
            // V2
            1.0f, 0.0f, 0.0f,
            // V3
            1.0f, 1.0f, 0.0f,
            // right
            // V3
            1.0f, 1.0f, 0.0f,
            // V2
            1.0f, 0.0f, 0.0f,
            // V6
            1.0f, 0.0f, -1.0f,
            // V7
            1.0f, 1.0f, -1.0f,
            // top
            // V4
            0.0f, 1.0f, -1.0f,
            // V0
            0.0f, 1.0f, 0.0f,
            // V3
            1.0f, 1.0f, 0.0f,
            // V7
            1.0f, 1.0f, -1.0f,
            // left
            // V4
            0.0f, 1.0f, -1.0f,
            // V5
            0.0f, 0.0f, -1.0f,
            // V1
            0.0f, 0.0f, 0.0f,
            // V0
            0.0f, 1.0f, 0.0f,
            // back
            // V7
            1.0f, 1.0f, -1.0f,
            // V6
            1.0f, 0.0f, -1.0f,
            // V5
            0.0f, 0.0f, -1.0f,
            // V4
            0.0f, 1.0f, -1.0f,
            // bottom
            // V1
            0.0f, 0.0f, 0.0f,
            // V5
            0.0f, 0.0f, -1.0f,
            // V6
            1.0f, 0.0f, -1.0f,
            // V2
            1.0f, 0.0f, 0.0f
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
        block = Block(
                Mesh(
                    program,
                    vertices,
                    makeColor1f(indices.size),
                    texCoords,
                    indices,
                    texture = Texture(
                        "assets.freeworld/textures/block/grass_block.png"
                    )
                )
                )
        world = World(5, 1, 5, block)
        nTexGuiProgram = GlProgram()
        nTexGuiProgram.createSh("shader/2d/n_tex")
        crossHair = CrossHair(
            Mesh(
                nTexGuiProgram,
                floatArrayOf(
                    -9f, 1f,
                    -9f, -1f,
                    9f, -1f,
                    9f, 1f,
                    -1f, -9f,
                    -1f, 9f,
                    1f, 9f,
                    1f, -9f,
                ),
                makeColor1f(8),
                null,
                intArrayOf(
                    0, 1, 2, 3, 4, 5, 6, 7
                ),
                2,
                mode = GL_QUADS
            )
        )
    }

    fun render(window: Window) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        if (window.resized) {
            crossHair.x = window.width / 2
            crossHair.y = window.height / 2
            glViewport(0, 0, window.width, window.height)
            window.resized = false
        }
        with(program) {
            bind()
            setUniform(
                "projectionMatrix",
                transformation.getProjectionMatrix(window)
            )
            setUniform("texSampler", 0)
            world.render(program, transformation)
        }
        glDisable(GL_DEPTH_TEST)
        renderGui(window)
        glEnable(GL_DEPTH_TEST)
        GlProgram.unbind()
    }

    private fun renderGui(window: Window) {
        with(nTexGuiProgram) {
            bind()
            setUniform(
                "projModelViewMat",
                transformation.getOrthoProjModelMatrix(
                    crossHair,
                    transformation.getOrthoProjMatrix(window)
                )
            )
            crossHair.render()
        }
    }

    override fun close() {
        block.close()
        crossHair.close()
        program.close()
        nTexGuiProgram.close()
        GlProgram.unbind()
    }
}