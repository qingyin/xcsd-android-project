
# 考勤接口

## 1. 班级考勤详情

0:缺勤，1：出勤，2：请假
url: /class_attendance

```
message ClassAttendanceRequest{
    required int64 classId = 1;  //班级ID
    required int64 date = 2;     //日期,格式yyyyMMdd
}

message ClassAttendanceResponse{
    repeated int64 present = 1;  //出席
    repeated int64 absence = 2;  //缺席
    repeated int64 leave = 3;    //请假
}
```

## 2. 宝宝考勤详情
url: /child_attendance

```
message ChildAttendanceRequest{
    required int64 month = 1;     //日期,格式yyyyMM
}


message ClassAttendanceResponse {
    repeated User present = 1; //出席
    repeated User absence = 2; //缺席
    repeated User leave = 3; //请假
}
```


## 3. 请假列表
url: /fetch_leave

```
enum LeaveStatus{
    APPLIED = 1;
    APPROVED = 2;
}

message Leave {
    required int64 id = 1;
    required string senderId = 1;
    required string senderName = 2;
    required string senderAvatar = 3;
    optional string reason = 4;
    required int64 beginDate = 5;
    required int64 endDate = 6;
    optional int64 replyUserId = 7;
    optional int64 replyUserName = 8;
    optional string reply= 9;
    required LeaveStatus status = 10;
    required int64 createdOn = 11;
}

message FetchLeaveRequest {
    required int64 sinceId = 1;
    required int64 maxId = 2;
}

message FetchLeaveResponse {
    repeated Leave leaves = 1;
}
```
## 4. 批假
url: /approve_leave

```
message ApproveLeaveRequest {
    required int64 leaveId = 1;
    optional string reply  = 2;
}

message ApproveLeaveResponse {
}
```
## 5. 请假
url: /apply_leave

```
message ApplyLeaveRequest {
    optional string reason = 1;
    required int32 beginDate = 2;
    required int32 endDate = 3;
}

message ApplyLeaveResponse {
}
```
## 6. 补考勤
url: /update_attendance

```

message UpdateAttendanceRequest{
    repeated int64 present = 1;  //补签为出勤的幼儿ID出席
    repeated int64 absence = 2;  //补签为缺席
    repeated int64 leave = 3;    //补签为请假
    required int64 date = 4; //补签日期 (时间戳)
}


message UpdateAttendanceResponse{
}
```

## 7.获取月份的休息日
url:/fetch_rest_day

```

message FetchRestDayRequest {
    required int64 date = 1; //年 时间戳
}

message FetchRestDayResponse {
    repeated int64 restDay = 1; //休息日期的时间戳
}
```

