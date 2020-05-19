package com.east.database.db.curd.impl;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.east.database.db.DaoUtil;
import com.east.database.db.curd.IUpdate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;


/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 * @description:
 * @author: jamin
 * @date: 2020/5/19
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class UpdateHepler<T> implements IUpdate<T> {

    private SQLiteDatabase mSQLiteDatabase; //数据库
    private Class<T> mClazz;//泛型类 --> 表名
    private Map<String, Method> mContentValuePutMethodMap; //反射的时候如果是相同的方法就存储起来这样能提高效率

    public UpdateHepler(SQLiteDatabase sqliteDatabase, Class<T> clazz,  Map<String, Method> contentValuePutMethodMap) {
        this.mSQLiteDatabase = sqliteDatabase;
        this.mClazz = clazz;
        this.mContentValuePutMethodMap = contentValuePutMethodMap;
    }

    @Override
    public Observable<Integer> update(final T obj, final String whereClause, final String... whereArgs) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                ContentValues contentValues = obj2ContentValues(obj);
                int raw = mSQLiteDatabase.update(DaoUtil.getTableName(mClazz),
                        contentValues,whereClause,whereArgs);

                emitter.onNext(raw);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
