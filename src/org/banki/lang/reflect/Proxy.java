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

package banki.lang.reflect;

import banki.io.Serializable;
import banki.lang.ref.WeakReference;
import banki.util.HashMap;
import banki.util.Map;
import banki.util.WeakHashMap;

/**
 * {@code Proxy} defines methods for creating dynamic proxy classes and instances.
 * A proxy class implements a declared set of interfaces and delegates method
 * invocations to an {@code InvocationHandler}.
 *
 * @see InvocationHandler
 * @since 1.3
 */
public class Proxy implements Serializable {

    private static final long serialVersionUID = -2222568056686623797L;

    // maps class loaders to created classes by interface names
    private static final Map<ClassLoader, Map<String, WeakReference<Class<?>>>> loaderCache = new WeakHashMap<ClassLoader, Map<String, WeakReference<Class<?>>>>();

    // to find previously created types
    private static final Map<Class<?>, String> proxyCache = new WeakHashMap<Class<?>, String>();

    private static int NextClassNameIndex = 0;

    /**
     * The invocation handler on which the method calls are dispatched.
     */
    protected InvocationHandler h;

    @SuppressWarnings("unused")
    private Proxy() {
    }

    /**
     * Constructs a new {@code Proxy} instance with the specified invocation
     * handler.
     *
     * @param h
     *            the invocation handler for the newly created proxy
     */
    protected Proxy(InvocationHandler h) {
        this.h = h;
    }

    /**
     * Returns the dynamically built {@code Class} for the specified interfaces.
     * Creates a new {@code Class} when necessary. The order of the interfaces
     * is relevant. Invocations of this method with the same interfaces but
     * different order result in different generated classes. The interfaces
     * must be visible from the supplied class loader; no duplicates are
     * permitted. All non-public interfaces must be defined in the same package.
     *
     * @param loader
     *            the class loader that will define the proxy class
     * @param interfaces
     *            an array of {@code Class} objects, each one identifying an
     *            interface that will be implemented by the returned proxy
     *            class
     * @return a proxy class that implements all of the interfaces referred to
     *         in the contents of {@code interfaces}
     * @throws IllegalArgumentException
     *                if any of the interface restrictions are violated
     * @throws NullPointerException
     *                if either {@code interfaces} or any of its elements are
     *                {@code null}
     */
    public static Class<?> getProxyClass(ClassLoader loader,
            Class<?>... interfaces) throws IllegalArgumentException {
        // check that interfaces are a valid array of visible interfaces
        if (interfaces == null) {
            throw new NullPointerException("interfaces == null");
        }
        String commonPackageName = null;
        for (int i = 0, length = interfaces.length; i < length; i++) {
            Class<?> next = interfaces[i];
            if (next == null) {
                throw new NullPointerException("interfaces[" + i + "] == null");
            }
            String name = next.getName();
            if (!next.isInterface()) {
                throw new IllegalArgumentException(name + " is not an interface");
            }
            if (loader != next.getClassLoader()) {
                try {
                    if (next != Class.forName(name, false, loader)) {
                        throw new IllegalArgumentException(name +
                                " is not visible from class loader");
                    }
                } catch (ClassNotFoundException ex) {
                    throw new IllegalArgumentException(name + " is not visible from class loader");
                }
            }
            for (int j = i + 1; j < length; j++) {
                if (next == interfaces[j]) {
                    throw new IllegalArgumentException(name + " appears more than once");
                }
            }
            if (!Modifier.isPublic(next.getModifiers())) {
                int last = name.lastIndexOf('.');
                String p = last == -1 ? "" : name.substring(0, last);
                if (commonPackageName == null) {
                    commonPackageName = p;
                } else if (!commonPackageName.equals(p)) {
                    throw new IllegalArgumentException("non-public interfaces must be " +
                            "in the same package");
                }
            }
        }

        // search cache for matching proxy class using the class loader
        synchronized (loaderCache) {
            Map<String, WeakReference<Class<?>>> interfaceCache = loaderCache
                    .get(loader);
            if (interfaceCache == null) {
                loaderCache
                        .put(
                                loader,
                                (interfaceCache = new HashMap<String, WeakReference<Class<?>>>()));
            }

            String interfaceKey = "";
            if (interfaces.length == 1) {
                interfaceKey = interfaces[0].getName();
            } else {
                StringBuilder names = new StringBuilder();
                for (int i = 0, length = interfaces.length; i < length; i++) {
                    names.append(interfaces[i].getName());
                    names.append(' ');
                }
                interfaceKey = names.toString();
            }

            Class<?> newClass;
            WeakReference<Class<?>> ref = interfaceCache.get(interfaceKey);
            if (ref == null) {
                String nextClassName = "$Proxy" + NextClassNameIndex++;
                if (commonPackageName != null && commonPackageName.length() > 0) {
                    nextClassName = commonPackageName + "." + nextClassName;
                }
                if (loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }
                newClass = generateProxy(nextClassName.replace('.', '/'), interfaces, loader);
                // Need a weak reference to the class so it can
                // be unloaded if the class loader is discarded
                interfaceCache.put(interfaceKey, new WeakReference<Class<?>>(newClass));
                synchronized (proxyCache) {
                    // the value is unused
                    proxyCache.put(newClass, "");
                }
            } else {
                newClass = ref.get();
                assert newClass != null : "\ninterfaceKey=\"" + interfaceKey + "\""
                                        + "\nloaderCache=\"" + loaderCache + "\""
                                        + "\nintfCache=\"" + interfaceCache + "\""
                                        + "\nproxyCache=\"" + proxyCache + "\"";
            }
            return newClass;
        }
    }

