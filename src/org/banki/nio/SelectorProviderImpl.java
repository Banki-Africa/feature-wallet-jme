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

import banki.io.IOException;
import banki.nio.channels.DatagramChannel;
import banki.nio.channels.Pipe;
import banki.nio.channels.ServerSocketChannel;
import banki.nio.channels.SocketChannel;
import banki.nio.channels.spi.AbstractSelector;
import banki.nio.channels.spi.SelectorProvider;

/**
 * @hide for banki.nio.channels.spi.SelectorProvider only.
 */
public final class SelectorProviderImpl extends SelectorProvider {
    public DatagramChannel openDatagramChannel() throws IOException {
        return new DatagramChannelImpl(this);
    }

    public Pipe openPipe() throws IOException {
        return new PipeImpl(this);
    }

    public AbstractSelector openSelector() throws IOException {
        return new SelectorImpl(this);
    }

    public ServerSocketChannel openServerSocketChannel() throws IOException {
        return new ServerSocketChannelImpl(this);
    }

    public SocketChannel openSocketChannel() throws IOException {
        return new SocketChannelImpl(this);
    }
}
