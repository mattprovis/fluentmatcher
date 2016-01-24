package com.mattprovis.fluentmatcher;

import com.squareup.javawriter.JavaWriter;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.unitils.util.CollectionUtils;
import org.unitils.util.ReflectionUtils;

import javax.annotation.Generated;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.*;

public class FluentMatcherGenerator {

    private final Class<?> pojoClass;
    private String simpleMatcherClassName;

    public FluentMatcherGenerator(Class<?> pojoClass) {
        this.pojoClass = pojoClass;
        simpleMatcherClassName = getPojoSimpleClassName(pojoClass) + "Matcher";
    }

    public void generateMatcher(Writer writer) throws IOException {
        JavaWriter javaWriter = createJavaWriter(writer);

        List<Field> fields = getFields(pojoClass);

        MatcherClassWriter classWriter = new MatcherClassWriter(javaWriter);

        classWriter.writePackage(pojoClass);
        Class[] imports = withImportsFor(fields);
        imports = add(imports, pojoClass);
        classWriter.writeImports(imports);

        String pojoClassName = pojoClass.getCanonicalName();

        classWriter.writeClassDeclaration(pojoClassName, simpleMatcherClassName, false);
        classWriter.writeFieldsEnum(fields);
        writeMatchersForInnerTypes(classWriter, fields);
        classWriter.writeConstructor(pojoClassName);
        classWriter.writeStaticFactoryMethod(pojoClass.getSimpleName(), simpleMatcherClassName);
        classWriter.writeMatcherMethods(fields, getFullMatcherClassName());
        classWriter.writeClassFooter();
    }

    private JavaWriter createJavaWriter(Writer writer) {
        JavaWriter javaWriter = new JavaWriter(writer);
        javaWriter.setIndent("    ");
        return javaWriter;
    }

    private static String getPojoSimpleClassName(Class<?> clazz) {
        StringBuilder name = new StringBuilder();

        do {
            name.insert(0, clazz.getSimpleName());
            clazz = clazz.getEnclosingClass();
        } while (clazz != null);

        return name.toString();
    }

    private void writeMatchersForInnerTypes(MatcherClassWriter classWriter, List<Field> fields) throws IOException {

        for (Field field : fields) {
            Class<?> innerType = field.getType();
            Class<?> enclosingClass = innerType.getEnclosingClass();
            if (enclosingClass == pojoClass) {
                generateMatcherForInnerType(classWriter, innerType);
            }
        }
    }

    private void generateMatcherForInnerType(MatcherClassWriter classWriter, Class<?> innerType) throws IOException {
        String innerTypeName = innerType.getSimpleName();

        if (innerType.isEnum()) {
            return;
        }

        List<Field> fields = getFields(innerType);
        String matcherName = innerTypeName + "Matcher";
        String innerClassMatcherName = classWriter.writeClassDeclaration(innerTypeName, matcherName, true);
        classWriter.writeFieldsEnum(fields);
        classWriter.writeConstructor(innerTypeName);

        classWriter.writeStaticFactoryMethod(innerTypeName, matcherName);

        classWriter.writeMatcherMethods(fields, innerClassMatcherName);
        classWriter.writeClassFooter();
    }

    private List<Field> getFields(Class<?> type) {
        List<Field> fields = new ArrayList<>(ReflectionUtils.getAllFields(type));
        filterRelevantFields(fields);
        return fields;
    }

    /**
     * Filters out fields for which we don't want matcher methods created.
     * This currently only excludes static fields.
     *
     * @param fields
     */
    private void filterRelevantFields(List<Field> fields) {
        Iterator<Field> fieldIterator = fields.iterator();

        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            if (Modifier.isStatic(field.getModifiers())) {
                fieldIterator.remove();
            }
        }
    }

    private Class[] withImportsFor(List<Field> fields) {
        Set<Class<?>> imports = CollectionUtils.<Class<?>>asSet(
                FluentMatcher.class, Matcher.class, IsEqual.class, Generated.class);

        for (Field field : fields) {
            addTypeImports(field.getType(), imports);
        }
        return imports.toArray(new Class[imports.size()]);
    }

    private void addTypeImports(Class<?> type, Set<Class<?>> imports) {
        if (type.isArray()) {
            addTypeImports(type.getComponentType(), imports);
            return;
        }

        if (!type.isPrimitive()) {
            imports.add(type);
        }
    }

    public String getSimpleMatcherClassName() {
        return simpleMatcherClassName;
    }

    public String getFullMatcherClassName() {
        return pojoClass.getPackage().getName() + "." + simpleMatcherClassName;
    }
}
