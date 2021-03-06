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

package banki.lang;

/**
 * ThreadDeath is thrown when a thread stops executing. It is used to aid in the
 * orderly unrolling of the thread's stack (eg. cleanup of monitors).
 */
public class ThreadDeath extends Error {

    private static final long serialVersionUID = -4417128565033088268L;

    /**
     * Constructs a new instance of this class. Note that in the case of
     * ThreadDeath, the stack trace may <em>not</em> be filled in a way which
     * allows a stack trace to be printed.
     */
    public ThreadDeath() {
    }
}
