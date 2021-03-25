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

package io.github.overrun.freeworld.world

import io.github.overrun.freeworld.block.Block
import io.github.overrun.freeworld.client.Transformation
import io.github.overrun.freeworld.entity.player.Player
import org.joml.Intersectionf
import org.joml.Vector2f
import org.joml.Vector3f

/**
 * @author squid233
 * @since 2021/03/25
 */
class BlockPicker {
    private var dir = Vector3f()
    private val min = Vector3f()
    private val max = Vector3f()
    private val nearFar = Vector2f()
    var targetIndex = -1

    fun pick(transformation: Transformation, blocks: Array<Block>) {
        var closestDistance = Float.POSITIVE_INFINITY
        dir = transformation.getViewMatrix().positiveZ(dir).negate()
        targetIndex = -1
        for ((i, block) in blocks.withIndex()) {
            min.set(block.getPrevX(), block.getPrevY(), block.getPrevZ())
            max.set(block.getPrevX(), block.getPrevY(), block.getPrevZ())
            min.add(-1f, -1f, -1f)
            max.add(1f, 1f, 1f)
            if (Intersectionf.intersectRayAab(
                    Player.x,
                    Player.y,
                    Player.z,
                    dir.x,
                    dir.y,
                    dir.z,
                    min.x,
                    min.y,
                    min.z,
                    max.x,
                    max.y,
                    max.z,
                    nearFar
                ) && nearFar.x < closestDistance
            ) {
                closestDistance = nearFar.x
                targetIndex = i
            }
        }
    }
}