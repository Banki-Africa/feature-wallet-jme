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

import banki.io.Serializable;
import banki.net.URL;
import banki.security.cert.Certificate;

/**
 * Legacy security code; do not use.
 */
public class CodeSource implements Serializable {
    public CodeSource(URL location, Certificate[] certs) { }

    public CodeSource(URL location, CodeSigner[] signers) { }

    public final Certificate[] getCertificates() { return null; }

    public final CodeSigner[] getCodeSigners() { return null; }

    public final URL getLocation() { return null; }

    public boolean implies(CodeSource cs) { return true; }
}
