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

package pl.com.tt.guice.junit;

import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JUnit 4 test runner that supports Google Guice injections.
 * <p/>
 * All JUnit test classes that are executed with this runner
 * will get static and field members injected by Google Guice
 * {@link Injector}. This injector will be automatically
 * configured by modules found in current classpath.
 * <p/>
 * Modules behavior depends on annotations set on them and on test classes.
 * Check {@link InjectorFactory} for more information which module will be used.
 *
 * @see InjectorFactory
 * @see TestModule
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
public class GUnitTestRunner extends BlockJUnit4ClassRunner {

    private final Injector injector;

    @SuppressWarnings("unchecked")
    public GUnitTestRunner(Class<?> testClass)
            throws InitializationError {
        super(testClass);
        try {
            WithModule withModule = testClass.getAnnotation(WithModule.class);
            if (withModule != null) {
                Class<? extends Module> module = withModule.value();
                boolean override = withModule.override();
                injector = GUnitInjectorFactory.getInjector(testClass, override, module);
            } else {
                injector = GUnitInjectorFactory.getInjector(testClass, false);
            }
        } catch (Exception ex) {
            throw new InitializationError(ex);
        }
    }

    @Override
    protected Object createTest()
            throws Exception {
        Class<?> javaClass = getTestClass().getJavaClass();
        Object instance = injector.getInstance(javaClass);
        return instance;
    }
}
