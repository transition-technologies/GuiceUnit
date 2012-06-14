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

package pl.com.tt.guice.reflections;

import java.util.List;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.FieldInfo;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.Scanner;

/**
 * Scan static class fields and accept only ones annotated with
 * configured annotation.
 *
 * <p/>
 * <strong>It assumes that Reflections uses JavaAssist.</strong><br/>
 * It 's needed because {@link MetadataAdapter} interface does not
 * allow to check access modifiers of fields.
 *
 * @see Scanner
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
public class StaticFieldAnnotationsScanner extends FieldAnnotationsScanner {

    @Override
    @SuppressWarnings("unchecked")
    public void scan(Object cls) {
        final String className = getMetadataAdapter().getClassName(cls);
        List<?> fields = getMetadataAdapter().getFields(cls);
        for (final Object field : fields) {
            List<String> fieldAnnotations = getMetadataAdapter().getFieldAnnotationNames(field);
            for (String fieldAnnotation : fieldAnnotations) {
                //TODO: Change this check to some getMetadataAdapter method when it's implemented in Reflections.
                FieldInfo fieldInfo = (FieldInfo) field;
                boolean isStatic = (fieldInfo.getAccessFlags() & AccessFlag.STATIC) != 0;
                //
                if (isStatic && acceptResult(fieldAnnotation)) {
                    String fieldName = getMetadataAdapter().getFieldName(field);
                    getStore().put(fieldAnnotation, String.format("%s.%s", className, fieldName));
                }
            }
        }
    }
}
