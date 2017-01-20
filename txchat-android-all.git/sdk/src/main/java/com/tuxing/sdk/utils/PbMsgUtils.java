package com.tuxing.sdk.utils;

import com.squareup.wire.Wire;
import com.tuxing.rpc.proto.App;
import com.tuxing.rpc.proto.Attach;
import com.tuxing.rpc.proto.AttachType;
import com.tuxing.rpc.proto.CommentType;
import com.tuxing.rpc.proto.MuteType;
import com.tuxing.rpc.proto.ParentType;
import com.tuxing.rpc.proto.PostType;
import com.tuxing.rpc.proto.QuestionAnswer;
import com.tuxing.rpc.proto.SendSmsCodeType;
import com.tuxing.rpc.proto.Tag;
import com.tuxing.rpc.proto.UserType;
import com.tuxing.sdk.db.entity.AbilityDetailList;
import com.tuxing.sdk.db.entity.AbilityPoint;
import com.tuxing.sdk.db.entity.Activity;
import com.tuxing.sdk.db.entity.CheckInRecord;
import com.tuxing.sdk.db.entity.ClassPicture;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.Feed;
import com.tuxing.sdk.db.entity.FeedMedicineTask;
import com.tuxing.sdk.db.entity.GardenMail;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.db.entity.HomeWorkGenerate;
import com.tuxing.sdk.db.entity.HomeWorkMember;
import com.tuxing.sdk.db.entity.HomeWorkRecord;
import com.tuxing.sdk.db.entity.HomeWorkUserRank;
import com.tuxing.sdk.db.entity.LightApp;
import com.tuxing.sdk.db.entity.Notice;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.modle.Answer;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.modle.CheckInCard;
import com.tuxing.sdk.modle.CommunionMessage;
import com.tuxing.sdk.modle.Expert;
import com.tuxing.sdk.modle.Knowledge;
import com.tuxing.sdk.modle.Question;
import com.tuxing.sdk.modle.QuestionTag;
import com.tuxing.sdk.modle.UpgradeInfo;
import com.xcsd.rpc.proto.ClassHomework;
import com.xcsd.rpc.proto.GenerateHomeworkResponse;
import com.xcsd.rpc.proto.Homework;
import com.xcsd.rpc.proto.HomeworkMember;
import com.xcsd.rpc.proto.UserRank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

//import com.xcsd.rpc.proto.HomeworkProto;

/**
 * Created by Alan on 2015/6/12.
 */
public class PbMsgUtils {
    private final static Logger logger = LoggerFactory.getLogger(PbMsgUtils.class);

    public static Department transObj(com.tuxing.rpc.proto.Department pbMsg) {
        Department department = new Department();
        department.setDepartmentId(pbMsg.id);
        department.setName(pbMsg.name);
        department.setParentId(pbMsg.parentId);
        department.setType(pbMsg.type.getValue());
        department.setChatGroupId(pbMsg.groupId);
        department.setShowParents(pbMsg.showParent);
        department.setAvatar(pbMsg.classPhoto);

        return department;
    }

    public static com.tuxing.rpc.proto.User transPbMsg(User user) {
        com.tuxing.rpc.proto.User.Builder pbBuilder = new com.tuxing.rpc.proto.User.Builder();

        pbBuilder.userId(user.getUserId());
        if (user.getAvatar() != null) {
            pbBuilder.avatar(user.getAvatar());
        }

        if (user.getAddress() != null) {
            pbBuilder.address(user.getAddress());
        }

        if (user.getBirthday() != null) {
            pbBuilder.birthday(user.getBirthday());
        }

        if (user.getNickname() != null) {
            pbBuilder.nickname(user.getNickname());
        }
        if (user.getCombinedNickname() != null) {
            pbBuilder.combinedNickname(user.getCombinedNickname());

        }

        if (user.getGuarder() != null) {
            pbBuilder.guarder(user.getGuarder());
        }

        if (user.getGender() != null) {
            if (user.getGender() == com.tuxing.rpc.proto.SexType.FEMALE.getValue()) {
                pbBuilder.sexType(com.tuxing.rpc.proto.SexType.FEMALE);
            } else {
                pbBuilder.sexType(com.tuxing.rpc.proto.SexType.MALE);

            }
        }

        if (user.getSignature() != null) {
            pbBuilder.sign(user.getSignature());
        }

        return pbBuilder.build();
    }

