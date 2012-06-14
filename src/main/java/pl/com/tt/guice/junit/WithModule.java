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

import com.google.inject.Module;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate your JUnit tests with this annotation to explicitly declare Guice module
 * to be used during it's execution.
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithModule {

    /**
     * Guice module class to configure injector for this test class.
     * @return
     */
    public Class<? extends Module> value();

    /**
     * If module should override all other modules or if it should be the only
     * one loaded for this test class.
     * <p/>
     * <strong>Note: </strong>Setting it to false will NOT load other modules.
     * Class declared in this annotation will be the only one used to configure injector.
     *
     * @return
     */
    public boolean override() default false;
}
