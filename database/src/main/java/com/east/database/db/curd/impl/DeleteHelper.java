package com.east.database.db.curd.impl;

import android.database.sqlite.SQLiteDatabase;

import com.east.database.db.DaoUtil;
import com.east.database.db.curd.IDelete;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;


/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 删除帮助类
 *  @author: jamin
 *  @date: 2020/5/19
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class DeleteHelper<T> implements IDelete<T> {

    private SQLiteDatabase mSQLiteDatabase; //数据库
    private Class<T> mClazz;//泛型类 --> 表名

    public DeleteHelper(SQLiteDatabase sqliteDatabase, Class<T> clazz) {
        this.mSQLiteDatabase = sqliteDatabase;
        this.mClazz = clazz;
    }

    @Override
    public Observable<Integer> delete(final String whereClause, final String... whereArgs) {
        //delete("user", "name=?", new String[]{delete_data});
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                int raw = mSQLiteDatabase.delete(DaoUtil.getTableName(mClazz),whereClause,whereArgs);
                emitter.onNext(raw);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Integer> deleteAll() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                int raw = mSQLiteDatabase.delete(DaoUtil.getTableName(mClazz),null,null);
                emitter.onNext(raw);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
