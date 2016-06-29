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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Stefan Pennndorf
 */
public class ForeignClassLoaderCustomLauncherExecutor implements Callable<List<String>> {
    private final TestDiscoveryRequest discoveryRequest;
    private final Launcher launcher;

    public ForeignClassLoaderCustomLauncherExecutor(Launcher launcher, TestDiscoveryRequest discoveryRequest) {
        super();
        this.launcher = launcher;
        this.discoveryRequest = discoveryRequest;

    }

    @Override
    public List<String> call() throws Exception {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        launcher.registerTestExecutionListeners(new ForeignClassLoaderTestExecutionListener(queue));
        launcher.execute(discoveryRequest);
        return new ArrayList<>(queue);
    }
}
