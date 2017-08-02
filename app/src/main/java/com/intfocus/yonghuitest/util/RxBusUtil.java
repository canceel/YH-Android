package com.intfocus.yonghuitest.util;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/02 下午2:17
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

public class RxBusUtil {
    private static RxBusUtil mInstance = null;
    private final SerializedSubject<Object, Object> mSubject;

    private RxBusUtil() {
        mSubject = new SerializedSubject<>(PublishSubject.create());
    }

    // 双重校验锁单例模式
    public static synchronized RxBusUtil getInstance() {
        if (mInstance == null) {
            synchronized (RxBusUtil.class) {
                if (mInstance == null)
                    mInstance = new RxBusUtil();
            }
        }
        return mInstance;
    }

    public void post(Object object) {
        mSubject.onNext(object);
    }

    public <T> Observable<T> toObservable(Class<T> eventType) {
        return mSubject.ofType(eventType);
    }

    public final <R> Observable<R> ofType(final Class<R> klass){
        return mSubject.filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object t) {
                return klass.isInstance(t);
            }
        }).cast(klass);
    }

}
