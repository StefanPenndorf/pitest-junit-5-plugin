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
import org.junit.gen5.engine.support.descriptor.JavaSource;
import org.junit.gen5.launcher.Launcher;
import org.junit.gen5.launcher.TestDiscoveryRequest;
import org.junit.gen5.launcher.TestExecutionListener;
import org.junit.gen5.launcher.TestIdentifier;
import org.junit.gen5.launcher.TestPlan;
import org.junit.gen5.launcher.main.LauncherFactory;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;
import org.pitest.testapi.foreignclassloader.Events;
import org.pitest.util.ClassLoaderDetectionStrategy;
import org.pitest.util.IsolationUtils;
import org.pitest.util.Unchecked;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static org.junit.gen5.engine.discovery.UniqueIdSelector.forUniqueId;
import static org.junit.gen5.launcher.main.TestDiscoveryRequestBuilder.request;

/**
 * @author Stefan Pennndorf
 */
class JUnit5TestUnit implements TestUnit {
    private final TestIdentifier testIdentifier;

    private final ClassLoaderDetectionStrategy loaderDetection;

    JUnit5TestUnit(TestIdentifier testIdentifier) {
        this.loaderDetection = IsolationUtils.loaderDetectionStrategy();
        this.testIdentifier = testIdentifier;

    }

    @Override
    public void execute(ClassLoader loader, ResultCollector rc) {
        final Launcher launcher = LauncherFactory.create();
        final String uniqueId = testIdentifier.getUniqueId().toString();
        final TestDiscoveryRequest discoveryRequest = request().select(forUniqueId(uniqueId)).build();
        final TestPlan testPlan = launcher.discover(discoveryRequest);
        final Optional<? extends Class<?>> testClazz = testPlan.getRoots().stream()
                .flatMap(t -> testPlan.getDescendants(t).stream())
                .map(TestIdentifier::getSource)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(s -> s instanceof JavaSource)
                .map(s -> (JavaSource)s)
                .map(JavaSource::getJavaClass)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        final boolean isFromDifferentLoader = testClazz
                .map(c -> loaderDetection.fromDifferentLoader(c, loader))
                .orElse(false);

        if (isFromDifferentLoader) {
            executeInDifferentClassLoader(loader, rc, launcher, discoveryRequest);

        } else {


            launcher.registerTestExecutionListeners(new TestExecutionListener() {

                @Override
                public void executionSkipped(TestIdentifier testIdentifier, String reason) {
                    rc.notifySkipped(getDescription());
                }

                @Override
                public void executionStarted(TestIdentifier testIdentifier) {
                    rc.notifyStart(getDescription());
                }

                @Override
                public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
                    if (testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
                        rc.notifyEnd(getDescription());
                    } else {
                        rc.notifyEnd(getDescription(), testExecutionResult.getThrowable().orElse(
                                new RuntimeException("Test failed but no throwable provided by JUnit")));
                    }
                }
            });
            launcher.execute(discoveryRequest);
        }
    }

    private void executeInDifferentClassLoader(ClassLoader loader, ResultCollector rc, Launcher launcher, TestDiscoveryRequest discoveryRequest) {
        // must jump through hoops to run in different class loader
        // when even our framework classes may be duplicated
        // translate everything via strings
        final ForeignClassLoaderCustomLauncherExecutor ce = new ForeignClassLoaderCustomLauncherExecutor(
                launcher, discoveryRequest);
        @SuppressWarnings("unchecked")
        Callable<List<String>> foreignCe = (Callable<List<String>>) IsolationUtils
                .cloneForLoader(ce, loader);

        try {
            final List<String> q = foreignCe.call();
            convertStringsToResults(rc, q);
        } catch (Exception ex) {
            throw Unchecked.translateCheckedException(ex);
        }
    }

    private void convertStringsToResults(final ResultCollector rc,
                                         final List<String> q) {
        Events.applyEvents(q, rc, this.getDescription());
    }


    @Override
    public Description getDescription() {
        final String displayName = testIdentifier.getDisplayName();
        final Class<?> testClass = inferTestClassFromSource(testIdentifier);
        return new Description(displayName, testClass);
    }

    private Class inferTestClassFromSource(TestIdentifier testIdentifier) {
        return testIdentifier.getSource()
                .filter(s -> JavaSource.class.isAssignableFrom(s.getClass()))
                .map(JavaSource.class::cast)
                .flatMap(JavaSource::getJavaClass)
                .orElse(null);

    }
}
