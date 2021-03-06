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

package banki.security;


/**
 * {@code UnrecoverableEntryException} indicates, that a {@code KeyStore.Entry}
 * cannot be recovered from a {@code KeyStore}.
 *
 * @see KeyStore
 * @see KeyStore.Entry
 */
public class UnrecoverableEntryException extends GeneralSecurityException {

    private static final long serialVersionUID = -4527142945246286535L;

    /**
     * Constructs a new instance of {@code UnrecoverableEntryException}.
     */
    public UnrecoverableEntryException() {
    }

    /**
     * Constructs a new instance of {@code UnrecoverableEntryException} with the
     * given message.
     *
     * @param msg
     *            the detail message for this exception.
     */
    public UnrecoverableEntryException(String msg) {
        super(msg);
    }
}
