package com.mattprovis.fluentmatcher.util.compilation;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MemoryBackedJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private ClassLoader classLoader = new MemoryBackedDelegatingClassLoader();

    private final Map<String, Bytecode> classes = new HashMap<>();

    public MemoryBackedJavaFileManager(StandardJavaFileManager standardJavaFileManager) {
        super(standardJavaFileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String name, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        Bytecode bytecode = new Bytecode(name);
        classes.put(name, bytecode);
        return bytecode;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classLoader;
    }

    class MemoryBackedDelegatingClassLoader extends ClassLoader {

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            Bytecode bytecode = classes.get(name);
            if (bytecode != null) {
                byte[] classBytes = bytecode.getBytes();
                return defineClass(name, classBytes, 0, classBytes.length);
            }

            return this.getClass().getClassLoader().loadClass(name);
        }
    }
}
