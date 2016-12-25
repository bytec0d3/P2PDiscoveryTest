package it.cnr.iit.peerservicediscoverytest;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class LogManager {

    private static final String BASE_DIR = "sdcard/wiOppLogs";
    private static final String TAG = "LogManager";

    private static LogManager instance;

    enum LOG_TYPE {TYPE_BATTERY, TYPE_NETWORK}

    private LogManager(){

        if(isExternalStorageWritable()) {
            File f = new File(BASE_DIR);
            if (!f.exists() && !f.mkdir())
                Log.e(TAG, "Directory not created");
        }else{
            Log.e(TAG, "External storage is not writable!");
        }
    }

    static LogManager getInstance(){

        if(instance == null) instance = new LogManager();

        return instance;
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static List<File> getListofLogFile(){
        File baseDir = new File(BASE_DIR);
        File files[] = baseDir.listFiles();

        List<File> logFiles = new ArrayList<>();
        logFiles.addAll(Arrays.asList(files));

        return logFiles;
    }

    private static File getLogFile(LOG_TYPE type){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String fileName = "";

        switch (type){
            case TYPE_BATTERY:
                fileName = "battery_";
                break;
            case TYPE_NETWORK:
                fileName = "network_";
                break;
        }

        fileName += formatter.format(date) + ".log";

        File f = new File(BASE_DIR+"/"+fileName);
        try {
            if(!f.exists() && !f.createNewFile())
                Log.e(TAG, "Log file not created");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    void logData(String data, LOG_TYPE type){

        try {

            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(getLogFile(type), true));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            Date date = new Date();

            data = date.getTime() + "," + formatter.format(date) + "," + data;

            bufferedWriter.append(data);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();

        }catch (IOException e){
            Log.e(TAG, "Error writing log file!");
            e.printStackTrace();
        }

    }
}
