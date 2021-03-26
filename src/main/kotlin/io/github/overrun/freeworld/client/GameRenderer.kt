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
import io.github.overrun.freeworld.util.HitResult
import io.github.overrun.freeworld.util.Utils.intArrayOfSize
import io.github.overrun.freeworld.util.Utils.makeColor1f
import io.github.overrun.freeworld.world.World
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.system.MemoryUtil
import java.io.Closeable

/**
 * @author squid233
 * @since 2021/03/18
 */
class GameRenderer : Closeable {
    private val transformation = Transformation()
    private val selectBuffer = MemoryUtil.memAllocInt(2000)
    private val hitResult = HitResult()
    private lateinit var block: Block
    private lateinit var world: World
    private lateinit var program: GlProgram
    private lateinit var nTexGuiProgram: GlProgram
    private lateinit var crossHair: CrossHair
    private lateinit var box: Mesh

    fun init() {
        program = GlProgram()
        program.createSh("shader/core/block")
        Blocks.init(program)
        block = Blocks.grassBlock
        world = World(32, 64, 32)
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

    fun input(window: Window) {
        if (window.isKeyPressed(GLFW_KEY_1))
            block = Blocks.grassBlock
        if (window.isKeyPressed(GLFW_KEY_2))
            block = Blocks.dirt
        if (!hitResult.isNull) {
            if (window.isMousePressed(GLFW_MOUSE_BUTTON_LEFT))
                world.setBlock(hitResult.x, hitResult.y, hitResult.z, Blocks.air)
            if (window.isMousePressed(GLFW_MOUSE_BUTTON_RIGHT)) {
                when (hitResult.face) {
                    Blocks.FACE_FRONT ->
                        world.setBlock(hitResult.x, hitResult.y, hitResult.z + 1, block)
                    Blocks.FACE_RIGHT ->
                        world.setBlock(hitResult.x + 1, hitResult.y, hitResult.z, block)
                    Blocks.FACE_TOP ->
                        world.setBlock(hitResult.x, hitResult.y + 1, hitResult.z, block)
                    Blocks.FACE_LEFT ->
                        world.setBlock(hitResult.x - 1, hitResult.y, hitResult.z, block)
                    Blocks.FACE_BACK ->
                        world.setBlock(hitResult.x, hitResult.y, hitResult.z - 1, block)
                    Blocks.FACE_BOTTOM ->
                        world.setBlock(hitResult.x, hitResult.y - 1, hitResult.z, block)
                }
            }
        }
    }

    fun update() = Unit

    private fun pick(viewMatrix: Matrix4f) {
        glSelectBuffer(selectBuffer.clear())
        glRenderMode(GL_SELECT)
        world.pick(program, transformation, viewMatrix)
        val hits = glRenderMode(GL_RENDER)
        selectBuffer.flip().limit(selectBuffer.capacity())
        var closest = 0L
        val names = intArrayOfSize(10)
        var hitNameCount = 0
        for (i in 0 until hits) {
            val nameCount = selectBuffer.get()
            val minZ = selectBuffer.get().toLong()
            selectBuffer.get()
            if (minZ >= closest && i != 0) {
                for (j in 0 until nameCount)
                    selectBuffer.get()
            } else {
                closest = minZ
                hitNameCount = nameCount
                for (j in 0 until nameCount) {
                    names[j] = selectBuffer.get()
                }
            }
        }
        if (hitNameCount > 0) {
            hitResult.isNull = false
            hitResult.x = names[1]
            hitResult.y = names[2]
            hitResult.z = names[3]
            hitResult.face = names[4]
        } else
            hitResult.isNull = true
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
            val viewMatrix = transformation.getViewMatrix()
            setUniform(
                "projectionMatrix",
                transformation.getPickMatrix(window)
            )
            setUniform("texSampler", 0)
            pick(viewMatrix)
            setUniform(
                "projectionMatrix",
                transformation.getProjectionMatrix(window)
            )
            world.render(program, transformation, viewMatrix)
            renderHint(viewMatrix)
        }
        glDisable(GL_DEPTH_TEST)
        disableCullFace()
        renderGui(window)
        enableCullFace()
        glEnable(GL_DEPTH_TEST)
        GlProgram.unbind()
    }

    private fun renderHint(viewMatrix: Matrix4f) {
        program.setUniform(
            "modelViewMatrix",
            transformation.getModelViewMatrix(hitResult, viewMatrix)
        )
        if (!hitResult.isNull) {
            if (!this::box.isInitialized)
                box = Mesh.of(
                    "box",
                    program,
                    floatArrayOf(
                        -0.001f, -0.001f, -0.001f,
                        -0.001f, 1.001f, -0.001f,
                        1.001f, 1.001f, -0.001f,
                        1.001f, -0.001f, -0.001f,
                        -0.001f, -0.001f, -1.001f,
                        -0.001f, 1.001f, -1.001f,
                        1.001f, 1.001f, -1.001f,
                        1.001f, -0.001f, -1.001f
                        /*0f, 0f, 0f,
                        0f, 1f, 0f,
                        1f, 1f, 0f,
                        1f, 0f, 0f,
                        0f, 0f, -1f,
                        0f, 1f, -1f,
                        1f, 0f, -1f,
                        1f, 0f, -1f*/
                    ),
                    floatArrayOf(
                        0f, 0f, 0f, 1f,
                        0f, 0f, 0f, 1f,
                        0f, 0f, 0f, 1f,
                        0f, 0f, 0f, 1f,
                        0f, 0f, 0f, 1f,
                        0f, 0f, 0f, 1f,
                        0f, 0f, 0f, 1f,
                        0f, 0f, 0f, 1f
                    ),
                    null,
                    intArrayOf(
                        // front
                        3, 2, 1, 0,
                        // right
                        7, 6, 2, 3,
                        // top
                        1, 2, 6, 5,
                        // left
                        0, 1, 5, 4,
                        // back
                        4, 5, 6, 7,
                        // bottom
                        0, 4, 7, 3
                    ),
                    mode = GL_QUADS
                )
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
            box.render()
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
        }
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
        MemoryUtil.memFree(selectBuffer)
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