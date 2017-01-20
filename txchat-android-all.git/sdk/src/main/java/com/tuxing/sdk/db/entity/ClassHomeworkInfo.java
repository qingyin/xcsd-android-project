package com.tuxing.sdk.db.entity;

/**
 * Created by apple on 16/4/8.
 */
public class ClassHomeworkInfo implements java.io.Serializable {
    public enum HomeworkType {
        CUSTOMIZED,		//1:定制作业
        UNIFIED,		//2:统一作业
    }
    private long homeworkId;
    private String className;		//班级名称
    private String title;
    private HomeworkType type;		//1定制作业 2统一作业
    private long sendTime;
    private int finishedCount;	//完成作业的数量
    private int totalCount;		//总数量

    public ClassHomeworkInfo(){}
    public ClassHomeworkInfo(long homeworkId,String className,String title,HomeworkType type,long sendTime,int finishedCount,int totalCount){
        this.homeworkId = homeworkId;
        this.className = className;
        this.title = title;
        this.type = type;
        this.sendTime = sendTime;
        this.finishedCount = finishedCount;
        this.totalCount = totalCount;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(long homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HomeworkType getType() {
        return type;
    }

    public void setType(HomeworkType type) {
        this.type = type;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public int getFinishedCount() {
        return finishedCount;
    }

    public void setFinishedCount(int finishedCount) {
        this.finishedCount = finishedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
