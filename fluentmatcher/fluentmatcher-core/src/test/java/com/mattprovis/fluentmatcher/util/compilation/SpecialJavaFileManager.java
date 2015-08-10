package com.mattprovis.fluentmatcher.util.compilation;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;

public class SpecialJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private SpecialClassLoader xcl;

    public SpecialJavaFileManager(StandardJavaFileManager sjfm, SpecialClassLoader xcl) {
        super(sjfm);
        this.xcl = xcl;
    }

    public JavaFileObject getJavaFileForOutput(Location location, String name, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        MemoryByteCode mbc = new MemoryByteCode(name);
        xcl.addClass(name, mbc);
        return mbc;
    }

    public ClassLoader getClassLoader(Location location) {
        return xcl;
    }
}
