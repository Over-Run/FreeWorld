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
import io.github.overrun.freeworld.client.Texture

/**
 * @author squid233
 * @since 2021/03/30
 */
class BlockMeshUnion(private val blockName: String) {
    val map = HashMap<Int, Mesh>()

    init {
        val texture = Texture("assets.freeworld/textures/block/$blockName.png")
        setMap(FACE_FRONT, texture, VERTICES_FRONT)
        setMap(FACE_RIGHT, texture, VERTICES_RIGHT)
        setMap(FACE_TOP, texture, VERTICES_TOP)
        setMap(FACE_LEFT, texture, VERTICES_LEFT)
        setMap(FACE_BACK, texture, VERTICES_BACK)
        setMap(FACE_BOTTOM, texture, VERTICES_BOTTOM)
        setMap(FACE_OVERLAY_FRONT, texture, VERTICES_OVERLAY_FRONT)
        setMap(FACE_OVERLAY_RIGHT, texture, VERTICES_OVERLAY_RIGHT)
        setMap(FACE_OVERLAY_LEFT, texture, VERTICES_OVERLAY_LEFT)
        setMap(FACE_OVERLAY_BACK, texture, VERTICES_OVERLAY_BACK)
    }

    fun render(face: Int) =
        map[face]?.render()

    private fun setMap(face: Int, texture: Texture, array: FloatArray) {
        map[face] = singleFaceMesh(
            face,
            blockName,
            texture,
            *array
        )
    }
}