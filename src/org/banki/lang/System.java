/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright (C) 2008 The Android Open Source Project
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

package banki.lang;

import dalvik.system.VMRuntime;
import dalvik.system.VMStack;
import banki.io.BufferedInputStream;
import banki.io.Console;
import banki.io.FileDescriptor;
import banki.io.FileInputStream;
import banki.io.FileOutputStream;
import banki.io.IOException;
import banki.io.InputStream;
import banki.io.PrintStream;
import banki.nio.channels.Channel;
import banki.nio.channels.spi.SelectorProvider;
import banki.util.AbstractMap;
import banki.util.Collections;
import banki.util.HashMap;
import banki.util.Map;
import banki.util.Properties;
import banki.util.Set;
import libcore.icu.ICU;
import libcore.io.Libcore;
import libcore.io.StructUtsname;
import libcore.util.ZoneInfoDB;

/**
 * Provides access to system-related information and resources including
 * standard input and output. Enables clients to dynamically load native
 * libraries. All methods of this class are accessed in a static way and the
 * class itself can not be instantiated.
 *
 * @see Runtime
 */
public final class System {

    /**
     * Default input stream.
     */
    public static final InputStream in;

    /**
     * Default output stream.
     */
    public static final PrintStream out;

    /**
     * Default error output stream.
     */
    public static final PrintStream err;

    private static final String lineSeparator;
    private static Properties systemProperties;

    static {
        err = new PrintStream(new FileOutputStream(FileDescriptor.err));
        out = new PrintStream(new FileOutputStream(FileDescriptor.out));
        in = new BufferedInputStream(new FileInputStream(FileDescriptor.in));
        lineSeparator = System.getProperty("line.separator");
    }

    /**
     * Sets the standard input stream to the given user defined input stream.
     *
     * @param newIn
     *            the user defined input stream to set as the standard input
     *            stream.
     */
    public static void setIn(InputStream newIn) {
        setFieldImpl("in", "Ljava/io/InputStream;", newIn);
    }

    /**
     * Sets the standard output stream to the given user defined output stream.
     *
     * @param newOut
     *            the user defined output stream to set as the standard output
     *            stream.
     */
    public static void setOut(PrintStream newOut) {
        setFieldImpl("out", "Ljava/io/PrintStream;", newOut);
    }

    /**
     * Sets the standard error output stream to the given user defined output
     * stream.
     *
     * @param newErr
     *            the user defined output stream to set as the standard error
     *            output stream.
     */
    public static void setErr(PrintStream newErr) {
        setFieldImpl("err", "Ljava/io/PrintStream;", newErr);
    }

    /**
     * Prevents this class from being instantiated.
     */
    private System() {
    }

    /**
     * Copies {@code length} elements from the array {@code src},
     * starting at offset {@code srcPos}, into the array {@code dst},
     * starting at offset {@code dstPos}.
     *
     * <p>The source and destination arrays can be the same array,
     * in which case copying is performed as if the source elements
     * are first copied into a temporary array and then into the
     * destination array.
     *
     * @param src
     *            the source array to copy the content.
     * @param srcPos
     *            the starting index of the content in {@code src}.
     * @param dst
     *            the destination array to copy the data into.
     * @param dstPos
     *            the starting index for the copied content in {@code dst}.
     * @param length
     *            the number of elements to be copied.
     */
    public static native void arraycopy(Object src, int srcPos, Object dst, int dstPos, int length);

    /**
     * Returns the current time in milliseconds since January 1, 1970 00:00:00.0 UTC.
     *
     * <p>This method always returns UTC times, regardless of the system's time zone.
     * This is often called "Unix time" or "epoch time".
     * Use a {@link banki.text.DateFormat} instance to format this time for display to a human.
     *
     * <p>This method shouldn't be used for measuring timeouts or
     * other elapsed time measurements, as changing the system time can affect
     * the results. Use {@link #nanoTime} for that.
     */
    public static native long currentTimeMillis();

    /**
     * Returns the current timestamp of the most precise timer available on the
     * local system.
     *
     * <p>This timestamp should only be used to measure a duration by comparing it
     * against another timestamp from the same process on the same device.
     * Values returned by this method do not have a defined correspondence to
     * wall clock times; the zero value is typically whenever the device last booted.
     * Use {@link #currentTimeMillis} if you want to know what time it is.
     */
    public static native long nanoTime();

