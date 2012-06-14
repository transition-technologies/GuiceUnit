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
import javax.inject.Named;

/**
 * Indicate that Guice module is only to be used when running
 * in DEV mode.
 * <p/>
 * Modules annotated with this annotation will not be used
 * when application is running in TEST or PROD modes. Only
 * when it's being executed in DEV mode.
 * <p/>
 * DEV module bindings will override production settings (modules
 * without mode annotation). So result after using this annotation
 * is that you get all production bindings overridden by your development
 * ones. It's especially usable when you use properties auto binding
 * to {@link Named} Strings and need to use different ones during development
 * than on production.
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DevelopmentModule {
}
