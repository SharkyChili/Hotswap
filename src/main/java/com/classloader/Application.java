package com.classloader;


import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

public class Application {



    public static String rootPath;



    public  static void run(Class<?> clazz) throws Exception{
        String rootPath = MyClassLoader.class.getResource("/").getPath().replaceAll("%20"," ");
        //   /    \
        rootPath = new File(rootPath).getPath();
        Application.rootPath = rootPath;
        MyClassLoader myClassLoader = new MyClassLoader(rootPath,rootPath+"/com");
        //用我们自己的类加载器加载程序入口
        startFileListener(rootPath);
        start0(myClassLoader);

    }


    public static   void  startFileListener(String rootPath) throws Exception {
        FileAlterationObserver fileAlterationObserver = new FileAlterationObserver(rootPath);
        fileAlterationObserver.addListener(new FileListener());
        FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor(500);
        fileAlterationMonitor.addObserver(fileAlterationObserver);
        fileAlterationMonitor.start();

        //要实现文件监听：  写一个线程 去定时监听某个路径下所有的文件
        //如果文件发生改动 就回调监听器

//        fileAlterationMonitor.getInterval()
    }


    //新的classload
    public static void start(){
        System.out.println("启动我们的应用程序");
        //Tomcat tomcat = new Tomcat();

        //Controller ...xxxx
        new Test().test();
    }


    public static void stop(){

//        ApplicationContext.stop;
        System.out.println("程序退出");
        //告诉jvm需要gc了
        System.gc();
        //告诉jvm可以清除对象引用
        System.runFinalization();
    }


//    @Override
//    protected void finalize() throws Throwable {
//        super.finalize();
//    }

    public static void start0(MyClassLoader classLoader) throws Exception {

        Class<?> aClass = classLoader.loadClass("com.classloader.Application");

        aClass.getMethod("start").invoke(aClass.newInstance());

    }



}
