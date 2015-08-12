package com.mattprovis.fluentmatcher.util.compilation;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class Bytecode extends SimpleJavaFileObject {

    private ByteArrayOutputStream byteStream;

    public Bytecode(String name) {
        super(URI.create("byte:///" + name + ".class"), Kind.CLASS);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        throw new IllegalStateException();
    }

    @Override
    public OutputStream openOutputStream() {
        byteStream = new ByteArrayOutputStream();
        return byteStream;
    }

    public byte[] getBytes() {
        return byteStream.toByteArray();
    }
}
