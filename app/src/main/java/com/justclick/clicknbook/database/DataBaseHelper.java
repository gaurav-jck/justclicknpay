package com.justclick.clicknbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.justclick.clicknbook.model.JctIfscByCodeResponse;
import com.justclick.clicknbook.model.OptModel;
import com.justclick.clicknbook.model.RblBankDetailByIFSCResponse;
import com.justclick.clicknbook.model.RblIfscByCodeResponse;
import com.justclick.clicknbook.model.RblRelationResponse;

import java.util.ArrayList;

/**
 * Created by tech on 7/13/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "JustClickKaro";
    public static final String PRIMARY_KEY_ID = "KeyId";

    public static final String TABLE_LOGIN_ID = "LoginIdTable";
    public static final String KEY_LOGIN_ID = "LoginId";

    public static final String TABLE_STATES_NAMES = "StateNameTable";
    public static final String KEY_STATE_NAME = "StateName";

    public static final String TABLE_CITY_NAMES = "CityNameTable";
    public static final String KEY_CIY_NAME = "CityName";

    public static final String TABLE_RBL_RELATION = "RblRelationTable";
    public static final String KEY_RELATION_NAME = "RelationName";
    public static final String KEY_RELATION_KEY = "RelationKey";

    public static final String TABLE_RBL_BANKS = "RblBanksTable";
    public static final String KEY_RBL_BANK_NAME = "RblBankName";
    public static final String KEY_RBL_BANK_KEY = "RblBankKey";
    public static final String KEY_RBL_BANK_DIGIT = "RblBankDigit";

    public static final String TABLE_OPERATOR_NAME = "OperatorNameTable";
    public static final String KEY_OPERATOR_TYPE = "OperatorType";
    public static final String KEY_OPERATOR_ID = "OperatorId";
    public static final String KEY_OPERATOR_NAME = "OperatorName";

    public static final String TABLE_JCT_BANKS = "JctBanksTable";
    public static final String KEY_JCT_BANK_NAME = "JctBankName";
    public static final String KEY_JCT_BANK_KEY = "JctBankKey";
    public static final String KEY_JCT_BANK_DIGIT = "JctBankDigit";


    private Context context;

    public DataBaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
//        Toast.makeText(context, "DATABASE_NAME", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_STATE_TABLE = "CREATE TABLE " + TABLE_STATES_NAMES + "( "
                + PRIMARY_KEY_ID + " INTEGER PRIMARY KEY ,"
                + KEY_STATE_NAME + " TEXT NOT NULL , " +
                "CONSTRAINT LoginUnique UNIQUE ("+KEY_STATE_NAME+"))";

        String SQL_CREATE_CITY_TABLE = "CREATE TABLE " + TABLE_CITY_NAMES + "( "
                + PRIMARY_KEY_ID + " INTEGER PRIMARY KEY ,"
                + KEY_CIY_NAME + " TEXT NOT NULL , " +
                "CONSTRAINT LoginUnique UNIQUE ("+KEY_CIY_NAME+"))";

        String SQL_CREATE_RBL_BANKS_TABLE = "CREATE TABLE " + TABLE_RBL_BANKS + "( "
                + PRIMARY_KEY_ID + " INTEGER PRIMARY KEY ,"
                + KEY_RBL_BANK_NAME + " TEXT NOT NULL , "
                + KEY_RBL_BANK_KEY + " TEXT NOT NULL , "
                + KEY_RBL_BANK_DIGIT + " TEXT NOT NULL , " +
                "CONSTRAINT LoginUnique UNIQUE ("+KEY_RBL_BANK_NAME+"))";

        String SQL_CREATE_OPERATOR_TABLE = "CREATE TABLE " + TABLE_OPERATOR_NAME + "( "
                + PRIMARY_KEY_ID + " INTEGER PRIMARY KEY ,"
                + KEY_OPERATOR_TYPE + " INTEGER NOT NULL , "
                + KEY_OPERATOR_ID + " TEXT NOT NULL , "
                + KEY_OPERATOR_NAME + " TEXT NOT NULL , " +
                "CONSTRAINT OperatorUnique UNIQUE ("+KEY_OPERATOR_TYPE+","+KEY_OPERATOR_NAME+"))";

        String SQL_CREATE_JCT_BANKS_TABLE = "CREATE TABLE " + TABLE_JCT_BANKS + "( "
                + PRIMARY_KEY_ID + " INTEGER PRIMARY KEY ,"
                + KEY_JCT_BANK_NAME + " TEXT NOT NULL , "
                + KEY_JCT_BANK_KEY + " TEXT NOT NULL , "
                + KEY_JCT_BANK_DIGIT + " TEXT NOT NULL , " +
                "CONSTRAINT LoginUnique UNIQUE ("+KEY_JCT_BANK_NAME+"))";


//        Toast.makeText(context, "onCreate", Toast.LENGTH_LONG).show();
        db.execSQL(SQL_CREATE_STATE_TABLE);
        db.execSQL(SQL_CREATE_CITY_TABLE);
        db.execSQL(SQL_CREATE_RBL_BANKS_TABLE);
        db.execSQL(SQL_CREATE_OPERATOR_TABLE);
        db.execSQL(SQL_CREATE_JCT_BANKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN_ID);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATES_NAMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY_NAMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPERATOR_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JCT_BANKS);

        // Create tables again
        onCreate(db);
    }

    public boolean insertStateNames(String value , String valueArr[]) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
       /* for(int i=0; i<value.length; i++){
            contentValues.put(KEY_STATE_NAME, value[i]);
        }*/
        contentValues.put(KEY_STATE_NAME, value);
        db.insert(TABLE_STATES_NAMES, null, contentValues);
        return true;
    }
    public ArrayList<String> getAllStateNames(String email) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_STATES_NAMES;
        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(KEY_STATE_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public boolean insertRblRelationNames(ArrayList<RblRelationResponse> array_list) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0; i<array_list.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_RELATION_KEY, array_list.get(i).Key);
            contentValues.put(KEY_RELATION_NAME, array_list.get(i).Name);
            db.insert(TABLE_RBL_RELATION, null, contentValues);
        }

        return true;
    }
    public ArrayList<RblRelationResponse> getAllRblRelationNames() {
        ArrayList<RblRelationResponse> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_RBL_RELATION;
        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            RblRelationResponse relationResponse=new RblRelationResponse();
            relationResponse.Name=res.getString(res.getColumnIndex(KEY_RELATION_NAME));
            relationResponse.Key=res.getString(res.getColumnIndex(KEY_RELATION_KEY));
            array_list.add(relationResponse);
            res.moveToNext();
        }
        return array_list;
    }

    public boolean insertRblBankNamesWithIFSC(ArrayList<RblIfscByCodeResponse> array_list) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0; i<array_list.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_RBL_BANK_KEY, array_list.get(i).Key);
            contentValues.put(KEY_RBL_BANK_NAME, array_list.get(i).Name);
            contentValues.put(KEY_RBL_BANK_DIGIT, array_list.get(i).Digit);
            db.insert(TABLE_RBL_BANKS, null, contentValues);
        }

        return true;
    }
    public ArrayList<RblIfscByCodeResponse> getRblBankNamesWithIFSC() {
        ArrayList<RblIfscByCodeResponse> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_RBL_BANKS;
        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            RblIfscByCodeResponse rblIfscByCodeResponse=new RblIfscByCodeResponse();
            rblIfscByCodeResponse.Name=res.getString(res.getColumnIndex(KEY_RBL_BANK_NAME));
            rblIfscByCodeResponse.Key=res.getString(res.getColumnIndex(KEY_RBL_BANK_KEY));
            rblIfscByCodeResponse.Digit=res.getString(res.getColumnIndex(KEY_RBL_BANK_DIGIT));
            array_list.add(rblIfscByCodeResponse);
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<OptModel.OptData> getAllOperatorNames(int type) {
        ArrayList<OptModel.OptData> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_OPERATOR_NAME + " WHERE "+KEY_OPERATOR_TYPE + " == "+type+"";
        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();

        OptModel optModel=new OptModel();
        while(res.isAfterLast() == false){
            OptModel.OptData model=optModel.new OptData();
            model.OptId=res.getString(res.getColumnIndex(KEY_OPERATOR_ID));
            model.OptName=res.getString(res.getColumnIndex(KEY_OPERATOR_NAME));
            array_list.add(model);
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<JctIfscByCodeResponse> getJctBankNamesWithIFSC() {
        ArrayList<JctIfscByCodeResponse> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_JCT_BANKS;
        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            JctIfscByCodeResponse jctIfscByCodeResponse=new JctIfscByCodeResponse();
            jctIfscByCodeResponse.Name=res.getString(res.getColumnIndex(KEY_JCT_BANK_NAME));
            jctIfscByCodeResponse.Key=res.getString(res.getColumnIndex(KEY_JCT_BANK_KEY));
            jctIfscByCodeResponse.Digit=res.getString(res.getColumnIndex(KEY_JCT_BANK_DIGIT));
            array_list.add(jctIfscByCodeResponse);
            res.moveToNext();
        }
        return array_list;
    }

}
