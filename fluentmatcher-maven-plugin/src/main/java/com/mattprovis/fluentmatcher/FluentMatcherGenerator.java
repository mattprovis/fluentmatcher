package com.mattprovis.fluentmatcher;

import com.google.common.primitives.Primitives;
import com.squareup.javawriter.JavaWriter;
import org.hamcrest.Matcher;
import org.unitils.util.ReflectionUtils;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.EnumSet.of;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.uncapitalize;

public class FluentMatcherGenerator {

    public static void generateMatcherFor(Class<?> beanClass, Writer out) throws IOException {
        JavaWriter javaWriter = new JavaWriter(out);

        String beanClassName = beanClass.getSimpleName();
        String matcherClassName = beanClassName + "Matcher";

        List<Field> fields = new ArrayList<>(ReflectionUtils.getAllFields(beanClass));

        HashSet<Class> imports = new HashSet<Class>();
        imports.add(FluentMatcher.class);
        imports.add(Matcher.class);

        for (Field field : fields) {
            Class<?> type = field.getType();
            if (!type.isPrimitive()) {
                imports.add(type);
            }
        }

        javaWriter
                .emitPackage(beanClass.getPackage().getName())
                .emitImports(imports.toArray(new Class[imports.size()]))
                .emitEmptyLine()
                .beginType(matcherClassName, "class", of(PUBLIC), FluentMatcher.class.getSimpleName() + "<" + beanClassName + ">")
                .emitEmptyLine();

        javaWriter
                .beginType("FieldName", "enum", of(PRIVATE));

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            boolean isLast = i == fields.size() - 1;
            javaWriter.emitEnumValue(field.getName(), isLast);
        }
        javaWriter.endType()
                .emitEmptyLine();

        javaWriter
                .beginConstructor(of(PUBLIC)).emitStatement("super(%s.class)", beanClassName).endConstructor()
                .emitEmptyLine()
                .beginMethod(matcherClassName, uncapitalize(beanClassName), of(PUBLIC, STATIC))
                .emitStatement("return new %s()", matcherClassName)
                .endMethod()
                .emitEmptyLine();

        for (Field field : fields) {
            String fieldName = field.getName();
            String fieldType = field.getType().getSimpleName();
            Type fieldGenericType = field.getGenericType();

            String methodName = "with" + capitalize(fieldName);

            String matcherType;
            if (fieldGenericType instanceof Class) {
                Class fieldClass = (Class) fieldGenericType;
                matcherType = "? super " + Primitives.wrap(fieldClass).getSimpleName();
            } else if (fieldGenericType instanceof ParameterizedType) {
                matcherType = "? super " + fieldType + "<Object>";
            } else {
                matcherType = "Object";
            }

            javaWriter.beginMethod(matcherClassName, methodName, of(PUBLIC), "Matcher<" + matcherType + ">", "matcher");
            javaWriter.emitStatement("registerFieldMatcher(FieldName.%s.name(), matcher)", fieldName);
            javaWriter.emitStatement("return this");
            javaWriter.endMethod().emitEmptyLine();
        }

        javaWriter
                .endType();
    }

}
