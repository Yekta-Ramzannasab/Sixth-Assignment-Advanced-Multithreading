package MonteCarloPI;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MonteCarloPi {

    static final long NUM_POINTS = 50_000_000L; //The number of points generated to estimate π.
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors(); //The number of threads the system can run simultaneously (based on the number of processor cores).

    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        // Without Threads
        System.out.println("Single threaded calculation started: ");
        long startTime = System.nanoTime();
        double piWithoutThreads = estimatePiWithoutThreads(NUM_POINTS);
        long endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (single thread): " + piWithoutThreads);
        System.out.println("Time taken (single threads): " + (endTime - startTime) / 1_000_000 + " ms");

        // With Threads
        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)\n",NUM_THREADS);
        startTime = System.nanoTime();
        double piWithThreads = estimatePiWithThreads(NUM_POINTS, NUM_THREADS);
        endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (Multi-threaded): " + piWithThreads);
        System.out.println("Time taken (Multi-threaded): " + (endTime - startTime) / 1_000_000 + " ms");


    }

//    Suppose a circle with radius 1 is inside a 2x2 square.
//    Circle: center at point (0,0), radius 1 → its equation becomes: x2+y2≤1
//    Square: its vertices are from (-1, -1) to (1, 1) → its total area = 4
//    If we scatter many random points inside this square, a percentage of them will fall inside the circle.
//    Now if we get this ratio by chance (i.e. by dividing the number of points inside the circle by the total points)

    // Monte Carlo Pi Approximation without threads
    public static double estimatePiWithoutThreads(long numPoints)
    { // TODO: Implement this method to calculate Pi using a single thread
        Random random = new Random();
        long insideCircle = 0;

        for (long i = 0; i < numPoints; i++) {
            double x = random.nextDouble() * 2 - 1; //Random number between 0.0 and 1.0 and multiply 2 then minus 1 gives us number between -1 and 1;
            double y = random.nextDouble() * 2 - 1;
            if (x * x + y * y <= 1) {
                insideCircle++;
            }
        }
        return 4.0 * insideCircle / numPoints;
    }

    // Monte Carlo Pi Approximation with threads
    public static double estimatePiWithThreads(long numPoints, int numThreads) throws InterruptedException, ExecutionException
    {
        // TODO: Implement this method to calculate Pi using multiple threads

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicLong totalInsideCircle = new AtomicLong(0);
        long pointsPerThread = numPoints / numThreads; //Each thread is going to check a certain number of points. So we divide the number of points between the threads.

        // Create and submit tasks
        for (int i = 0; i < numThreads; i++) { //for any thread define a task and send it to the executor
            final long threadPoints = (i == numThreads - 1) ?
                    (numPoints - pointsPerThread * (numThreads - 1)) : pointsPerThread;


            executor.submit(() -> {
                Random random = new Random();
                long insideCircle = 0;

                for (long j = 0; j < threadPoints; j++) {
                    double x = random.nextDouble() * 2 - 1;
                    double y = random.nextDouble() * 2 - 1;
                    if (x * x + y * y <= 1) {
                        insideCircle++;
                    }
                }

                totalInsideCircle.addAndGet(insideCircle);
            });
        }

        // Shutdown and wait for completion
        executor.shutdown(); // Do not accept more tasks but finishes undone ones
        executor.awaitTermination(1, TimeUnit.MINUTES);

        return 4.0 * totalInsideCircle.get() / numPoints;
    }
}