package be.hvwebsites.itembord.services;

import android.content.Context;

public class FileBaseService {
    private String fileBaseDir;

    public FileBaseService(String deviceModel, Context inContext) {
        // packageNm en path zijn overbodig geworden !!

        // Bepaal filepath
        if (deviceModel.equals("GT-I9100")){
            // Internal files
            this.fileBaseDir = inContext.getFilesDir().getPath();
        }else {
            // External files
            this.fileBaseDir = inContext.getExternalFilesDir(null).getAbsolutePath();
        }

        boolean debug = true;
    }

    public String getFileBaseDir() {
        return fileBaseDir;
    }
}
