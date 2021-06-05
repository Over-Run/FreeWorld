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

package org.overrun.freeworld.client;

import org.overrun.freeworld.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL12.*;
import static org.overrun.freeworld.FreeWorld.logger;

/**
 * @author squid233
 * @since 2021/05/13
 */
public final class Texture {
    private static final Map<String, BufferedImage> IMAGE_CACHE =
            new HashMap<>();
    private static final Map<Identifier, Integer> TEXTURES =
            new HashMap<>();
    private static int lastId = 0;

    public static int load(Identifier id, int mode) {
        if (TEXTURES.containsKey(id)) {
            return TEXTURES.get(id);
        }
        BufferedImage img = getImage("assets." +
                id.getNamespace() +
                "/" +
                id.getPath());
        int w = img.getWidth();
        int h = img.getHeight();
        int tex = load(w,
                h,
                img.getRGB(0, 0, w, h, null, 0, w),
                img.getColorModel().hasAlpha() ? GL_BGRA : GL_BGR,
                mode);
        TEXTURES.put(id, tex);
        return tex;
    }

    public static int load(Identifier id) {
        return load(id, GL_NEAREST);
    }

    public static int load(int w, int h, int[] pixels, int format, int mode) {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, mode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mode);
        glTexImage2D(GL_TEXTURE_2D,
                0,
                GL_RGBA,
                w,
                h,
                0,
                format,
                GL_UNSIGNED_BYTE,
                pixels);
        return tex;
    }

    public static void bind(int id) {
        if (id != lastId) {
            glBindTexture(GL_TEXTURE_2D, id);
            lastId = id;
        }
    }

    public static void bind(Identifier id) {
        bind(load(id));
    }

    public static void delete(Identifier id) {
        glDeleteTextures(TEXTURES.get(id));
    }

    public static void clear() {
        for (int i : TEXTURES.values()) {
            glDeleteTextures(i);
        }
    }

    public static BufferedImage getImage(String path, boolean reload) {
        if (reload) {
            return loadImage(path);
        }
        if (IMAGE_CACHE.containsKey(path)) {
            return IMAGE_CACHE.get(path);
        }
        return loadImage(path);
    }

    public static BufferedImage loadImage(String path) {
        try (InputStream is = ClassLoader.getSystemResourceAsStream(path)) {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(is));
            IMAGE_CACHE.put(path, image);
            return image;
        } catch (Exception e) {
            logger.catching(e);
            BufferedImage image = new BufferedImage(2,
                    2,
                    BufferedImage.TYPE_INT_ARGB);
            image.setRGB(0, 0, 0xfff800f8);
            image.setRGB(1, 0, 0xff000000);
            image.setRGB(0, 1, 0xff000000);
            image.setRGB(1, 1, 0xfff800f8);
            return image;
        }
    }

    public static BufferedImage getImage(String path) {
        return getImage(path, false);
    }
}