    /**
     * Causes the VM to stop running and the program to exit. If
     * {@link #runFinalizersOnExit(boolean)} has been previously invoked with a
     * {@code true} argument, then all objects will be properly
     * garbage-collected and finalized first.
     *
     * @param code
     *            the return code.
     */
    public static void exit(int code) {
        Runtime.getRuntime().exit(code);
    }

    /**
     * Indicates to the VM that it would be a good time to run the
     * garbage collector. Note that this is a hint only. There is no guarantee
     * that the garbage collector will actually be run.
     */
    public static void gc() {
        Runtime.getRuntime().gc();
    }

    /**
     * Returns the value of the environment variable with the given name {@code
     * var}.
     *
     * @param name
     *            the name of the environment variable.
     * @return the value of the specified environment variable or {@code null}
     *         if no variable exists with the given name.
     */
    public static String getenv(String name) {
        return getenv(name, null);
    }

    private static String getenv(String name, String defaultValue) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        String value = Libcore.os.getenv(name);
        return (value != null) ? value : defaultValue;
    }

    /**
     * Returns an unmodifiable map of all available environment variables.
     *
     * @return the map representing all environment variables.
     */
    public static Map<String, String> getenv() {
        Map<String, String> map = new HashMap<String, String>();
        for (String entry : Libcore.os.environ()) {
            int index = entry.indexOf('=');
            if (index != -1) {
                map.put(entry.substring(0, index), entry.substring(index + 1));
            }
        }
        return new SystemEnvironment(map);
    }

    /**
     * Returns the inherited channel from the creator of the current virtual
     * machine.
     *
     * @return the inherited {@link Channel} or {@code null} if none exists.
     * @throws IOException
     *             if an I/O error occurred.
     * @see SelectorProvider
     * @see SelectorProvider#inheritedChannel()
     */
    public static Channel inheritedChannel() throws IOException {
        return SelectorProvider.provider().inheritedChannel();
    }

    /**
     * Returns the system properties. Note that this is not a copy, so that
     * changes made to the returned Properties object will be reflected in
     * subsequent calls to getProperty and getProperties.
     *
     * @return the system properties.
     */
    public static Properties getProperties() {
        if (systemProperties == null) {
            initSystemProperties();
        }
        return systemProperties;
    }

    private static void initSystemProperties() {
        VMRuntime runtime = VMRuntime.getRuntime();
        Properties p = new Properties();

        String projectUrl = "http://www.android.com/";
        String projectName = "The Android Project";

        p.put("banki.boot.class.path", runtime.bootClassPath());
        p.put("banki.class.path", runtime.classPath());

        // None of these four are meaningful on Android, but these keys are guaranteed
        // to be present for System.getProperty. For banki.class.version, we use the maximum
        // class file version that dx currently supports.
        p.put("banki.class.version", "50.0");
        p.put("banki.compiler", "");
        p.put("banki.ext.dirs", "");
        p.put("banki.version", "0");

        p.put("banki.home", getenv("JAVA_HOME", "/system"));

        p.put("banki.io.tmpdir", "/tmp");
        p.put("banki.library.path", getenv("LD_LIBRARY_PATH"));

        p.put("banki.specification.name", "Dalvik Core Library");
        p.put("banki.specification.vendor", projectName);
        p.put("banki.specification.version", "0.9");

        p.put("banki.vendor", projectName);
        p.put("banki.vendor.url", projectUrl);
        p.put("banki.vm.name", "Dalvik");
        p.put("banki.vm.specification.name", "Dalvik Virtual Machine Specification");
        p.put("banki.vm.specification.vendor", projectName);
        p.put("banki.vm.specification.version", "0.9");
        p.put("banki.vm.vendor", projectName);
        p.put("banki.vm.version", runtime.vmVersion());

        p.put("file.separator", "/");
        p.put("line.separator", "\n");
        p.put("path.separator", ":");

        p.put("banki.runtime.name", "Android Runtime");
        p.put("banki.runtime.version", "0.9");
        p.put("banki.vm.vendor.url", projectUrl);

        p.put("file.encoding", "UTF-8");
        p.put("user.language", "en");
        p.put("user.region", "US");

        p.put("user.home", getenv("HOME", ""));
        p.put("user.name", getenv("USER", ""));

        StructUtsname info = Libcore.os.uname();
        p.put("os.arch", info.machine);
        p.put("os.name", info.sysname);
        p.put("os.version", info.release);

        // Undocumented Android-only properties.
        p.put("android.icu.library.version", ICU.getIcuVersion());
        p.put("android.icu.unicode.version", ICU.getUnicodeVersion());
        // TODO: it would be nice to have this but currently it causes circularity.
        // p.put("android.tzdata.version", ZoneInfoDB.getVersion());
        parsePropertyAssignments(p, specialProperties());

        // Override built-in properties with settings from the command line.
        parsePropertyAssignments(p, runtime.properties());

        systemProperties = p;
    }

    /**
     * Returns an array of "key=value" strings containing information not otherwise
     * easily available, such as #defined library versions.
     */
    private static native String[] specialProperties();

    /**
     * Adds each element of 'assignments' to 'p', treating each element as an
     * assignment in the form "key=value".
     */
    private static void parsePropertyAssignments(Properties p, String[] assignments) {
        for (String assignment : assignments) {
            int split = assignment.indexOf('=');
            String key = assignment.substring(0, split);
            String value = assignment.substring(split + 1);
            p.put(key, value);
        }
    }

    /**
     * Returns the value of a particular system property or {@code null} if no
     * such property exists.
     *
     * <p>The following properties are always provided by the Dalvik VM:
     * <p><table BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
     * <tr BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
     *     <td><b>Name</b></td>        <td><b>Meaning</b></td>                    <td><b>Example</b></td></tr>
     * <tr><td>file.separator</td>     <td>{@link banki.io.File#separator}</td>    <td>{@code /}</td></tr>
     *
     * <tr><td>banki.class.path</td>    <td>System class path</td>                 <td>{@code .}</td></tr>
     * <tr><td>banki.class.version</td> <td>(Not useful on Android)</td>           <td>{@code 50.0}</td></tr>
     * <tr><td>banki.compiler</td>      <td>(Not useful on Android)</td>           <td>Empty</td></tr>
     * <tr><td>banki.ext.dirs</td>      <td>(Not useful on Android)</td>           <td>Empty</td></tr>
     * <tr><td>banki.home</td>          <td>Location of the VM on the file system</td> <td>{@code /system}</td></tr>
     * <tr><td>banki.io.tmpdir</td>     <td>See {@link banki.io.File#createTempFile}</td> <td>{@code /sdcard}</td></tr>
     * <tr><td>banki.library.path</td>  <td>Search path for JNI libraries</td>     <td>{@code /system/lib}</td></tr>
     * <tr><td>banki.vendor</td>        <td>Human-readable VM vendor</td>          <td>{@code The Android Project}</td></tr>
     * <tr><td>banki.vendor.url</td>    <td>URL for VM vendor's web site</td>      <td>{@code http://www.android.com/}</td></tr>
     * <tr><td>banki.version</td>       <td>(Not useful on Android)</td>           <td>{@code 0}</td></tr>
     *
     * <tr><td>banki.specification.version</td>    <td>VM libraries version</td>        <td>{@code 0.9}</td></tr>
     * <tr><td>banki.specification.vendor</td>     <td>VM libraries vendor</td>         <td>{@code The Android Project}</td></tr>
     * <tr><td>banki.specification.name</td>       <td>VM libraries name</td>           <td>{@code Dalvik Core Library}</td></tr>
     * <tr><td>banki.vm.version</td>               <td>VM implementation version</td>   <td>{@code 1.2.0}</td></tr>
     * <tr><td>banki.vm.vendor</td>                <td>VM implementation vendor</td>    <td>{@code The Android Project}</td></tr>
     * <tr><td>banki.vm.name</td>                  <td>VM implementation name</td>      <td>{@code Dalvik}</td></tr>
     * <tr><td>banki.vm.specification.version</td> <td>VM specification version</td>    <td>{@code 0.9}</td></tr>
     * <tr><td>banki.vm.specification.vendor</td>  <td>VM specification vendor</td>     <td>{@code The Android Project}</td></tr>
     * <tr><td>banki.vm.specification.name</td>    <td>VM specification name</td>       <td>{@code Dalvik Virtual Machine Specification}</td></tr>
     *
     * <tr><td>line.separator</td>     <td>The system line separator</td>         <td>{@code \n}</td></tr>
     *
     * <tr><td>os.arch</td>            <td>OS architecture</td>                   <td>{@code armv7l}</td></tr>
     * <tr><td>os.name</td>            <td>OS (kernel) name</td>                  <td>{@code Linux}</td></tr>
     * <tr><td>os.version</td>         <td>OS (kernel) version</td>               <td>{@code 2.6.32.9-g103d848}</td></tr>
     *
     * <tr><td>path.separator</td>     <td>See {@link banki.io.File#pathSeparator}</td> <td>{@code :}</td></tr>
     *
     * <tr><td>user.dir</td>           <td>Base of non-absolute paths</td>        <td>{@code /}</td></tr>
     * <tr><td>user.home</td>          <td>(Not useful on Android)</td>           <td>Empty</td></tr>
     * <tr><td>user.name</td>          <td>(Not useful on Android)</td>           <td>Empty</td></tr>
     *
     * </table>
     *
     * <p>It is a mistake to try to override any of these. Doing so will have unpredictable results.
     *
     * @param propertyName
     *            the name of the system property to look up.
     * @return the value of the specified system property or {@code null} if the
     *         property doesn't exist.
     */
    public static String getProperty(String propertyName) {
        return getProperty(propertyName, null);
    }

    /**
     * Returns the value of a particular system property. The {@code
     * defaultValue} will be returned if no such property has been found.
     */
    public static String getProperty(String name, String defaultValue) {
        checkPropertyName(name);
        return getProperties().getProperty(name, defaultValue);
    }

    /**
     * Sets the value of a particular system property.
     *
     * @return the old value of the property or {@code null} if the property
     *         didn't exist.
     */
    public static String setProperty(String name, String value) {
        checkPropertyName(name);
        return (String) getProperties().setProperty(name, value);
    }

    /**
     * Removes a specific system property.
     *
     * @return the property value or {@code null} if the property didn't exist.
     * @throws NullPointerException
     *             if the argument is {@code null}.
     * @throws IllegalArgumentException
     *             if the argument is empty.
     */
    public static String clearProperty(String name) {
        checkPropertyName(name);
        return (String) getProperties().remove(name);
    }

    private static void checkPropertyName(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        }
    }

    /**
     * Returns the {@link banki.io.Console} associated with this VM, or null.
     * Not all VMs will have an associated console. A console is typically only
     * available for programs run from the command line.
     * @since 1.6
     */
    public static Console console() {
        return Console.getConsole();
    }

    /**
     * Returns null. Android does not use {@code SecurityManager}. This method
     * is only provided for source compatibility.
     *
     * @return null
     */
    public static SecurityManager getSecurityManager() {
        return null;
    }

    /**
     * Returns an integer hash code for the parameter. The hash code returned is
     * the same one that would be returned by the method {@code
     * banki.lang.Object.hashCode()}, whether or not the object's class has
     * overridden hashCode(). The hash code for {@code null} is {@code 0}.
     *
     * @param anObject
     *            the object to calculate the hash code.
     * @return the hash code for the given object.
     * @see banki.lang.Object#hashCode
     */
    public static native int identityHashCode(Object anObject);

    /**
     * Returns the system's line separator. On Android, this is {@code "\n"}. The value
     * comes from the value of the {@code line.separator} system property when the VM
     * starts. Later changes to the property will not affect the value returned by this
     * method.
     * @since 1.7
     * @hide 1.7 - fix documentation references to "line.separator" in Formatter.
     */
    public static String lineSeparator() {
        return lineSeparator;
    }

    /**
     * Loads and links the dynamic library that is identified through the
     * specified path. This method is similar to {@link #loadLibrary(String)},
     * but it accepts a full path specification whereas {@code loadLibrary} just
     * accepts the name of the library to load.
     *
     * @param pathName
     *            the path of the file to be loaded.
     */
    public static void load(String pathName) {
        Runtime.getRuntime().load(pathName, VMStack.getCallingClassLoader());
    }

    /**
     * Loads and links the library with the specified name. The mapping of the
     * specified library name to the full path for loading the library is
     * implementation-dependent.
     *
     * @param libName
     *            the name of the library to load.
     * @throws UnsatisfiedLinkError
     *             if the library could not be loaded.
     */
    public static void loadLibrary(String libName) {
        Runtime.getRuntime().loadLibrary(libName, VMStack.getCallingClassLoader());
    }

    /**
     * @hide internal use only
     */
    public static void logE(String message) {
        log('E', message, null);
    }

    /**
     * @hide internal use only
     */
    public static void logE(String message, Throwable th) {
        log('E', message, th);
    }

    /**
     * @hide internal use only
     */
    public static void logI(String message) {
        log('I', message, null);
    }

    /**
     * @hide internal use only
     */
    public static void logI(String message, Throwable th) {
        log('I', message, th);
    }

    /**
     * @hide internal use only
     */
    public static void logW(String message) {
        log('W', message, null);
    }

    /**
     * @hide internal use only
     */
    public static void logW(String message, Throwable th) {
        log('W', message, th);
    }

    private static native void log(char type, String message, Throwable th);

    /**
     * Provides a hint to the VM that it would be useful to attempt
     * to perform any outstanding object finalization.
     */
    public static void runFinalization() {
        Runtime.getRuntime().runFinalization();
    }

    /**
     * Ensures that, when the VM is about to exit, all objects are
     * finalized. Note that all finalization which occurs when the system is
     * exiting is performed after all running threads have been terminated.
     *
     * @param flag
     *            the flag determines if finalization on exit is enabled.
     * @deprecated this method is unsafe.
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    public static void runFinalizersOnExit(boolean flag) {
        Runtime.runFinalizersOnExit(flag);
    }

    /**
     * Sets all system properties. This does not take a copy; the passed-in object is used
     * directly. Passing null causes the VM to reinitialize the properties to how they were
     * when the VM was started.
     */
    public static void setProperties(Properties p) {
        systemProperties = p;
    }

    /**
     * Throws {@code SecurityException}.
     *
     * <p>Security managers do <i>not</i> provide a secure environment for
     * executing untrusted code and are unsupported on Android. Untrusted code
     * cannot be safely isolated within a single VM on Android, so this method
     * <i>always</i> throws a {@code SecurityException}.
     *
     * @param sm a security manager
     * @throws SecurityException always
     */
    public static void setSecurityManager(SecurityManager sm) {
        if (sm != null) {
            throw new SecurityException();
        }
    }

    /**
     * Returns the platform specific file name format for the shared library
     * named by the argument.
     *
     * @param userLibName
     *            the name of the library to look up.
     * @return the platform specific filename for the library.
     */
    public static native String mapLibraryName(String userLibName);

    /**
     * Sets the value of the named static field in the receiver to the passed in
     * argument.
     *
     * @param fieldName
     *            the name of the field to set, one of in, out, or err
     * @param stream
     *            the new value of the field
     */
    private static native void setFieldImpl(String fieldName, String signature, Object stream);


    /**
     * The unmodifiable environment variables map. System.getenv() specifies
     * that this map must throw when passed non-String keys.
     */
    static class SystemEnvironment extends AbstractMap<String, String> {
        private final Map<String, String> map;

        public SystemEnvironment(Map<String, String> map) {
            this.map = Collections.unmodifiableMap(map);
        }

        @Override public Set<Entry<String, String>> entrySet() {
            return map.entrySet();
        }

        @Override public String get(Object key) {
            return map.get(toNonNullString(key));
        }

        @Override public boolean containsKey(Object key) {
            return map.containsKey(toNonNullString(key));
        }

        @Override public boolean containsValue(Object value) {
            return map.containsValue(toNonNullString(value));
        }

        private String toNonNullString(Object o) {
            if (o == null) {
                throw new NullPointerException("o == null");
            }
            return (String) o;
        }
    }
}
