package com.hit.project.pipedream.data;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScoresTable {

    static List<ScoreRecord> _globalScoresTable;
    final static String FILE_NAME = "PipeDreamScores.dat";

    static {
        _globalScoresTable = null;
    }

    public static void saveNewRecord(String nickname,int score)
    {
        if (_globalScoresTable == null)
        {
            System.out.println("in saveNewRecord, _globalScoresTable is null!");
            return;
        }
        ScoreRecord newRecord = new ScoreRecord(nickname,score,new Date());
        _globalScoresTable.add(newRecord);
    }

    public static List<ScoreRecord> getAllScores()
    {
        return _globalScoresTable;
    }

    private static void loadFromDevice(Context context)
    {
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object dataFromFile = is.readObject();
            if (dataFromFile == null)
            {
                _globalScoresTable = new ArrayList<>();
                System.out.println("in loadFromDevice,no data on file.");
            }else {
                _globalScoresTable = (ArrayList<ScoreRecord>)dataFromFile;
            }
            is.close();
            fis.close();
        }
        catch (FileNotFoundException e) { System.out.println("in saveToDevice, FileNotFoundException:" + e.getMessage());}
        catch (IOException e) {System.out.println("in saveToDevice, IOException:" + e.getMessage());}
        catch (ClassNotFoundException e) {System.out.println("in saveToDevice, ClassNotFoundException:" + e.getMessage());}
    }

    private static void saveToDevice(Context context)
    {
        if (_globalScoresTable == null)
        {
            System.out.println("in saveToDevice, _globalScoresTable is null!");
            return;
        }
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(_globalScoresTable);
            os.close();
            fos.close();
        }
        catch (IOException e) {
            System.out.println("in saveToDevice, IOException:" + e.getMessage());
        }
    }

}
