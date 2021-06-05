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

package org.overrun.freeworld.entity;

import org.overrun.freeworld.world.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.overrun.freeworld.client.Keyboard.isKeyDown;

/**
 * @author squid233
 * @since 2021/05/05
 */
public class Player extends Entity {
    public Player(World world) {
        super(world);
        resetPos();
        setSize(0.6f, 1.62f);
        heightOffset = height - (height - 1.44f);
    }

    @Override
    public void tick() {
        super.tick();
        float xa = 0;
        float za = 0;
        if (isKeyDown(GLFW_KEY_R)) {
            resetPos();
        }
        if (isKeyDown(GLFW_KEY_W)) {
            --za;
        }
        if (isKeyDown(GLFW_KEY_S)) {
            ++za;
        }
        if (isKeyDown(GLFW_KEY_A)) {
            --xa;
        }
        if (isKeyDown(GLFW_KEY_D)) {
            ++xa;
        }
        if (isKeyDown(GLFW_KEY_SPACE) && onGround) {
            yd = 0.5f;
        }
        moveRelative(xa, za, onGround ? 0.1f : 0.02f);
        yd -= 0.08;
        move(xd, yd, zd);
        xd *= 0.91f;
        yd *= 0.98f;
        zd *= 0.91f;
        if (onGround) {
            xd *= 0.7f;
            zd *= 0.7f;
        }
    }
}
