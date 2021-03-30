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

package io.github.overrun.freeworld.event;

import io.github.overrun.freeworld.block.Block;
import io.github.overrun.freeworld.block.Blocks;
import io.github.overrun.freeworld.client.FreeWorldClient;
import io.github.overrun.freeworld.entity.player.Player;
import io.github.overrun.freeworld.world.World;

import static io.github.overrun.freeworld.FreeWorld.getLogger;
import static java.lang.Integer.parseInt;

/**
 * @author squid233
 * @since 2021/03/27
 */
@FunctionalInterface
public interface CommandListener {
    CommandListener SET_BLOCK = (command, args) -> {
        if ("setblock".equals(command) && args.length >= 3) {
            int x;
            try {
                x = parseInt(args[0]);
            } catch (NumberFormatException e) {
                getLogger().error(args[0] + " is not a number");
                return false;
            }
            int y;
            try {
                y = parseInt(args[1]);
            } catch (NumberFormatException e) {
                getLogger().error(args[1] + " is not a number");
                return false;
            }
            int z;
            try {
                z = parseInt(args[2]);
            } catch (NumberFormatException e) {
                getLogger().error(args[2] + "is not a number");
                return false;
            }
            // interact with client direct
            World world = FreeWorldClient.INSTANCE.getWorld();
            if (args.length >= 4) {
                if (world != null) {
                    Block block;
                    switch (args[3]) {
                        case "grass_block":
                            block = Blocks.grassBlock;
                            break;
                        case "dirt":
                            block = Blocks.dirt;
                            break;
                        default:
                            block = Blocks.air;
                    }
                    world.setBlock(x, y, z, block, true);
                }
            } else {
                if (world != null) {
                    world.setBlock(x, y, z, Blocks.air, true);
                }
            }
            return true;
        }
        return false;
    };
    CommandListener TP = (command, args) -> {
        if ("tp".equals(command) && args.length >= 3) {
            float x;
            try {
                x = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                getLogger().error(args[0] + "is not a floating point");
                return false;
            }
            float y;
            try {
                y = Float.parseFloat(args[1]);
            } catch (NumberFormatException e) {
                getLogger().error(args[1] + "is not a floating point");
                return false;
            }
            float z;
            try {
                z = Float.parseFloat(args[2]);
            } catch (NumberFormatException e) {
                getLogger().error(args[2] + "is not a floating point");
                return false;
            }
            Player.INSTANCE.setX(x);
            Player.INSTANCE.setY(y);
            Player.INSTANCE.setZ(z);
            return true;
        }
        return false;
    };

    /**
     * This method trigger on command has sent.
     * <p>
     * For example: {@code a 0 0 0}<br>
     * The command will be {@code a}.<br>
     * The args will be {@code [0, 0, 0]}.
     *
     * @param command The command. Implementor should compare this param.
     * @param args    The arguments.
     * @return If success, return true; otherwise false.
     */
    boolean onCommand(String command, String... args);
}
