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

import io.github.overrun.freeworld.client.game.GameObject
import io.github.overrun.freeworld.client.game.Rotatable
import io.github.overrun.freeworld.client.game.Scalable
import io.github.overrun.freeworld.entity.player.Player
import org.joml.Matrix4f
import java.lang.Math.toRadians

/**
 * @author squid233
 * @since 2021/03/21
 */
class Transformation {
    companion object {
        private val FOV = toRadians(70.0).toFloat()
    }

    private val projectionMatrix = Matrix4f()
    private val modelViewMatrix = Matrix4f()
    private val orthoMatrix = Matrix4f()
    private val viewMatrix = Matrix4f()

    fun getProjectionMatrix(window: Window): Matrix4f =
        projectionMatrix.setPerspective(
            FOV,
            window.width.toFloat() / window.height.toFloat(),
            0.05f,
            1000f
        )

    fun getOrthoProjMatrix(window: Window): Matrix4f =
        orthoMatrix.setOrtho2D(
            0f,
            window.width.toFloat(),
            window.height.toFloat(),
            0f
        )

    fun getOrthoProjModelMatrix(gameObject: GameObject, orthoMatrix: Matrix4f): Matrix4f {
        val modelMatrix = Matrix4f().translate(
            gameObject.getPrevX(),
            gameObject.getPrevY(),
            gameObject.getPrevZ()
        )
        if (gameObject is Rotatable)
            modelMatrix.rotateXYZ(
                toRadians(gameObject.rotX.toDouble()).toFloat(),
                toRadians(gameObject.rotY.toDouble()).toFloat(),
                toRadians(gameObject.rotZ.toDouble()).toFloat()
            )
        if (gameObject is Scalable)
            modelMatrix.scale(gameObject.scale)
        return Matrix4f(orthoMatrix).mul(modelMatrix)
    }

    fun getViewMatrix(): Matrix4f =
        viewMatrix.rotationX(toRadians(Player.rotX.toDouble()).toFloat())
            .rotateY(toRadians(Player.rotY.toDouble()).toFloat())
            .translate(-Player.x, -Player.y, -Player.z)

    fun getModelViewMatrix(gameObject: GameObject, viewMatrix: Matrix4f): Matrix4f =
        Matrix4f(viewMatrix).mul(modelViewMatrix.translation(
            gameObject.getPrevX(),
            gameObject.getPrevY(),
            gameObject.getPrevZ()
        ))
}