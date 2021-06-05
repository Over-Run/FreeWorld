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

package io.github.overrun.freeworld.client;

import io.github.overrun.freeworld.client.game.GameObject;
import io.github.overrun.freeworld.client.game.Rotatable;
import io.github.overrun.freeworld.client.game.Scalable;
import io.github.overrun.freeworld.entity.player.Player;
import org.joml.Matrix4f;

import static org.joml.Math.toRadians;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetIntegerv;

/**
 * @author squid233
 * @since 2021/04/03
 */
public final class Transformation {
    private static final float FOV = toRadians(70);
    private static final float Z_NEAR = 0.05f;
    private static final float Z_FAR = 1000;

    private static final Matrix4f PROJECTION = new Matrix4f();
    private static final Matrix4f MODEL = new Matrix4f();
    private static final Matrix4f MODEL_VIEW = new Matrix4f();
    private static final Matrix4f ORTHO = new Matrix4f();
    private static final Matrix4f ORTHO_CPY = new Matrix4f();
    private static final Matrix4f VIEW = new Matrix4f();
    private static final Matrix4f VIEW_CPY = new Matrix4f();
    private static final int[] VIEWPORT_BUF = new int[16];

    public static Matrix4f getProjectionMatrix(Window window) {
        return PROJECTION.setPerspective(
                FOV,
                (float) window.getWidth() / (float) window.getHeight(),
                Z_NEAR,
                Z_FAR
        );
    }

    public static Matrix4f getPickMatrix(Window window) {
        glGetIntegerv(GL_VIEWPORT, VIEWPORT_BUF);
        return PROJECTION.identity()
                .pick(
                        window.getWidth() / 2f,
                        window.getHeight() / 2f,
                        2f,
                        2f,
                        VIEWPORT_BUF
                ).perspective(
                        FOV,
                        (float) window.getWidth() / (float) window.getHeight(),
                        Z_NEAR,
                        Z_FAR
                );
    }

    public static Matrix4f getOrthoProjMatrix(Window window) {
        return ORTHO.setOrtho2D(
                0f,
                window.getWidth(),
                window.getHeight(),
                0f
        );
    }

    public static Matrix4f getOrthoProjModelMatrix(GameObject gameObject, Matrix4f orthoMatrix) {
        Matrix4f modelMatrix = MODEL.translation(
                gameObject.getPrevX(),
                gameObject.getPrevY(),
                gameObject.getPrevZ()
        );
        if (gameObject instanceof Rotatable) {
            Rotatable rotatable = (Rotatable) gameObject;
            modelMatrix.rotateXYZ(
                    toRadians(rotatable.getRotX()),
                    toRadians(rotatable.getRotY()),
                    toRadians(rotatable.getRotZ())
            );
        }
        if (gameObject instanceof Scalable) {
            modelMatrix.scale(((Scalable) gameObject).getScale());
        }
        return ORTHO_CPY.set(orthoMatrix).mul(modelMatrix);
    }

    public static Matrix4f getViewMatrix() {
        return VIEW.rotationX(toRadians(Player.INSTANCE.getRotX()))
                .rotateY(toRadians(Player.INSTANCE.getRotY()))
                .translate(
                        -Player.INSTANCE.getX(),
                        -(Player.INSTANCE.getY() + 1.44f),
                        -Player.INSTANCE.getZ()
                );
    }

    public static Matrix4f getModelViewMatrix(GameObject gameObject, Matrix4f viewMatrix) {
        return VIEW_CPY.set(viewMatrix)
//        Matrix4f(VIEW)
                .mul(
                        MODEL_VIEW.translation(
                                gameObject.getPrevX(),
                                gameObject.getPrevY(),
                                gameObject.getPrevZ()
                        )
                );
    }
}
