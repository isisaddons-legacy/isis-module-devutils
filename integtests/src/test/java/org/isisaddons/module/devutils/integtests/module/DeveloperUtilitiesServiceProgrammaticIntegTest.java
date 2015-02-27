/*
 *  Copyright 2013~2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.devutils.integtests.module;

import java.util.List;
import javax.inject.Inject;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.isisaddons.module.devutils.dom.DeveloperUtilitiesService;
import org.isisaddons.module.devutils.fixture.dom.DevUtilsDemoObject;
import org.isisaddons.module.devutils.fixture.dom.DevUtilsDemoObjects;
import org.isisaddons.module.devutils.fixture.scripts.DevUtilsDemoObjectsFixture;
import org.isisaddons.module.devutils.integtests.DevUtilsModuleIntegTest;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.value.Clob;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DeveloperUtilitiesServiceProgrammaticIntegTest extends DevUtilsModuleIntegTest {

    DevUtilsDemoObject demoObject;

    @Inject
    DevUtilsDemoObjects devUtilsDemoObjects;
    @Inject
    DeveloperUtilitiesService developerUtilitiesService;

    @Before
    public void setUpData() throws Exception {
        scenarioExecution().install(new DevUtilsDemoObjectsFixture());
    }

    @Before
    public void setUp() throws Exception {

        final List<DevUtilsDemoObject> all = wrap(devUtilsDemoObjects).listAll();
        assertThat(all.size(), is(3));

        demoObject = all.get(0);
        assertThat(demoObject.getName(), is("Foo"));

    }

    public static class DownloadLayout extends DeveloperUtilitiesServiceProgrammaticIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final String expected = Resources.toString(Resources.getResource(getClass(), "expected.json"), Charsets.UTF_8);

            // when
            final Clob clob = developerUtilitiesService.downloadLayout(demoObject);

            // then
            final CharSequence chars = clob.getChars();
            final String actual = chars.toString();

            System.out.println(actual);
            assertThat(actual, is(expected));
        }


    }

}