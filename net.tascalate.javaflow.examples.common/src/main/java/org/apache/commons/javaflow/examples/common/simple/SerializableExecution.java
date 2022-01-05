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
package org.apache.commons.javaflow.examples.common.simple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.api.continuable;

public class SerializableExecution implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public @continuable void run() {
        for (long i = 1; i <= 5; i++) {
            System.out.println("Exe before suspend");
            
            // FileOutputStream is not serializable, but var is out of scope
            // when suspending, so it doesn't causes problems
            
            // Seems that without artificial "if" the code works only with EJC compiler + Agent
            // Otherwise in bytecode "oos" variable scope is a whole "for" loop body
            if (guardSerialization()) {
                try {
                FileOutputStream oos = new FileOutputStream(File.createTempFile("tascalate", ".cfz"));
                    try {
                        oos.toString();
                    } finally {
                        oos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            Object fromCaller = Continuation.suspend(i);
            System.out.println("Exe after suspend: " + fromCaller);
        }
        
    }
    
    private static boolean guardSerialization() {
        return true;
    }
}
