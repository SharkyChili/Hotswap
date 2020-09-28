package com.cacheclassloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 让缓存永远返回一个class对象，这样就不会走父类加载器
 * 实现方法：在构造方法里加载类
 */
public class CacheClassLoader extends ClassLoader {

    public String rootPath;
    public List<String> clazzs;

    public CacheClassLoader(String rootPath, String... classPaths) throws Exception{
        //classPaths:需要被热部署的目录
        this.rootPath = rootPath;
        this.clazzs = new ArrayList<>();

        for (String classPath : classPaths) {
            scanClassPath(new File(classPath));
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> aClass = findLoadedClass(name);
        if(aClass == null){
            //这个类不需要由本类加载器加载
            if(!clazzs.contains(name)){
                aClass = getSystemClassLoader().loadClass(name);
            }else {
                //这个类需要由我们加载，但是确实加载不到
                throw new ClassNotFoundException("没有加载到类");
            }
        }
        return aClass;
    }

    public void scanClassPath(File file) throws Exception{
        if(file.isDirectory()){
            for (File listFile : file.listFiles()) {
                scanClassPath(listFile);
            }
        }else {
            String fileName = file.getName();
            String filePath = file.getPath();
            String endName = fileName.substring(fileName.lastIndexOf(".") + 1);
            if(endName.equals("class")){
                //现在如何把class文件加载成class对象
                InputStream inputStream = new FileInputStream(file);
                byte[] bytes = new byte[(int)file.length()];
                inputStream.read(bytes);

                String className = fileNameToClassName(filePath);

                //加载成class对象且放入缓存
                Class<?> aClass = defineClass(className, bytes, 0, bytes.length);
                clazzs.add(className);
            }

        }
    }

    public String fileNameToClassName(String filePath){
        String replace = filePath.replace(rootPath, "").replaceAll("\\\\",".");
        String className = replace.substring(1, replace.lastIndexOf("."));
        return className;
    }

    //先做热替换
    //再做热部署
    public static void main(String[] args) throws Exception{
        String rootPath = CacheClassLoader.class.getResource("/").getPath();
        //将 / 转换为 \\
        rootPath = new File(rootPath).getPath();

        while(true){
            CacheClassLoader cacheClassLoader = new CacheClassLoader(rootPath, rootPath + "/com/cacheclassloader");
            Class<?> aClass = cacheClassLoader.loadClass("com.cacheclassloader.CacheTest");
            aClass.getMethod("test").invoke(aClass.newInstance());

            Thread.sleep(2000);
        }
    }
}
