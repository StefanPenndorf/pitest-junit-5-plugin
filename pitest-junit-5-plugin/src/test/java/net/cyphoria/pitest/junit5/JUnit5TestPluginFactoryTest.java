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

import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Disabled;
import org.junit.gen5.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.functional.Option;
import org.pitest.help.Help;
import org.pitest.help.PitHelpError;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.TestUnitFinder;
import org.pitest.util.IsolationUtils;

import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertFalse;
import static org.junit.gen5.api.Assertions.expectThrows;
import static org.mockito.Mockito.when;

/**
 * @author Stefan Pennndorf
 */
class JUnit5TestPluginFactoryTest {

    @Mock
    private TestGroupConfig groupConfig;

    @Mock
    private ClassByteArraySource source;

    private ClassByteArraySource realSource;


    private JUnit5TestPluginFactory factory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.realSource = new ClassloaderByteArraySource(
                IsolationUtils.getContextClassLoader());

        this.factory = new JUnit5TestPluginFactory();

        when(this.source.getBytes("org.junit.Test")).thenReturn(
                Option.none());
        when(this.source.getBytes("org.junit.gen5.api.Test")).thenReturn(
                Option.none());
    }

    @Test
    void shouldCreateAConfigurationThatFindsJUnitTestsWhenJUnit5OnClassPath() {
        putJUnit5OnClasspath();

        final TestUnitFinder finder = factory.createTestFrameworkConfiguration(groupConfig, source).testUnitFinder();

        assertFalse(finder.findTestUnits(JUnit5TestPluginFactoryTest.class).isEmpty());
    }

    @Test
    @Disabled
    void shouldThrowPitErrorWhenNoJunit5OnClassPath() {
        final PitHelpError error = expectThrows(
                PitHelpError.class,
                () -> factory.createTestFrameworkConfiguration(groupConfig, source)
        );

        assertEquals(new PitHelpError(Help.NO_TEST_LIBRARY).getMessage(), error.getMessage());
    }


    private void putJUnit5OnClasspath() {
        when(this.source.getBytes("org.junit.gen5.api.Test")).thenReturn(
                this.realSource.getBytes("org.junit.gen5.api.Test"));
    }

}