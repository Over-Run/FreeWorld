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

package io.github.overrun.freeworld.block

import io.github.overrun.freeworld.block.Blocks.*
import io.github.overrun.freeworld.client.Mesh
import io.github.overrun.freeworld.client.game.GameObject
import io.github.overrun.freeworld.util.shape.VoxelShape
import io.github.overrun.freeworld.util.shape.VoxelShapes

/**
 * @author squid233
 * @since 2021/03/24
 */
open class Block(val union: BlockMeshUnion?) : GameObject {
    override val mesh: Mesh? = null
    var x = 0
    var y = 0
    var z = 0

    override fun getPrevX() = x.toFloat()

    override fun getPrevY() = y.toFloat()

    override fun getPrevZ() = z.toFloat()

    fun faceIs(face: Int, expected: Int) = (face and expected) == expected

    fun renderFace(face: Int, expected: Int) {
        if (faceIs(face, expected))
            union?.render(expected)
    }

    open fun getOutlineShape() = getCollisionShape()

    open fun getCollisionShape(): VoxelShape = VoxelShapes.FULL_CUBE

    open fun render(face: Int) {
        if (face <= 0) return
        renderFace(face, FACE_FRONT)
        renderFace(face, FACE_RIGHT)
        renderFace(face, FACE_TOP)
        renderFace(face, FACE_LEFT)
        renderFace(face, FACE_BACK)
        renderFace(face, FACE_BOTTOM)
        renderFace(face, FACE_OVERLAY_FRONT)
        renderFace(face, FACE_OVERLAY_RIGHT)
        renderFace(face, FACE_OVERLAY_LEFT)
        renderFace(face, FACE_OVERLAY_BACK)
    }
}