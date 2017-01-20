package com.tuxing.sdk.manager.impl;

import android.content.Context;
import android.os.Handler;

import com.tuxing.sdk.db.entity.DataReport;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.DataReportEvent;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.DataReportManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.SerializeUtils;
import com.xcsd.rpc.proto.Event;
import com.xcsd.rpc.proto.EventType;
import com.xcsd.rpc.proto.ReportEventRequest;
import com.xcsd.rpc.proto.ReportEventResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by apple on 16/10/24.
 */
public class DataReportManagerImpl implements DataReportManager {
    private final static Logger logger = LoggerFactory.getLogger(DataReportManager.class);
    private static DataReportManager instance;

    private final static int maxReportDataNum = 30;
    private static Integer waitReportDataNum = 0;
    private static Long countDown = 1000*60*2L;//倒计时10分钟
    private static boolean reporting = false;
    private static Map<Long,List<Event>> sendingReportDataMap = new HashMap<Long,List<Event>>();
    private static boolean IsNoticeFinishLogin = false;
    HttpClient httpClient = HttpClient.getInstance();
    private Context context;

    private Handler onTimerHandle = new Handler();
    private Runnable onTimerRunnable = null;

    public synchronized static DataReportManager getInstance(){
        if (instance == null){
            instance = new DataReportManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }
        return instance;
    }

    @Override
    public void init(Context context){
        this.context = context;
        if(EventBus.getDefault().isRegistered(this) == false) {
            EventBus.getDefault().register(this);
            //this.noticeFinishLogin();
            logger.debug("DataReportManagerImpl::init===================register(this)");
        }
        logger.debug("DataReportManagerImpl::init===================init");
    }

    @Override
    public void destroy(){
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        //onTimerHandle.removeCallbacks(onTimerRunnable);
        logger.debug("DataReportManagerImpl::init===================destroy");
    }
    private void checkRegistered(){
        if(EventBus.getDefault().isRegistered(this) == false) {
            EventBus.getDefault().register(this);
            logger.debug("DataReportManagerImpl::checkRegistered===================register(this)");
        }
        logger.debug("DataReportManagerImpl::checkRegistered==================="+Boolean.toString(this.reporting));
    }

    public void onEventAsync(DataReportEvent event){
        logger.debug("DataReportManagerImpl::onEventAsync:DataReportEvent:----------" + event.getMsg());
        switch (event.getEvent()){
            case REPORT_GENERAL:
                UserDbHelper.getInstance().insertDataReport(
                        event.getUserId(),event.getEventType().getValue(),
                        event.getBid(), event.getTimestamp(),event.getExtendedInfo());

                this.waitReportDataNum = this.waitReportDataNum + 1;
                if(this.waitReportDataNum >= maxReportDataNum){
                    reportStrategy();
                }
                break;
            case REPORT_KEY_ON_BACKGROUND:
                reportStrategy();
                break;
            case REPORT_FIXED_TIME:
                reportStrategy();
                break;
            case REPORT_MAX_NUM:
                reportStrategy();
                break;

            case REPORT_SUCCESS:
                long serialNo = event.getSerialNo();
                UserDbHelper.getInstance().deleteDataReport(serialNo);
                int num = this.sendingReportDataMap.get(serialNo).size();
                this.sendingReportDataMap.remove(serialNo);
                this.waitReportDataNum = this.waitReportDataNum - num;
                if(this.waitReportDataNum < 0){
                    this.waitReportDataNum = 0;
                }

                this.reporting = false;
                if(this.waitReportDataNum >= maxReportDataNum){
                    DataReportEvent drEvent = new DataReportEvent(DataReportEvent.eType.REPORT_MAX_NUM,"_REPORT_MAX_NUM");
                    EventBus.getDefault().post(drEvent);
                }
                break;
            case REPORT_FAILED:
                this.reporting = false;
                break;
            default:
                break;
        }
    }

    private void reportStrategy(){
        if(this.reporting == true){
            logger.debug("DataReportManagerImpl::reportDataManager reporting = true");
            return;
        }
        if(sendingReportDataMap.size() > 0){
            this.reporting = true;

            logger.debug("DataReportManagerImpl::reportDataManager reporting------num="+sendingReportDataMap.size());
            sendReportedData();
        } else {
            List<DataReport> drList = UserDbHelper.getInstance().getDataReport();
            if(drList != null && translateData(drList) > 0){
                this.reporting = true;

                logger.debug("DataReportManagerImpl::reportDataManager want to report------num="+drList.size());
                sendReportedData();
            }
        }
    }


