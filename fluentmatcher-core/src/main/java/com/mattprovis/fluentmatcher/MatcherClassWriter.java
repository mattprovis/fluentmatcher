package com.mattprovis.fluentmatcher;

import com.squareup.javawriter.JavaWriter;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.List;

import static java.util.EnumSet.of;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

public class MatcherClassWriter {

    private final JavaWriter javaWriter;
    private final String generatedAnnotationAttributes;

    public MatcherClassWriter(JavaWriter javaWriter) {
        this.javaWriter = javaWriter;
        generatedAnnotationAttributes = getGeneratedAnnotationAttributes();
    }

    public void writePackage(Class<?> pojoClass) throws IOException {
        javaWriter
                .emitPackage(pojoClass.getPackage().getName());
    }

    public void writeImports(Class[] imports) throws IOException {
        javaWriter
                .emitImports(imports);
    }

    public String writeClassDeclaration(String pojoClassName, String matcherName, boolean isInner) throws IOException {
        String extendsType = FluentMatcher.class.getSimpleName() + "<" + pojoClassName + ">";

        EnumSet<Modifier> modifiers = isInner ? of(STATIC, PUBLIC) : of(PUBLIC);

        javaWriter
                .emitEmptyLine()
                .emitAnnotation(Generated.class, generatedAnnotationAttributes)
                .beginType(matcherName, "class", modifiers, extendsType)
                .emitEmptyLine();

        return matcherName;
    }

    public void writeFieldsEnum(List<Field> fields) throws IOException {
        javaWriter
                .beginType("FieldName", "enum", of(PROTECTED));

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            boolean isLast = i == fields.size() - 1;
            javaWriter.emitEnumValue(field.getName(), isLast);
        }
        javaWriter.endType()
                .emitEmptyLine();
    }

    public void writeConstructor(String pojoClassName) throws IOException {
        javaWriter
                .beginConstructor(of(PROTECTED)).emitStatement("super(%s.class)", pojoClassName).endConstructor()
                .emitEmptyLine();
    }

    public void writeStaticFactoryMethod(String className, String matcherName) throws IOException {
        javaWriter
                .beginMethod(matcherName, uncapitalize(className), of(PUBLIC, STATIC))
                .emitStatement("return new %s()", matcherName)
                .endMethod()
                .emitEmptyLine();
    }

    public void writeClassFooter() throws IOException {
        javaWriter.endType();
        javaWriter.emitEmptyLine();
    }

    private String getGeneratedAnnotationAttributes() {
        String valueAttribute = "value = \"" + FluentMatcherGenerator.class.getName() + "\"";
        String dateAttribute = "date = \"" + new DateTime().toString(ISODateTimeFormat.dateTime()) + "\"";
        return valueAttribute + ", " + dateAttribute;
    }

    public void writeMatcherMethods(List<Field> fields, String matcherClassName) throws IOException {
        for (Field field : fields) {
            MatcherMethodWriter matcherMethodWriter = new MatcherMethodWriter(javaWriter, matcherClassName, field);
            matcherMethodWriter.writeWithValueMethod();
            matcherMethodWriter.writeWithMatcherMethod();
            matcherMethodWriter.writeIsAndIsNotMethodsForBoolean();
        }
    }
}
