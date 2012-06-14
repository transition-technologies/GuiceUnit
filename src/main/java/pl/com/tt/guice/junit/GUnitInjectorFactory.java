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

import pl.com.tt.guice.InjectorFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import pl.com.tt.guice.InjectorFactory.MODE;

/**
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
@Slf4j
public class GUnitInjectorFactory {

    /**
     * Cached injector.
     * We cache it in case we done some extensive classpath scanning
     * for modules and tests. It will not change between tests
     * if we done all this automatically anyway.
     */
    private static Collection<Class<? extends Module>> allModules;

    @SuppressWarnings("unchecked")
    static Injector getInjector(Class<?> test, boolean override, Class<? extends Module>... module)
            throws InstantiationException, IllegalAccessException {
        Collection<Class<? extends Module>> modules = Arrays.asList(module);
        return createInjector(test, modules, override);
    }

    /**
     * Scan current Java classpath for Guice Modules. This scan will only find classes
     * that <b>directly</b> extend {@link AbstractModule} or implement {@link Module}
     * and are not from
     * <code>com.google</code> package (to exclude internal Guice
     * modules)
     *
     * @return all classes that are Guice modules
     */
    static Collection<Class<? extends Module>> findAllModules() {
        Set<URL> classPathMembers = getClasspathMembers();

        if (allModules == null) {
            allModules = InjectorFactory.findAllModules(classPathMembers);
        }

        return allModules;
    }

    /**
     * Get classpath elements to scan for classes.
     * This method will return only elements that are not zip or jar files
     * this should contain application sources and not it's dependencies.
     * <p/>
     * Will fallback to full classpath if failed.
     *
     * @return
     */
    static Set<URL> getClasspathMembers() {
        String javaClassPath = System.getProperty("java.class.path");
        String[] classPathElems = javaClassPath.split(File.pathSeparator);
        Set<URL> classPathMembers = new HashSet<URL>();

        for (String elem : classPathElems) {
            try {
                if (!elem.endsWith(".jar") && !elem.endsWith(".zip")) {
                    classPathMembers.add(new File(elem).toURI().toURL());
                }
            } catch (MalformedURLException ex) {
                log.error("Could not create URL for classpath element: " + elem, ex);
            }
        }
        return classPathMembers;
    }

    @SuppressWarnings("unchecked")
    private static Injector createInjector(Class<?> test, Collection<Class<? extends Module>> testModules, boolean override)
            throws InstantiationException, IllegalAccessException {

        Collection<Class<? extends Module>> modules = testModules;
        if (modules == null || modules.isEmpty()) {
            log.debug("No module passed in. Will use all modules found in classpath.");
            modules = findAllModules();
        } else if (override) {
            log.debug("Passed modules in override mode.");
            modules = findAllModules();
            modules.removeAll(testModules);
        }

        log.info("Creating injector with modules: " + modules.toString());
        Module combinedModule = InjectorFactory.setupModule(modules, MODE.TEST);

        if (override && testModules != null && !testModules.isEmpty()) {
            Module overrideModule = InjectorFactory.setupModule(testModules, MODE.TEST);
            log.debug("Overriding modules with: " + testModules.toString());
            combinedModule = Modules.override(combinedModule).with(overrideModule);
        }

        HashSet<Class<?>> staticInjects = new HashSet<Class<?>>();

        if (override || testModules == null || testModules.isEmpty()) {
            Set<URL> classpathMembers = getClasspathMembers();
            staticInjects.addAll(InjectorFactory.findAllStaticInjects(classpathMembers));
        } else {
            log.info("Using JUnit and @WithModule without override. Will not inject static members to classes other than current test.");
        }

        staticInjects.add(test);

        Module module = new WrapperModule(combinedModule, staticInjects.toArray(new Class[staticInjects.size()]));

        log.info("Created injector with: " + modules.size() + " module(s).");
        Injector injector = Guice.createInjector(module);
        return injector;
    }

    /**
     * Module that wraps user created modules, installs them and
     * does a static injection on passed in classes according to
     * user modules configuration
     * <p/>
     * Module itself does not bind anything.
     */
    private static class WrapperModule implements Module {

        private Module base;
        private Class<?>[] staticInjectClasses;

        public WrapperModule(Module base, Class<?>... staticInjectClasses) {
            this.base = base;
            this.staticInjectClasses = staticInjectClasses;
        }

        public void configure(Binder binder) {
            binder.install(base);
            log.debug("Injected values for static variables in " + staticInjectClasses
                    + " classes: " + Arrays.toString(staticInjectClasses));
            binder.requestStaticInjection(staticInjectClasses);
        }
    }
}
