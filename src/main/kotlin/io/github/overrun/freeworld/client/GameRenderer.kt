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
import io.github.overrun.freeworld.block.Blocks
import io.github.overrun.freeworld.client.GlStateManager.disableCullFace
import io.github.overrun.freeworld.client.GlStateManager.enableCullFace
import io.github.overrun.freeworld.client.gui.CrossHair
import io.github.overrun.freeworld.util.Utils.makeColor1f
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

    fun init() {
        program = GlProgram()
        program.createSh("shader/core/block")
        Blocks.init(program)
        world = World(5, 2, 5)
        nTexGuiProgram = GlProgram()
        nTexGuiProgram.createSh("shader/2d/n_tex")
        crossHair = CrossHair(
            Mesh.of(
                "cross_hair",
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
                dim = 2,
                mode = GL_QUADS
            )
        )
    }

    fun update(window: Window) =
        world.pick(transformation, window)

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
        disableCullFace()
        renderGui(window)
        enableCullFace()
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
        transformation.close()
        Mesh.closeAll()
        if (this::program.isInitialized) {
            program.close()
            program.disableVertexAttribArrays("vert", "in_color", "in_texCoord")
        }
        if (this::nTexGuiProgram.isInitialized) {
            nTexGuiProgram.close()
            nTexGuiProgram.disableVertexAttribArrays("vert", "in_color")
        }
        GlProgram.unbind()
    }
}