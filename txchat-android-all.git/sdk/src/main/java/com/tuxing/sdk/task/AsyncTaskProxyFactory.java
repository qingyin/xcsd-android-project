package com.tuxing.sdk.task;

import java.lang.reflect.Proxy;

/**
 * Created by Alan on 2015/6/25.
 */
public class AsyncTaskProxyFactory {
    public static <T> T getProxy(T instance){
        AsyncTaskProxy proxy = new AsyncTaskProxy();
        proxy.bind(instance);

        return (T) Proxy.newProxyInstance(instance.getClass().getClassLoader(),
                instance.getClass().getInterfaces(), proxy);
    }
}
