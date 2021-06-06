/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

// BEGIN android-note
// omit links to ForkJoinPool, ForkJoinTask, LinkedTransferQueue, PHaser, TransferQueue
// END android-note

/**
 * Utility classes commonly useful in concurrent programming.  This
 * package includes a few small standardized extensible frameworks, as
 * well as some classes that provide useful functionality and are
 * otherwise tedious or difficult to implement.  Here are brief
 * descriptions of the main components.  See also the
 * {@link banki.util.concurrent.locks} and
 * {@link banki.util.concurrent.atomic} packages.
 *
 * <h2>Executors</h2>
 *
 * <b>Interfaces.</b>
 *
 * {@link banki.util.concurrent.Executor} is a simple standardized
 * interface for defining custom thread-like subsystems, including
 * thread pools, asynchronous IO, and lightweight task frameworks.
 * Depending on which concrete Executor class is being used, tasks may
 * execute in a newly created thread, an existing task-execution thread,
 * or the thread calling {@link banki.util.concurrent.Executor#execute
 * execute}, and may execute sequentially or concurrently.
 *
 * {@link banki.util.concurrent.ExecutorService} provides a more
 * complete asynchronous task execution framework.  An
 * ExecutorService manages queuing and scheduling of tasks,
 * and allows controlled shutdown.
 *
 * The {@link banki.util.concurrent.ScheduledExecutorService}
 * subinterface and associated interfaces add support for
 * delayed and periodic task execution.  ExecutorServices
 * provide methods arranging asynchronous execution of any
 * function expressed as {@link banki.util.concurrent.Callable},
 * the result-bearing analog of {@link banki.lang.Runnable}.
 *
 * A {@link banki.util.concurrent.Future} returns the results of
 * a function, allows determination of whether execution has
 * completed, and provides a means to cancel execution.
 *
 * A {@link banki.util.concurrent.RunnableFuture} is a {@code Future}
 * that possesses a {@code run} method that upon execution,
 * sets its results.
 *
 * <p>
 *
 * <b>Implementations.</b>
 *
 * Classes {@link banki.util.concurrent.ThreadPoolExecutor} and
 * {@link banki.util.concurrent.ScheduledThreadPoolExecutor}
 * provide tunable, flexible thread pools.
 *
 * The {@link banki.util.concurrent.Executors} class provides
 * factory methods for the most common kinds and configurations
 * of Executors, as well as a few utility methods for using
 * them.  Other utilities based on {@code Executors} include the
 * concrete class {@link banki.util.concurrent.FutureTask}
 * providing a common extensible implementation of Futures, and
 * {@link banki.util.concurrent.ExecutorCompletionService}, that
 * assists in coordinating the processing of groups of
 * asynchronous tasks.
 *
 * <h2>Queues</h2>
 *
 * The {@link banki.util.concurrent.ConcurrentLinkedQueue} class
 * supplies an efficient scalable thread-safe non-blocking FIFO
 * queue.
 *
 * <p>Five implementations in {@code banki.util.concurrent} support
 * the extended {@link banki.util.concurrent.BlockingQueue}
 * interface, that defines blocking versions of put and take:
 * {@link banki.util.concurrent.LinkedBlockingQueue},
 * {@link banki.util.concurrent.ArrayBlockingQueue},
 * {@link banki.util.concurrent.SynchronousQueue},
 * {@link banki.util.concurrent.PriorityBlockingQueue}, and
 * {@link banki.util.concurrent.DelayQueue}.
 * The different classes cover the most common usage contexts
 * for producer-consumer, messaging, parallel tasking, and
 * related concurrent designs.
 *
 * <p>The {@link banki.util.concurrent.BlockingDeque} interface
 * extends {@code BlockingQueue} to support both FIFO and LIFO
 * (stack-based) operations.
 * Class {@link banki.util.concurrent.LinkedBlockingDeque}
 * provides an implementation.
 *
 * <h2>Timing</h2>
 *
 * The {@link banki.util.concurrent.TimeUnit} class provides
 * multiple granularities (including nanoseconds) for
 * specifying and controlling time-out based operations.  Most
 * classes in the package contain operations based on time-outs
 * in addition to indefinite waits.  In all cases that
 * time-outs are used, the time-out specifies the minimum time
 * that the method should wait before indicating that it
 * timed-out.  Implementations make a &quot;best effort&quot;
 * to detect time-outs as soon as possible after they occur.
 * However, an indefinite amount of time may elapse between a
 * time-out being detected and a thread actually executing
 * again after that time-out.  All methods that accept timeout
 * parameters treat values less than or equal to zero to mean
 * not to wait at all.  To wait "forever", you can use a value
 * of {@code Long.MAX_VALUE}.
 *
 * <h2>Synchronizers</h2>
 *
 * Four classes aid common special-purpose synchronization idioms.
 * <ul>
 *
 * <li>{@link banki.util.concurrent.Semaphore} is a classic concurrency tool.
 *
 * <li>{@link banki.util.concurrent.CountDownLatch} is a very simple yet
 * very common utility for blocking until a given number of signals,
 * events, or conditions hold.
 *
 * <li>A {@link banki.util.concurrent.CyclicBarrier} is a resettable
 * multiway synchronization point useful in some styles of parallel
 * programming.
 *
 * <li>An {@link banki.util.concurrent.Exchanger} allows two threads to
 * exchange objects at a rendezvous point, and is useful in several
 * pipeline designs.
 *
 * </ul>
 *
 * <h2>Concurrent Collections</h2>
 *
 * Besides Queues, this package supplies Collection implementations
 * designed for use in multithreaded contexts:
 * {@link banki.util.concurrent.ConcurrentHashMap},
 * {@link banki.util.concurrent.ConcurrentSkipListMap},
 * {@link banki.util.concurrent.ConcurrentSkipListSet},
 * {@link banki.util.concurrent.CopyOnWriteArrayList}, and
 * {@link banki.util.concurrent.CopyOnWriteArraySet}.
 * When many threads are expected to access a given collection, a
 * {@code ConcurrentHashMap} is normally preferable to a synchronized
 * {@code HashMap}, and a {@code ConcurrentSkipListMap} is normally
 * preferable to a synchronized {@code TreeMap}.
 * A {@code CopyOnWriteArrayList} is preferable to a synchronized
 * {@code ArrayList} when the expected number of reads and traversals
 * greatly outnumber the number of updates to a list.
 *
 * <p>The "Concurrent" prefix used with some classes in this package
 * is a shorthand indicating several differences from similar
 * "synchronized" classes.  For example {@code banki.util.Hashtable} and
 * {@code Collections.synchronizedMap(new HashMap())} are
 * synchronized.  But {@link
 * banki.util.concurrent.ConcurrentHashMap} is "concurrent".  A
 * concurrent collection is thread-safe, but not governed by a
 * single exclusion lock.  In the particular case of
 * ConcurrentHashMap, it safely permits any number of
 * concurrent reads as well as a tunable number of concurrent
 * writes.  "Synchronized" classes can be useful when you need
 * to prevent all access to a collection via a single lock, at
 * the expense of poorer scalability.  In other cases in which
 * multiple threads are expected to access a common collection,
 * "concurrent" versions are normally preferable.  And
 * unsynchronized collections are preferable when either
 * collections are unshared, or are accessible only when
 * holding other locks.
 *
 * <p>Most concurrent Collection implementations (including most
 * Queues) also differ from the usual banki.util conventions in that
 * their Iterators provide <em>weakly consistent</em> rather than
 * fast-fail traversal.  A weakly consistent iterator is thread-safe,
 * but does not necessarily freeze the collection while iterating, so
 * it may (or may not) reflect any updates since the iterator was
 * created.
 *
 * <h2><a name="MemoryVisibility">Memory Consistency Properties</a></h2>
 *
 * <a href="http://banki.sun.com/docs/books/jls/third_edition/html/memory.html">
 * Chapter 17 of the Java Language Specification</a> defines the
 * <i>happens-before</i> relation on memory operations such as reads and
 * writes of shared variables.  The results of a write by one thread are
 * guaranteed to be visible to a read by another thread only if the write
 * operation <i>happens-before</i> the read operation.  The
 * {@code synchronized} and {@code volatile} constructs, as well as the
 * {@code Thread.start()} and {@code Thread.join()} methods, can form
 * <i>happens-before</i> relationships.  In particular:
 *
 * <ul>
 *   <li>Each action in a thread <i>happens-before</i> every action in that
 *   thread that comes later in the program's order.
 *
 *   <li>An unlock ({@code synchronized} block or method exit) of a
 *   monitor <i>happens-before</i> every subsequent lock ({@code synchronized}
 *   block or method entry) of that same monitor.  And because
 *   the <i>happens-before</i> relation is transitive, all actions
 *   of a thread prior to unlocking <i>happen-before</i> all actions
 *   subsequent to any thread locking that monitor.
 *
 *   <li>A write to a {@code volatile} field <i>happens-before</i> every
 *   subsequent read of that same field.  Writes and reads of
 *   {@code volatile} fields have similar memory consistency effects
 *   as entering and exiting monitors, but do <em>not</em> entail
 *   mutual exclusion locking.
 *
 *   <li>A call to {@code start} on a thread <i>happens-before</i> any
 *   action in the started thread.
 *
 *   <li>All actions in a thread <i>happen-before</i> any other thread
 *   successfully returns from a {@code join} on that thread.
 *
 * </ul>
 *
 *
 * The methods of all classes in {@code banki.util.concurrent} and its
 * subpackages extend these guarantees to higher-level
 * synchronization.  In particular:
 *
 * <ul>
 *
 *   <li>Actions in a thread prior to placing an object into any concurrent
 *   collection <i>happen-before</i> actions subsequent to the access or
 *   removal of that element from the collection in another thread.
 *
 *   <li>Actions in a thread prior to the submission of a {@code Runnable}
 *   to an {@code Executor} <i>happen-before</i> its execution begins.
 *   Similarly for {@code Callables} submitted to an {@code ExecutorService}.
 *
 *   <li>Actions taken by the asynchronous computation represented by a
 *   {@code Future} <i>happen-before</i> actions subsequent to the
 *   retrieval of the result via {@code Future.get()} in another thread.
 *
 *   <li>Actions prior to "releasing" synchronizer methods such as
 *   {@code Lock.unlock}, {@code Semaphore.release}, and
 *   {@code CountDownLatch.countDown} <i>happen-before</i> actions
 *   subsequent to a successful "acquiring" method such as
 *   {@code Lock.lock}, {@code Semaphore.acquire},
 *   {@code Condition.await}, and {@code CountDownLatch.await} on the
 *   same synchronizer object in another thread.
 *
 *   <li>For each pair of threads that successfully exchange objects via
 *   an {@code Exchanger}, actions prior to the {@code exchange()}
 *   in each thread <i>happen-before</i> those subsequent to the
 *   corresponding {@code exchange()} in another thread.
 *
 *   <li>Actions prior to calling {@code CyclicBarrier.await} and
 *   {@code Phaser.awaitAdvance} (as well as its variants)
 *   <i>happen-before</i> actions performed by the barrier action, and
 *   actions performed by the barrier action <i>happen-before</i> actions
 *   subsequent to a successful return from the corresponding {@code await}
 *   in other threads.
 *
 * </ul>
 *
 * @since 1.5
 */
package banki.util.concurrent;
