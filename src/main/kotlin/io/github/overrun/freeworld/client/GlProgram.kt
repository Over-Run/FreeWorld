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

import io.github.overrun.freeworld.FreeWorld.Companion.logger
import io.github.overrun.freeworld.util.Utils.use
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack
import java.io.Closeable

/**
 * @author squid233
 * @since 2021/03/18
 */
class GlProgram : Closeable {
    private val uniforms = HashMap<String, Int>()
    private val programId = glCreateProgram()
    private var vshId = 0
    private var fshId = 0

    init {
        if (programId == 0) throw NullPointerException("Failed to create GL program")
    }

    fun createVsh(src: String) {
        vshId = createShader(src, GL_VERTEX_SHADER)
    }

    fun createFsh(src: String) {
        fshId = createShader(src, GL_FRAGMENT_SHADER)
    }

    private fun createShader(src: String, type: Int): Int {
        val id = glCreateShader(type)
        if (id == 0)
            throw NullPointerException(
                "Failed to create shader (Shader type: $type)"
            )
        glShaderSource(id, src)
        glCompileShader(id)
        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE)
            throw RuntimeException("Error compiling shader src: ${glGetShaderInfoLog(id)}")
        glAttachShader(programId, id)
        return id
    }

    fun link() {
        glLinkProgram(programId)
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw RuntimeException("Error linking GL program: ${glGetProgramInfoLog(programId)}")
        }
        if (vshId != 0) {
            glDetachShader(programId, vshId)
        }
        if (fshId != 0) {
            glDetachShader(programId, fshId)
        }
        glValidateProgram(programId)
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
            logger.warn(glGetProgramInfoLog(programId))
        }
    }

    fun bind() =
        glUseProgram(programId)

    fun unbind() =
        glUseProgram(0)

    fun getUniform(name: String) =
        uniforms.computeIfAbsent(name) { s: String ->
            val loc = glGetUniformLocation(programId, s)
            require(loc >= 0) { "Couldn't find uniform: $s" }
            loc
        }

    fun setUniform(name: String, matrix4f: Matrix4f) =
        MemoryStack.stackPush().use {
            glUniformMatrix4fv(
                getUniform(name),
                false,
                matrix4f[it.mallocFloat(16)]
            )
        }

    fun enableVertexAttribArray(name: String) =
        glEnableVertexAttribArray(glGetAttribLocation(programId, name))

    fun vertexAttribPointer(
        name: String,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int
    ) = glVertexAttribPointer(
        glGetAttribLocation(programId, name),
        size,
        type,
        normalized,
        stride,
        0
    )

    fun disableVertexAttribArrays(vararg names: String) {
        for (nm in names)
            glDisableVertexAttribArray(glGetAttribLocation(programId, nm))
    }

    override fun close() {
        unbind()
        if (programId != 0) glDeleteProgram(programId)
        if (vshId != 0) glDeleteShader(vshId)
        if (fshId != 0) glDeleteShader(fshId)
    }
}