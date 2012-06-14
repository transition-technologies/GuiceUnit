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
 * Test with single module with override set to true.
 * In this test class all bindings from other modules should also be available
 * only overriden by declared module bindings.
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
@RunWith(GUnitTestRunner.class)
@WithModule(value = ModuleOverrideTest.OverridingModule.class, override = true)
public class ModuleOverrideTest {

    @Inject
    @Named("static")
    private static String s;
    @Inject
    @Named("static2")
    private static String s2;
    @Inject
    @Named("field")
    private String f;
    @Inject
    @Named("field2")
    private String f2;
    @Inject
    private Injector injector;

    /**
     * Test overriden bindings
     */
    @Test
    public void testOverriddenBindings() {
        assertEquals("static-override", s);
        assertEquals("field-override", f);
    }

    /**
     * Test bindings from other modules.
     * Bindings from other modules should be available here
     * as override is set to true in {@link WithModule} annotation.
     */
    @Test
    public void testOtherModulesBindings() {
        assertEquals("static2", s2);
        assertEquals("field2", f2);
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

    public static class OverridingModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(String.class).annotatedWith(Names.named("static")).toInstance("static-override");
            bind(String.class).annotatedWith(Names.named("field")).toInstance("field-override");
        }
    }
}