    public static User transObj(com.tuxing.rpc.proto.User pbMsg) {
        User user = new User();
        user.setUserId(pbMsg.userId);
        user.setUsername(Wire.get(pbMsg.userName, ""));
        user.setType(pbMsg.userType.getValue());

        user.setAvatar(pbMsg.avatar);
        user.setMobile(pbMsg.mobile);
        user.setSignature(pbMsg.sign);
        user.setChildUserId(pbMsg.childUserId);
        user.setBirthday(pbMsg.birthday);
        user.setAddress(pbMsg.address);
        user.setNickname(pbMsg.nickname);
        user.setCombinedNickname(pbMsg.combinedNickname);
        user.setRealname(pbMsg.realname);
        user.setClassName(pbMsg.className);
        user.setGardenName(pbMsg.gardenName);
        user.setClassId(pbMsg.classId);
        user.setGardenId(pbMsg.gardenId);
        user.setPositionId(pbMsg.positionId);
        user.setPositionName(pbMsg.positionName);
        user.setGuarder(pbMsg.guarder);

        if (pbMsg.sexType != null) {
            user.setGender(pbMsg.sexType.getValue());
        }

        if (pbMsg.ParentType != null) {
            user.setRelativeType(pbMsg.ParentType.getValue());
        }

        user.setActivated(Wire.get(pbMsg.activated, false));

        return user;
    }

    public static Notice transObj(com.tuxing.rpc.proto.Notice pbMsg) {
        Notice notice = new Notice();
        notice.setNoticeId(pbMsg.id);
        notice.setContent(pbMsg.content);
        notice.setSenderUserId(pbMsg.sendUserId);
        notice.setSenderName(pbMsg.senderName);
        notice.setSenderAvatar(pbMsg.senderAvatar);
        notice.setSendTime(pbMsg.sendTime);

        JSONArray attachments = new JSONArray();
        for (com.tuxing.rpc.proto.Attach attachment : pbMsg.attaches) {
            try {
                JSONObject jsonAttach = new JSONObject();
                jsonAttach.put("url", attachment.fileurl);
                jsonAttach.put("type", attachment.attachType.getValue());

                attachments.put(jsonAttach);
            } catch (JSONException e) {
                logger.error("Json Exception", e);
            }
        }

        notice.setAttachments(attachments.toString());
        notice.setSummary(pbMsg.content.length() > 10 ?
                pbMsg.content.substring(0, 10) : pbMsg.content);
        notice.setUnread(!pbMsg.isRead);

        return notice;
    }

    public static CheckInRecord transObj(com.tuxing.rpc.proto.Checkin pbMsg) {
        CheckInRecord checkInRecord = new CheckInRecord();
        checkInRecord.setCheckInRecordId(pbMsg.userId);
        checkInRecord.setGardenId(pbMsg.gardenId);
        checkInRecord.setCheckInRecordId(pbMsg.id);
        checkInRecord.setCardNum(pbMsg.cardCode);
        checkInRecord.setCheckInTime(pbMsg.checkinTime);
        checkInRecord.setUserId(pbMsg.userId);
        checkInRecord.setUserName(pbMsg.userName);
        JSONArray attachments = new JSONArray();
        if (pbMsg.attach != null) {
            try {
                JSONObject jsonAttach = new JSONObject();
                jsonAttach.put("url", pbMsg.attach.fileurl);
                jsonAttach.put("type", pbMsg.attach.attachType.getValue());

                attachments.put(jsonAttach);
            } catch (JSONException e) {
                logger.error("Json Exception", e);
            }
        }

        checkInRecord.setSnapshots(attachments.toString());

        checkInRecord.setParentName(pbMsg.parentName);
        checkInRecord.setClassName(pbMsg.className);

        return checkInRecord;
    }

