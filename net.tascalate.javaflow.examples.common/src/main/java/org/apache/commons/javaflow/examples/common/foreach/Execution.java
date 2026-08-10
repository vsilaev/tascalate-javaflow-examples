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
package org.apache.commons.javaflow.examples.common.foreach;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.api.continuable;

public class Execution implements Runnable {

    @Override
    public @continuable void run() {
        for (Long l :  new SuspendableIterable<Long>(Arrays.asList(1L, 2L, 3L))) {
            SuspendableIterable<String> source = new SuspendableIterable<String>(Arrays.asList("A", "B", "C")); 
            for (String s : source) {
                System.out.println("Exe before suspend");
                Object fromCaller = Continuation.suspend(l + s);
                System.out.println("Exe after suspend: " + fromCaller);
            }
            
            for (String s : new SuspendableIterable<String>(Arrays.asList("D", "E", "F"))) {
                System.out.println("Exe before suspend");
                Object fromCaller = Continuation.suspend(l + s);
                System.out.println("Exe after suspend: " + fromCaller);
            }
            
            for (String s : createSource()) {
                System.out.println("Exe before suspend");
                Object fromCaller = Continuation.suspend(l+ s);
                System.out.println("Exe after suspend: " + fromCaller);
            }
    
    
            
            SuspendableIterator<String> v2 = createSource().iterator();
            if (v2.hasNext()) {
                System.out.println("From manual iterator " + v2.next());
                if (v2.hasNext()) {
                    System.out.println("From manual iterator " + v2.next());
                }
            }
        }
    }
    
    static SuspendableIterable<String> createSource() {
        return new SuspendableIterable<String>(Arrays.asList("X", "Y", "Z"));
    }
    
    static class SuspendableIterable<T> implements Iterable<T> {
        private final Iterable<T> delegate;
        
        SuspendableIterable(Iterable<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        @continuable
        public SuspendableIterator<T> iterator() {
            return new SuspendableIterator<T>(delegate.iterator());
        }
    }
    
    static class SuspendableIterator<E> implements Iterator<E> {
        private final Iterator<E> delegate;
        
        SuspendableIterator(Iterator<E> delegate) {
            this.delegate = delegate;
        }

        @Override
        @continuable
        public boolean hasNext() {
            boolean result = delegate.hasNext();
            Continuation.suspend("SUSPEND FROM hasNext() " + result);
            return result;
        }

        @Override
        @continuable
        public E next() {
            E result = delegate.next();
            Continuation.suspend("SUSPEND FROM next() " + result);
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
