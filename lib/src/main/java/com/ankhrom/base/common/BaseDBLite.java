package com.ankhrom.base.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ankhrom.base.common.statics.StringHelper;

import java.io.File;
import java.util.Locale;

/**
 *
 */
public class BaseDBLite extends SQLiteOpenHelper {

    private static final String COMMA = ",";

    private SQLiteDatabase DB;

    private String table;
    private String[] column;

    public BaseDBLite(Context context, String name, String table, String... columns) {
        super(context, name, null, 1);

        this.table = table;
        column = columns;
    }

    public SQLiteDatabase openToWrite() {

        if (DB != null) {
            close();
        }

        DB = this.getWritableDatabase();

        return DB;
    }

    public SQLiteDatabase openToRead() {

        if (DB != null) {
            close();
        }

        DB = this.getReadableDatabase();

        return DB;
    }

    public void close() {

        DB = null;
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    public static void createTable(Context context, String dbName, String table, String... columnExpresion) {

        BaseDBLite db = new BaseDBLite(context, dbName, table, columnExpresion);
        db.openToWrite();
        db.createTable();
        db.close();
    }

    public static void addColumn(Context context, String dbName, String table, String... columnExpresion) {

        BaseDBLite db = new BaseDBLite(context, dbName, table);
        db.openToWrite();
        for (String column : columnExpresion) {
            db.addColumn(column);
        }
        db.close();
    }

    protected void createTable() {

        DB.execSQL(String.format(Locale.US, "CREATE table %s (%s)", table, createExpression()));
    }

    public void addColumn(String column) {

        DB.execSQL(String.format(Locale.US, "ALTER table %s ADD %s", table, column));
    }

    private String createExpression() {

        StringBuilder builder = new StringBuilder();

        for (String cex : column) {
            builder.append(cex).append(COMMA);
        }

        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP table IF EXISTS " + table);
        onCreate(db);
    }

    public void drop() {

        DB.execSQL("DROP table IF EXISTS " + table);
    }

    public boolean insertRow(String... values) {

        ContentValues content = new ContentValues(values.length);

        for (int i = 0; i < values.length; i++) {
            content.put(column[i], values[i]);
        }

        return DB.insert(table, null, content) > -1;
    }

    public boolean insertIfNotExist(String... values) {

        ContentValues content = new ContentValues(values.length);

        for (int i = 0; i < values.length; i++) {
            content.put(column[i], values[i]);
        }

        return DB.insertWithOnConflict(table, null, content, SQLiteDatabase.CONFLICT_IGNORE) > -1;
    }

    public boolean updateRow(String id, String column, String value) {

        ContentValues values = new ContentValues();
        values.put(column, value);

        return DB.update(table, values, this.column[0] + " = ?", new String[]{id}) > 0;
    }

    public boolean updateRow(String id, String[] column, String[] value) {

        ContentValues values = new ContentValues();
        for (int i = 0; i < column.length; i++) {
            values.put(column[i], value[i]);
        }

        return DB.update(table, values, this.column[0] + " = ?", new String[]{id}) > 0;
    }

    public boolean updateRow(String id, String column, String value, String where, String[] whereArgs) {

        if (StringHelper.isEmpty(where)) {
            return updateRow(id, column, value);
        }

        ContentValues values = new ContentValues();
        values.put(column, value);

        String[] args = new String[whereArgs.length + 1];
        args[0] = id;
        System.arraycopy(whereArgs, 0, args, 1, whereArgs.length);

        return DB.update(table, values, String.format(Locale.US, "%s = ? AND %s", this.column[0], where), args) > 0;
    }

    public boolean updateRow(String id, String[] column, String[] value, String where, String[] whereArgs) {

        if (StringHelper.isEmpty(where)) {
            return updateRow(id, column, value);
        }

        ContentValues values = new ContentValues();
        for (int i = 0; i < column.length; i++) {
            values.put(column[i], value[i]);
        }

        String[] args = new String[whereArgs.length + 1];
        args[0] = id;
        System.arraycopy(whereArgs, 0, args, 1, whereArgs.length);

        return DB.update(table, values, String.format(Locale.US, "%s = ? AND %s", this.column[0], where), args) > 0;
    }

    public void deleteRows() {

        DB.delete(table, null, null);
    }

    public String[][] getData() {

        Cursor cursor = DB.query(table, column, null, null, null, null, null);

        return getCursorData(cursor);
    }

    public String[][] getData(String... column) {

        if (column == null) {
            column = this.column;
        }

        Cursor cursor = DB.query(table, column, null, null, null, null, null);

        return getCursorData(cursor);
    }

    public String[][] getData(String where, String[] args, String orderBy) {

        Cursor cursor = DB.query(table, column, where, args, null, null, orderBy);

        return getCursorData(cursor);
    }

    public String[][] getData(String select, String sqlCondition, String[] args) {


        Cursor cursor = DB.rawQuery(String.format(Locale.US, "SELECT %s FROM %s %s", select, table, sqlCondition), args);

        return getCursorData(cursor);
    }

    public String[][] getData(String rawSql, String[] args) {

        Cursor cursor = DB.rawQuery(rawSql, args);

        return getCursorData(cursor);
    }

    public String[] getRow(String id) {

        Cursor cursor = DB.rawQuery(String.format(Locale.US, "SELECT * FROM %s WHERE %s = ?", table, column[0]), new String[]{id});

        String[] row = null;

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            row = getCursorRow(cursor);
        }

        cursor.close();

        return row;
    }

