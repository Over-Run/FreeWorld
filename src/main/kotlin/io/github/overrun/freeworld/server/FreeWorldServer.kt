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

package io.github.overrun.freeworld.server

import io.github.overrun.freeworld.FreeWorld.Companion.logger
import io.github.overrun.freeworld.block.Blocks
import io.github.overrun.freeworld.client.FreeWorldClient
import io.github.overrun.freeworld.event.CommandManager
import java.util.*
import kotlin.collections.ArrayList

/**
 * The dedicated server for game.
 *
 * @author squid233
 * @since 2021/03/27
 */
class FreeWorldServer : Runnable {
    private lateinit var thread: Thread
    var running = false

    fun start() {
        if (!this::thread.isInitialized) {
            CommandManager.registerAll({ command, args ->
                if (command == "setblock" && args.size >= 3) {
                    val x = args[0].toIntOrNull()
                    if (x == null) {
                        logger.error("${args[0]} is not a number")
                        return@registerAll false
                    }
                    val y = args[1].toIntOrNull()
                    if (y == null) {
                        logger.error("${args[1]} is not a number")
                        return@registerAll false
                    }
                    val z = args[2].toIntOrNull()
                    if (z == null) {
                        logger.error("${args[2]} is not a number")
                        return@registerAll false
                    }
                    // interact with client direct
                    if (args.size >= 4)
                        FreeWorldClient.world?.setBlock(
                            x,
                            y,
                            z,
                            when (args[3]) {
                                "air" -> Blocks.air
                                "grass_block" -> Blocks.grassBlock
                                "dirt" -> Blocks.dirt
                                else -> Blocks.air
                            },
                            true
                        )
                    else
                        FreeWorldClient.world?.setBlock(
                            x,
                            y,
                            z,
                            Blocks.air,
                            true
                        )
                    return@registerAll true
                }
                false
            })
            running = true
            thread = Thread(this, "FreeWorldServer")
            thread.start()
        }
    }

    override fun run() {
        print("> ")
        val sc = Scanner(System.`in`)
        val list = ArrayList<String>()
        lateinit var arr: Array<String>
        lateinit var strSc: Scanner
        while (running) {
            if (sc.hasNextLine() && running) {
                strSc = Scanner(sc.nextLine())
                while (strSc.hasNext()) {
                    list.add(strSc.next())
                }
                arr = Array(list.size - 1) { "" }
                for (i in arr.indices) {
                    arr[i] = list[i + 1]
                }
                CommandManager.post(list[0], *arr)
                list.clear()
                print("> ")
                strSc.close()
            }
        }
    }
}