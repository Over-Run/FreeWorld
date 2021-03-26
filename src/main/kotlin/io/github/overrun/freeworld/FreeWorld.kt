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

package io.github.overrun.freeworld

import io.github.overrun.freeworld.client.GameRenderer
import io.github.overrun.freeworld.client.IGameLogic
import io.github.overrun.freeworld.client.Window
import io.github.overrun.freeworld.entity.player.Player
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFW.*
import java.io.Closeable

/**
 * @author squid233
 * @since 2021/03/18
 */
class FreeWorld : IGameLogic, Closeable {
    companion object {
        const val VERSION = "0.1.0"
        const val MOUSE_SENSITIVITY = 70.0f / 100.0f
        val logger = LogManager.getLogger("FreeWorld")!!
    }

    private val renderer = GameRenderer()
    private var lastMouseX = 0
    private var lastMouseY = 0

    override fun init() =
        renderer.init()

    override fun input(window: Window) {
        if (Player.canMoveCamera) {
            Player.rotX += (window.mouseY - lastMouseY) * MOUSE_SENSITIVITY
            Player.rotY += (window.mouseX - lastMouseX) * MOUSE_SENSITIVITY
            if (Player.rotX > 90f)
                Player.rotX = 90f
            if (Player.rotX < -90f)
                Player.rotX = -90f
            if (window.isKeyPressed(GLFW_KEY_W))
                Player.moveRelative(0f, 0f, Player.speed())
            if (window.isKeyPressed(GLFW_KEY_S))
                Player.moveRelative(0f, 0f, -Player.speed())
            if (window.isKeyPressed(GLFW_KEY_A))
                Player.moveRelative(-Player.speed(), 0f, 0f)
            if (window.isKeyPressed(GLFW_KEY_D))
                Player.moveRelative(Player.speed(), 0f, 0f)
            if (window.isKeyPressed(GLFW_KEY_SPACE))
                Player.moveRelative(0f, Player.speed(), 0f)
            if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
                Player.moveRelative(0f, -Player.speed(), 0f)
        }
        lastMouseX = window.mouseX
        lastMouseY = window.mouseY
        renderer.input(window)
    }

    override fun update(delta: Float) =
        renderer.update()

    override fun render(window: Window) =
        renderer.render(window)

    override fun close() =
        renderer.close()
}