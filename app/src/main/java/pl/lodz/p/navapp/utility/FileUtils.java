package pl.lodz.p.navapp.utility;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static pl.lodz.p.navapp.utility.ApplicationConstants.FILE_READ_ERROR;
import static pl.lodz.p.navapp.utility.ApplicationConstants.VERSION_FILE_NAME;

/**
 * Created by Łukasz Świtoń on 09.12.2016.
 */

public class FileUtils {
    public static void writeVersionToFile(int data, Context context) {
        try {
            String version = String.valueOf(data);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(VERSION_FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(version);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static int readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(VERSION_FILE_NAME);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            return FILE_READ_ERROR;
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            return FILE_READ_ERROR;
        }

        return Integer.parseInt(ret);
    }
}
