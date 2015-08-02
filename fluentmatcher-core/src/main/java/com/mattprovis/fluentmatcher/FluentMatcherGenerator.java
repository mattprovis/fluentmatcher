package com.mattprovis.fluentmatcher;

import com.google.common.primitives.Primitives;
import com.squareup.javawriter.JavaWriter;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.unitils.util.ReflectionUtils;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.EnumSet.of;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

public class FluentMatcherGenerator {

    private FluentMatcherGenerator() { }

    public static void generateMatcherFor(Class<?> beanClass, Writer out) throws IOException {
        JavaWriter javaWriter = new JavaWriter(out);

        String beanClassName = beanClass.getSimpleName();
        String matcherClassName = beanClassName + "Matcher";

        List<Field> fields = new ArrayList<>(ReflectionUtils.getAllFields(beanClass));

        writeClassDeclaration(javaWriter, beanClass, beanClassName, matcherClassName, getImports(fields));

        writeFieldsEnum(javaWriter, fields);

        writeConstructor(javaWriter, beanClassName);

        writeStaticFactoryMethod(javaWriter, beanClassName, matcherClassName);

        for (Field field : fields) {

            writeWithValueMethodForField(javaWriter, matcherClassName, field);
            writeWithMatcherMethodForField(javaWriter, matcherClassName, field);

            if (asList(Boolean.class, boolean.class)
                    .contains(field.getType())) {
                writeIsAndIsNotMethodsForBooleanField(javaWriter, matcherClassName, field);
            }
        }

        writeClassFooter(javaWriter);
    }

    private static Class[] getImports(List<Field> fields) {
        HashSet<Class<?>> imports = new HashSet<>();
        imports.add(FluentMatcher.class);
        imports.add(Matcher.class);
        imports.add(IsEqual.class);

        for (Field field : fields) {
            addTypeImports(field.getType(), imports);
        }
        return imports.toArray(new Class[imports.size()]);
    }

    private static void addTypeImports(Class<?> type, HashSet<Class<?>> imports) {
        if (type.isArray()) {
            addTypeImports(type.getComponentType(), imports);
            return;
        }

        if (!type.isPrimitive()) {
            imports.add(type);
            return;
        }
    }

    private static void writeClassDeclaration(JavaWriter javaWriter, Class<?> beanClass, String beanClassName, String matcherClassName, Class[] imports) throws IOException {
        String extendsType = FluentMatcher.class.getSimpleName() + "<" + beanClassName + ">";
        javaWriter
                .emitPackage(beanClass.getPackage().getName())
                .emitImports(imports)
                .emitEmptyLine()
                .beginType(matcherClassName, "class", of(PUBLIC), extendsType)
                .emitEmptyLine();
    }

