package com.pdc.spring.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类操作工具类
 * @author pdc
 */
public final class ClassUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器，如果使用了Tomcat，就不怕使用的是SharedClassLoader，导致加载不到类了
     * 即获取当前线程中的ClassLoader
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     * 初始化是指是否执行类的静态代码块
     * 为了提高加载类的性能，可将isInitialized设置为false
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class failure", e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 加载类（默认将初始化类）
     */
    public static Class<?> loadClass(String className) {
        return loadClass(className, true);
    }

    /**
     * 获取指定包名下的所有类
     * 通过ClassLoader来查找指定包
     * 如果是在classes文件夹下的class文件，则用遍历文件的方式来获取该包下的所有类名。
     * 如果这个包名是jar包里面的，那么需要通过遍历jar包内文件的方式来获取该包下的所有类的类名
     * 类、包名为.,目录为/
     */
    public static Set<Class<?>> getClassSet(String packageName) {
        Set<Class<?>> classSet = new HashSet<>();
        try {
            //配置文件里的定义用.分隔
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();//协议
                    if ("file".equals(protocol)) {
                        String packagePath = url.getPath();
                        //H:/mySpring/test5/target/test5-1.0.0/WEB-INF/classes/com/pdc/test5/
                        System.out.println(packagePath);
                        addClass(classSet, packagePath, packageName);
                    } else if ("jar".equals(protocol)) {
                        JarURLConnection jarUrlConnection = (JarURLConnection) url.openConnection();
                        if (jarUrlConnection != null) {
                            JarFile jarFile = jarUrlConnection.getJarFile();//读取Jar包下的文件
                            if (jarFile != null) {
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")) {
                                        String className = jarEntryName.//去掉.class，得到类全限定名
                                                substring(0, jarEntryName.lastIndexOf(".")).
                                                replaceAll("/", ".");
                                        doAddClass(classSet, className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("get class set failure", e);
            throw new RuntimeException(e);
        }
        return classSet;
    }

    /**
     * 添加包下的类
     * @param classSet 用来保存所有加载到JVM的类的集合
     * @param packagePath 用来获取路径下的文件
     * @param packageName 需要加载的类的包名
     */
    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        //获取包下的所有.class文件或文件夹，分别处理
        File[] files = new File(packagePath).listFiles(file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtil.isNotEmpty(packageName)) {
                    className = packageName + "." + className;
                }
                doAddClass(classSet, className);
            } else {//若当前包下的内容不为文件，则递归判断其子包，直到为文件,注意要为全限定名
                String subPackagePath = fileName;
                if (StringUtil.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtil.isNotEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet, subPackagePath, subPackageName);
            }
        }
    }

    /**
     * 添加指定类名的类,因为并不是所有类都需要加载（只有controller、service、factory），所以默认为false
     * @param classSet
     * @param className
     */
    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }
}