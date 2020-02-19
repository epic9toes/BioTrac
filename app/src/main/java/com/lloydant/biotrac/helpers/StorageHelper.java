package com.lloydant.biotrac.helpers;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class StorageHelper {

    Context mContext;

    public StorageHelper(Context context) {
        mContext = context;
    }

    public boolean checkUserFolderExist(String studentID){
        if (mContext.getFilesDir().exists()){
            File folder = new File(mContext.getFilesDir() + "/" + studentID);
            if (folder.exists()){
                return true;
            }
            return false;
        }
        return false;
    }

    public String createUserFolder(String studentID){
        String pathname = mContext.getFilesDir() + "/" + studentID + "/";
        if (!checkUserFolderExist(studentID)){
            File folder = new File(pathname);
            folder.mkdir();
        }
        return pathname;
    }

    public String saveJsonFile(String filename, String content, String owner){

//        Create the file
        File file = new File(createUserFolder(owner), filename + ".json");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();

            return "Saved!";

        } catch (FileNotFoundException e) {
            return "Error: " + e.getMessage();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getFilePath(String filename, String owner) {
        String pathname = mContext.getFilesDir() + "/" + owner + "/" + filename;
        File file = new File(pathname);
        String dir = file.getAbsolutePath();
        return dir;
    }


    public String readJsonFile(String owner, String filename){
        File file = new File(createUserFolder(owner) + filename);
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }

            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
