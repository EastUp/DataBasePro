package com.east.database.db.curd.impl;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.east.database.db.DaoUtil;
import com.east.database.db.curd.IInsert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;


/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 * @description: 数据库插入帮助类
 * @author: jamin
 * @date: 2020/5/19
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class InsertHelper<T> implements IInsert<T> {

    private SQLiteDatabase mSQLiteDatabase; //数据库
    private Class<T> mClazz;//泛型类 --> 表名
    private Map<String, Method> mContentValuePutMethodMap; //反射的时候如果是相同的方法就存储起来这样能提高效率

    public InsertHelper(SQLiteDatabase sqliteDatabase, Class<T> clazz,  Map<String, Method> contentValuePutMethodMap) {
        this.mSQLiteDatabase = sqliteDatabase;
        this.mClazz = clazz;
        this.mContentValuePutMethodMap = contentValuePutMethodMap;
    }

    @Override
    public Observable<Long> insert(final T obj) {
        /*ContentValues values = new ContentValues();
        values.put("name",person.getName());
        values.put("age",person.getAge());
        values.put("flag",person.getFlag());
        db.insert("Person",null,values);*/

        return Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Long> emitter) {
                ContentValues values = obj2ContentValues(obj);
                long value = mSQLiteDatabase.insert(DaoUtil.getTableName(mClazz),null,values);
                emitter.onNext(value);
                emitter.onComplete();
            }
        });

    }


    //插入5000条数据只需要200多ms
    @Override
    public Observable<Object> insert(final List<T> datas) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                //批量插入，加入事务
                mSQLiteDatabase.beginTransaction();
                for (T data : datas) {
                    //调用的还是单条插入
                    ContentValues values = obj2ContentValues(data);
                    long value = mSQLiteDatabase.insert(DaoUtil.getTableName(mClazz),null,values);
                }
                mSQLiteDatabase.setTransactionSuccessful();
                mSQLiteDatabase.endTransaction();
                emitter.onComplete();
            }
        });
    }


    /**
     * obj 转换成 contentValues
     */
    private ContentValues obj2ContentValues(T obj) {
        ContentValues contentValues = new ContentValues();
        Field[] fields = mClazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String columnName = field.getName(); //列名
                Object value = field.get(obj);

                String simpleName = field.getType().getSimpleName();
                Method put = mContentValuePutMethodMap.get(simpleName);
                if(put == null){
                    //获取filed对应需要调用的ContentValue的put方法
                    put = ContentValues.class.getDeclaredMethod("put", String.class, value.getClass());
                    mContentValuePutMethodMap.put(simpleName,put);
                }
                //赋值
                put.invoke(contentValues,columnName,value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentValues;
    }

}
