package com.east.database.db.curd;


import io.reactivex.rxjava3.core.Observable;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 数据更新操作
 *  @author: jamin
 *  @date: 2020/5/19
 * |---------------------------------------------------------------------------------------------------------------|
 */
public interface IUpdate<T> {


    Observable<Integer> update(T obj, String whereClause, String... whereArgs);
}
