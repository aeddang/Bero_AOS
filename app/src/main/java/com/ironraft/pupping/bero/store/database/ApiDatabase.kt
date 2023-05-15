package com.ironraft.pupping.bero.store.database
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class ApiDatabase(ctx:Context): SQLiteOpenHelper(ctx, "Server.db", null, 1){
    private val TABLE = "ApiData"
    init {
        writableDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS " + TABLE +
                    "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "itemId TEXT," +
                    "jsonString TEXT);"
        )
    }
    override fun onCreate(db: SQLiteDatabase?) {}
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    data class Row(val id:Int = -1, var itemId:String = "", var jsonString:String = "")

    fun insert(row: Row) {
        val addRowValue = ContentValues()
        addRowValue.put("itemId", row.itemId)
        addRowValue.put("jsonString", row.jsonString)
        writableDatabase.insert(TABLE, null, addRowValue)
        writableDatabase.close()
    }


    fun update(row: Row) {
        val updateRowValue = ContentValues()
        updateRowValue.put("itemId", row.itemId)
        updateRowValue.put("jsonString", row.jsonString)
        writableDatabase.update(TABLE, updateRowValue, "_id=?", arrayOf( row.id.toString()))
        writableDatabase.close()
    }

    fun delete(row: Row) {
        writableDatabase.delete( TABLE,"_id=?", arrayOf( row.id.toString()))
        writableDatabase.close()
    }
    fun getData(itemId:String): Row? {
        val db = readableDatabase
        val cursor: Cursor? = db.query(TABLE, arrayOf("_id",  "itemId", "jsonString"), "itemId=?", arrayOf( itemId ), null, null, null)
        var currentData: Row? = null
        if (cursor != null) {
            if(cursor.count > 0 ) {
                cursor.moveToFirst()
                currentData = Row(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
                )
            }
            cursor.close()
        }
        db.close()
        return currentData
    }

}