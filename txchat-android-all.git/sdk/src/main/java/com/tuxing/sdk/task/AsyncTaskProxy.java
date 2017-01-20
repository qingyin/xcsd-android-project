package com.tuxing.sdk.task;

import com.tuxing.sdk.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Alan on 2015/6/25.
 */
public class AsyncTaskProxy implements InvocationHandler {
    private final static Logger logger = LoggerFactory.getLogger(AsyncTaskProxy.class);
    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    Object object;

    public void bind(Object obj){
        this.object = obj;
    }


    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        if(method.isAnnotationPresent(AsyncMethod.class)){
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        method.invoke(object, args);
                    }catch (Exception e){
                        logger.error("Proxy object invoke error", e);
                    }
                }
            });

            return null;
        }else{
            return method.invoke(object, args);

        }
    }
}