    public String getMin(String column) {

        Cursor cursor = DB.rawQuery(String.format(Locale.US, "SELECT MIN(%s) FROM %s", column, table), null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            String min = cursor.getString(0);
            cursor.close();
            return min;
        }

        cursor.close();

        return null;
    }

    public String getMax(String column) {

        Cursor cursor = DB.rawQuery(String.format(Locale.US, "SELECT MAX(%s) FROM %s", column, table), null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            String min = cursor.getString(0);
            cursor.close();
            return min;
        }

        cursor.close();

        return null;
    }

    public String[][] getRandom(String where, String[] whereArgs, int limit) {

        return getData(where, whereArgs, "RANDOM() LIMIT " + limit);
    }

    public int getCount(String select, String where, String[] whereArgs) {

        int count = 0;

        if (StringHelper.isEmpty(select)) {
            select = column[0];
        }

        Cursor cursor;
        if (StringHelper.isEmpty(where)) {
            cursor = DB.rawQuery(String.format(Locale.US, "SELECT COUNT(%s) FROM %s", select, table), null);
        } else {
            cursor = DB.rawQuery(String.format(Locale.US, "SELECT COUNT(%s) FROM %s WHERE %s", select, table, where), whereArgs);
        }

        cursor.moveToFirst();
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public boolean isEmpty() {

        Cursor cursor = DB.rawQuery(String.format(Locale.US, "SELECT %s FROM %s LIMIT 1", column[0], table), null);

        boolean isEmpty = cursor.getCount() == 0;

        cursor.close();

        return isEmpty;
    }

    public int getColumnIndex(String name) {

        for (int i = 0; i < column.length; i++) {
            if (name.equals(column[i])) {
                return i;
            }
        }

        return -1;
    }

    public String getColumnName(int index) {

        return column[index];
    }

    public int getColumnCount() {

        return column.length;
    }

    public String getTable() {

        return table;
    }

    public void setTable(String name) {

        this.table = name;
    }

    public String[] getColumn() {
        return column;
    }

    public void setColumn(String... column) {
        this.column = column;
    }

    public static String[][] getCursorData(Cursor cursor) {

        String[][] data = new String[cursor.getCount()][];
        if (data.length > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                data[cursor.getPosition()] = getCursorRow(cursor);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return data;
    }

    public static String[] getCursorRow(Cursor cursor) {

        int count = cursor.getColumnCount();
        String[] row = new String[count];
        for (int i = 0; i < count; i++) {
            row[i] = cursor.getString(i);
        }

        return row;
    }

    public static boolean exist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);

        return dbFile.exists();
    }

    public static void delete(Context context, String dbName) {

        context.deleteDatabase(dbName);
    }

    public static String varcharKey(String name) {

        return name + " VARCHAR PRIMARY KEY NOT NULL";
    }

    public static String integerKey(String name) {

        return name + " INTEGER PRIMARY KEY NOT NULL";
    }

    public static String varcharNotNull(String name) {

        return name + " VARCHAR NOT NULL";
    }

    public static String integerNotNull(String name) {

        return name + " INTEGER NOT NULL";
    }

    public static String varchar(String name) {

        return name + " VARCHAR";
    }

    public static String integer(String name) {

        return name + " INTEGER";
    }
}
