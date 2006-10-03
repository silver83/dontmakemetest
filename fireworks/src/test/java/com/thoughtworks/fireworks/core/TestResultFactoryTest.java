/*
 *    Copyright (c) 2006 LiXiao.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.thoughtworks.fireworks.core;

import com.thoughtworks.turtlemock.Turtle;
import com.thoughtworks.turtlemock.internal.ProxyTypeMock;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;

public class TestResultFactoryTest extends TestCase {
    public void testCreateTestResult() throws Exception {
        ProxyTypeMock testListener = Turtle.mock(TestListener.class);
        ProxyTypeMock counterListener = Turtle.mock(TestCounterListener.class);

        TestListener[] testListeners = new TestListener[]{(TestListener) testListener.mockTarget()};
        TestCounterListener[] counterListeners = new TestCounterListener[]{(TestCounterListener) counterListener.mockTarget()};

        TestResultFactory factory = new TestResultFactory(testListeners, counterListeners);
        TestResult result = factory.createTestResult();
        counterListener.assertDid("testResult").with(new Integer(0), new Integer(0), new Integer(0));

        result.startTest(this);
        result.endTest(this);

        testListener.assertDid("startTest");
        testListener.assertDid("endTest");

        counterListener.assertDid("testResult").with(new Integer(1), new Integer(0), new Integer(0));
    }

    public void testShouldIgnoreNullArgs() throws Exception {
        TestResultFactory factory = new TestResultFactory(null, null);
        assertNotNull(factory.createTestResult());
    }
}