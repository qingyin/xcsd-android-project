package com.tuxing.sdk.event;


import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.xcsd.rpc.proto.EventType;

/**
 * Created by apple on 16/10/24.
 */
public class DataReportEvent extends BaseEvent {
    public enum eType{
        REPORT_GENERAL,
        REPORT_KEY_ON_BACKGROUND,
        REPORT_FIXED_TIME,
        REPORT_MAX_NUM,

        REPORT_SUCCESS,
        REPORT_FAILED,
    }

    private eType event;

    private Long userid;//触发事件的用户ID
    private EventType eventType;
    private String bid;//业务id
    private Long timestamp;//业务时间
    private String extendedInfo;//扩展信息 考虑用json或直接写值  标注有ext:的需要填

    private Long serialNo;

    public DataReportEvent(eType event,String msg){
        super(msg);
        this.event = event;
    }

    public DataReportEvent(eType event,String msg,EventType eventType,String bid,Long userId,Long timestamp,String extendedInfo){
        super(msg);
        this.event = event;
        this.eventType = eventType;
        this.userid = userId;
        this.bid = bid;
        this.timestamp = timestamp;
        this.extendedInfo = extendedInfo;
    }

    public eType getEvent(){
        return this.event;
    }

    public String getBid() {
        return bid;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Long getTimestamp() {
        return timestamp;
    }
    public Long getUserId() {
        return userid;
    }
    public String getExtendedInfo(){
        return extendedInfo;
    }

    public long getSerialNo(){
        return this.serialNo;
    }

    public void setSerialNo(long serialNo){
        this.serialNo = serialNo;
    }
};

