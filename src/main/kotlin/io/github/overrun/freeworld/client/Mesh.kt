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

import io.github.overrun.freeworld.util.Utils.NULL
import org.lwjgl.opengl.GL15.*
import java.io.Closeable

/**
 * @author squid233
 * @since 2021/03/23
 */
class Mesh private constructor(
    private val program: GlProgram,
    private val vertices: FloatArray,
    val colors: FloatArray,
    private val texCoords: FloatArray?,
    indices: IntArray,
    private val texture: Texture?,
    private val dim: Int,
    private val mode: Int
) : Closeable {
    private val vertVbo = glGenBuffers()
    private val colorVbo: Int = glGenBuffers()
    private var texVbo = NULL
    private val idxVbo: Int
    val vertexCount = indices.size

    companion object {
        @JvmStatic
        private val meshes = HashMap<String, Mesh>()

        /**
         * Check the [Mesh] is in the map.
         *
         * @param name [Mesh] id.
         * @return Whether the [Mesh] is in the map.
         */
        @JvmStatic
        fun isPresent(name: String) =
            meshes.containsKey(name) && meshes[name] != null

        @JvmStatic
        @JvmOverloads
        fun of(
            name: String,
            program: GlProgram?,
            vertices: FloatArray?,
            colors: FloatArray?,
            texCoords: FloatArray?,
            indices: IntArray?,
            texture: Texture? = null,
            dim: Int? = 3,
            mode: Int? = GL_TRIANGLES
        ): Mesh {
            if (isPresent(name))
                return meshes[name]!!
            else {
                val mesh = Mesh(
                    program!!,
                    vertices!!,
                    colors!!,
                    texCoords,
                    indices!!,
                    texture,
                    dim!!,
                    mode!!
                )
                meshes[name] = mesh
                return mesh
            }
        }

        /** Clean memory. */
        @JvmStatic
        fun closeAll() {
            for (v in meshes.values) {
                v.close()
            }
            meshes.clear()
        }
    }

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
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
        program.enableVertAttribArrPtr("vert", dim, GL_FLOAT, false, 0)
        // colors
        glBindBuffer(GL_ARRAY_BUFFER, colorVbo)
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW)
        program.enableVertAttribArrPtr("in_color", 4, GL_FLOAT, false, 0)
        if (texture != null) {
            // texture coordinates
            glBindBuffer(GL_ARRAY_BUFFER, texVbo)
            glBufferData(GL_ARRAY_BUFFER, texCoords!!, GL_STATIC_DRAW)
            program.enableVertAttribArrPtr("in_texCoord", 2, GL_FLOAT, false, 0)
        }
        glBindBuffer(GL_ARRAY_BUFFER, GL_NONE)
    }

    override fun close() {
        texture?.close()
        glBindBuffer(GL_ARRAY_BUFFER, GL_NONE)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE)
        glDeleteBuffers(vertVbo)
        glDeleteBuffers(colorVbo)
        if (texture != null)
            glDeleteBuffers(texVbo)
        glDeleteBuffers(idxVbo)
    }
}