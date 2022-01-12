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
package org.apache.commons.javaflow.examples.common.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.api.continuable;
import org.apache.commons.javaflow.core.ContinuableProxy;

public class ContinuableProxyExample {
    
    static interface ContinuableRunnable extends Runnable {
        @continuable void run();
    }
    
    public static void main(String[] args) {
        InvocationHandler continuableHandler = new InvocationHandler() {
            @Override
            public @continuable Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    if ("toString".equals(method.getName())) {
                        return "<PROXY>";
                    }
                    return method.invoke(this, args);
                }
                for (String s : new String[] {"A", "B", "C"}) {
                    Continuation.suspend(s);
                }
                // For some unknown error it's impossible to invoke
                // another continuable object right here
                // but call via own method works (!!!)
                // Pretty insane...
                run("V"); 
                return null;
            }
            
            @continuable void run(String prefix) {
                for (String s : new String[] {"X", "Y", "Z"}) {
                    NestedClass inner = new NestedClass(prefix + s);
                    inner.call();
                }
            }
            
            @Override
            public String toString() {
                return "<HANDLER>";
            }
        };
        
        ContinuableRunnable execution = (ContinuableRunnable) Proxy.newProxyInstance(
                ContinuableProxyExample.class.getClassLoader(), 
                new Class<?>[]{ContinuableRunnable.class, ContinuableProxy.class /* Mandatory marker interface */}, 
                continuableHandler);
        
        System.out.println(execution.getClass().getSuperclass() + " " + (execution instanceof Proxy));
        
        for (Continuation cc = Continuation.startWith(execution); null != cc;) {
            final String valueFromContinuation = String.class.cast(cc.value());
            System.out.println(">>Interrupted " + valueFromContinuation);
            // Let's continuation resume
            cc = cc.resume("processed-" + valueFromContinuation);
        }
    }
}
