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
 * CharArrayBuffer, ReadWriteCharArrayBuffer and ReadOnlyCharArrayBuffer compose
 * the implementation of array based char buffers.
 * <p>
 * ReadWriteCharArrayBuffer extends CharArrayBuffer with all the write methods.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p>
 *
 */
final class ReadWriteCharArrayBuffer extends CharArrayBuffer {

    static ReadWriteCharArrayBuffer copy(CharArrayBuffer other, int markOfOther) {
        ReadWriteCharArrayBuffer buf =
                new ReadWriteCharArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadWriteCharArrayBuffer(char[] array) {
        super(array);
    }

    ReadWriteCharArrayBuffer(int capacity) {
        super(capacity);
    }

    ReadWriteCharArrayBuffer(int capacity, char[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    @Override
    public CharBuffer asReadOnlyBuffer() {
        return ReadOnlyCharArrayBuffer.copy(this, mark);
    }

    @Override
    public CharBuffer compact() {
        System.arraycopy(backingArray, position + offset, backingArray, offset, remaining());
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    @Override
    public CharBuffer duplicate() {
        return copy(this, mark);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override char[] protectedArray() {
        return backingArray;
    }

    @Override int protectedArrayOffset() {
        return offset;
    }

    @Override boolean protectedHasArray() {
        return true;
    }

    @Override
    public CharBuffer put(char c) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        backingArray[offset + position++] = c;
        return this;
    }

    @Override
    public CharBuffer put(int index, char c) {
        checkIndex(index);
        backingArray[offset + index] = c;
        return this;
    }

    @Override
    public CharBuffer put(char[] src, int srcOffset, int charCount) {
        if (charCount > remaining()) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, srcOffset, backingArray, offset + position, charCount);
        position += charCount;
        return this;
    }

    @Override
    public CharBuffer slice() {
        return new ReadWriteCharArrayBuffer(remaining(), backingArray, offset + position);
    }

}
