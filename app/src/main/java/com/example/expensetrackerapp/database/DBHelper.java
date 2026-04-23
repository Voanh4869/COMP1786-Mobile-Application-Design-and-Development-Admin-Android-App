package com.example.expensetrackerapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ExpenseDB";
    public static final int DATABASE_VERSION = 2; // Incremented version

    // Project Table
    public static final String TABLE_PROJECT = "projects";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_DESC = "description";
    public static final String COL_START = "start_date";
    public static final String COL_END = "end_date";
    public static final String COL_MANAGER = "manager";
    public static final String COL_STATUS = "status";
    public static final String COL_BUDGET = "budget";

    // Expense Table
    public static final String TABLE_EXPENSE = "expenses";
    public static final String COL_EXP_ID = "expense_id";
    public static final String COL_EXP_PROJECT_ID = "project_id";
    public static final String COL_EXP_DATE = "date";
    public static final String COL_EXP_AMOUNT = "amount";
    public static final String COL_EXP_CURRENCY = "currency";
    public static final String COL_EXP_TYPE = "type";
    public static final String COL_EXP_PAYMENT_METHOD = "payment_method";
    public static final String COL_EXP_CLAIMANT = "claimant";
    public static final String COL_EXP_PAYMENT_STATUS = "payment_status";
    public static final String COL_EXP_DESCRIPTION = "description";
    public static final String COL_EXP_LOCATION = "location";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROJECT_TABLE = "CREATE TABLE " + TABLE_PROJECT + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NAME + " TEXT,"
                + COL_DESC + " TEXT,"
                + COL_START + " TEXT,"
                + COL_END + " TEXT,"
                + COL_MANAGER + " TEXT,"
                + COL_STATUS + " TEXT,"
                + COL_BUDGET + " REAL"
                + ")";

        String CREATE_EXPENSE_TABLE = "CREATE TABLE " + TABLE_EXPENSE + "("
                + COL_EXP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_EXP_PROJECT_ID + " INTEGER,"
                + COL_EXP_DATE + " TEXT,"
                + COL_EXP_AMOUNT + " REAL,"
                + COL_EXP_CURRENCY + " TEXT,"
                + COL_EXP_TYPE + " TEXT,"
                + COL_EXP_PAYMENT_METHOD + " TEXT,"
                + COL_EXP_CLAIMANT + " TEXT,"
                + COL_EXP_PAYMENT_STATUS + " TEXT,"
                + COL_EXP_DESCRIPTION + " TEXT,"
                + COL_EXP_LOCATION + " TEXT,"
                + "FOREIGN KEY(" + COL_EXP_PROJECT_ID + ") REFERENCES " + TABLE_PROJECT + "(" + COL_ID + ")"
                + ")";

        db.execSQL(CREATE_PROJECT_TABLE);
        db.execSQL(CREATE_EXPENSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECT);
        onCreate(db);
    }
}