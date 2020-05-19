package com.east.database.db.curd.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.east.database.db.DaoUtil;
import com.east.database.db.curd.IQuery;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;


/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 查询
 *  @author: jamin
 *  @date: 2020/5/19
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class QueryHelper<T> implements IQuery<T> {

    private SQLiteDatabase mSQLiteDatabase; //数据库
    private Class<T> mClazz;//泛型类 --> 表名

    public QueryHelper(SQLiteDatabase sqliteDatabase, Class<T> clazz) {
        this.mSQLiteDatabase = sqliteDatabase;
        this.mClazz = clazz;
    }

    @Override
    public Observable<List<T>> queryList() {
        return Observable.create(new ObservableOnSubscribe<List<T>>() {
            @Override
            public void subscribe(ObservableEmitter<List<T>> emitter) throws Exception {
                Cursor cursor = mSQLiteDatabase.query(DaoUtil.getTableName(mClazz),
                        null, null, null, null, null, null);
                List<T> datas = cursorToList(cursor);
                emitter.onNext(datas);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<T>> queryList(final String selection, final String... selectionArgs){
        return Observable.create(new ObservableOnSubscribe<List<T>>() {
            @Override
            public void subscribe(ObservableEmitter<List<T>> emitter) throws Exception {
                Cursor cursor = mSQLiteDatabase.query(DaoUtil.getTableName(mClazz),
                        null, selection, selectionArgs, null, null, null);
                List<T> datas = cursorToList(cursor);
                emitter.onNext(datas);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<List<T>> queryList(final boolean distinck, final String[] column, final String selection,
                                         final String[] selectionArgs,
                                         final String groupBy, final String having,
                                         final String orderBy, final String limit){

        return Observable.create(new ObservableOnSubscribe<List<T>>() {
            @Override
            public void subscribe(ObservableEmitter<List<T>> emitter) throws Exception {
                Cursor cursor = mSQLiteDatabase.query(distinck,DaoUtil.getTableName(mClazz),
                        column, selection, selectionArgs, groupBy, having, orderBy,limit);
                List<T> datas = cursorToList(cursor);
                emitter.onNext(datas);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<T>> rawQueryList(final String sql, final String[] selectionArgs){
        return Observable.create(new ObservableOnSubscribe<List<T>>() {
            @Override
            public void subscribe(ObservableEmitter<List<T>> emitter) throws Exception {
                Cursor cursor = mSQLiteDatabase.rawQuery(sql,selectionArgs);
                List<T> datas = cursorToList(cursor);
                emitter.onNext(datas);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    /**
     *  游标转List
     */
    private List<T> cursorToList(Cursor cursor) {
        List<T> list = new ArrayList<>();
        if(cursor!=null && cursor.moveToFirst()){
            // 不断从游标中获取数据
            do{
                try {
                    //反射获取对象
                    T t = mClazz.newInstance();

                    Field[] fields = mClazz.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String columnName = field.getName();

                        int columnIndex = cursor.getColumnIndex(columnName);
                        Method cursorMethod = getCursorMethod(field); //获取对应的方法
                        Object value = cursorMethod.invoke(cursor, columnIndex); //数据库中查询到了值

                        // 处理一些特殊的部分
                        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                            if ("0".equals(String.valueOf(value))) {
                                value = false;
                            } else if ("1".equals(String.valueOf(value))) {
                                value = true;
                            }
                        } else if (field.getType() == char.class || field.getType() == Character.class) {
                            value = ((String) value).charAt(0);
                        } else if (field.getType() == Date.class) {
                            long date = (Long) value;
                            if (date <= 0) {
                                value = null;
                            } else {
                                value = new Date(date);
                            }
                        }

                        //反射赋值
                        field.set(t,value);
                    }

                    list.add(t); //加入集合中
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }while (cursor.moveToNext());
        }
        return list;
    }

    /**
     *  通过反射获取Cursor对应获取值的方法
     */
    private Method getCursorMethod(Field field) throws Exception{
        String typeName;
        Class<?> type = field.getType();

        if(type.isPrimitive()){ // 如果是基本类型
            String name = type.getName();
            typeName = name.substring(0,1).toUpperCase(Locale.US)+name.substring(1);
        }else{
            typeName = type.getSimpleName();
        }
//        Log.d("TAG","Field的类型name 为 --> "+typeName);
        String methodName = "get" + typeName;
        if ("getBoolean".equals(methodName)) {
            methodName = "getInt";
        } else if ("getChar".equals(methodName) || "getCharacter".equals(methodName)) {
            methodName = "getString";
        } else if ("getDate".equals(methodName)) {
            methodName = "getLong";
        } else if ("getInteger".equals(methodName)) {
            methodName = "getInt";
        }

        //通过反射获取方法
        return Cursor.class.getDeclaredMethod(methodName,int.class);
    }
}
