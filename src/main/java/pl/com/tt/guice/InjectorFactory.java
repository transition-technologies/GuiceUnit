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

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.reflections.util.FilterBuilder.Include;
import org.reflections.util.Utils;
import pl.com.tt.guice.reflections.StaticFieldAnnotationsScanner;

/**
 * Factory producing injector from modules configured according to application
 * mode.
 * <p/>
 * This factory is responsible for preparing combined module. It is
 * producing configuration based on current run mode of application.
 * It first loads and merges all production modules (without mode annotations).
 * Then overrides them with module annotated as {@link MasterModule}
 * (This is considered as final production module)
 * and then overrides it with modules depending on application run mode:
 *
 * <ul>
 * <li>{@link MODE#DEV} - application is running in development mode.
 * <p>Override production module with merged modules annotated with {@link DevelopmentModule}</p></li>
 * <li>{@link MODE#TEST} - application is running in test mode.
 * <p>Override production module with merged modules annotated with {@link TestModule}</p></li>
 * <li>{@link MODE#PROD} - application is running in production mode.
 * <p>Do not override final production module with either test nor development modules</p></li>
 * </ul>
 *
 * @see DevelopmentModule
 * @see TestModule
 * @see MasterModule
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
public class InjectorFactory {

    /**
     * Current application runtime mode
     */
    public enum MODE {

        /**
         * Application is running in production mode.
         */
        PROD,
        /**
         * Application is running in development mode
         */
        DEV,
        /**
         * Application is running in test mode (during execution
         * of unit/integration/acceptance tests)
         */
        TEST
    };

    /**
     * Create new Injector according to runtime mode from passed in modules .
     *
     * @param modules modules to use for Injector configuration
     * @param mode runtime mode of application
     * @return configured and ready to use Injector
     * @throws InstantiationException if could not create instance of passed in module
     * @throws IllegalAccessException if could not create instance of passed in module
     */
    public static Injector createInjector(Iterable<Class<? extends Module>> modules, MODE mode)
            throws InstantiationException, IllegalAccessException {

        Module module = setupModule(modules, mode);
        Injector injector = Guice.createInjector(module);

        return injector;
    }

    /**
     * Create new Injector according to runtime mode from all modules that could
     * be found in classpath.
     * <p/>
     * <strong> Full classpath scan for modules is very expensive.
     * Remember to reuse created injector</strong>
     *
     * @param mode runtime mode of application
     * @return configured and ready to use Injector
     * @throws InstantiationException if could not create instance of passed in module
     * @throws IllegalAccessException if could not create instance of passed in module
     */
    public static Injector createInjector(MODE mode)
            throws InstantiationException, IllegalAccessException {
        Collection<Class<? extends Module>> allModules = findAllModules();
        return createInjector(allModules, mode);
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
    public static Collection<Class<? extends Module>> findAllModules() {
        Set<URL> classPathMembers = ClasspathHelper.forJavaClassPath();
        Collection<Class<? extends Module>> allModules = InjectorFactory.findAllModules(classPathMembers);

        return allModules;
    }

    /**
     * Scan classpath members for Guice Modules. This scan will only find classes
     * that <b>directly</b> extend {@link AbstractModule} or implement {@link Module}
     * and are not from
     * <code>com.google</code> package (to exclude internal Guice
     * modules)
     *
     * @param classPathMembers classpath members to scan
     * @return all classes that are Guice modules
     */
    @SuppressWarnings("unchecked")
    public static Collection<Class<? extends Module>> findAllModules(Set<URL> classPathMembers) {
        //Search only for classes with Module in name.
        //Exclude Google Guice internal injectors
        // and our wrapper module.
        Predicate<String> filter = new FilterBuilder().include(".*Module.*").exclude("com\\.google\\..*").
                exclude("pl\\.com\\.tt\\.guice\\.junit\\.GUnitInjectorFactory\\$WrapperModule.*");
        //Find all Guice modules
        ConfigurationBuilder config = new ConfigurationBuilder().setUrls(classPathMembers).
                setScanners(new SubTypesScanner()).filterInputsBy(filter);
        Reflections reflections = new Reflections(config);

        Set<Class<? extends Module>> moduleClasses = new HashSet<Class<? extends Module>>();
        moduleClasses.addAll(reflections.getSubTypesOf(Module.class));
        moduleClasses.addAll(reflections.getSubTypesOf(AbstractModule.class));
        return moduleClasses;
    }

    /**
     * Scan classpath members for classes that have static fields annotated with
     * {@link Inject}.
     *
     * <p/>
     * This scan will only find classes that are not from
     * <code>com.google</code> package (to exclude internal Guice
     * modules)
     *
     * @param classPathMembers classpath members to scan
     * @return all classes that are Guice modules
     */
    @SuppressWarnings("unchecked")
    public static Collection<Class<?>> findAllStaticInjects(Set<URL> classPathMembers) {
        //Search only for classes with Module in name.
        //Exclude Google Guice internal injectors
        // and our wrapper module.
        Predicate<String> filter = new FilterBuilder().include(".*").exclude("com\\.google\\..*").
                exclude("pl\\.com\\.tt\\.guice\\.junit\\.GUnitInjectorFactory\\$WrapperModule.*");
        //Find all classes with @Inject fields
        StaticFieldAnnotationsScanner scanner = new StaticFieldAnnotationsScanner();
        scanner.filterResultsBy(new FilterBuilder().include(Inject.class.getName()));
        ConfigurationBuilder config = new ConfigurationBuilder().setUrls(classPathMembers).
                setScanners(scanner).filterInputsBy(filter);
        Reflections reflections = new Reflections(config);
        
        Multimap<String, String> keys = reflections.getStore().get(scanner);
        Collection<String> fieldFqns = keys.get(Inject.class.getName());
        Set<Class<?>> classes = new HashSet<Class<?>>(fieldFqns.size());
        for (String string : fieldFqns) {
            Field field = Utils.getFieldFromString(string);
            classes.add(field.getDeclaringClass());
        }

        return classes;
    }

    /**
     * Configure combined module from passed in modules according to application
     * runtime mode. Created module can be used for {@link Injector} creation.
     *
     * @param modules modules to use for Injector configuration
     * @param mode runtime mode of application
     * @return configured and ready to use Injector
     * @throws InstantiationException if could not create instance of passed in module
     * @throws IllegalAccessException if could not create instance of passed in module
     */
    public static Module setupModule(Iterable<Class<? extends Module>> modules, MODE mode)
            throws InstantiationException, IllegalAccessException {
        List<Module> testModules = new ArrayList<Module>();
        List<Module> devModules = new ArrayList<Module>();
        List<Module> prodModules = new ArrayList<Module>();
        Module masterModule = null;


        for (Class<? extends Module> moduleClass : modules) {
            int modifiers = moduleClass.getModifiers();
            if (!Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers)
                    && Modifier.isPublic(modifiers)) {
                if (moduleClass.isAnnotationPresent(TestModule.class)) {
                    testModules.add(moduleClass.newInstance());
                } else if (moduleClass.isAnnotationPresent(DevelopmentModule.class)) {
                    devModules.add(moduleClass.newInstance());
                } else if (moduleClass.isAnnotationPresent(MasterModule.class)) {
                    masterModule = moduleClass.newInstance();
                } else {
                    prodModules.add(moduleClass.newInstance());
                }
            }
        }

        Module module = Modules.combine(prodModules);
        if (masterModule != null) {
            module = Modules.override(prodModules).with(masterModule);
        }

        if (mode == MODE.TEST && !testModules.isEmpty()) {
            module = Modules.override(module).with(testModules);
        } else if (mode == MODE.DEV && !devModules.isEmpty()) {
            module = Modules.override(module).with(devModules);
        }

        return module;
    }
}
