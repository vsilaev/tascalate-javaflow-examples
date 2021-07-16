package org.apache.commons.javaflow.examples.skynet;

import org.apache.commons.javaflow.api.*;

import java.security.SecureRandom;
import java.util.Random;

// https://github.com/vsilaev/tascalate-javaflow/blob/master/net.tascalate.javaflow.examples/src/main/java/org/apache/commons/javaflow/examples/again/Execution.java
public class Example {

    public static void main(String[] argv) throws Exception {
        Continuation cc = Continuation.startWith(new Execution());
        System.out.println("In main loop after prologue");
        cc = cc.resume(); // will loop in Execution due to call to again()
        System.out.println("In main done");
        System.out.println("===");
    }
}

class Execution implements Runnable {

    public @continuable void run() {
        Random rnd = new SecureRandom();
        try {
            Continuation.suspend();
            // LOOP_START
            System.out.println("resumed");

            int r = rnd.nextInt(5);
            if (r != 0) {
                System.out.println("do it again, r=" + r);
                Continuation.again(); // like "GOTO LOOP_START", first statement
                // after closest suspend()
            }

            System.out.println("done");
        } finally {
            // This will be called only once
            System.out.println("Finally is called");
        }
    }
}
