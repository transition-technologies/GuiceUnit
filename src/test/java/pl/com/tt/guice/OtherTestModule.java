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
import com.google.inject.name.Names;

/**
 *
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
@TestModule
public class OtherTestModule extends AbstractModule {

    public OtherTestModule() {
    }

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("other-static")).toInstance("other-static");
        bind(String.class).annotatedWith(Names.named("other-field")).toInstance("other-field");
    }
}
