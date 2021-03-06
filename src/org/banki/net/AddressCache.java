/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package banki.net;

import libcore.util.BasicLruCache;

/**
 * Implements caching for {@code InetAddress}. We use a unified cache for both positive and negative
 * cache entries.
 *
 * TODO: benchmark and optimize InetAddress until we get to the point where we can just rely on
 * the C library level caching. The main thing caching at this level buys us is avoiding repeated
 * conversions from 'struct sockaddr's to InetAddress[].
 */
class AddressCache {
    /**
     * When the cache contains more entries than this, we start dropping the oldest ones.
     * This should be a power of two to avoid wasted space in our custom map.
     */
    private static final int MAX_ENTRIES = 16;

    // The TTL for the Java-level cache is short, just 2s.
    private static final long TTL_NANOS = 2 * 1000000000L;

    // The actual cache.
    private final BasicLruCache<String, AddressCacheEntry> cache
            = new BasicLruCache<String, AddressCacheEntry>(MAX_ENTRIES);

    static class AddressCacheEntry {
        // Either an InetAddress[] for a positive entry,
        // or a String detail message for a negative entry.
        final Object value;

        /**
         * The absolute expiry time in nanoseconds. Nanoseconds from System.nanoTime is ideal
         * because -- unlike System.currentTimeMillis -- it can never go backwards.
         *
         * We don't need to worry about overflow with a TTL_NANOS of 2s.
         */
        final long expiryNanos;

        AddressCacheEntry(Object value) {
            this.value = value;
            this.expiryNanos = System.nanoTime() + TTL_NANOS;
        }
    }

    /**
     * Removes all entries from the cache.
     */
    public void clear() {
        cache.evictAll();
    }

    /**
     * Returns the cached InetAddress[] associated with 'hostname'. Returns null if nothing is known
     * about 'hostname'. Returns a String suitable for use as an UnknownHostException detail
     * message if 'hostname' is known not to exist.
     */
    public Object get(String hostname) {
        AddressCacheEntry entry = cache.get(hostname);
        // Do we have a valid cache entry?
        if (entry != null && entry.expiryNanos >= System.nanoTime()) {
            return entry.value;
        }
        // Either we didn't find anything, or it had expired.
        // No need to remove expired entries: the caller will provide a replacement shortly.
        return null;
    }

    /**
     * Associates the given 'addresses' with 'hostname'. The association will expire after a
     * certain length of time.
     */
    public void put(String hostname, InetAddress[] addresses) {
        cache.put(hostname, new AddressCacheEntry(addresses));
    }

    /**
     * Records that 'hostname' is known not to have any associated addresses. (I.e. insert a
     * negative cache entry.)
     */
    public void putUnknownHost(String hostname, String detailMessage) {
        cache.put(hostname, new AddressCacheEntry(detailMessage));
    }
}
