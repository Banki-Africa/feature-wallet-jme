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
 * DoubleArrayBuffer implements all the shared readonly methods and is extended
 * by the other two classes.
 * </p>
 * <p>
 * All methods are marked final for runtime performance.
 * </p>
 *
 */
abstract class DoubleArrayBuffer extends DoubleBuffer {

    protected final double[] backingArray;

    protected final int offset;

    DoubleArrayBuffer(double[] array) {
        this(array.length, array, 0);
    }

    DoubleArrayBuffer(int capacity) {
        this(capacity, new double[capacity], 0);
    }

    DoubleArrayBuffer(int capacity, double[] backingArray, int offset) {
        super(capacity);
        this.backingArray = backingArray;
        this.offset = offset;
    }

    @Override
    public final double get() {
        if (position == limit) {
            throw new BufferUnderflowException();
        }
        return backingArray[offset + position++];
    }

    @Override
    public final double get(int index) {
        checkIndex(index);
        return backingArray[offset + index];
    }

    @Override
    public final DoubleBuffer get(double[] dst, int dstOffset, int doubleCount) {
        if (doubleCount > remaining()) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(backingArray, offset + position, dst, dstOffset, doubleCount);
        position += doubleCount;
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