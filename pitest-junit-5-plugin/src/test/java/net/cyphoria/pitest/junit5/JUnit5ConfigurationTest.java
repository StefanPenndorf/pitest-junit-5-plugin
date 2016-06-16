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

import org.junit.gen5.api.BeforeAll;
import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Disabled;
import org.junit.gen5.api.Test;
import org.junit.gen5.launcher.main.LauncherFactory;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestUnit;
import org.pitest.testapi.TestUnitFinder;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertFalse;
import static org.junit.gen5.api.Assertions.assertTrue;
import static org.junit.gen5.api.Assertions.fail;

/**
 * @author Stefan Pennndorf
 */
class JUnit5ConfigurationTest {

    private JUnit5Configuration configuration;
    private TestUnitFinder testUnitFinder;

    static class DummyTestClass {

        @Test
        void packagePrivateTestMethod() {

        }

        @Test
        public void publicTestMethod() {

        }

        @BeforeEach
        public void nonTestMethod() {

        }

    }

    static class TestClazzWithStaticSetup {

        @BeforeAll
        static void doFunStuff() {

        }

        @Test
        void packagePrivateTestMethod() {

        }

        @Test
        public void publicTestMethod() {

        }

        @BeforeEach
        public void nonTestMethod() {

        }

    }


    @BeforeEach
    void setup() {
        configuration = new JUnit5Configuration(LauncherFactory.create());
        testUnitFinder = configuration.testUnitFinder();
    }

    @Test
    void findsNoTestsOnNonTestClass() {
        assertThat(testUnitFinder.findTestUnits(JUnit5Configuration.class), is(empty()));

        assertTrue(
                testUnitFinder.findTestUnits(JUnit5Configuration.class).isEmpty());
    }

    @Test
    void findsTestsOnJUnit5TestClass() {
        assertThat(testUnitFinder.findTestUnits(DummyTestClass.class), is(not(empty())));

        assertFalse(
                testUnitFinder.findTestUnits(DummyTestClass.class).isEmpty());
    }

    @Test
    void findsAllAtTestMethodsAsTestUnits() {
        assertThat(testUnitFinder.findTestUnits(DummyTestClass.class), hasSize(2));

        assertEquals(
                2,
                configuration.testUnitFinder().findTestUnits(DummyTestClass.class).size());
    }

    @Test
    void findsCorrectTestUnitNames() {

        final List<String> testsNames = configuration.testUnitFinder().findTestUnits(DummyTestClass.class).stream()
                .map(TestUnit::getDescription)
                .map(Description::getName)
                .collect(Collectors.toList());

        assertThat(testsNames, containsInAnyOrder(
                "publicTestMethod",
                "packagePrivateTestMethod"
        ));
    }

    @Test
    void findsCorrectTestClass() {

        final List<String> testsNames = configuration.testUnitFinder().findTestUnits(DummyTestClass.class).stream()
                .map(TestUnit::getDescription)
                .map(Description::getQualifiedName)
                .collect(Collectors.toList());

        assertThat(testsNames, containsInAnyOrder(
                "net.cyphoria.pitest.junit5.JUnit5ConfigurationTest$DummyTestClass.publicTestMethod",
                "net.cyphoria.pitest.junit5.JUnit5ConfigurationTest$DummyTestClass.packagePrivateTestMethod"
        ));
    }


    @Test
    @Disabled
    void findsSingleTestUnitWithBeforeAll() {
        assertThat(testUnitFinder.findTestUnits(TestClazzWithStaticSetup.class), hasSize(1));

        assertEquals(
                1,
                configuration.testUnitFinder().findTestUnits(TestClazzWithStaticSetup.class).size());
    }

    @Test
    @Disabled
    void doesNotFindDisabledTestMethod() {
        fail("TODO");
    }

    @Test
    @Disabled
    void doesNotFindDisabledTestClasses() {
        fail("TODO");
    }



}