    private void sendReportedData(){
        List<Event> sendList = new ArrayList<>();
        long serialNo = 0l;
        for(Map.Entry<Long,List<Event>> en : sendingReportDataMap.entrySet()){
            sendList = en.getValue();
            serialNo = en.getKey();
            break;
        }
        logger.debug("DataReportManagerImpl::sendReportedData---num="+sendList.size());
        final ReportEventRequest request = new ReportEventRequest.Builder()
                .eventList(sendList)
                .serialNo(serialNo)
                .sendTime(System.currentTimeMillis())
                .build();
        httpClient.sendRequest(RequestUrl.DATA_EVENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ReportEventResponse response = SerializeUtils.parseFrom(data, ReportEventResponse.class);
                int result = response.result;////1成功  2失败 3已处理
                String msg = response.msg;
                Long sysTimestamp = response.sysTimestamp;
                logger.debug("back-----DataReportManagerImpl::sendReportedData---result="+result);
                logger.debug("back-----DataReportManagerImpl::sendReportedData---response.serialNo="+response.serialNo);
                if (result == 1 || result == 3) {
                    DataReportEvent bEvent = new DataReportEvent(DataReportEvent.eType
                            .REPORT_SUCCESS, "report_success");
                    bEvent.setSerialNo(response.serialNo);
                    EventBus.getDefault().post(bEvent);

                    EventBus.getDefault().post(new LoginEvent(
                            LoginEvent.EventType.REOPRTNOEWLOGOUT,
                            null,
                            null));
                } else {
                    EventBus.getDefault().post(new DataReportEvent(DataReportEvent.eType
                            .REPORT_FAILED, "report_failed"));
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new DataReportEvent(DataReportEvent.eType.REPORT_FAILED,
                        "report_failed"));
            }
        });
    }

    private void OnTimerStart(){
        onTimerHandle.removeCallbacks(onTimerRunnable);
        onTimerRunnable = new Runnable() {
            @Override
            public void run() {
                try{
                    Double min = countDown.intValue() / 1000.0 / 60.0;
                    String str = "each " + min.toString() + " min report data";
                    logger.debug("DataReportManagerImpl::OnTimerStart:----------" + str);
                    DataReportEvent event = new DataReportEvent(DataReportEvent.eType.REPORT_FIXED_TIME,str);
                    EventBus.getDefault().post(event);
                    onTimerHandle.postDelayed(this,countDown);
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("exception...");
                }
            }
        };
        onTimerHandle.postDelayed(onTimerRunnable, countDown);
    }

    @Override
    public void noticeFinishLogin(){
        logger.debug("DataReportManagerImpl::noticeFinishLogin:waitReportDataNum");
        if (IsNoticeFinishLogin == false){
            IsNoticeFinishLogin = true;
            this.waitReportDataNum = UserDbHelper.getInstance().getDataReportAllCount();
            logger.debug("DataReportManagerImpl::noticeFinishLogin:waitReportDataNum = " + this.waitReportDataNum
                    .toString());

            if(this.waitReportDataNum > 0){
                logger.debug("DataReportManagerImpl::noticeFinishLogin:waitReportDataNum-------- = " + this.waitReportDataNum.toString());
                this.waitReportDataNum = translateData(UserDbHelper.getInstance().getDataReportSended());
            }
            OnTimerStart();
        }
    }


    private EventType getEventyType(int eType){
        for (EventType et : EventType.values()){
            if(et.getValue() == eType){
                return et;
            }
        }
        logger.debug("DataReportManagerImpl::getEventyType has dirty data");
        return null;
    }

    private int translateData(List<DataReport> drList){
        int num = 0;
        if(drList != null && drList.size() > 0){
            num = drList.size();
            long serialNo = System.currentTimeMillis();
            List<Event> sendList = new ArrayList<>();
            List<Long> idList = new ArrayList<>();
            for(DataReport dr : drList){
                Event event = new Event(dr.getUserId(),getEventyType(dr.getEventType()),
                        dr.getBid(),dr.getTimestamp(),dr.getExtendedInfo());
                sendList.add(event);
                idList.add(dr.getId());
            }
            UserDbHelper.getInstance().updateDataReportState(idList, serialNo);
            sendingReportDataMap.put(serialNo, sendList);
        }
        return num;
    }

    @Override
    public void reportNow(){
        checkRegistered();
        DataReportEvent event = new DataReportEvent(
                DataReportEvent.eType.REPORT_KEY_ON_BACKGROUND,
                "event REPORT_KEY_ON_BACKGROUND");
        EventBus.getDefault().post(event);
    }

    @Override
    public void reportEvent(EventType eventType){
        if(eventType == EventType.APP_LOGIN){
            this.reporting = false;
        }
        checkRegistered();
        DataReportEvent event = new DataReportEvent(
                DataReportEvent.eType.REPORT_GENERAL,
                "event reportEvent",
                eventType,
                "",
                GlobalDbHelper.getInstance().getLoginUser().getUserId(),
                System.currentTimeMillis(),
                "");
        EventBus.getDefault().post(event);
    }

    @Override
    public void reportEventBid(EventType eventType,String bid){
        checkRegistered();
        DataReportEvent event = new DataReportEvent(
                DataReportEvent.eType.REPORT_GENERAL,
                "event reportEventBid",
                eventType,
                bid,
                GlobalDbHelper.getInstance().getLoginUser().getUserId(),
                System.currentTimeMillis(),
                "");
        EventBus.getDefault().post(event);
    }

    @Override
    public void reportGameData(EventType eventType,String bid,Long userId){
        checkRegistered();
        Long temp = userId;
        if (temp == null){
            temp = GlobalDbHelper.getInstance().getLoginUser().getUserId();
        }
        DataReportEvent event = new DataReportEvent(
                DataReportEvent.eType.REPORT_GENERAL,
                "event reportGameData",
                eventType,
                bid,
                temp,
                System.currentTimeMillis(),
                "");
        EventBus.getDefault().post(event);
    }

    @Override
    public void reportExtendedInfo(EventType eventType,String bid,String extendedInfo){
        checkRegistered();
        DataReportEvent event = new DataReportEvent(
                DataReportEvent.eType.REPORT_GENERAL,
                "event reportExtendedInfo",
                eventType,
                bid,
                GlobalDbHelper.getInstance().getLoginUser().getUserId(),
                System.currentTimeMillis(),
                extendedInfo);
        EventBus.getDefault().post(event);
    }

    @Override
    public void reportExtendedInfo(EventType eventType,String bid,String extendedInfo,Long userId){
        checkRegistered();
        Long temp = userId;
        if (temp == null){
            temp = GlobalDbHelper.getInstance().getLoginUser().getUserId();
        }
        DataReportEvent event = new DataReportEvent(
                DataReportEvent.eType.REPORT_GENERAL,
                "event reportExtendedInfo",
                eventType,
                bid,
                temp,
                System.currentTimeMillis(),
                extendedInfo);
        EventBus.getDefault().post(event);
    }
};
