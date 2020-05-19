@[TOC]（第三方开源库 RxJava - Android实际开发场景)

## 注意:

1. //Observable 里面的代码逻辑发送需要执行的话必须要设置Observer才行,或者必须接一个operate 操作(相当于设置了observer)

```
Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                
                //需要执行的话必须要设置Observer才行,或者必须接一个operate 操作(相当于设置了observer)
                
                int raw = mSQLiteDatabase.delete(DaoUtil.getTableName(mClazz),whereClause,whereArgs);
                emitter.onNext(raw);
                emitter.onComplete();
            }
        });
  
```

2. blockingFirst的坑

```
Observable.create(new ObservableOnSubscribe<Integer>(){...})..subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).blockingFirst()

这样线程会阻塞住主线程

```













































































 


      
     
 

