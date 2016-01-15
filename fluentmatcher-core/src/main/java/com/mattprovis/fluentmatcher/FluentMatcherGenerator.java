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

public class FluentMatcherGenerator {

    private final Class<?> beanClass;
    private final JavaWriter javaWriter;
    private final String beanClassName;
    private final String matcherClassName;

    public FluentMatcherGenerator(Class<?> beanClass, Writer writer) {
        javaWriter = new JavaWriter(writer);
        javaWriter.setIndent("    ");
        this.beanClass = beanClass;
        beanClassName = beanClass.getSimpleName();
        matcherClassName = beanClassName + "Matcher";
    }

    public void generateMatcher() throws IOException {
        List<Field> fields = new ArrayList<>(ReflectionUtils.getAllFields(beanClass));
        filterRelevantFields(fields);

        MatcherClassWriter classWriter = new MatcherClassWriter(javaWriter, beanClass, beanClassName, matcherClassName);
        classWriter.writeClassDeclaration(withImportsFor(fields));
        classWriter.writeFieldsEnum(fields);
        classWriter.writeConstructor();
        classWriter.writeStaticFactoryMethod();
        writeMatcherMethods(fields);
        classWriter.writeClassFooter();
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

    private void writeMatcherMethods(List<Field> fields) throws IOException {
        for (Field field : fields) {
            MatcherMethodWriter matcherMethodWriter = new MatcherMethodWriter(javaWriter, matcherClassName, field);
            matcherMethodWriter.writeWithValueMethod();
            matcherMethodWriter.writeWithMatcherMethod();
            matcherMethodWriter.writeIsAndIsNotMethodsForBoolean();
        }
    }
}
