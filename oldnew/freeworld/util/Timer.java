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

package org.overrun.freeworld.util;

/**
 * @author squid233
 * @since 2021/05/04
 */
public final class Timer {
    private static final long NS_PER_SECOND = 1_000_000_000;
    private static final long MAX_NS_PER_UPDATE = 1_000_000_000;
    private static final int MAX_TICKS_PER_UPDATE = 100;
    private final float tps;
    private long lastTime = System.nanoTime();
    public int ticks;
    /**
     * {@code delta} or {@code interval}
     */
    public float interval;
    public float timeScale = 1;
    public float fps;
    public float passedTime;

    public Timer(float tps) {
        this.tps = tps;
    }

    public void advanceTime() {
        long now = System.nanoTime();
        long passedNs = now - this.lastTime;
        lastTime = now;
        if (passedNs < 0) {
            passedNs = 0;
        }
        if (passedNs > MAX_NS_PER_UPDATE) {
            passedNs = MAX_NS_PER_UPDATE;
        }
        fps = (float) (NS_PER_SECOND / passedNs);
        passedTime += passedNs * timeScale * tps / NS_PER_SECOND;
        ticks = (int) passedTime;
        if (ticks > MAX_TICKS_PER_UPDATE) {
            ticks = MAX_TICKS_PER_UPDATE;
        }
        passedTime -= ticks;
        interval = passedTime;
    }
}
