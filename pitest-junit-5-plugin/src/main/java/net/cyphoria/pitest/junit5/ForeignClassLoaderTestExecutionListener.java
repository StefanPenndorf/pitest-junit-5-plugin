/*
 * Copyright 2016 Stefan Penndorf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package net.cyphoria.pitest.junit5;

import org.junit.gen5.engine.TestExecutionResult;
import org.junit.gen5.launcher.TestExecutionListener;
import org.junit.gen5.launcher.TestIdentifier;
import org.pitest.functional.SideEffect2;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.foreignclassloader.Fail;
import org.pitest.testapi.foreignclassloader.Skipped;
import org.pitest.testapi.foreignclassloader.Start;
import org.pitest.testapi.foreignclassloader.Success;
import org.pitest.util.IsolationUtils;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Stefan Pennndorf
 */
public class ForeignClassLoaderTestExecutionListener implements TestExecutionListener {

    private final ConcurrentLinkedQueue<String> events;

    public ForeignClassLoaderTestExecutionListener(ConcurrentLinkedQueue<String> queue) {
        this.events = queue;
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        storeAsString(new Start());
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        storeAsString(new Skipped());
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if(testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
            storeAsString(new Success());
        } else {
            storeAsString(new Fail(testExecutionResult.getThrowable().orElse(null)));
        }
    }

    private void storeAsString(
            final SideEffect2<ResultCollector, Description> result) {
        this.events.add(IsolationUtils.toXml(result));
    }

}
