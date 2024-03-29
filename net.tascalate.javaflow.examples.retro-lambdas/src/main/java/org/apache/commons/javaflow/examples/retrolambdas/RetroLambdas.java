/**
 * ﻿Copyright 2013-2022 Valery Silaev (http://vsilaev.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.javaflow.examples.retrolambdas;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.api.ccs;
import org.apache.commons.javaflow.api.continuable;

public class RetroLambdas {
    public static void main(String[] argv) throws Exception {
        RetroLambdas demo = new RetroLambdas();
        forEach(
            demo::runExamples, 
            (String s) -> System.out.println("Interrupted " + s)
        );

        System.out.println("===");

    }

    String ref = " ** ";

    private @continuable void runExamples() {

        // @Continuable method references and @Continuable SAM interfaces are
        // supported
        MyContinuableRunnable r1 = this::lambdaDemo;
        r1.run();

        MyContinuableRunnable r2 = () -> {
            System.out.println("ContinuableRunnable by arrow function -- before");
            Continuation.suspend(" ** ContinuableRunnable by arrow function");
            System.out.println("ContinuableRunnable by arrow function -- after");
        };
        r2.run();

        // Lambda reference MUST have annotated CallSite if SAM interface is not
        // @continuable
        // Notice that we still MUST create right interface
        // (MyContinuableRunnable in this case)
        @ccs
        Runnable closure = (MyContinuableRunnable)() -> {
            System.out.println("Plain Runnable Lambda by arrow function -- before");
            Continuation.suspend(" ** Plain Runnable Lambda by arrow function" + ref);
            System.out.println("Plain Runnable Lambda by arrow function -- after");
        };
        closure.run();

        // We can't use stream.forEach as is, but we can provide good enough
        // helpers
        // See org.apache.commons.javaflow.examples.lambdas.ContinuableAdapters
        // for
        // definition of "consumer" & "forEach"
        List<String> listOfStrings = Arrays.asList("AA", "BB", "CC");
        forEach(
            listOfStrings,
            firstAndThen(this::yieldString1, this::yieldString2)
        );

    }

    // Must be annotated to be used as lambda by method-ref
    @continuable void lambdaDemo() {
        System.out.println("Lambda by method reference -- before");
        Continuation.suspend(" ** Lambda by method reference** ");
        System.out.println("Lambda by method reference -- after");
    }

    @continuable void yieldString1(String s) {
        System.out.println("Before yield I " + s);
        Continuation.suspend("yield I " + s);
        System.out.println("After yield I " + s);
    }

    @continuable void yieldString2(String s) {
        System.out.println("Before yield II");
        Continuation.suspend("yield II " + s);
        System.out.println("After yield II");
    }
    
    public static @continuable <T> void forEach(MyContinuableRunnable coroutine, MyContinuableConsumer<? super T> action) {
        forEach(Continuation.startSuspendedWith(coroutine, true), action);
    }
    
    public static @continuable <T> void forEach(Continuation coroutine, MyContinuableConsumer<? super T> action) {
        Continuation cc = coroutine.singleShot();
        try {
            Object param = null;
            while (null != cc) {
                cc = cc.resume(param);
                T value = valueOf(cc);
                param = value;
            }
        } finally {
            if (null != cc) {
                cc.terminate();
            }
        }
    }
    
    public static @continuable <T> void forEach(Iterable<T> iterable, MyContinuableConsumer<? super T> action) {
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.next());
        }
    }
    
    
    /**
     * Returns a composed {@code ContinuableConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code ContinuableConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    public static <T> MyContinuableConsumer<T> firstAndThen(MyContinuableConsumer<? super T> self, MyContinuableConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return new MyContinuableConsumer<T>() {
            public void accept(T t) {
                self.accept(t);
                after.accept(t);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> T valueOf(Continuation continuation) {
        return continuation == null ? null: (T)continuation.value();
    }
}
