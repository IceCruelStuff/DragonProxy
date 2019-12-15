/*
 * DragonProxy
 * Copyright (C) 2016-2019 Dragonet Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You can view the LICENSE file for more details.
 *
 * https://github.com/DragonetMC/DragonProxy
 */
package org.dragonet.proxy.network.session.cache;

import com.nukkitx.nbt.util.VarInts;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class NBTOutputStream implements DataOutput, AutoCloseable {

    private final DataOutputStream stream;
    private final ByteOrder endianness;
    private final boolean network;

    public NBTOutputStream(OutputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTOutputStream(OutputStream stream, ByteOrder endianness) {
        this(stream, endianness, false);
    }

    public NBTOutputStream(OutputStream stream, ByteOrder endianness, boolean network) {
        this.stream = stream instanceof DataOutputStream ? (DataOutputStream) stream : new DataOutputStream(stream);
        this.endianness = endianness;
        this.network = network;
    }

    public ByteOrder getEndianness() {
        return endianness;
    }

    public boolean isNetwork() {
        return network;
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.stream.write(bytes);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        this.stream.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.stream.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.stream.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v) >> 16;
        }
        this.stream.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Character.reverseBytes((char) v);
        }
        this.stream.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        if (network) {
            VarInts.writeInt(this.stream, v);
        } else {
            if (endianness == ByteOrder.LITTLE_ENDIAN) {
                v = Integer.reverseBytes(v);
            }
            this.stream.writeInt(v);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        if (network) {
            VarInts.writeLong(this.stream, v);
        } else {
            if (endianness == ByteOrder.LITTLE_ENDIAN) {
                v = Long.reverseBytes(v);
            }
            this.stream.writeLong(v);
        }
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.stream.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        this.stream.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (network) {
            VarInts.writeUnsignedInt(stream, bytes.length);
        } else {
            this.writeShort(bytes.length);
        }
        this.stream.write(bytes);
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
