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

import com.google.inject.Module;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that Guice module is only to be used when running
 * in test mode.
 *
 * Modules annotated with this annotation will not be used
 * when application is running in DEV or PROD modes. Only
 * when it's being executed in test mode.
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestModule {

    public Class<? extends Module> overwrites() default Module.class;
}
