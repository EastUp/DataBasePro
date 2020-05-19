package com.east.database.db.curd;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;


/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 数据插入
 *  @author: jamin
 *  @date: 2020/5/19
 * |---------------------------------------------------------------------------------------------------------------|
 */
public interface IInsert<T> {
    //插入一条数据
    Observable<Long> insert(T t);

    //插入多条数据  500条数据才最多200ms
    Observable<Object>  insert(List<T> datas);
}
