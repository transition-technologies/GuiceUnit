/*
 * Copyright (c) 2012 Transition Technologies S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.com.tt.guice;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.com.tt.guice.junit.GUnitTestRunner;
import pl.com.tt.guice.junit.WithModule;
import static org.junit.Assert.*;

/**
 * Test for GUnit runner with only single module
 * declared on test class without override.
 * <p/>
 * In this configuration only bindings from this module
 * will be available.
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
@RunWith(GUnitTestRunner.class)
@WithModule(value = SingleModuleTest.SingleModule.class, override = false)
public class SingleModuleTest {

    @Inject
    @Named("static-single")
    private static String s;
    @Inject
    @Named("field-single")
    private String f;
    @Inject
    private Injector injector;

    /**
     * Test injections from configured module
     */
    @Test
    public void testCurrentModuleBindings() {
        assertEquals("static-single", s);
        assertEquals("field-single", f);
    }

    /**
     * Test injections from other modules. These should not be available
     * because this test has module declaration without override.
     */
    @Test(expected = ConfigurationException.class)
    public void testOtherModulesBindings() {
        Key<String> key = Key.get(String.class, Names.named("static"));
        injector.getInstance(key);
    }

    /**
     * Test injections of totally unknown bindings.
     * These should fail.
     */
    @Test(expected = ConfigurationException.class)
    public void testUnknownBindings() {
        Key<String> key = Key.get(String.class, Names.named("unknown"));
        injector.getInstance(key);
    }

    public static class SingleModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(String.class).annotatedWith(Names.named("static-single")).toInstance("static-single");
            bind(String.class).annotatedWith(Names.named("field-single")).toInstance("field-single");
        }
    }
}
