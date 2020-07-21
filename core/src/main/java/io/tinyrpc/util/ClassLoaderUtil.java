package io.tinyrpc.util;

public final class ClassLoaderUtil {

    /**
     * 得到当前ClassLoader
     */
    public static ClassLoader getCurrentClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassLoaderUtil.class.getClassLoader();
        }
        return cl == null ? ClassLoader.getSystemClassLoader() : cl;
    }

    /**
     * 得到当前ClassLoader
     *
     * @param clazz 某个类
     */
    public static ClassLoader getClassLoader(final Class<?> clazz) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            return loader;
        }
        if (clazz != null) {
            loader = clazz.getClassLoader();
            if (loader != null) {
                return loader;
            }
        }
        return ClassLoader.getSystemClassLoader();
    }
}
