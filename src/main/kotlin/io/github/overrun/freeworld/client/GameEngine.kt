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

import io.github.overrun.freeworld.util.Timer


/**
 * @author squid233
 * @since 2021/03/18
 */
class GameEngine(
    title: String,
    width: Int,
    height: Int,
    vSync: Boolean,
    private val logic: IGameLogic
) {
    companion object {
        val TARGET_FPS = System.getProperty("freeworld.fps", "60").toInt()
        const val TARGET_UPS = 30
    }

    private val window = Window(title, width, height, vSync)
    private val timer = Timer()

    fun init() {
        window.init()
        timer.init()
        logic.init()
        window.show()
    }

    fun input() =
        logic.input(window)

    fun update(interval: Float) =
        logic.update(interval, window)

    fun render() {
        logic.render(window)
        window.update()
    }

    private fun loop() {
        var elapsedTime: Float
        var accumulator = 0f
        val interval = 1f / TARGET_UPS
        while (!window.shouldClose()) {
            elapsedTime = timer.getElapsedTime()
            accumulator += elapsedTime
            input()
            while (accumulator >= interval) {
                update(interval)
                accumulator -= interval
            }
            render()
            if (window.vSync) {
                sync()
            }
        }
        window.close()
    }

    private fun sync() {
        val loopSlot: Float = 1f / TARGET_FPS
        val endTime = timer.lastLoopTime + loopSlot
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1)
            } catch (ie: InterruptedException) {
            }
        }
    }

    fun run() {
        init()
        loop()
    }
}