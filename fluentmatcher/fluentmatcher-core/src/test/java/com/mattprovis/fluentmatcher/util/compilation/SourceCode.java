package com.mattprovis.fluentmatcher.util.compilation;

import org.apache.commons.lang3.StringUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

public class SourceCode extends SimpleJavaFileObject {
    private String sourceCode;

    public SourceCode(String name, String sourceCode) {
        super(createURI(name), Kind.SOURCE);
        this.sourceCode = sourceCode;
    }

    private static URI createURI(String name) {
        String simpleClassName = StringUtils.substringAfterLast(name, ".");
        return URI.create(simpleClassName + ".java");
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceCode;
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(sourceCode.getBytes());
    }
}