    private static void writeFieldsEnum(JavaWriter javaWriter, List<Field> fields) throws IOException {
        javaWriter
                .beginType("FieldName", "enum", of(PRIVATE));

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            boolean isLast = i == fields.size() - 1;
            javaWriter.emitEnumValue(field.getName(), isLast);
        }
        javaWriter.endType()
                .emitEmptyLine();
    }

    private static void writeConstructor(JavaWriter javaWriter, String beanClassName) throws IOException {
        javaWriter
                .beginConstructor(of(PRIVATE)).emitStatement("super(%s.class)", beanClassName).endConstructor()
                .emitEmptyLine();
    }

    private static void writeStaticFactoryMethod(JavaWriter javaWriter, String beanClassName, String matcherClassName) throws IOException {
        javaWriter
                .beginMethod(matcherClassName, uncapitalize(beanClassName), of(PUBLIC, STATIC))
                .emitStatement("return new %s()", matcherClassName)
                .endMethod()
                .emitEmptyLine();
    }

    private static void writeWithValueMethodForField(JavaWriter javaWriter, String matcherClassName, Field field) throws IOException {
        String fieldName = field.getName();
        String methodName = "with" + capitalize(fieldName);

        String fieldType = null;
        Type fieldGenericType = field.getGenericType();
        if (field.getType().isArray()) {
            fieldType = field.getType().getCanonicalName();
        } else if (fieldGenericType instanceof Class) {
            Class fieldClass = (Class) fieldGenericType;
            fieldType = Primitives.wrap(fieldClass).getName();
        } else if (fieldGenericType instanceof ParameterizedType) {
            ParameterizedType fieldClass = (ParameterizedType) fieldGenericType;
            Type rawType = fieldClass.getRawType();
            if (rawType instanceof Class) {
                Type[] typeArguments = fieldClass.getActualTypeArguments();

                String genericType = "";
                for (int i = 0; i < typeArguments.length; i++) {
                    Type typeArgument = typeArguments[i];
                    if (typeArgument instanceof Class) {
                        Class typeArgumentClass = (Class) typeArgument;
                        genericType += (i == 0 ? "" : ", ") + "? extends " + typeArgumentClass.getName();
                    }
                }

                fieldType = ((Class) rawType).getName() + "<" + genericType + ">";
            }
        }

        if (fieldType == null) {
            fieldType = "Object";
        }

        javaWriter.beginMethod(matcherClassName, methodName, of(PUBLIC), fieldType, "expectedValue");
        javaWriter.emitStatement("registerFieldMatcher(FieldName.%s.name(), IsEqual.equalTo(expectedValue))", fieldName);
        javaWriter.emitStatement("return this");
        javaWriter.endMethod().emitEmptyLine();
    }

    private static void writeWithMatcherMethodForField(JavaWriter javaWriter, String matcherClassName, Field field) throws IOException {
        String fieldName = field.getName();
        String methodName = "with" + capitalize(fieldName);

        String matcherType = null;
        Type fieldGenericType = field.getGenericType();
        if (field.getType().isArray()) {
            matcherType = field.getType().getCanonicalName();
        } else if (fieldGenericType instanceof Class) {
            Class fieldClass = (Class) fieldGenericType;
            matcherType = "? super " + Primitives.wrap(fieldClass).getName();
        } else if (fieldGenericType instanceof ParameterizedType) {
            ParameterizedType fieldClass = (ParameterizedType) fieldGenericType;
            Type rawType = fieldClass.getRawType();
            if (rawType instanceof Class) {
                matcherType = "? super " + ((Class) rawType).getName() + "<Object>";
            }
        }

        if (matcherType == null) {
            matcherType = "Object";
        }

        javaWriter.beginMethod(matcherClassName, methodName, of(PUBLIC), "Matcher<" + matcherType + ">", "matcher");
        javaWriter.emitStatement("registerFieldMatcher(FieldName.%s.name(), matcher)", fieldName);
        javaWriter.emitStatement("return this");
        javaWriter.endMethod().emitEmptyLine();
    }

    private static void writeIsAndIsNotMethodsForBooleanField(JavaWriter javaWriter, String matcherClassName, Field field) throws IOException {
        String fieldName = field.getName();

        String methodName = "is" + capitalize(fieldName);
        javaWriter.beginMethod(matcherClassName, methodName, of(PUBLIC));
        javaWriter.emitStatement("registerFieldMatcher(FieldName.%s.name(), IsEqual.equalTo(true))", fieldName);
        javaWriter.emitStatement("return this");
        javaWriter.endMethod().emitEmptyLine();

        methodName = "isNot" + capitalize(fieldName);
        javaWriter.beginMethod(matcherClassName, methodName, of(PUBLIC));
        javaWriter.emitStatement("registerFieldMatcher(FieldName.%s.name(), IsEqual.equalTo(false))", fieldName);
        javaWriter.emitStatement("return this");
        javaWriter.endMethod().emitEmptyLine();
    }

    private static void writeClassFooter(JavaWriter javaWriter) throws IOException {
        javaWriter.endType();
    }
}
