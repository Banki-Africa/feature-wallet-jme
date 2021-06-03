/*
 * Copyright (C) 2020 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.moshi;

import java.io.EOFException;
import java.io.IOException;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

/**
 * This source reads a prefix of another source as a JSON value and then terminates. It can read
 * top-level arrays, objects, or strings only.
 *
 * <p>It implements {@linkplain JsonReader#setLenient lenient parsing} and has no mechanism to
 * enforce strict parsing. If the input is not valid or lenient JSON the behavior of this source is
 * unspecified.
 */
final class JsonValueSource implements Source {
  static final ByteString STATE_JSON = ByteString.encodeUtf8("[]{}\"'/#");
  static final ByteString STATE_SINGLE_QUOTED = ByteString.encodeUtf8("'\\");
  static final ByteString STATE_DOUBLE_QUOTED = ByteString.encodeUtf8("\"\\");
  static final ByteString STATE_END_OF_LINE_COMMENT = ByteString.encodeUtf8("\r\n");
  static final ByteString STATE_C_STYLE_COMMENT = ByteString.encodeUtf8("*");
  static final ByteString STATE_END_OF_JSON = ByteString.EMPTY;

  private final BufferedSource source;
  private final Buffer buffer;

  /** If non-empty, data from this should be returned before data from {@link #source}. */
  private final Buffer prefix;

  /**
   * The state indicates what kind of data is readable at {@link #limit}. This also serves
   * double-duty as the type of bytes we're interested in while in this state.
   */
  private ByteString state;

  /**
   * The level of nesting of arrays and objects. When the end of string, array, or object is
   * reached, this should be compared against 0. If it is zero, then we've read a complete value and
   * this source is exhausted.
   */
  private int stackSize;

  /** The number of bytes immediately returnable to the caller. */
  private long limit = 0;

  private boolean closed = false;

  JsonValueSource(BufferedSource source) {
    this(source, new Buffer(), STATE_JSON, 0);
  }

  JsonValueSource(BufferedSource source, Buffer prefix, ByteString state, int stackSize) {
    this.source = source;
    this.buffer = source.getBuffer();
    this.prefix = prefix;
    this.state = state;
    this.stackSize = stackSize;
  }

  /**
   * Advance {@link #limit} until any of these conditions are met:
   *
   * <ul>
   *   <li>Limit is at least {@code byteCount}. We can satisfy the caller's request!
   *   <li>The JSON value is complete. This stream is exhausted.
   *   <li>We have some data to return and returning more would require reloading the buffer. We
   *       prefer to return some data immediately when more data requires blocking.
   * </ul>
   *
   * @throws EOFException if the stream is exhausted before the JSON object completes.
   */
  private void advanceLimit(long byteCount) throws IOException {
    while (limit < byteCount) {
      // If we've finished the JSON object, we're done.
      if (state == STATE_END_OF_JSON) {
        return;
      }

      // If we can't return any bytes without more data in the buffer, grow the buffer.
      if (limit == buffer.size()) {
        if (limit > 0L) return;
        source.require(1L);
      }

      // Find the next interesting character for the current state. If the buffer doesn't have one,
      // then we can read the entire buffer.
      long index = buffer.indexOfElement(state, limit);
      if (index == -1L) {
        limit = buffer.size();
        continue;
      }

      byte b = buffer.getByte(index);

      if (state == STATE_JSON) {
        switch (b) {
          case '[':
          case '{':
            stackSize++;
            limit = index + 1;
            break;

          case ']':
          case '}':
            stackSize--;
            if (stackSize == 0) state = STATE_END_OF_JSON;
            limit = index + 1;
            break;

          case '\"':
            state = STATE_DOUBLE_QUOTED;
            limit = index + 1;
            break;

          case '\'':
            state = STATE_SINGLE_QUOTED;
            limit = index + 1;
            break;

          case '/':
            source.require(index + 2);
            byte b2 = buffer.getByte(index + 1);
            if (b2 == '/') {
              state = STATE_END_OF_LINE_COMMENT;
              limit = index + 2;
            } else if (b2 == '*') {
              state = STATE_C_STYLE_COMMENT;
              limit = index + 2;
            } else {
              limit = index + 1;
            }
            break;

          case '#':
            state = STATE_END_OF_LINE_COMMENT;
            limit = index + 1;
            break;
        }

      } else if (state == STATE_SINGLE_QUOTED || state == STATE_DOUBLE_QUOTED) {
        if (b == '\\') {
          source.require(index + 2);
          limit = index + 2;
        } else {
          state = (stackSize > 0) ? STATE_JSON : STATE_END_OF_JSON;
          limit = index + 1;
        }

      } else if (state == STATE_C_STYLE_COMMENT) {
        source.require(index + 2);
        if (buffer.getByte(index + 1) == '/') {
          limit = index + 2;
          state = STATE_JSON;
        } else {
          limit = index + 1;
        }

      } else if (state == STATE_END_OF_LINE_COMMENT) {
        limit = index + 1;
        state = STATE_JSON;

      } else {
        throw new AssertionError();
      }
    }
  }

  /**
   * Discards any remaining JSON data in this source that was left behind after it was closed. It is
   * an error to call {@link #read} after calling this method.
   */
  public void discard() throws IOException {
    closed = true;
    while (state != STATE_END_OF_JSON) {
      advanceLimit(8192);
      source.skip(limit);
    }
  }

  @Override
  public long read(Buffer sink, long byteCount) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    if (byteCount == 0) return 0L;

    // If this stream has a prefix, consume that first.
    if (!prefix.exhausted()) {
      long prefixResult = prefix.read(sink, byteCount);
      byteCount -= prefixResult;
      if (buffer.exhausted()) return prefixResult; // Defer a blocking call.
      long suffixResult = read(sink, byteCount);
      return suffixResult != -1L ? suffixResult + prefixResult : prefixResult;
    }

    advanceLimit(byteCount);

    if (limit == 0) {
      if (state != STATE_END_OF_JSON) throw new AssertionError();
      return -1L;
    }

    long result = Math.min(byteCount, limit);
    sink.write(buffer, result);
    limit -= result;
    return result;
  }

  @Override
  public Timeout timeout() {
    return source.timeout();
  }

  @Override
  public void close() throws IOException {
    // Note that this does not close the underlying source; that's the creator's responsibility.
    closed = true;
  }
}
