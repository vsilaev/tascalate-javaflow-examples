package org.apache.commons.javaflow.examples.skynet;

// needs to be in this package to have access to `exec`

import org.apache.commons.javaflow.api.*;
import java.util.concurrent.*;
import java.util.Random;

public class Stateloop {

    private static int N = 1000000;
    private static int NRecursive = 1000;

    // The work in this function is taken from:
    // https://github.com/puniverse/quasar/blob/master/quasar-core/src/jmh/java/co/paralleluniverse/fibers/FiberOverheadJMHBenchmark.java
    private static long rands[] = new long[(N + 1) * 4];

    static {
        Random rnd = ThreadLocalRandom.current();
        for (int i = 0; i < rands.length; i++)
            rands[i] = rnd.nextLong();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new Stateloop().runLoopNever();
        new Stateloop().runRecursiveOften();
//        new Stateloop().runRecursiveOnce();
    }

    @continuable
    private long recursiveOnce(int r)  {
        long a = rands[(r << 2)];
        long b = rands[(r << 2) + 1];
        long c = rands[(r << 2) + 3];
        long res;
        if (r > 0)
            res = recursiveOnce(r - 1);
        else {
            Continuation.suspend();
            res = rands[(r << 2) + 4];
        }
        return a + b + c + res;
    }

    @continuable
    private long recursiveOften(int r)  {
        long a = rands[(r << 2)];
        long b = rands[(r << 2) + 1];
        long c = rands[(r << 2) + 3];
        long res;
        if (r > 0) {
            Continuation.suspend();
            res = recursiveOften(r - 1);
        } else {
            res = rands[(r << 2) + 4];
        }
        return a + b + c + res;
    }

    @continuable
    private long loopNever(int n) {

        int r = n;
        long res = 0L;

        while (nopark() && r > 0) {
            nopark(); // for r = get()
            long a = rands[(r << 2)];
            long b = rands[(r << 2) + 1];
            long c = rands[(r << 2) + 3];
            res = a + b + c + res;
            nopark(); // for put(r-1)
            r--;
        }
        return res;
    }

    public void runLoopNever() {
        Continuation c = Continuation.startWith(new Runnable() {
            @continuable public void run() {
                Continuation.suspend(loopNever(N));
            }
        });
        c.value();
        c.resume();
    }

    public void runRecursiveOnce() {
        Continuation c = Continuation.startWith(new Runnable() {
            @continuable public void run() {
                @SuppressWarnings("unused")
                long res = recursiveOnce(NRecursive);
            }
        });
        c.resume();
    }

    public void runRecursiveOften() {
        Continuation c = Continuation.startWith(new Runnable() {
            @continuable public void run() {
                @SuppressWarnings("unused")
                long res = recursiveOften(NRecursive);
            }
        });
        for (int i=0; i < NRecursive; i++) { c = c.resume(); }
    }

    static int dummy = 2;

    // never actually suspends, but forces bytecode instrumentation to instrument
    // in order to measure the instrumentation overhead
    @continuable
    private static boolean nopark() {
        if (dummy > 1) { return true; } else { Continuation.suspend(); return true; }
    }
}