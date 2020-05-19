package com.east.database.db.curd;


import io.reactivex.rxjava3.core.Observable;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 删除
 *  @author: jamin
 *  @date: 2020/5/19
 * |---------------------------------------------------------------------------------------------------------------|
 */
public interface IDelete<T>  {

    /**
     *  删除指定列
     */
    Observable<Integer> delete(String whereClause, String... whereArgs);

    /**
     * 删除所有
     */
    Observable<Integer> deleteAll();

}