    public static CheckInCard transObj(com.tuxing.rpc.proto.BindCardInfo pbMsg) {
        CheckInCard card = new CheckInCard();
        card.setCardNum(pbMsg.cardCode);
        card.setUserName(pbMsg.nickName);
        card.setAvatar(pbMsg.avatar);
        card.setUserId(pbMsg.parentId);
        if (pbMsg.parentType != null) {
            card.setRelativeType(pbMsg.parentType.getValue());
        }

        return card;
    }

    public static FeedMedicineTask transObj(com.tuxing.rpc.proto.FeedMedicineTask pbMsg) {
        FeedMedicineTask task = new FeedMedicineTask();
        task.setTaskId(pbMsg.id);
        task.setBeginDate(pbMsg.beginDate);
        task.setSenderId(pbMsg.parentUserId);
        task.setSenderName(pbMsg.parentName);
        task.setSenderAvatar(pbMsg.parentAvatarUrl);
        task.setClassId(pbMsg.classId);
        task.setClassName(pbMsg.className);
        task.setClassAvatar(pbMsg.classAvatarUrl);
        task.setDescription(pbMsg.desc);
        task.setSendTime(pbMsg.createdOn);
        task.setUpdated(!pbMsg.hasRead);

        JSONArray attachments = new JSONArray();
        for (com.tuxing.rpc.proto.Attach attachment : pbMsg.attaches) {
            try {
                JSONObject jsonAttach = new JSONObject();
                jsonAttach.put("url", attachment.fileurl);
                jsonAttach.put("type", attachment.attachType.getValue());

                attachments.put(jsonAttach);
            } catch (JSONException e) {
                logger.error("Json Exception", e);
            }
        }

        task.setAttachments(attachments.toString());

        return task;
    }

    public static GardenMail transObj(com.tuxing.rpc.proto.GardenMail pbMsg) {
        GardenMail gardenMail = new GardenMail();
        gardenMail.setMailId(pbMsg.id);
        gardenMail.setAnonymous(pbMsg.anonymous);
        gardenMail.setContent(pbMsg.content);
        gardenMail.setSenderId(pbMsg.fromUserId);
        gardenMail.setSenderName(pbMsg.fromUsername);
        gardenMail.setSenderAvatar(pbMsg.fromUserAvatarUrl);
        gardenMail.setGardenId(pbMsg.gardenId);
        gardenMail.setGardenName(pbMsg.gardenName);
        gardenMail.setGardenAvatar(pbMsg.gardenAvatarUrl);
        gardenMail.setSendTime(pbMsg.createdOn);
        gardenMail.setUpdated(!pbMsg.hasRead);

        return gardenMail;
    }

    public static Comment transObj(com.tuxing.rpc.proto.Comment pbMsg) {
        Comment comment = new Comment();
        comment.setCommentId(pbMsg.id);
        comment.setCommentType(pbMsg.commentType.getValue());
        comment.setContent(pbMsg.content);
        comment.setTargetType(pbMsg.targetType.getValue());
        comment.setTopicId(pbMsg.targetId);
        comment.setTopicUserId(pbMsg.targetUserId);
        comment.setSenderId(pbMsg.userId);
        comment.setSenderName(pbMsg.userNickName);
        comment.setSenderAvatar(pbMsg.userAvatarUrl);
        comment.setReplayToUserId(pbMsg.toUserId);
        comment.setReplayToUserName(pbMsg.toUserNickName);
        comment.setSendTime(pbMsg.createOn);

        return comment;
    }

    public static Comment transObj(com.tuxing.rpc.proto.Like pbMsg) {
        Comment comment = new Comment();
        comment.setCommentId(pbMsg.commentId);
        comment.setCommentType(com.tuxing.rpc.proto.CommentType.LIKE.getValue());
        comment.setTopicId(pbMsg.targetId);
        comment.setSenderId(pbMsg.userId);
        comment.setSenderName(pbMsg.nickName);
        comment.setSenderAvatar(pbMsg.userAvatarUrl);

        return comment;
    }

