package com.example.administrator.testbylt;




import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 * 这是任务条目的通用格式的实体类，不用内部类的原因是BaseMultiItemQuickAdapter读不出内部类里的内容报错
 * 注意：这里必须重写getItemType()，返回的常量必须与继承自BaseMultiItemQuickAdapter里想要绑定的view的viewtype一致！
 * 且任务条目表及其关联的任务执行人和发起人表的本地数据库id都改名为localId
 */

public class TaskItemBean implements Serializable {
    private static final long serialVersionUID = 15L;
    //这是一个多重实体类，用来在fragment里放多个不同类型不同布局的任务条目
    public static final int TASK_PENDING_EXECUTION = 1;//待执行
    public static final int TASK_PENDING_EVALUATION = 2;//待评价
    public static final int TASK_HAS_BEEN_COMPLETED = 3;//已完结

    //这些数据不用再adapter里
    public static final int HANDLING_ALARM_TASK = 5;//处理警报任务
    public static final int UPLOAD_IDCARD_INFORMATION_TASK = 6;//上传身份证信息
    public static final int UPLOAD_RESIDENT_INFORMATION_TASK = 7;//上传居民详细信息
    public static final int UPLOAD_CHECK_BLACKLIST_TASK = 8;//验证黑名单
    public static final int QUANTITATIVE_SURVEY_TASK = 9;//定量查楼,也叫入户登记
    public static final int ABNORMAL_ENTRANCE_TASK = 10;//处理非正常出入任务，例如审核类似常住出入却没有登记原因的人
    public static final int ADDITIONAL_RECORDING_TASK = 11;//复核人员身份并补录常住居民信息任务
    public static final int GENERAL_INSTRUCTIION_TASK = 12;//一般指示性任务





    private Long localId;
    private String severid;
    private String taskTitle;
    private String taskContent;
    private int taskType;
    private boolean isRead;
    private int status;
    private String createDate;
    private String startDate;
    private String deadlineDate;

    private Long associatedOriginatorId;//此id用来关联任务条目表TaskItemBean
    private boolean isExpired;//前台必要，后台不建\
    private int priority;//优先级，前台未来可能要用，后台暂时不建
    private Long associatedIdCardId;

    private Long associatedResidentInfomationId;

    private Long associatedCheckBlacklistId;

    public TaskItemBean(Long localId, String severid, String taskTitle,
                        String taskContent, int taskType, boolean isRead, int status,
                        String createDate, String startDate, String deadlineDate,
                        Long associatedOriginatorId, boolean isExpired, int priority,
                        Long associatedIdCardId, Long associatedResidentInfomationId,
                        Long associatedCheckBlacklistId) {
        this.localId = localId;
        this.severid = severid;
        this.taskTitle = taskTitle;
        this.taskContent = taskContent;
        this.taskType = taskType;
        this.isRead = isRead;
        this.status = status;
        this.createDate = createDate;
        this.startDate = startDate;
        this.deadlineDate = deadlineDate;
        this.associatedOriginatorId = associatedOriginatorId;
        this.isExpired = isExpired;
        this.priority = priority;
        this.associatedIdCardId = associatedIdCardId;
        this.associatedResidentInfomationId = associatedResidentInfomationId;
        this.associatedCheckBlacklistId = associatedCheckBlacklistId;
    }

    public TaskItemBean(String severid){
        this.severid=severid;
    }





    public Long getLocalId() {
        return this.localId;
    }



    public void setLocalId(Long localId) {
        this.localId = localId;
    }



    public String getSeverid() {
        return this.severid;
    }



    public void setSeverid(String severid) {
        this.severid = severid;
    }



    public String getTaskTitle() {
        return this.taskTitle;
    }



    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }



    public String getTaskContent() {
        return this.taskContent;
    }



    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }



    public int getTaskType() {
        return this.taskType;
    }



    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }



    public boolean getIsRead() {
        return this.isRead;
    }



    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }



    public int getStatus() {
        return this.status;
    }



    public void setStatus(int status) {
        this.status = status;
    }



    public String getCreateDate() {
        return this.createDate;
    }



    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }



    public String getStartDate() {
        return this.startDate;
    }



    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }



    public String getDeadlineDate() {
        return this.deadlineDate;
    }



    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }



    public Long getAssociatedOriginatorId() {
        return this.associatedOriginatorId;
    }



    public void setAssociatedOriginatorId(Long associatedOriginatorId) {
        this.associatedOriginatorId = associatedOriginatorId;
    }



    public boolean getIsExpired() {
        return this.isExpired;
    }



    public void setIsExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }



    public int getPriority() {
        return this.priority;
    }



    public void setPriority(int priority) {
        this.priority = priority;
    }



    public Long getAssociatedIdCardId() {
        return this.associatedIdCardId;
    }



    public void setAssociatedIdCardId(Long associatedIdCardId) {
        this.associatedIdCardId = associatedIdCardId;
    }



    public Long getAssociatedResidentInfomationId() {
        return this.associatedResidentInfomationId;
    }



    public void setAssociatedResidentInfomationId(
            Long associatedResidentInfomationId) {
        this.associatedResidentInfomationId = associatedResidentInfomationId;
    }



    public Long getAssociatedCheckBlacklistId() {
        return this.associatedCheckBlacklistId;
    }



    public void setAssociatedCheckBlacklistId(Long associatedCheckBlacklistId) {
        this.associatedCheckBlacklistId = associatedCheckBlacklistId;
    }





}
