package com.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 热部署
 */
public class MyClassLoader extends ClassLoader {

    //目的 让缓存里面永远能返回一个Class对象 这样就不需要走父类加载器了
    //在构造方法里面加载类  loadClass

    //项目的根路径
    public  String rootPath;

    //所有需要由我这个类加载器加载的类存在这个集合
    public List<String> clazzs;
    //两个classloader  一个是负责加载 需要被热部署的代码的
    //一个是加载系统的一些类的

        //classPaths: 需要被热部署的加载器去加载的目录
    public MyClassLoader(String rootPath,String... classPaths) throws Exception{
        this.rootPath = rootPath;
        this.clazzs = new ArrayList<>();

        for (String classPath : classPaths) {
            scanClassPath(new File(classPath));
        }
    }


    //扫描项目里面传进来的一些class
    public void scanClassPath(File file) throws Exception{
        if (file.isDirectory()){
            for (File file1 : file.listFiles()) {
                scanClassPath(file1);
            }
        }else{
            String fileName = file.getName();
            String filePath = file.getPath();
            String  endName = fileName.substring(fileName.lastIndexOf(".")+1);
            if (endName.equals("class")){
                //现在我们加载到的是一个Class文件
                //如何吧一个Class文件 加载成一个Class对象？？？？
                InputStream inputStream = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                inputStream.read(bytes);

                String className = fileNameToClassName(filePath);
                //文件名转类名
                //类名
                 defineClass(className, bytes, 0, bytes.length);
                clazzs.add(className);
                //loadClass 是从当前ClassLoader里面去获取一个Class对象
            }
        }

    }


    public  String fileNameToClassName(String filePath){
        //d: //project//com//luban//xxxx
        String className = filePath.replace(rootPath,"").replaceAll("\\\\",".");
//        com.luban.className.class
        className  =  className.substring(1,className.lastIndexOf("."));
        return className;
        //com.luban.classNamexxxx
    }
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> loadClass = findLoadedClass(name);
        //第一情况 这个类 不需要由我们加载
        //第二种情况 这个类需要由我们加载 但是 确实加载不到
        if (loadClass ==null){
            if (!clazzs.contains(name)){
                loadClass = getSystemClassLoader().loadClass(name);
            }else{
                throw  new ClassNotFoundException("没有加载到类");
            }
        }
        return loadClass;
    }

        //先做热替换  先加载单个Class

        //new Test().xxx

        //当文件被修改的时候再进行热部署

    public static void main(String[] args)  throws Exception{

            //双亲委派机制

        Application.run(MyClassLoader.class);

//        new Test();

        //给一个程序入口

//         while (true){
//             MyClassLoader myClassLoader = new MyClassLoader(rootPath,rootPath+"/com");
//             Class<?> aClass = myClassLoader.loadClass("com.Test");
//             aClass.getMethod("test").invoke(aClass.newInstance());
//             new Test().test();
//
//
//
//             Thread.sleep(2000);
//         }


    }
}
