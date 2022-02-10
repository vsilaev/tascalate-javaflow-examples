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
package org.apache.commons.javaflow.examples.common.test;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.api.continuable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleTestSuite {
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testContinuation() {
        List<Integer> returned = new ArrayList<>();
        String[] strings = {"A", "B", "C"};
        for (Continuation cc = Continuation.startWith(new Execution(), true); null != cc;) {
            final int valueFromContinuation = Number.class.cast(cc.value()).intValue();
            // System.out.println("Interrupted " + valueFromContinuation);
            // Let's continuation resume
            returned.add(valueFromContinuation);
            cc = cc.resume(strings[valueFromContinuation % strings.length]);
        }
        // System.out.println("ALL DONE");    
        assertArrayEquals(toIntArray(returned), new int[] {1,2,3,4,5});
    }
    
    private static int[] toIntArray(List<Integer> list) {
        int[] returnInt = new int[list.size()];
        int valueIndex = 0;
        
        for (Integer value : list) {
            returnInt[valueIndex++] = value.intValue();
        }
        
        return returnInt;
    }
    
    static class Execution implements Runnable {

        @Override
        public @continuable void run() {
            for (long i = 1; i <= 5; i++) {
                // System.out.println("Exe before suspend");
                @SuppressWarnings("unused")
                Object fromCaller = Continuation.suspend(i);
                // System.out.println("Exe after suspend: " + fromCaller);
            }
        }
    }

}
