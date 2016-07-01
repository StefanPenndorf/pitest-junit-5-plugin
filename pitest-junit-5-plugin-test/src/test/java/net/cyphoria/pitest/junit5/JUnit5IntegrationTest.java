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

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.gen5.api.AfterEach;
import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Test;
import org.junit.gen5.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Stefan Pennndorf
 */
class JUnit5IntegrationTest {

    private static final String VERSION = "1.1.9";

    private static Logger LOGGER = LoggerFactory.getLogger(JUnit5IntegrationTest.class);

    private long startTime;

    private Verifier verifier;

    @BeforeEach
    void beforeEachTest(final TestInfo testInfo) {
        LOGGER.info("running test '{}'", testInfo.getDisplayName());
        startTime = System.currentTimeMillis();
    }

    @AfterEach
    void afterEachTest(final TestInfo testInfo) {
        LOGGER.info("duration of test '{}' {}ms", testInfo.getDisplayName(),
                System.currentTimeMillis() - startTime);
    }

    @Test
    void mutatestSimpleCodeEndExecutesTest() throws IOException, VerificationException {
        prepare("/simple");

        verifier.executeGoal("test");
        verifier.executeGoal("org.pitest:pitest-maven:mutationCoverage");


        verifier.verifyTextInLog("Generated 4 mutations Killed 2");


        // At most we ought to run 8 tests (possibly less not looked at what the tests are doing)
        verifier.verifyTextInLog("Ran 8 tests");

        // This is what we actually observe
        // verifier.verifyTextInLog("Ran 9 tests");
        
        // Investigation needed here. 
        // Two possible explainations for the observed behaviour
        // 1. Something isn't quite right with the test targetting in pitest (unrelated to this plugin)
        // 2. This plugin is detecting more than 2 tests in the ProductionCodeTest test class.
         
    }


    private File prepare(String testPath) throws IOException, VerificationException {

        Path testFolder = Files.createTempDirectory("pit");
        String path = ResourceExtractor.extractResourcePath(getClass(), testPath,
                testFolder.toFile(), true).getAbsolutePath();

        verifier = new Verifier(path);
        verifier.setAutoclean(false);
        verifier.setDebug(true);
        verifier.getCliOptions().add("-Dpit.version=" + VERSION);
        verifier.getCliOptions().add(
                "-Dthreads=" + (Runtime.getRuntime().availableProcessors()));

        return new File(testFolder.toAbsolutePath().toFile(), testPath);
    }

}
