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

import org.overrun.freeworld.util.MathHelper;
import org.overrun.freeworld.world.World;
import org.overrun.freeworld.world.phys.AABB;

import java.util.List;

/**
 * @author squid233
 * @since 2021/05/05
 */
public class Entity {
    protected final World world;
    public float xo, yo, zo;
    public float x, y, z;
    public float xd, yd, zd;
    public float xRot, yRot;
    public AABB aabb;
    public boolean onGround;
    public boolean removed;
    protected float heightOffset;
    protected float width, height;

    public Entity(World world) {
        this.world = world;
    }

    protected void resetPos() {
        float x = (float) Math.random() * (float) world.width;
        float y = (float) (world.height + 10);
        float z = (float) Math.random() * (float) world.depth;
        setPos(x, y, z);
    }

    public void kill() {
        removed = true;
    }

    protected void setSize(float w, float h) {
        width = w;
        height = h;
    }

    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        float w = width / 2;
        float h = height / 2;
        aabb = AABB.of(x - w, y - h, z - w, x + w, y + h, z + w);
    }

    public void turn(float xo, float yo) {
        xRot -= yo * 0.15;
        yRot += xo * 0.15;
        if (xRot < -90) {
            xRot = -90;
        }
        if (xRot > 90) {
            xRot = 90;
        }
    }

    public void tick() {
        xo = x;
        yo = y;
        zo = z;
    }

    public void move(float xa, float ya, float za) {
        float xaOrg = xa;
        float yaOrg = ya;
        float zaOrg = za;
        List<AABB> aabbs = world.getCollision(aabb.expand(xa, ya, za));
        for (AABB aabb : aabbs) {
            ya = aabb.clipYCollide(this.aabb, ya);
        }
        aabb.move(0, ya, 0);
        for (AABB aabb : aabbs) {
            xa = aabb.clipXCollide(this.aabb, xa);
        }
        aabb.move(xa, 0, 0);
        for (AABB aabb : aabbs) {
            za = aabb.clipZCollide(this.aabb, za);
        }
        aabb.move(0, 0, za);
        onGround = MathHelper.notEquals(yaOrg, ya) && yaOrg < 0;
        if (MathHelper.notEquals(xaOrg, xa)) {
            xd = 0;
        }
        if (MathHelper.notEquals(yaOrg, ya)) {
            yd = 0;
        }
        if (MathHelper.notEquals(zaOrg, za)) {
            zd = 0;
        }
        x = (aabb.x0 + aabb.x1) / 2;
        y = aabb.y0 + heightOffset;
        z = (aabb.z0 + aabb.z1) / 2;
    }

    public void moveRelative(float xa, float za, float speed) {
        float dist = xa * xa + za * za;
        if (dist >= 0.01f) {
            dist = speed / (float) Math.sqrt(dist);
            xa *= dist;
            za *= dist;
            float sin = (float) Math.sin(Math.toRadians(yRot));
            float cos = (float) Math.cos(Math.toRadians(yRot));
            xd += xa * cos - za * sin;
            zd += za * cos + xa * sin;
        }
    }

    public boolean isLit() {
        return world.isLit((int) x, (int) y, (int) z);
    }
}
