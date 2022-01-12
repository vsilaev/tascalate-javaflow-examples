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
package org.apache.commons.javaflow.examples.cglib;

import java.lang.reflect.Method;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.api.continuable;
import org.apache.commons.javaflow.core.ContinuableProxy;
import org.apache.commons.javaflow.core.CustomContinuableProxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;
import net.sf.cglib.proxy.Proxy;

public class CgLibExample {

    static class Inner {
        @continuable void call(String s) {
            System.out.println("INNER BEFORE:: " + s);
            Continuation.suspend(111);
            System.out.println("INNER AFTER:: " + s);
        }
    }
    
    public static void main(String[] argv) throws Exception {
        testPassThrougProxy();
        testContinuableCglibProxy();
        testContinuableCustomProxy();
    }
    
    
    public static void testPassThrougProxy() throws Exception {
        System.out.println("==== PASS-THROUGH PROXY ====");
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(new URLClassLoader(new URL[0]));
        enhancer.setSuperclass(Execution.class);
        enhancer.setCallbackFilter(new CallbackFilter() {
            @Override
            public int accept(Method method) {
                return "run".equals(method.getName()) ? 1 : 0;
            }
        });
        enhancer.setCallbacks(new Callback[]{NoOp.INSTANCE, new MethodInterceptor() {
            @Override
            public Object intercept(Object obj,  java.lang.reflect.Method method, Object[] args, MethodProxy proxy) throws Throwable {
                System.out.println(">>> Entering " + method);
                // Call to continuable method in "pass-through" fashion
                // It works as ordinal, no special handling necessary
                Object result = proxy.invokeSuper(obj, args);
                System.out.println("<<< Exiting " + method);
                return result;
            }
  
        }});
        enhancer.setUseFactory(false);
        Execution proxy = (Execution) enhancer.create();

        String[] strings = {"A", "B", "C"};
        for (Continuation cc = Continuation.startWith(proxy); null != cc;) {
            final int valueFromContinuation = Number.class.cast(cc.value()).intValue();
            System.out.println("Interrupted " + valueFromContinuation);
            // Let's continuation resume
            cc = cc.resume(strings[valueFromContinuation % strings.length]);
        }
        System.out.println("ALL DONE");        
    }
    
    public static void testContinuableCglibProxy() throws Exception {
        System.out.println("==== STANDARD  CONTINUABLE CGLIB PROXY ====");
        final MyInterface proxy = (MyInterface)Proxy.newProxyInstance(
            new URLClassLoader(new URL[0]),
            new Class[]{ MyInterface.class, ContinuableProxy.class /* Marker */ },
            new InvocationHandler() {
                @Override
                public @continuable Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
                    if (method.getDeclaringClass() == Object.class) {
                        return method.invoke(this, args);
                    }
                    run();
                    return null;
                }
                
                @continuable void run() {
                    for (long i = 1; i <= 5; i++) { 
                        System.out.println("Exe before suspend");
                        Object fromCaller = Continuation.suspend(i);
                        System.out.println("Exe after suspend: " + fromCaller);
                    }
                }
                
                public String toString() {
                    return "proxy-handler(" + getClass().getName() + ") @ " + System.identityHashCode(this);
                }
            }
        );
        
        System.out.println(proxy.getClass());
        System.out.println(proxy.getClass().getSuperclass());
        System.out.println(proxy);
        
        class Runner implements Runnable {

            @Override
            public @continuable void run() {
                System.out.println("IN");
                proxy.process(10);
                System.out.println("OUT");
            }
            
            public String toString() {
                return "runnable";
            }
            
        }

        long[] strings = {11, 22, 33};
        for (Continuation cc = Continuation.startWith(new Runner()); null != cc;) {
            final int valueFromContinuation = Number.class.cast(cc.value()).intValue();
            System.out.println("Interrupted " + valueFromContinuation);
            // Let's continuation resume
            cc = cc.resume(strings[valueFromContinuation % strings.length]);
        }

        System.out.println("ALL DONE");        
    }
    
    public static void testContinuableCustomProxy() throws Exception {
        System.out.println("==== CUSTOM CONTINUABLE CGLIB PROXY ====");
        
        Enhancer enhancer = new Enhancer();
        enhancer.setCallbackFilter(new CallbackFilter() {
            @Override
            public int accept(Method method) {
                String name = method.getName();
                if ("process".equals(name)) {
                    return 2; // Actual continuable method
                } else if ("getInvocationHandler".equals(name)) {
                    return 1; // CustomContinuableProxy.getInvocationHandler
                } else {
                    return 0; // Methods inherited from Object
                }
            }
        });
        final Callback process = new InvocationHandler() {
            @Override
            public @continuable Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                run(Number.class.cast(args[0]).intValue());
                return null;
            }
            
            @continuable void run(int count) {
                Inner inner = new Inner();
                for (long i = 1; i <= count; i++) { 
                    System.out.println("Exe before suspend");
                    Object fromCaller = Continuation.suspend(i - 1);
                    System.out.println("Exe after suspend: " + fromCaller);
                    inner.call("INT " + i);
                }                
            }
            
            public String toString() {
                return "invocation-handler(" + getClass().getName() + ") @ " + System.identityHashCode(this);
            }
        };
        Callback getInvocationHandler = new FixedValue() {
            public Object loadObject() {
                return process;
            }
        };
        enhancer.setCallbacks(new Callback[] {NoOp.INSTANCE /* Object methods */, getInvocationHandler, process}); 
        enhancer.setSuperclass(Object.class);
        enhancer.setInterfaces(new Class<?>[] {MyInterface.class, CustomContinuableProxy.class});
        enhancer.setUseFactory(false);
        
        final MyInterface proxy = (MyInterface) enhancer.create();
        System.out.println(proxy.getClass());
        System.out.println(proxy);
        
        class Runner implements Runnable {

            @Override
            public @continuable void run() {
                System.out.println("IN");
                proxy.process(10);
                System.out.println("OUT");
            }
            
            public String toString() {
                return "runnable";
            }
            
        }

        long[] strings = {11, 22, 33};
        for (Continuation cc = Continuation.startWith(new Runner()); null != cc;) {
            final int valueFromContinuation = Number.class.cast(cc.value()).intValue();
            System.out.println("Interrupted " + valueFromContinuation);
            // Let's continuation resume
            cc = cc.resume(strings[valueFromContinuation % strings.length]);
        }

        System.out.println("ALL DONE");        
    }

}
