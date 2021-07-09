/**
 * ﻿Copyright 2013-2021 Valery Silaev (http://vsilaev.com)
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
package org.apache.commons.javaflow.examples.lambdas;

import org.apache.commons.javaflow.api.Continuation;

import net.tascalate.javaflow.CloseableIterator;
import net.tascalate.javaflow.Continuations;

public class LambdasExampleMinimalIterator {

    public static void main(String[] argv) throws Exception {

        // Create suspended continuation
        Continuation cc = Continuations.create(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    System.out.println("Exe before suspend");
                    Continuation.suspend(i);
                    System.out.println("Exe after suspend");
                }
            } finally {
                System.out.println("Continuation gracefully exited");
            }
        }, true); // Single-shot for performance, single resume is used here
        
        // use try-with-resources to close the coIterator 
        // (and hence terminate underlying continuation) 
        // in case of early exit
        try (CloseableIterator<Integer> i = Continuations.iteratorOf(cc)) {
            int c = 0;
            while (i.hasNext()) {
                System.out.println("Interrupted " + i.next());
                c++;
                if (c == 3) {
                    // Emulate early exit
                    break;
                }
            }
        }

        System.out.println("===");
    }

}