    public static Feed transObj(com.tuxing.rpc.proto.Feed pbMsg) {
        Feed feed = new Feed();
        feed.setFeedId(pbMsg.id);
        feed.setContent(pbMsg.content);
        feed.setUserId(pbMsg.userId);
        feed.setUserName(pbMsg.userNickName);
        feed.setFeedType(Constants.FEED_TYPE.USER_FEED);
        if (pbMsg.userType != null) {
            feed.setUserType(pbMsg.userType.getValue());
        }
        feed.setUserAvatar(pbMsg.userAvatarUrl);
        feed.setPublishTime(pbMsg.createOn);

        List<Comment> commentList = new ArrayList<>();

        for (com.tuxing.rpc.proto.Comment pbComment : pbMsg.comments) {
            commentList.add(transObj(pbComment));
        }

        feed.setComments(commentList);

        List<Comment> likeList = new ArrayList<>();

        for (com.tuxing.rpc.proto.Like pbLike : pbMsg.likes) {
            likeList.add(transObj(pbLike));
        }

        feed.setLikes(likeList);

        JSONArray attachments = new JSONArray();
        for (com.tuxing.rpc.proto.Attach attachment : pbMsg.attaches) {
            try {
                JSONObject jsonAttach = new JSONObject();
                jsonAttach.put("url", attachment.fileurl);
                jsonAttach.put("type", attachment.attachType.getValue());

                attachments.put(jsonAttach);
            } catch (JSONException e) {
                logger.error("Json Exception", e);
            }
        }
        feed.setAttachments(attachments.toString());

        feed.setHasMoreComment(pbMsg.hasMoreComment);

        return feed;
    }

    public static Feed trans2Feed(com.tuxing.rpc.proto.Activity pbMsg) {
        Feed feed = new Feed();
        feed.setFeedId(Long.MAX_VALUE);
        feed.setContent(pbMsg.title);
        feed.setUserName(pbMsg.nickname);

        feed.setUserAvatar(pbMsg.avatarUrl);
        feed.setPublishTime(pbMsg.createOn);
        feed.setFeedType(Constants.FEED_TYPE.ACTIVITY_FEED);
        feed.setActivityId(pbMsg.id);

        JSONArray attachments = new JSONArray();
        for (String attachment : pbMsg.picUrl) {
            try {
                JSONObject jsonAttach = new JSONObject();
                jsonAttach.put("url", attachment);
                jsonAttach.put("type", AttachType.PIC);

                attachments.put(jsonAttach);
            } catch (JSONException e) {
                logger.error("Json Exception", e);
            }
        }
        feed.setAttachments(attachments.toString());

        return feed;
    }

    public static Activity transObj(com.tuxing.rpc.proto.Activity pbMsg) {
        Activity activity = new Activity();
        activity.setActivityId(pbMsg.id);
        activity.setTitle(pbMsg.title);
        activity.setSenderName(pbMsg.nickname);

        activity.setSenderAvatar(pbMsg.avatarUrl);
        activity.setCreatedOn(pbMsg.createOn);

        JSONArray attachments = new JSONArray();
        for (String attachment : pbMsg.picUrl) {
            try {
                JSONObject jsonAttach = new JSONObject();
                jsonAttach.put("url", attachment);
                jsonAttach.put("type", AttachType.PIC);

                attachments.put(jsonAttach);
            } catch (JSONException e) {
                logger.error("Json Exception", e);
            }
        }
        activity.setAttachments(attachments.toString());

        return activity;
    }

    public static ContentItem transObj(com.tuxing.rpc.proto.Post pbMsg) {
        ContentItem item = new ContentItem();
        item.setItemId(pbMsg.id);
        item.setContentType(pbMsg.postType.getValue());
        item.setCoverImageUrl(pbMsg.coverImageUrl);
        item.setPublishTime(pbMsg.createdOn);
        item.setTitle(pbMsg.title);
        item.setSummary(pbMsg.summary);
        item.setContent(pbMsg.content);
        item.setPostUrl(pbMsg.postUrl);

        return item;
    }

