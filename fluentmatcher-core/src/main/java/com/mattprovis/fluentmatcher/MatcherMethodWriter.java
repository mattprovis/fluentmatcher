package com.mattprovis.fluentmatcher;

import com.google.common.primitives.Primitives;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static java.util.EnumSet.of;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apache.commons.lang3.StringUtils.capitalize;

public class MatcherMethodWriter {
    private JavaWriter javaWriter;
    private Field field;
    private String matcherReturnType;

    public MatcherMethodWriter(JavaWriter javaWriter, String matcherClassName, Field field) {
        this.javaWriter = javaWriter;
        this.matcherReturnType = "<SubclassOfMatcher extends " + matcherClassName + "> SubclassOfMatcher";
        this.field = field;
    }

    public void writeWithValueMethod() throws IOException {
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

        javaWriter.beginMethod(matcherReturnType, methodName, of(PUBLIC), fieldType, "expectedValue");
        javaWriter.emitStatement("registerFieldMatcher(FieldName.%s.name(), IsEqual.equalTo(expectedValue))", fieldName);
        javaWriter.emitStatement("return (SubclassOfMatcher) this");
        javaWriter.endMethod().emitEmptyLine();
    }

    public void writeWithMatcherMethod() throws IOException {
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

        javaWriter.beginMethod(matcherReturnType, methodName, of(PUBLIC), "Matcher<" + matcherType + ">", "matcher");
        javaWriter.emitStatement("registerFieldMatcher(FieldName.%s.name(), matcher)", fieldName);
        javaWriter.emitStatement("return (SubclassOfMatcher) this");
        javaWriter.endMethod().emitEmptyLine();
    }

    public void writeIsAndIsNotMethodsForBoolean() throws IOException {
        if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
            String fieldName = field.getName();

            String methodName = "is" + capitalize(fieldName);
            javaWriter.beginMethod(matcherReturnType, methodName, of(PUBLIC));
            javaWriter.emitStatement("registerFieldMatcher(FieldName.%s.name(), IsEqual.equalTo(true))", fieldName);
            javaWriter.emitStatement("return (SubclassOfMatcher) this");
            javaWriter.endMethod().emitEmptyLine();

            methodName = "isNot" + capitalize(fieldName);
            javaWriter.beginMethod(matcherReturnType, methodName, of(PUBLIC));
            javaWriter.emitStatement("registerFieldMatcher(FieldName.%s.name(), IsEqual.equalTo(false))", fieldName);
            javaWriter.emitStatement("return (SubclassOfMatcher) this");
            javaWriter.endMethod().emitEmptyLine();
        }
    }
}
