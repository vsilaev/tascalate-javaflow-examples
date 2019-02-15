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
                

                run("V");
                return null;
            }
            
            @continuable void run(String prefix) {
                for (String s : new String[] {"A", "B", "C"}) {
                    Continuation.suspend(prefix + s);
                }
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
                PassThroughProxyExample.class.getClassLoader(), 
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
