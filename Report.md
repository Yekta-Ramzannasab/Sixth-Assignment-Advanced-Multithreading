# Report on AtomicInteger and Atomic Variables in Java

## 1. What output do you get from the program? Why?

Output:

Atomic Counter: 2000000
Normal Counter: 1782354

Explanation:

The Atomic Counter correctly reaches 2000000 because it uses AtomicInteger, which is thread-safe and ensures that all increments are executed atomically across threads.

The Normal Counter does not reach 2000000 because it uses a regular int and is not thread-safe. Multiple threads updating the counter simultaneously lead to race conditions, causing missed updates.



---

## 2. What is the purpose of AtomicInteger in this code?

The AtomicInteger class is used to perform atomic operations on an integer value without using locks. Its main purposes are:

Ensuring thread-safety without explicit synchronization.

Avoiding race conditions when multiple threads increment or read the same variable.

Enabling lock-free algorithms, which are generally faster and more scalable.


For example:

private static AtomicInteger atomicCounter = new AtomicInteger(0);
atomicCounter.incrementAndGet();


---

## 3. What thread-safety guarantees does atomicCounter.incrementAndGet() provide?

incrementAndGet() performs the increment operation atomically, meaning:

No two threads can interfere with each other during the increment.

The method uses CAS (Compare-And-Swap) internally to guarantee atomicity.

It's lock-free, thus avoiding the performance costs of traditional locks while still ensuring correct results even under concurrent access.



---

## 4. In which situations would using a lock be a better choice than an atomic variable?

While atomic variables are efficient for simple operations, using locks may be preferable when:

The critical section is complex, involving multiple operations or resources.

You need compound actions that must be executed together atomically (e.g., updating multiple shared variables at once).

You require blocking behavior or want to use condition variables (wait, notify).


In short: Use atomic variables for simple, fine-grained operations, and use locks for more complex, coarse-grained synchronization.


---

## 5. Besides AtomicInteger, what other data types are available in the java.util.concurrent.atomic package?

The java.util.concurrent.atomic package provides several classes for atomic operations:

AtomicInteger

AtomicLong

AtomicBoolean

AtomicReference<T> – for holding object references atomically

AtomicIntegerArray

AtomicLongArray

AtomicReferenceArray<T> – for holding arrays of atomic references


These classes help avoid synchronization issues and make multi-threaded programming easier and more efficient.

---

# Multi-threaded vs. Single-threaded Performance in Monte Carlo π Estimation

## 1. Was the multi-threaded implementation always faster than the single-threaded one?
No, not always. While multi-threading can significantly speed up computations for large-scale simulations, it does not guarantee better performance in all cases. Several factors influence whether the multi-threaded version is faster.

---

## 2. If not, what factors cause slowdowns in multi-threading?
Several factors can reduce or even negate the performance benefits of multi-threading:

### A. Overhead of Thread Management
- Thread creation/destruction has a cost. If the task is too small, the overhead may outweigh the benefits.
- Context switching between threads consumes CPU resources, reducing efficiency.

### B. Synchronization Overhead (Lock Contention)
- If threads frequently access shared resources (e.g., a counter for points inside the circle), locks (mutexes) introduce delays.
- False sharing (when threads modify variables on the same cache line) can degrade performance.

### C. CPU Core Utilization
- If the system has few CPU cores, multi-threading may not provide much benefit.
- Hyper-threading (SMT) helps but does not double performance per core.

### D. Memory Bottlenecks
- If threads generate and process data faster than RAM can supply, performance plateaus.
- Cache thrashing occurs when threads compete for cache space.

### E. Workload Imbalance
- If one thread gets more work than others, some cores sit idle while others finish.

---

## 3. How to Mitigate These Issues?
### A. Reduce Synchronization Overhead
- Use atomic operations instead of locks where possible.
- Local counters per thread (reduce global updates).
- False sharing fix: Pad or align variables to separate cache lines.

### B. Optimize Thread Count
- Use thread pools (reuse threads instead of recreating them).
- Limit threads to available CPU cores (e.g., std::thread::hardware_concurrency() in C++).

### C. Improve Work Distribution
- Divide work evenly (e.g., assign equal points per thread).
- Dynamic scheduling (let threads grab new work when done).

### D. Memory Access Optimization
- Minimize shared memory access (each thread should work on independent data).
- Batch processing (reduce frequent updates to shared counters).

### E. Benchmark & Tune
- Test with different thread counts (sometimes 2-4 threads outperform 8+ due to overhead).
- Measure scaling efficiency (how much faster it gets with more threads).

---

## Conclusion
Multi-threading usually speeds up Monte Carlo π estimation for large computations, but not always. The key is to:
1. Minimize synchronization (avoid locks where possible).
2. Balance workloads (even distribution of tasks).
3. Optimize thread count (match hardware capabilities).
4. Reduce memory bottlenecks (localize data per thread).

If done correctly, multi-threading can provide near-linear speedups. Otherwise, it may even slow down due to overheads. Proper benchmarking is essential.