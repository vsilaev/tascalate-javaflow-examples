package org.apache.commons.javaflow.examples.skynet;

import org.apache.commons.javaflow.api.*;

public class SkynetSuspend implements Runnable {
    public static void main(String[] argv) {
        // Warm-up
        for (int i = 0; i < 100; i++) {
            runOnce();
        }
        
        Long before = System.currentTimeMillis();
        Continuation c = null;
        for (int i = 0; i < 500; i++) {
            c = runOnce();
        }
        Long after = System.currentTimeMillis();
        System.out.println(c.value());
        System.out.println((after - before) / 500 + "ms");
    }
    
    static Continuation runOnce() {
        Continuation c = Continuation.startWith(new FiberSuspend(0, 1000000, 10));
        c.value();
        return c;
    }

    public static void skynet(int n) {
        Continuation c = Continuation.startWith(new FiberSuspend(0, n, 10));
        c.value();
    }

    public void run() {
        Continuation c = Continuation.startWith(new FiberSuspend(0, 1000000, 10));
        c.value();
    }
}

class FiberSuspend implements Runnable {
    final long num; final int size; final int div;

    FiberSuspend(long num, int size, int div) { 
        this.num = num; this.size = size; this.div = div;
    }

    public @continuable void run() {
        if (size == 1) {
            Continuation.suspend(num);
            return;
        }

        long sum = 0;

        for (int i = 0; i < div; i++) {
            long subNum = num + i * (size / div);
            Continuation c = Continuation.startWith(new FiberSuspend(subNum, size / div, div)); 
            long res = (Long)c.value();
            c.resume();
            sum += res; 
        }
        Continuation.suspend(sum);
        return;
    }

    public String toString() { return "num = " + num; }
}