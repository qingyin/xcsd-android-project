package com.tuxing.sdk.manager;


import java.util.Map;

/**
 * Created by Alan on 2015/7/10.
 */
public interface CounterManager extends BaseManager {
    /***
     * 从服务器更新计数器
     */
    void updateCounters();

    /***
     * 重置某个计数器
     * @param counter 计数器名
     */
    void resetCounter(String counter);

    /***
     * 计算器减1
     * @param counter 计数器名
     */
    void decCounter(String counter);

    /***
     * 返回所有counter
     * @return 所有counter
     */
    Map<String, Integer> getCounters();
}
