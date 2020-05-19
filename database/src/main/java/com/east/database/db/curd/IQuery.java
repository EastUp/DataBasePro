package com.east.database.db.curd;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;


/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 查询辅助类
 *  @author: jamin
 *  @date: 2020/5/19
 * |---------------------------------------------------------------------------------------------------------------|
 */
public interface IQuery<T> {

    // 查询所有
    Observable<List<T>> queryList();

    //根据条件查询
    Observable<List<T>> queryList(String selection, String... selectionArgs);

    //根据条件查询
    Observable<List<T>> queryList(boolean distinck, String[] column, String selection,
                                  String[] selectionArgs,
                                  String groupBy, String having,
                                  String orderBy, String limit);

    //自己编写sql查询
    Observable<List<T>> rawQueryList(String sql, String[] selectionArgs);

}
