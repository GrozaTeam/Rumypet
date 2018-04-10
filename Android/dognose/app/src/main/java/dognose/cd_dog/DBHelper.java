package dognose.cd_dog;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by paeng on 2018. 4. 2..
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE OWNERLIST (_id INTEGER PRIMARY KEY AUTOINCREMENT, loginId TEXT, loginPw TEXT, ownerName TEXT, ownerPhone TEXT);");
        db.execSQL("CREATE TABLE DOGLIST (_id INTEGER PRIMARY KEY AUTOINCREMENT, ownerId TEXT, name TEXT, species TEXT, gender TEXT, birth TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertOwner(String loginId, String loginPw, String ownerName, String ownerPhone){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO OWNERLIST VALUES(null, '" + loginId + "', '" + loginPw + "', '" + ownerName + "' , '"+ ownerPhone+"');");
        db.close();
    }
    public void insertDog(String ownerId, String name, String species, String gender, String birth){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO DOGLIST VALUES(null, '" + ownerId + "', '" + name + "', '" + species + "' , '"+ gender + "' , '"+birth+"');");
        db.close();
    }

    public String[] getAllDataOwnerForChecker(){

        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        String[] resultList = new String[1000];

        Cursor cursor = db.rawQuery("SELECT * FROM OWNERLIST", null);
        int i = 0;
        while (cursor.moveToNext()) {
            result ="id: "
                    + cursor.getString(0)
                    + " || ID : "
                    + cursor.getString(1)
                    + " || PW : "
                    + cursor.getString(2)
                    + "\n || Name: "
                    + cursor.getString(3)
                    + " || Phone: "
                    + cursor.getString(4);

            resultList[i] = result;
            i++;
        }
        return resultList;
    }

    public String[] getAllDataDogForChecker(){



        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        String[] resultList = new String[1000];


        Cursor cursor = db.rawQuery("SELECT * FROM DOGLIST", null);


        int i = 0;
        while (cursor.moveToNext()) {
            result ="id: "
                    + cursor.getString(0)
                    + " || ownerID: "
                    + cursor.getString(1)
                    + " || Name: "
                    + cursor.getString(2)
                    + "\n || Species: "
                    + cursor.getString(3)
                    + " || Gender: "
                    + cursor.getString(4)
                    + " || Birth: "
                    + cursor.getString(5);

            resultList[i] = result;
            i++;
        }

        return resultList;
    }

    public String getPwById(String OwnerId){
        SQLiteDatabase db = getReadableDatabase();
        String result;
        Cursor cursor = db.rawQuery("SELECT * FROM OWNERLIST WHERE loginId = '"+OwnerId+"';", null);
        cursor.moveToFirst();
        result = cursor.getString(2);


        return result;
    }
    public String[] getResultOwnerDogList(String ownerId){

        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        String[] resultList = new String[1000];

        Cursor cursor = db.rawQuery("SELECT * FROM DOGLIST WHERE ownerId = '"+ownerId+"';", null);
        int i = 0;
        while (cursor.moveToNext()) {
            result = cursor.getString(0)
                    + "/"
                    + cursor.getString(1)
                    + "/"
                    + cursor.getString(2)
                    + "/"
                    + cursor.getString(3)
                    + "/"
                    + cursor.getString(4)
                    + "/"
                    + cursor.getString(5);

            resultList[i] = result;
            i++;
        }
        return resultList;
    }


    public String[] getAllIdForDoubleCheck(){
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        String[] resultList = new String[1000];

        Cursor cursor = db.rawQuery("SELECT * FROM OWNERLIST", null);

        int i = 0;
        while (cursor.moveToNext()) {
            result = cursor.getString(1);

            resultList[i] = result;
            i++;
        }
        return resultList;
    }
}
