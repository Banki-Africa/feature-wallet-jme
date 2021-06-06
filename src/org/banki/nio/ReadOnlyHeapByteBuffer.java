/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package banki.nio;

/**
 * HeapByteBuffer, ReadWriteHeapByteBuffer and ReadOnlyHeapByteBuffer compose
 * the implementation of array based byte buffers.
 * <p>
 * ReadOnlyHeapByteBuffer extends HeapByteBuffer with all the write methods
 * throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p>
 *
 */
final class ReadOnlyHeapByteBuffer extends HeapByteBuffer {

    static ReadOnlyHeapByteBuffer copy(HeapByteBuffer other, int markOfOther) {
        ReadOnlyHeapByteBuffer buf =
                new ReadOnlyHeapByteBuffer(other.backingArray, other.capacity(), other.offset);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadOnlyHeapByteBuffer(byte[] backingArray, int capacity, int arrayOffset) {
        super(backingArray, capacity, arrayOffset);
    }

    @Override
    public ByteBuffer asReadOnlyBuffer() {
        return ReadOnlyHeapByteBuffer.copy(this, mark);
    }

    @Override
    public ByteBuffer compact() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer duplicate() {
        return copy(this, mark);
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override byte[] protectedArray() {
        throw new ReadOnlyBufferException();
    }

    @Override int protectedArrayOffset() {
        throw new ReadOnlyBufferException();
    }

    @Override boolean protectedHasArray() {
        return false;
    }

    @Override
    public ByteBuffer put(byte b) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer put(int index, byte b) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer put(byte[] src, int srcOffset, int byteCount) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer putDouble(double value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer putDouble(int index, double value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer putFloat(float value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer putFloat(int index, float value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer putInt(int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer putInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer putLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer putLong(long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer putShort(int index, short value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer putShort(short value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer put(ByteBuffer buf) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuffer slice() {
        return new ReadOnlyHeapByteBuffer(backingArray, remaining(), offset + position);
    }
}
