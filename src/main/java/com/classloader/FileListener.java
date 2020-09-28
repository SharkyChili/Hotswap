package com.classloader;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;

public class FileListener  extends FileAlterationListenerAdaptor{

    @Override
    public void onFileChange(File file) {
        if (file.getName().indexOf(".class")!= -1){

            try {
                MyClassLoader myClassLoader = new MyClassLoader(Application.rootPath,Application.rootPath+"/com");
                   Application.stop();
                   Application.start0(myClassLoader);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
}
