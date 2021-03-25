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
import io.github.overrun.freeworld.client.GlStateManager.*
import io.github.overrun.freeworld.entity.player.Player
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil
import java.io.Closeable
import kotlin.math.floor


/**
 * @author squid233
 * @since 2021/03/18
 */
class Window(
    val title: String,
    width: Int,
    height: Int,
    val vSync: Boolean = true
) : Closeable {
    private var handle = 0L
    var resized = false
    var width = width
    private set
    var height = height
    private set
    var mouseX = 0
    private set
    var mouseY = 0
    private set

    fun init() {
        GLFWErrorCallback.create { error, description ->
            logger.error("########## GL ERROR ##########")
            logger.error("$error: ${MemoryUtil.memUTF8(description)}")
        }
        check(glfwInit()) { "Unable to initialize GLFW" }
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2)
        handle = glfwCreateWindow(width, height, title, 0, 0)
        if (handle == 0L) throw NullPointerException("Failed to create the window")
        glfwSetFramebufferSizeCallback(handle) { _, w, h ->
            width = w
            height = h
            resized = true
        }
        glfwSetKeyCallback(
            handle
        ) { window, key, _, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true)
            }
            if (key == GLFW_KEY_GRAVE_ACCENT && action == GLFW_RELEASE) {
                Player.canMoveCamera = !Player.canMoveCamera
                setCursorMode(
                    if (Player.canMoveCamera)
                        GLFW_CURSOR_DISABLED
                    else GLFW_CURSOR_NORMAL
                )
            }
        }
        glfwSetCursorPosCallback(handle) { _, x, y ->
            mouseX = floor(x).toInt()
            mouseY = floor(y).toInt()
        }
        val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        if (vidMode != null) {
            glfwSetWindowPos(
                handle,
                (vidMode.width() - width) / 2,
                (vidMode.height() - height) / 2
            )
        }
        glfwMakeContextCurrent(handle)
        glfwSwapInterval(if (vSync) 1 else 0)
        GL.createCapabilities(true)
        glClearColor(.4f, .6f, .9f, 1f)
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LEQUAL)
        enableCullFace()
        cullFaceBack()
        enableBlend()
        blendFuncAlpha()
    }

    fun show() =
        glfwShowWindow(handle)

    fun isKeyPressed(key: Int) =
        glfwGetKey(handle, key) == GLFW_PRESS

    fun setCursorMode(value: Int) =
        glfwSetInputMode(handle, GLFW_CURSOR, value)

    fun shouldClose() =
        glfwWindowShouldClose(handle)

    fun update() {
        glfwSwapBuffers(handle)
        glfwPollEvents()
    }

    override fun close() {
        Callbacks.glfwFreeCallbacks(handle)
        glfwDestroyWindow(handle)
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }
}