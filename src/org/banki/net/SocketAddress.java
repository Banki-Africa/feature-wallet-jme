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

package banki.net;

import banki.io.Serializable;

/**
 * This abstract class represents a protocol-independent base for
 * socket-endpoint representing classes. The class has to be implemented
 * according to a specific protocol.
 */
public abstract class SocketAddress implements Serializable {

    private static final long serialVersionUID = 5215720748342549866L;

    /**
     * Creates a new {@code SocketAddress} instance.
     */
    public SocketAddress() {
    }
}
