package org.apache.commons.javaflow.examples.retrolambdas;

import org.apache.commons.javaflow.api.continuable;

import static net.tascalate.javaflow.util.Continuations.yield;

import net.tascalate.javaflow.util.CloseableIterator;
import net.tascalate.javaflow.util.Continuations;

public class RetroLambdas {

    public static void main(String[] argv) {
        Continuations
            .streamOf(RetroLambdas::produceStrings)
            .map(s -> "Produced by method ref: " + s)
            .forEach(System.out::println);
        
        try (CloseableIterator<String> generator = Continuations.iteratorOf(() -> {
            yield( complexCalculation("1") );
            yield( complexCalculation("2") );
            yield( complexCalculation("3") );
        })) {
            while (generator.hasNext()) {
                System.out.println("Produced by anonymous lambda: " + generator.next());
            }
        } 
    }
    
    static @continuable void produceStrings() {
        String v;
        
        v = complexCalculation("A");
        yield(v);
        
        v = complexCalculation("B");
        yield(v);
        
        v = complexCalculation("C");
        yield(v);
    }
    
    private static String complexCalculation(String param) {
        System.out.println("Calculate " + param + ": START");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
            
        }
        System.out.println("Calculate " + param + ": FINISH");
        return param;
    }
    
}
