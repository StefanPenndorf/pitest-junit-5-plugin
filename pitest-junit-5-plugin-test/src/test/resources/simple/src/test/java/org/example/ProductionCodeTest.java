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
package org.example;

import org.junit.gen5.api.Test;

/**
 *
 *
 * @author Stefan Pennndorf
 */
class ProductionCodeTest {

    @Test
    void executes() {
        final ProductionCode p = new ProductionCode();
        p.calculateOutput();

        assertTrue(p.ran);
    }

    @Test
    void doesNotExecute() {
        final ProductionCode p = new ProductionCode();
        p.x = 4;
        p.calculateOutput();

        assertFalse(p.ran);
    }

}