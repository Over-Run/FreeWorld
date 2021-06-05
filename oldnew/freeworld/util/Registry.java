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

import org.jetbrains.annotations.NotNull;
import org.overrun.freeworld.block.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author squid233
 * @since 2021/05/04
 */
public class Registry<T> implements Iterable<T> {
    public static final Registry<Block> BLOCK = new Registry<>();
    private final Map<Identifier, T> id2entry = new LinkedHashMap<>();
    private final Map<T, Identifier> entry2id = new LinkedHashMap<>();
    private final Map<T, Integer> entry2rawId = new LinkedHashMap<>();
    private final List<T> rawId2entry = new ArrayList<>();
    private T defaultEntry;

    public <E extends T> E register(Identifier id, E entry) {
        id2entry.put(id, entry);
        entry2id.put(entry, id);
        entry2rawId.put(entry, entry2rawId.size());
        rawId2entry.add(entry);
        return entry;
    }

    public <E extends T> E register(String id, E entry) {
        return register(new Identifier(id), entry);
    }

    public void setDefaultEntry(T defaultEntry) {
        this.defaultEntry = defaultEntry;
    }

    public T get(Identifier id) {
        T t = id2entry.get(id);
        return t == null ? defaultEntry : t;
    }

    public T get(String id) {
        return get(new Identifier(id));
    }

    public T get(int rawId) {
        return (rawId < 0 || rawId >= rawId2entry.size())
                ? defaultEntry
                : rawId2entry.get(rawId);
    }

    public Identifier get(T entry) {
        Identifier id = entry2id.get(entry);
        return id == null ? entry2id.get(defaultEntry) : id;
    }

    public int getRawId(T entry) {
        return entry2rawId.getOrDefault(entry, 0);
    }

    public int size() {
        return id2entry.size();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return id2entry.values().iterator();
    }
}
