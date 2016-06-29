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
import org.pitest.classinfo.ClassName;
import org.pitest.classinfo.Repository;
import org.pitest.help.Help;
import org.pitest.help.PitHelpError;
import org.pitest.testapi.Configuration;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.TestPluginFactory;

/**
 * @author Stefan Pennndorf
 */
public class JUnit5TestPluginFactory implements TestPluginFactory {


    @Override
    public Configuration createTestFrameworkConfiguration(TestGroupConfig testGroupConfig, ClassByteArraySource source) {
        final Repository classRepository = new Repository(source);

        final ClassName name = ClassName.fromString("org.junit.gen5.api.Test");
        if (classRepository.fetchClass(name).hasNone()) {
            throw new PitHelpError(Help.UNKNOWN_MUTATOR, name.toString());
        }

        return new JUnit5Configuration();
    }

    @Override
    public String description() {
        return "A Test Discovery Plugin for JUnit 5";
    }

}
