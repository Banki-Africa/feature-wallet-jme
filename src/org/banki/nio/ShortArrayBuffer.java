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
 * ShortArrayBuffer, ReadWriteShortArrayBuffer and ReadOnlyShortArrayBuffer
 * compose the implementation of array based short buffers.
 * <p>
 * ShortArrayBuffer implements all the shared readonly methods and is extended
 * by the other two classes.
 * </p>
 * <p>
 * All methods are marked final for runtime performance.
 * </p>
 *
 */
abstract class ShortArrayBuffer extends ShortBuffer {

    protected final short[] backingArray;

    protected final int offset;

    ShortArrayBuffer(short[] array) {
        this(array.length, array, 0);
    }

    ShortArrayBuffer(int capacity) {
        this(capacity, new short[capacity], 0);
    }

    ShortArrayBuffer(int capacity, short[] backingArray, int offset) {
        super(capacity);
        this.backingArray = backingArray;
        this.offset = offset;
    }

    @Override
    public final short get() {
        if (position == limit) {
            throw new BufferUnderflowException();
        }
        return backingArray[offset + position++];
    }

    @Override
    public final short get(int index) {
        checkIndex(index);
        return backingArray[offset + index];
    }

    @Override
    public final ShortBuffer get(short[] dst, int dstOffset, int shortCount) {
        if (shortCount > remaining()) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(backingArray, offset + position, dst, dstOffset, shortCount);
        position += shortCount;
        return this;
    }

    @Override
    public final boolean isDirect() {
        return false;
    }

    @Override
    public final ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

}
