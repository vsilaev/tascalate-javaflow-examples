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
package org.apache.commons.javaflow.examples.trampoline;

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.api.continuable;
import org.apache.commons.javaflow.examples.modular_package.ZClass;

public class Execution implements Runnable {

    @Override
    public @continuable void run() {
        ZClass z = new ZClass();
        System.out.println("Object from opened modular package: " + z + ", ClassLoader = " + z.getClass().getClassLoader() + ", Module = " + z.getClass().getModule());
        System.out.println("Running inside " + Execution.class + ", ClassLoader = " + Execution.class.getClassLoader() + ", Module = " + Execution.class.getModule());
        for (long i = 0; i <= 5; i++) {
            System.out.println("Exe before suspend");
            Object fromCaller = Continuation.suspend(i);
            System.out.println("Exe after suspend: " + fromCaller);
        }
    }
}