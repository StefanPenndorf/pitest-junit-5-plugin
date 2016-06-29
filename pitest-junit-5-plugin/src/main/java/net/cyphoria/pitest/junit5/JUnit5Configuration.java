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

import org.junit.gen5.launcher.Launcher;
import org.junit.gen5.launcher.TestDiscoveryRequest;
import org.junit.gen5.launcher.TestIdentifier;
import org.junit.gen5.launcher.TestPlan;
import org.junit.gen5.launcher.main.LauncherFactory;
import org.pitest.classinfo.ClassInfo;
import org.pitest.functional.Option;
import org.pitest.help.PitHelpError;
import org.pitest.testapi.Configuration;
import org.pitest.testapi.TestClassIdentifier;
import org.pitest.testapi.TestSuiteFinder;
import org.pitest.testapi.TestUnitFinder;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.gen5.engine.discovery.ClassSelector.forClass;
import static org.junit.gen5.launcher.main.TestDiscoveryRequestBuilder.request;

/**
 * @author Stefan Pennndorf
 */
public class JUnit5Configuration implements Configuration {

    public JUnit5Configuration() {
    }


    @Override
    public TestUnitFinder testUnitFinder() {
        final Launcher launcher = LauncherFactory.create();
        return testClass -> {
            TestDiscoveryRequest discoveryRequest = request().select(forClass(testClass)).build();
            final TestPlan testPlan = launcher.discover(discoveryRequest);

            return testPlan.getRoots().stream()
                    .map(testPlan::getDescendants)
                    .flatMap(Collection::stream)
                    .filter(TestIdentifier::isTest)
                    .map(JUnit5TestUnit::new)
                    .collect(Collectors.toList());
        };

    }

    @Override
    public TestSuiteFinder testSuiteFinder() {
        return aClass -> Collections.emptyList();
    }

    @Override
    public TestClassIdentifier testClassIdentifier() {
        return new TestClassIdentifier() {
            @Override
            public boolean isATestClass(ClassInfo classInfo) {
                return classInfo.getName().asJavaName().endsWith("Test");
            }

            @Override
            public boolean isIncluded(ClassInfo classInfo) {
                return true;
            }
        };
    }

    @Override
    public Option<PitHelpError> verifyEnvironment() {
        return Option.none();
    }

}