    /**
     * Returns an instance of the dynamically built class for the specified
     * interfaces. Method invocations on the returned instance are forwarded to
     * the specified invocation handler. The interfaces must be visible from the
     * supplied class loader; no duplicates are permitted. All non-public
     * interfaces must be defined in the same package.
     *
     * @param loader
     *            the class loader that will define the proxy class
     * @param interfaces
     *            an array of {@code Class} objects, each one identifying an
     *            interface that will be implemented by the returned proxy
     *            object
     * @param h
     *            the invocation handler that handles the dispatched method
     *            invocations
     * @return a new proxy object that delegates to the handler {@code h}
     * @throws IllegalArgumentException
     *                if any of the interface restrictions are violated
     * @throws NullPointerException
     *                if the interfaces or any of its elements are null
     */
    public static Object newProxyInstance(ClassLoader loader,
            Class<?>[] interfaces, InvocationHandler h)
            throws IllegalArgumentException {
        if (h == null) {
            throw new NullPointerException("h == null");
        }
        try {
            return getProxyClass(loader, interfaces).getConstructor(
                    new Class<?>[] { InvocationHandler.class }).newInstance(
                    new Object[] { h });
        } catch (NoSuchMethodException ex) {
            throw (InternalError) (new InternalError(ex.toString())
                    .initCause(ex));
        } catch (IllegalAccessException ex) {
            throw (InternalError) (new InternalError(ex.toString())
                    .initCause(ex));
        } catch (InstantiationException ex) {
            throw (InternalError) (new InternalError(ex.toString())
                    .initCause(ex));
        } catch (InvocationTargetException ex) {
            Throwable target = ex.getTargetException();
            throw (InternalError) (new InternalError(target.toString())
                    .initCause(target));
        }
    }

    /**
     * Indicates whether or not the specified class is a dynamically generated
     * proxy class.
     *
     * @param cl
     *            the class
     * @return {@code true} if the class is a proxy class, {@code false}
     *         otherwise
     * @throws NullPointerException
     *                if the class is {@code null}
     */
    public static boolean isProxyClass(Class<?> cl) {
        if (cl == null) {
            throw new NullPointerException("cl == null");
        }
        synchronized (proxyCache) {
            return proxyCache.containsKey(cl);
        }
    }

    /**
     * Returns the invocation handler of the specified proxy instance.
     *
     * @param proxy
     *            the proxy instance
     * @return the invocation handler of the specified proxy instance
     * @throws IllegalArgumentException
     *                if the supplied {@code proxy} is not a proxy object
     */
    public static InvocationHandler getInvocationHandler(Object proxy)
            throws IllegalArgumentException {

        if (isProxyClass(proxy.getClass())) {
            return ((Proxy) proxy).h;
        }

        throw new IllegalArgumentException("not a proxy instance");
    }

    native private static Class generateProxy(String name, Class[] interfaces,
        ClassLoader loader);

    /*
     * The VM clones this method's descriptor when generating a proxy class.
     * There is no implementation.
     */
    native private static void constructorPrototype(InvocationHandler h);
}
