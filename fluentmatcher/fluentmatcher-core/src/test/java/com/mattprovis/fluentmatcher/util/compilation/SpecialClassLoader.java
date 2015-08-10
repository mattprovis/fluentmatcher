package com.mattprovis.fluentmatcher.util.compilation;

import java.util.HashMap;
import java.util.Map;

public class SpecialClassLoader extends ClassLoader {
    private Map<String, MemoryByteCode> m = new HashMap<>();

    public Class<?> findClass(String name) throws ClassNotFoundException {
        MemoryByteCode mbc = m.get(name);
        if (mbc == null) {
            mbc = m.get(name.replace(".", "/"));
            if (mbc == null) {
                return this.getClass().getClassLoader().loadClass(name);
            }
        }
        return defineClass(name, mbc.getBytes(), 0, mbc.getBytes().length);
    }

    public void addClass(String name, MemoryByteCode mbc) {
        m.put(name, mbc);
    }
}
