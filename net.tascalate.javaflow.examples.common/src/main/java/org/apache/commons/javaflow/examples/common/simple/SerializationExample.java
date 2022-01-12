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
package org.apache.commons.javaflow.examples.common.simple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.javaflow.api.Continuation;

public class SerializationExample {
    
    public static void main(String[] argv) throws Exception {
        Continuation x = Continuation.startWith(new SerializableExecution(), true);
        System.out.println(x);
        System.out.println("Interrupted " + x.value());
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        try {
            oos.writeObject(x);
        } finally {
            if (oos != null) { oos.close(); }
        }
        
        Continuation y;
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream iis = new ObjectInputStream(bis);
        try {
            y = (Continuation)iis.readObject();
        } finally {
            if (iis != null) { iis.close(); }
        }
        
        System.out.println(y);
        System.out.println("Restored " + y.value());
        
        Continuation next = y.singleShot().resume(111);
        System.out.print("Interrupted " + next.value());
    }
}
