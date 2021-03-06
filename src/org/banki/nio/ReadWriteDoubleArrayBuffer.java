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
 * DoubleArrayBuffer, ReadWriteDoubleArrayBuffer and ReadOnlyDoubleArrayBuffer
 * compose the implementation of array based double buffers.
 * <p>
 * ReadWriteDoubleArrayBuffer extends DoubleArrayBuffer with all the write
 * methods.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p>
 *
 */
final class ReadWriteDoubleArrayBuffer extends DoubleArrayBuffer {

    static ReadWriteDoubleArrayBuffer copy(DoubleArrayBuffer other, int markOfOther) {
        ReadWriteDoubleArrayBuffer buf =
                new ReadWriteDoubleArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadWriteDoubleArrayBuffer(double[] array) {
        super(array);
    }

    ReadWriteDoubleArrayBuffer(int capacity) {
        super(capacity);
    }

    ReadWriteDoubleArrayBuffer(int capacity, double[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    @Override
    public DoubleBuffer asReadOnlyBuffer() {
        return ReadOnlyDoubleArrayBuffer.copy(this, mark);
    }

    @Override
    public DoubleBuffer compact() {
        System.arraycopy(backingArray, position + offset, backingArray, offset, remaining());
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    @Override
    public DoubleBuffer duplicate() {
        return copy(this, mark);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override double[] protectedArray() {
        return backingArray;
    }

    @Override int protectedArrayOffset() {
        return offset;
    }

    @Override boolean protectedHasArray() {
        return true;
    }

    @Override
    public DoubleBuffer put(double c) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        backingArray[offset + position++] = c;
        return this;
    }

    @Override
    public DoubleBuffer put(int index, double c) {
        checkIndex(index);
        backingArray[offset + index] = c;
        return this;
    }

    @Override
    public DoubleBuffer put(double[] src, int srcOffset, int doubleCount) {
        if (doubleCount > remaining()) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, srcOffset, backingArray, offset + position, doubleCount);
        position += doubleCount;
        return this;
    }

    @Override
    public DoubleBuffer slice() {
        return new ReadWriteDoubleArrayBuffer(remaining(), backingArray, offset + position);
    }

}
