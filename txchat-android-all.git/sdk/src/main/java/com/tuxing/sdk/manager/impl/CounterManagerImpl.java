package com.tuxing.sdk.manager.impl;

import android.content.Context;

import com.tuxing.rpc.proto.Counter;
import com.tuxing.rpc.proto.FetchCounterResponse;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.event.StateChangeEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.CheckInManager;
import com.tuxing.sdk.manager.ContentManager;
import com.tuxing.sdk.manager.CounterManager;
import com.tuxing.sdk.manager.HomeWorkManager;
import com.tuxing.sdk.manager.NoticeManager;
import com.tuxing.sdk.manager.QuoraManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.SerializeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.greenrobot.event.EventBus;

//import com.tuxing.rpc.proto.Counter;
//import com.tuxing.rpc.proto.FetchCounterResponse;

/**
 * Created by Alan on 2015/7/10.
 */
public class CounterManagerImpl implements CounterManager {
    private final static Logger logger = LoggerFactory.getLogger(CounterManager.class);
    private static CounterManager instance;

    HttpClient httpClient = HttpClient.getInstance();

    private NoticeManager noticeManager;
    private CheckInManager checkInManager;
    private ContentManager contentManager;
    private HomeWorkManager homeWorkManager;
    private QuoraManager quoraManager;

    Map<String, Integer> counters = new ConcurrentHashMap<>();
    private Context context;

    private CounterManagerImpl() {
        EventBus.getDefault().register(CounterManagerImpl.this);

        counters.put(Constants.COUNTER.ACTIVITY, 0);
        counters.put(Constants.COUNTER.ANNOUNCEMENT, 0);
        counters.put(Constants.COUNTER.CHECK_IN, 0);
        counters.put(Constants.COUNTER.FEED, 0);
        counters.put(Constants.COUNTER.LEARN_GARDEN, 0);
        counters.put(Constants.COUNTER.MAIL, 0);
        counters.put(Constants.COUNTER.MEDICINE, 0);
        counters.put(Constants.COUNTER.NOTICE, 0);
        counters.put(Constants.COUNTER.COMMENT, 0);
        counters.put(Constants.COUNTER.HOMEWORK, 0);
        counters.put(Constants.COUNTER.COMMUNION, 0);
    }

    public synchronized static CounterManager getInstance() {
        if (instance == null) {
            instance = new CounterManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context;

        noticeManager = NoticeManagerImpl.getInstance();
        checkInManager = CheckInManagerImpl.getInstance();
        contentManager = ContentManagerImpl.getInstance();
        homeWorkManager = HomeWorkManagerImpl.getInstance();
        quoraManager = QuoraManagerImpl.getInstance();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void updateCounters() {
        //logger.debug("CounterManager::updateCounters()");

        httpClient.sendRequest(RequestUrl.UPDATE_COUNTER, new byte[]{}, new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchCounterResponse response = SerializeUtils.parseFrom(data, FetchCounterResponse.class);

                Set<Integer> changeList = new HashSet<>();
                for (Counter txCounter : response.counters) {
                    //logger.debug("Item:{}, Counter:{}", txCounter.getItem(), txCounter.getCount());
                    Integer value = counters.get(txCounter.item);

                    if (value != null) {
                        if (txCounter.count != value) {

                            if (txCounter.count > value) {
                                checkNeedFetchData(txCounter.item);
                            }

                            if (txCounter.item.equals(Constants.COUNTER.CHECK_IN) ||
                                    txCounter.item.equals(Constants.COUNTER.LEARN_GARDEN)) {
                                if (txCounter.count > value) {
                                    counters.put(txCounter.item, txCounter.count);
                                    changeList.addAll(Constants.COUNTER_TAB_MAP.get(txCounter.item));
                                }
                            } else {
                                counters.put(txCounter.item, txCounter.count);
                                changeList.addAll(Constants.COUNTER_TAB_MAP.get(txCounter.item));
                            }
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(changeList)) {
                    EventBus.getDefault().post(new StateChangeEvent(
                            StateChangeEvent.EventType.STATE_CHANGED,
                            null,
                            new ArrayList<>(changeList)));
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                logger.error("Cannot update state.", cause);
            }
        });
    }

    private void checkNeedFetchData(String name) {
        if (name.equals(Constants.COUNTER.NOTICE)) {
            noticeManager.getLatestNotice(Constants.MAILBOX_INBOX);
        } else if (name.equals(Constants.COUNTER.CHECK_IN)) {
            checkInManager.getLatestCheckInRecords();
        } else if (name.equals(Constants.COUNTER.LEARN_GARDEN)) {
            contentManager.getLatestGroupItems();
        } else if (name.equals(Constants.COUNTER.HOMEWORK)) {

            homeWorkManager.getLatestHomeWorkList();
        }else if (name.equals(Constants.COUNTER.COMMUNION)){
            quoraManager.getLatestMessages();
        }
    }

    @Override
    public void resetCounter(String name) {
        logger.debug("CounterManager::resetCounter(), name={}", name);

        counters.put(name, 0);

        EventBus.getDefault().post(new StateChangeEvent(
                StateChangeEvent.EventType.STATE_CHANGED,
                null,
                Constants.COUNTER_TAB_MAP.get(name)
        ));
    }

    @Override
    public void decCounter(String name) {
        logger.debug("CounterManager::resetCounter(), name={}", name);

        if (counters.containsKey(name)) {
            int value = counters.get(name);
            if (value > 0) {
                value = value - 1;
                counters.put(name, value);

                if (value == 0) {
                    EventBus.getDefault().post(new StateChangeEvent(
                            StateChangeEvent.EventType.STATE_CHANGED,
                            null,
                            Constants.COUNTER_TAB_MAP.get(name)
                    ));
                }
            }
        }
    }

    @Override
    public Map<String, Integer> getCounters() {
        return counters;
    }

    public void onEvent(LoginEvent event) {
        switch (event.getEvent()) {
            case LOGOUT:
            case KICK_OFF:
            case TOKEN_EXPIRED:
                //clear all counter
                for (String counter : counters.keySet()) {
                    counters.put(counter, 0);
                }
                break;
        }
    }
}