    public static UpgradeInfo transObj(com.tuxing.rpc.proto.Upgrade pbMsg) {
        UpgradeInfo upgradeInfo = new UpgradeInfo();

        upgradeInfo.setShowAtMain(Wire.get(pbMsg.showAtMain, false));
        upgradeInfo.setForceUpgrade(Wire.get(pbMsg.mustUpdate, false));
        upgradeInfo.setHasNewVersion(Wire.get(pbMsg.isUpdate, false));
        upgradeInfo.setShowMsg(pbMsg.showMsg);
        upgradeInfo.setUpgradeMsg(pbMsg.updateMsg);
        upgradeInfo.setUpgradeMsg(pbMsg.updateMsg);
        upgradeInfo.setUpgradeUrl(pbMsg.updateUrl);
        upgradeInfo.setVersion(pbMsg.versionCode);

        return upgradeInfo;
    }

    public static ClassPicture transObj(com.tuxing.rpc.proto.DepartmentPhoto pbMsg) {
        ClassPicture picture = new ClassPicture();

        picture.setPicId(pbMsg.id);
        picture.setCreatedOn(pbMsg.createOn);
        picture.setPicUrl(pbMsg.fileKey);

        return picture;
    }

    public static PostType transPostType(int value) {
        for (PostType postType : PostType.values()) {
            if (postType.getValue() == value) {
                return postType;
            }
        }

        return null;
    }

    public static AttachType transAttachType(int value) {
        for (AttachType attachType : AttachType.values()) {
            if (attachType.getValue() == value) {
                return attachType;
            }
        }

        return null;
    }

    public static ParentType transParentType(int value) {
        for (ParentType parentType : ParentType.values()) {
            if (parentType.getValue() == value) {
                return parentType;
            }
        }

        return null;
    }

    public static SendSmsCodeType transSendSmsCodeType(int value) {
        for (SendSmsCodeType sendSmsCodeType : SendSmsCodeType.values()) {
            if (sendSmsCodeType.getValue() == value) {
                return sendSmsCodeType;
            }
        }

        return null;
    }

    public static CommentType transCommentType(int value) {
        for (CommentType commentType : CommentType.values()) {
            if (commentType.getValue() == value) {
                return commentType;
            }
        }

        return null;
    }

    public static MuteType transMuteType(int value) {
        for (MuteType muteType : MuteType.values()) {
            if (muteType.getValue() == value) {
                return muteType;
            }
        }

        return null;
    }

//    public static HomeWorkInfo transObj(HomeworkProto.Homework hw){
//        HomeWorkInfo hwInfo = new HomeWorkInfo();
//        hwInfo.setId(hw.getId());
//        hwInfo.setMemberId(hw.getMemberId());
//        hwInfo.setTitle(hw.getTitle());
//        hwInfo.setSendUserId(hw.getSendUserId());
//        hwInfo.setSenderName(hw.getSenderName());
//        hwInfo.setSenderAvatar(hw.getSenderAvatar());
//        hwInfo.setTargetName(hw.getTargetName());
//        hwInfo.setStatus(HomeWorkInfo.HomeworkStatus.valueOf(hw.getStatus().toString()));
//        hwInfo.setHasRead(hw.getHasRead());
//        hwInfo.setSendTime(hw.getSendTime());
//        return hwInfo;
//    }

    public static HomeWorkRecord transObj(Homework hw) {
        HomeWorkRecord hwRecord = new HomeWorkRecord(
                null,
                hw.id,
                hw.memberId,
                hw.title,
                hw.sendUserId,
                hw.senderName,
                hw.senderAvatar,
                hw.targetName,
                hw.status.getValue(),
                hw.hasRead,
                hw.sendTime
        );
        return hwRecord;
    }

    public static HomeWorkClass transObj(ClassHomework hw) {
        HomeWorkClass hwInfo = new HomeWorkClass(
                null,
                hw.homeworkId,
                hw.className,
                hw.title,
                hw.type.getValue(),
                hw.sendTime,
                hw.finishedCount,
                hw.totalCount
        );
        return hwInfo;
    }

    public static HomeWorkUserRank transObj(UserRank ur) {
        HomeWorkUserRank userRank = new HomeWorkUserRank(
                null,
                ur.rank,
                ur.userId,
                ur.name,
                ur.avatar,
                ur.score
        );
        return userRank;
    }

