package org.apache.commons.javaflow.examples.trampoline;

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.api.continuable;
import org.apache.commons.javaflow.providers.asmx.AsmxResourceTransformationFactory;
import org.apache.commons.javaflow.spi.ResourceTransformationFactory;
import org.apache.commons.javaflow.tools.runtime.ApplicationLoader;

public class SelfStartingApplication {
    
    public @continuable static void main(String[] argv) {
        ResourceTransformationFactory factory = new AsmxResourceTransformationFactory();
        if (ApplicationLoader.trampoline(factory, SelfStartingApplication.class, argv)) {
            System.out.println("Application was forked in continuations-enabled mode, this method is exiting");
            return;
        } else {
            System.out.println("Resuming application in continuations-enabled mode, this method is continuing...");
        }
        
        String[] strings = {"A", "B", "C"};
        for (Continuation cc = Continuation.startWith(new Execution()); null != cc;) {
            final int valueFromContinuation = Number.class.cast(cc.value()).intValue();
            System.out.println("Interrupted " + valueFromContinuation);
            // Let's continuation resume
            cc = cc.resume(strings[valueFromContinuation % strings.length]);
        }

        System.out.println("ALL DONE");
    }
}
