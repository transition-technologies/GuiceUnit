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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import pl.com.tt.guice.InjectorFactory.MODE;

/**
 * Indicate that Guice module should be treated as primary and
 * override all other production modules.
 * <p/>
 * This module will still be overridden by {@link TestModule}s and {@link DevelopmentModule}s
 * if running in {@link MODE#TEST} or {@link MODE#DEV} mode.
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MasterModule {
}