    public static HomeWorkMember transObj(HomeworkMember hwm, long homeworkId) {
        HomeWorkMember info = new HomeWorkMember(
                null,
                hwm.memberId,
                hwm.name,
                hwm.avatar,
                hwm.status.getValue(),
                hwm.score,
                hwm.specialAttention,
                homeworkId
        );
        return info;
    }

    public static HomeWorkGenerate transObj(GenerateHomeworkResponse.UserHomework uhw) {
        HomeWorkGenerate info = new HomeWorkGenerate(
                null,
                uhw.childUserId,
                uhw.name,
                uhw.avatar,
                uhw.generateCount,
                uhw.remainMaxCount,
                uhw.specialAttention
        );
        return info;
    }

    public static Attachment transObj(Attach pbMsg) {
        Attachment attachment = new Attachment();
        attachment.setType(pbMsg.attachType.getValue());
        attachment.setFileUrl(pbMsg.fileurl);

        return attachment;
    }


    public static Question transObj(com.tuxing.rpc.proto.Question pbMsg) {
        Question question = new Question();

        question.setQuestionId(pbMsg.id);
        question.setAuthorUserId(pbMsg.authorId);
        question.setAuthorUserName(pbMsg.authorName);
        question.setAuthorUserAvatar(pbMsg.authorAvatar);
        question.setAnswerCount(pbMsg.replyNum);
        question.setViewCount(pbMsg.viewNum);
        question.setTagId(pbMsg.tagId);
        question.setTag(pbMsg.tagName);
        question.setTitle(pbMsg.title);
        question.setContent(pbMsg.content);
        question.setAnswered(pbMsg.hasAnswered);
        question.setCreateOn(pbMsg.createOn);

        List<Attachment> attachments = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pbMsg.attaches)) {

            for (Attach attach : pbMsg.attaches) {
                attachments.add(transObj(attach));
            }
        }

        question.setAttachments(attachments);
        question.setTitleSummary(Wire.get(pbMsg.titleSummary, ""));
        question.setContentSummary(Wire.get(pbMsg.contentSummary, ""));
        return question;
    }

    public static QuestionTag transObj(Tag pbMsg) {
        QuestionTag tag = new QuestionTag();

        tag.setTagId(pbMsg.id);
        tag.setName(pbMsg.name);
        tag.setParentId(pbMsg.parentId);
        tag.setType(pbMsg.type);

        return tag;
    }

    public static Answer transObj(QuestionAnswer pbMsg) {
        Answer answer = new Answer();

        answer.setAnswerId(pbMsg.id);
        answer.setContent(pbMsg.content);
        answer.setAttachments(null);
        answer.setAuthorUserId(pbMsg.authorId);
        answer.setAuthorUserName(pbMsg.authorName);
        answer.setAuthorUserAvatar(pbMsg.authorAvatar);
        answer.setAuthorTitle(pbMsg.authorTitle);
        answer.setCommentCount(pbMsg.replyNum);
        answer.setThanksCount(pbMsg.thankNum);
        answer.setExpert(pbMsg.userType == UserType.EXPERT);
        answer.setQuestionId(pbMsg.questionId);
        answer.setQuestionTitle(pbMsg.questionTitle);
        answer.setThanked(Wire.get(pbMsg.hasThanked, false));
        answer.setCreateOn(pbMsg.createOn);
        answer.setUpdateOn(pbMsg.updateOn);

        List<Attachment> attachments = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pbMsg.attaches)) {

            for (Attach attach : pbMsg.attaches) {
                attachments.add(transObj(attach));
            }
        }

        answer.setAttachments(attachments);
        answer.setQuestionAuthor(pbMsg.questionAuthor);

        List<Attachment> questionAttachments = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pbMsg.questionAttaches)) {
            for (Attach attach : pbMsg.questionAttaches) {
                questionAttachments.add(transObj(attach));
            }
        }
        answer.setQuestionAttaches(questionAttachments);
        answer.setQuestionTagId(Wire.get(pbMsg.questionTagId, 0l));
        answer.setQuestionTagName(Wire.get(pbMsg.questionTagName, ""));

        return answer;
    }

    public static Knowledge transObj(com.tuxing.rpc.proto.Knowledge pbMsg) {
        Knowledge knowledge = new Knowledge();

        knowledge.setKnowledgeId(pbMsg.id);
        knowledge.setTitle(pbMsg.title);
        knowledge.setDescription(pbMsg.description);
        knowledge.setType(pbMsg.contentType);
        knowledge.setCoverImageUrl(pbMsg.coverPicUrl);
        knowledge.setContentUrl(pbMsg.contentUrl);
        knowledge.setAuthorUserId(pbMsg.authorId);
        knowledge.setAuthorTitle(pbMsg.authorTitle);
        knowledge.setAuthorAvatar(pbMsg.authorAvatar);
        knowledge.setCommentCount(pbMsg.replyNum);
        knowledge.setThankCount(pbMsg.likedNum);
        knowledge.setCreateOn(pbMsg.createOn);
        knowledge.setThanked(pbMsg.hasLiked);

        List<QuestionTag> questionTagList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pbMsg.tags)) {
            for (Tag tag : pbMsg.tags) {
                questionTagList.add(transObj(tag));
            }
        }

        knowledge.setTags(questionTagList);


        return knowledge;
    }

    public static Expert transObj(com.tuxing.rpc.proto.Expert pbMsg) {
        Expert expert = new Expert();

        expert.setExpertId(pbMsg.id);
        expert.setName(pbMsg.name);
        expert.setAvatar(pbMsg.avatar);
        expert.setTitle(pbMsg.title);
        expert.setDescription(pbMsg.description);
        expert.setSign(pbMsg.sign);
        expert.setAnswerCount(pbMsg.answerNum);
        expert.setKnowledgeCount(pbMsg.knowledgeNum);
        expert.setThankCount(pbMsg.thankNum);
        expert.setBanner(pbMsg.rankBanner);

        List<QuestionTag> questionTagList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pbMsg.specialities)) {
            for (Tag tag : pbMsg.specialities) {
                questionTagList.add(transObj(tag));
            }
        }

        expert.setSpecialities(questionTagList);

        return expert;
    }

    public static CommunionMessage transObj(com.tuxing.rpc.proto.CommunionMessage pbMsg) {
        CommunionMessage message = new CommunionMessage();
        message.setMessageId(pbMsg.id);
        message.setUserType(pbMsg.optUserType);
        message.setUserName(pbMsg.optUserName);
        message.setUserAvatar(pbMsg.optUserAvater);
        message.setTitle(pbMsg.title);
        message.setContent(pbMsg.content);
        message.setTargetId(pbMsg.objId);
        message.setAction(pbMsg.action);
        if (pbMsg.refQuestion != null) {
            message.setQuestion(transObj(pbMsg.refQuestion));
        }
        if (pbMsg.refAnswer != null) {
            message.setAnswer(transObj(pbMsg.refAnswer));
        }
        message.setCreateOn(pbMsg.createOn);

        return message;
    }

    public static LightApp transObj(App app) {
        LightApp lightApp = new LightApp();

        lightApp.setName(app.name);
        lightApp.setModuleName(app.sign);
        lightApp.setIcon(app.icon);
        lightApp.setType(app.appType.getValue());
        lightApp.setUrl(app.url);
        lightApp.setCounterName(app.counterName);
        lightApp.setCounterType(app.counterType.getValue());
        lightApp.setIsNew(app.isNewApp);

        return lightApp;
    }

    public static AbilityDetailList transObj(com.xcsd.rpc.proto.AbilityDetail args) {
        AbilityDetailList adl = new AbilityDetailList();

        adl.setAbility(args.ability.getValue());
        adl.setLevel(args.level);
        adl.setAvgLevel(args.avgLevel);
        adl.setMaxScore(args.maxScore);
        adl.setPercentage(args.percentage);
        adl.setScore(args.score);

        return adl;
    }

    public static AbilityPoint transObj(com.xcsd.rpc.proto.AbilityStatResponse.Point point) {
        AbilityPoint adl = new AbilityPoint();

        adl.setScore(point.score);
        adl.setNumber(point.number);

        return adl;
    }

}
