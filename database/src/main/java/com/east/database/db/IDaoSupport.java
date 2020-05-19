package com.east.database.db;

import android.database.sqlite.SQLiteDatabase;

import com.east.database.db.curd.IDelete;
import com.east.database.db.curd.IInsert;
import com.east.database.db.curd.IQuery;
import com.east.database.db.curd.IUpdate;


/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 对表的操作
 *  @author: jamin
 *  @date: 2020/5/18
 * |---------------------------------------------------------------------------------------------------------------|
 */
public interface IDaoSupport<T> extends IInsert<T>, IQuery<T>, IDelete<T>, IUpdate<T> {

    /**
     *  初始化表
     * @param sqLiteDatabase
     * @param clazz  需要创建的表名
     */
    void init(SQLiteDatabase sqLiteDatabase, Class<T> clazz);


}
