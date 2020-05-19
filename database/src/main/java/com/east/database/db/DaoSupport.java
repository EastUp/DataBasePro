package com.east.database.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.collection.ArrayMap;

import com.east.database.db.curd.IDelete;
import com.east.database.db.curd.IInsert;
import com.east.database.db.curd.IQuery;
import com.east.database.db.curd.IUpdate;
import com.east.database.db.curd.impl.InsertHelper;
import com.east.database.db.curd.impl.QueryHelper;
import com.east.database.db.curd.impl.UpdateHepler;
import com.east.database.db.curd.impl.DeleteHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;


/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 数据库具体的操作类
 *  @author: jamin
 *  @date: 2020/5/18
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class DaoSupport<T> implements IDaoSupport<T> {

    private final static String TAG = "DaoSupport";
    private SQLiteDatabase mSQLiteDatabase; //数据库
    private Class<T> mClazz;//泛型类 --> 表名
    private IInsert<T> mInsertHelper;//插入辅助类
    private IQuery<T> mQueryHelper;//查询辅助类
    private IDelete<T> mDeleteHelper;//删除辅助类
    private IUpdate<T> mUpdateHelper;//更新辅助类

    private final Map<String, Method> mContentValuePutMethodMap
            = new ArrayMap<>();  //反射的时候如果是相同的方法就存储起来这样能提高效率


    @Override
    public void init(SQLiteDatabase sqLiteDatabase, Class<T> clazz) {
        this.mSQLiteDatabase = sqLiteDatabase;
        this.mClazz = clazz;
        mInsertHelper = new InsertHelper<>(mSQLiteDatabase,mClazz,mContentValuePutMethodMap);
        mQueryHelper = new QueryHelper<>(mSQLiteDatabase,mClazz);
        mDeleteHelper = new DeleteHelper<>(mSQLiteDatabase,mClazz);
        mUpdateHelper = new UpdateHepler<>(mSQLiteDatabase,mClazz,mContentValuePutMethodMap);


        //如果没有则 创建表
                /*"create table if not exists Person ("
                + "id integer primary key autoincrement, "
                + "name text, "
                + "age integer, "
                + "flag boolean)";*/
        StringBuffer sb = new StringBuffer();

        sb.append("create table if not exists ")
                .append(DaoUtil.getTableName(mClazz))
                .append(" (id integer primary key autoincrement, ");

        Field[] fields = mClazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            String type = field.getType().getSimpleName();
            // type需要进行转换 int --> integer, String text;
            sb.append(name).append(" ").append(DaoUtil.getColumnType(type)).append(", ");
        }

        sb.replace(sb.length()-2,sb.length(),")");

        String createTableSql = sb.toString();

        Log.d(TAG,createTableSql);

        //创建表
        mSQLiteDatabase.execSQL(createTableSql);
    }

    @Override
    public Observable<Long> insert(T t) {
        return mInsertHelper.insert(t);
    }

    @Override
    public Observable<Object>  insert(List<T> datas) {
       return mInsertHelper.insert(datas);
    }

    @Override
    public Observable<List<T>> queryList() {
        return mQueryHelper.queryList();
    }

    @Override
    public Observable<List<T>> queryList(String selection, String... selectionArgs) {
        return mQueryHelper.queryList(selection,selectionArgs);
    }

    @Override
    public Observable<List<T>> queryList(boolean distinck, String[] column, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return mQueryHelper.queryList(distinck, column, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    @Override
    public Observable<List<T>> rawQueryList(String sql, String[] selectionArgs) {
        return mQueryHelper.rawQueryList(sql,selectionArgs);
    }

    @Override
    public Observable<Integer> delete(String whereClause, String... whereArgs) {
        return mDeleteHelper.delete(whereClause,whereArgs);
    }

    @Override
    public Observable<Integer> deleteAll() {
        return mDeleteHelper.deleteAll();
    }

    @Override
    public Observable<Integer> update(T obj, String whereClause, String... whereArgs) {
        return mUpdateHelper.update(obj,whereClause,whereArgs);
    }
}
