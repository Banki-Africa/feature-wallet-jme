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
 * FloatArrayBuffer, ReadWriteFloatArrayBuffer and ReadOnlyFloatArrayBuffer
 * compose the implementation of array based float buffers.
 * <p>
 * ReadWriteFloatArrayBuffer extends FloatArrayBuffer with all the write
 * methods.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p>
 *
 */
final class ReadWriteFloatArrayBuffer extends FloatArrayBuffer {

    static ReadWriteFloatArrayBuffer copy(FloatArrayBuffer other, int markOfOther) {
        ReadWriteFloatArrayBuffer buf =
                new ReadWriteFloatArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadWriteFloatArrayBuffer(float[] array) {
        super(array);
    }

    ReadWriteFloatArrayBuffer(int capacity) {
        super(capacity);
    }

    ReadWriteFloatArrayBuffer(int capacity, float[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    @Override
    public FloatBuffer asReadOnlyBuffer() {
        return ReadOnlyFloatArrayBuffer.copy(this, mark);
    }

    @Override
    public FloatBuffer compact() {
        System.arraycopy(backingArray, position + offset, backingArray, offset, remaining());
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    @Override
    public FloatBuffer duplicate() {
        return copy(this, mark);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override float[] protectedArray() {
        return backingArray;
    }

    @Override int protectedArrayOffset() {
        return offset;
    }

    @Override boolean protectedHasArray() {
        return true;
    }

    @Override
    public FloatBuffer put(float c) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        backingArray[offset + position++] = c;
        return this;
    }

    @Override
    public FloatBuffer put(int index, float c) {
        checkIndex(index);
        backingArray[offset + index] = c;
        return this;
    }

    @Override
    public FloatBuffer put(float[] src, int srcOffset, int floatCount) {
        if (floatCount > remaining()) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, srcOffset, backingArray, offset + position, floatCount);
        position += floatCount;
        return this;
    }

    @Override
    public FloatBuffer slice() {
        return new ReadWriteFloatArrayBuffer(remaining(), backingArray, offset + position);
    }

}
