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

import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassInfo;
import org.pitest.functional.Option;
import org.pitest.help.PitHelpError;
import org.pitest.testapi.Configuration;
import org.pitest.testapi.TestClassIdentifier;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.TestPluginFactory;
import org.pitest.testapi.TestSuiteFinder;
import org.pitest.testapi.TestUnitFinder;

import java.util.Collections;

/**
 * @author Stefan Pennndorf
 */
public class JUnit5TestPluginFactory implements TestPluginFactory {
    @Override
    public Configuration createTestFrameworkConfiguration(TestGroupConfig testGroupConfig, ClassByteArraySource classByteArraySource) {
        return new Configuration() {
            @Override
            public TestUnitFinder testUnitFinder() {
                return aClass -> Collections.emptyList();
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
                        return false;
                    }

                    @Override
                    public boolean isIncluded(ClassInfo classInfo) {
                        return false;
                    }
                };
            }

            @Override
            public Option<PitHelpError> verifyEnvironment() {
                return Option.none();
            }
        };
    }

    @Override
    public String description() {
        return "A Test Discovery Plugin for JUnit 5";
    }
}
