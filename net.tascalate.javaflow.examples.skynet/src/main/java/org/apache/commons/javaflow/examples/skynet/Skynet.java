package org.apache.commons.javaflow.examples.skynet;

import org.apache.commons.javaflow.api.*;

public class Skynet implements Runnable {

    public static void main(String[] argv) {
        Long before = System.currentTimeMillis();
        System.out.println(skynet(1000000));
        Long after = System.currentTimeMillis();
        System.out.println(after - before + "ms");
    }

    public static long skynet(int n) {
        Fiber f = new Fiber(0, n, 10);
        @SuppressWarnings("unused")
        Continuation c = Continuation.startWith(f);
        return f.res;
    }

    public void run() {
        skynet(1000000);
    }
}

class Fiber implements Runnable {
    final long num; final int size; final int div;

    Long res = 0L; // boxed, for better comparison

    Fiber(long num, int size, int div) {
        this.num = num; this.size = size; this.div = div;
    }

    public @continuable void run() {
        if (size == 1) {
            res = num;
            return;
        }

        long sum = 0L;

        for (int i = 0; i < div; i++) {
            long subNum = num + i * (size / div);
            Fiber f = new Fiber(subNum, size / div, div);
            @SuppressWarnings("unused")
            Continuation c = Continuation.startWith(f);
            sum += f.res;
        }
        res = sum;
        return;
    }

    public String toString() { return "num = " + num; }
}