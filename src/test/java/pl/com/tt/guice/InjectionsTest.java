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

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.com.tt.guice.junit.GUnitTestRunner;
import static junit.framework.Assert.*;

/**
 * Test of basic injections with GUnit.
 * In this test we should have access to all available modules.c
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
@RunWith(GUnitTestRunner.class)
public class InjectionsTest {

    @Inject
    @Named("static")
    private static String s;
    @Inject
    @Named("other-static")
    private String os;
    @Inject
    @Named("field")
    private String f;
    @Inject
    @Named("other-field")
    private String of;
    @Inject
    private Injector injector;

    /**
     * Test injections from main module.
     */
    @Test
    public void testMainBindings() {
        assertEquals("static", s);
        assertEquals("field", f);
    }

    /**
     * Test injections from other module.
     */
    @Test
    public void testOtherBindings() {
        assertEquals("other-static", os);
        assertEquals("other-field", of);
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
}
