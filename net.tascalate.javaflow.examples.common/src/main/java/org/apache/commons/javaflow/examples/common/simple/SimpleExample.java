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

import org.apache.commons.javaflow.api.Continuation;

public class SimpleExample {

    public static void main(String[] argv) throws Exception {

        String[] strings = {"A", "B", "C"};
        for (Continuation cc = Continuation.startWith(new Execution()); null != cc;) {
            final int valueFromContinuation = Number.class.cast(cc.value()).intValue();
            System.out.println("Interrupted " + valueFromContinuation);
            // Let's continuation resume
            cc = cc.resume(strings[valueFromContinuation % strings.length]);
        }

        System.out.println("ALL DONE");

    }

}
