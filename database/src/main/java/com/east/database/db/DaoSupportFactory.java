package com.east.database.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import androidx.collection.ArrayMap;

import java.io.File;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 * @description: 数据库表的工厂，方便后面使用第三方的时候也可以有很好的扩展性
 * @author: jamin
 * @date: 2020/5/18
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class DaoSupportFactory {

    private SQLiteDatabase mSQLiteDatabase;

    private ArrayMap<Class<?>,IDaoSupport<?>> mDaoMap = new ArrayMap<>(); //保存Dao示例
    private volatile static DaoSupportFactory mFactory;

    private DaoSupportFactory() {


        // 把数据库放到内存卡里面  6.0以上需要权限
        File dbRoot = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "nhdz"
                + File.separator + "database");
        if(!dbRoot.exists())
            dbRoot.mkdirs();

        File dbFile = new File(dbRoot,"nhdz.db");

        // 打开或者创建一个数据库
        mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

    }

    public static DaoSupportFactory getFactory() {
        if (mFactory == null) {
            synchronized (DaoSupportFactory.class) {
                if (mFactory == null)
                    mFactory = new DaoSupportFactory();
            }
        }
        return mFactory;
    }

    /**
     * 数据到对应的表
     */
    public <T> IDaoSupport<T> getDao(Class<T> clazz){
        if (mDaoMap.containsKey(clazz)) {
            return (IDaoSupport<T>) mDaoMap.get(clazz);
        }
        IDaoSupport<T> daoSupport = new DaoSupport<>();
        daoSupport.init(mSQLiteDatabase,clazz);
        mDaoMap.put(clazz,daoSupport);
        return daoSupport;
    }

}
