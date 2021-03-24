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

package io.github.overrun.freeworld.world

import io.github.overrun.freeworld.block.Block
import io.github.overrun.freeworld.client.GlProgram
import io.github.overrun.freeworld.client.Transformation

/**
 * @author squid233
 * @since 2021/03/24
 */
class World(
    val width: Int,
    val height: Int,
    val depth: Int,
    block: Block
) {
    private val blocks = Array(width * height * depth) { block }

    fun render(program: GlProgram, transformation: Transformation) {
        val viewMatrix = transformation.getViewMatrix()
        var i = 0
        for (x in 0 until width) {
            for (z in 0 until depth) {
                for (y in 0 until height) {
                    val block = blocks[i]
                    block.x = x
                    block.y = y
                    block.z = z
                    program.setUniform(
                        "modelViewMatrix",
                        transformation.getModelViewMatrix(block, viewMatrix)
                    )
                    block.render()
                    ++i
                }
            }
        }
    }
}