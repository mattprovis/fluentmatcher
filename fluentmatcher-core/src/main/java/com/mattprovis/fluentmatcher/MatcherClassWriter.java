package com.mattprovis.fluentmatcher;

import com.squareup.javawriter.JavaWriter;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import javax.annotation.Generated;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static java.util.EnumSet.of;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

public class MatcherClassWriter {

    private final Class<?> beanClass;
    private final JavaWriter javaWriter;
    private final String beanClassName;
    private final String matcherClassName;

    public MatcherClassWriter(JavaWriter javaWriter, Class<?> beanClass, String beanClassName, String matcherClassName) {
        this.javaWriter = javaWriter;
        this.beanClass = beanClass;
        this.beanClassName = beanClassName;
        this.matcherClassName = matcherClassName;
    }

    public void writeClassDeclaration(Class[] imports) throws IOException {
        String extendsType = FluentMatcher.class.getSimpleName() + "<" + beanClassName + ">";

        String valueAttribute = "value = \"" + FluentMatcherGenerator.class.getName() + "\"";
        String dateAttribute = "date = \"" + new DateTime().toString(ISODateTimeFormat.dateTime()) + "\"";
        String generatedAnnotationAttributes = valueAttribute + ", " + dateAttribute;

        javaWriter
                .emitPackage(beanClass.getPackage().getName())
                .emitImports(imports)
                .emitEmptyLine()
                .emitAnnotation(Generated.class, generatedAnnotationAttributes)
                .beginType(matcherClassName, "class", of(PUBLIC), extendsType)
                .emitEmptyLine();
    }

    public void writeFieldsEnum(List<Field> fields) throws IOException {
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

    public void writeConstructor() throws IOException {
        javaWriter
                .beginConstructor(of(PRIVATE)).emitStatement("super(%s.class)", beanClassName).endConstructor()
                .emitEmptyLine();
    }

    public void writeStaticFactoryMethod() throws IOException {
        javaWriter
                .beginMethod(matcherClassName, uncapitalize(beanClassName), of(PUBLIC, STATIC))
                .emitStatement("return new %s()", matcherClassName)
                .endMethod()
                .emitEmptyLine();
    }

    public void writeClassFooter() throws IOException {
        javaWriter.endType();
    }
}
