package com.tuxing.sdk.manager;

import com.tuxing.rpc.proto.TagType;
import com.tuxing.rpc.proto.UserType;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.modle.CommunionMessage;

import java.util.List;

/**
 * Created by apple on 16/4/20.
 */
public interface QuoraManager extends BaseManager {

    /***
     * 获取最热的问题列表, 分页显示
     * 获取成功,返回QuestionEvent.EventType.GET_TOP_HOT_QUESTION_SUCCESS事件, 包括Question列表
     * 获取失败,返回QuestionEvent.EventType.GET_TOP_HOT_QUESTION_FAILED事件, 包括有错误信息
     *
     * @param page 页码
     */
    void getTopHotQuestions(int page);

    /***
     * 获取最新的问题列表, 分页显示
     * 获取成功,返回返回QuestionEvent.EventType.GET_LATEST_QUESTION_SUCCESS事件, 包括Question列表
     * 获取失败,返回返回QuestionEvent.EventType.GET_LATEST_QUESTION_FAILED事件, 包括有错误信息
     *
     * @param userId 问题的提出者,可不传入
     */
    void getLatestQuestions(Long userId);

    /***
     * 获取最新的问题列表, 分页显示
     * 获取成功,返回返回QuestionEvent.EventType.GET_HISTORY_QUESTION_SUCCESS事件, 包括Question列表
     * 获取失败,返回返回QuestionEvent.EventType.GET_LATEST_QUESTION_FAILED事件, 包括有错误信息
     *
     * @param userId        问题的提出者,可不传入
     * @param maxQuestionId 上次返回的最大的问题ID
     */
    void getHistoryQuestions(Long userId, long maxQuestionId);


    /***
     * 根据ID获取问题的详情
     * 获取成功,返回返回QuestionEvent.EventType.GET_QUESTION_BY_ID_SUCCESS事件, 包括Question列表
     * 获取失败,返回返回QuestionEvent.EventType.GET_QUESTION_BY_ID_FAILED事件, 包括有错误信息
     *
     * @param questionId 问题的ID
     */
    void getQuestionById(long questionId);

    /***
     * 获取问题的分类标签
     * 获取成功,返回QuestionTagEvent.EventType.FETCH_TAGS_SUCCESS事件, 包括QuestionTag列表
     * 获取失败,返回QuestionTagEvent.EventType.FETCH_TAGS_FAILED事件, 包括有错误信息
     *
     * @param tagType 标签分类
     */
    void getQuestionTags(TagType tagType);

    /***
     * 提问
     * 请求成功, 返回QuestionEvent.EventType.ASK_QUESTION_SUCCESS事件
     * 请求成功, 返回QuestionEvent.EventType.ASK_QUESTION_FAILED事件
     *
     * @param tagId       问题的分类标签ID
     * @param title       问题的标题
     * @param content     问题的具体内容
     * @param attachments 问题相关的图片
     */
    void askQuestion(Long expertId, Long tagId, String title, String content, List<Attachment> attachments);

    /***
     * 回答问题
     * 请求成功, 返回AnswerEvent.EventType.ANSWER_QUESTION_SUCCESS事件
     * 请求成功, 返回AnswerEvent.EventType.ANSWER_QUESTION_FAILED事件
     *
     * @param questionId  回答的问题ID
     * @param content     回答的内容
     * @param attachments 回答相关的图片
     */
    void answerQuestion(long questionId, String content, List<Attachment> attachments);

    /***
     * 获取最新的问题
     * 获取成功,返回AnswerEvent.EventType.GET_LATEST_ANSWER_SUCCESS事件, 包括Answer列表
     * 获取失败,返回AnswerEvent.EventType.GET_LATEST_ANSWER_FAILED事件, 包括有错误信息
     *
     * @param questionId 问题ID
     * @param userId     用户ID,包括普通用户和专家
     * @param userType   用户类型,是专家还是普通用户
     */
    void getLatestAnswers(Long questionId, Long userId, UserType userType);

    /***
     * 获取历史的问题
     * 获取成功,返回AnswerEvent.EventType.GET_HISTORY_ANSWER_SUCCESS事件, 包括Answer列表
     * 获取失败,返回AnswerEvent.EventType.GET_HISTORY_ANSWER_FAILED事件, 包括有错误信息
     *
     * @param questionId  问题ID
     * @param userId      用户ID,包括普通用户和专家
     * @param userType    用户类型,是专家还是普通用户
     * @param maxAnswerId 上次返回的最大的回答ID
     */
    void getHistoryAnswers(Long questionId, Long userId, UserType userType, long maxAnswerId);

    /***
     * 删除回答
     * 删除成功,返回AnswerEvent.EventType.DELETE_ANSWER_SUCCESS事件
     * 删除失败,返回AnswerEvent.EventType.DELETE_ANSWER_FAILED事件, 包括有错误信息
     *
     * @param answerId 回答ID
     */
    void deleteAnswer(long answerId);

    /***
     * 获取最新的评论
     * 请求成功, 返回CommentEvent.GET_LATEST_ANSWER_COMMENTS_SUCCESS, 包括有Comment的列表
     * 请求失败, 返回CommentEvent.GET_LATEST_ANSWER_COMMENTS_FAILED, 包括有错误信息
     *
     * @param targetId 回答/宝典ID
     * @param targetType 评论的对象, 回答or宝典
     */
    //void getLatestComment(long targetId, TargetType targetType);

    /***
     * 获取历史评论
     * 请求成功, 返回CommentEvent.EventType.GET_HISTORY_ANSWER_COMMENTS_SUCCESS, 包括有Comment的列表
     * 请求失败, 返回CommentEvent.EventType.GET_HISTORY_ANSWER_COMMENTS_FAILED, 包括有错误信息
     *
     * @param targetId 回答/宝典ID
     * @param targetType 评论的对象, 回答or宝典
     * @param maxCommentId 上次返回的最大的评论ID
     */
    //void getHistoryComment(long targetId, TargetType targetType, long maxCommentId);

    /***
     * 删除评论
     * 删除成功, 返回CommentEvent.EventType.DELETE_COMMENT_SUCCESS
     * 删除失败, 返回CommentEvent.EventType.DELETE_COMMENT_FAILED
     *
     * @param commentId 评论ID
     */
    //void deleteComment(long commentId);

    /***
     * 发布一个评论
     * 发布成功，触发事件CommentEvent.EventType.REPLAY_ANSWER_SUCCESS
     * 发布失败，触发事件CommentEvent.EventType.REPLAY_ANSWER_FAILED
     * @param targetId  回答ID或是宝典文章ID
     * @param content   评论的内容
     * @param replayToUserId  评论的回复人ID，可以不填写
     * @param replayToUserName 评论的回复人名，可以不填写
     * @param commentType 评论类型，1是点赞，2是文字
     * @param targetType 评论的是回答还是文章
     */
//    void publishComment(long targetId, String content, Long replayToUserId,
//                        String replayToUserName, CommentType commentType, TargetType targetType);

    /***
     * 获取最热的宝典文章列表
     * 请求成功,触发KnowledgeEvent.EventType.GET_TOP_HOT_KNOWLEDGE_SUCCESS事件,其中包括有宝典文章的列表
     * 请求失败,触发KnowledgeEvent.EventType.GET_TOP_HOT_KNOWLEDGE_FAILED事件,其中包括有失败原因
     *
     * @param page 页码
     */
    void getTopHotKnowledge(int page);

    /***
     * 根据分类或者专家获取最新的宝典文章列表
     * 请求成功,触发KnowledgeEvent.EventType.GET_LATEST_KNOWLEDGE_SUCCESS事件,其中包括有宝典文章的列表
     * 请求失败,触发KnowledgeEvent.EventType.GET_LATEST_KNOWLEDGE_FAILED事件,其中包括有失败原因
     *
     * @param tagId    分类标签ID
     * @param expertId 专家ID
     */
    void getLatestKnowledge(Long tagId, Long expertId);

    /***
     * 根据分类或者专家获取历史的宝典文章列表
     * 请求成功,触发KnowledgeEvent.EventType.GET_HISTORY_KNOWLEDGE_SUCCESS事件,其中包括有宝典文章的列表
     * 请求失败,触发KnowledgeEvent.EventType.GET_HISTORY_KNOWLEDGE_FAILED事件,其中包括有失败原因
     *
     * @param tagId          分类标签ID
     * @param expertId       专家ID
     * @param maxKnowledgeId 上次返回的最大ID
     */
    void getHistoryKnowledge(Long tagId, Long expertId, long maxKnowledgeId);

    /***
     * 根据宝典文章的ID获取宝典文章的详情
     * 请求成功,触发KnowledgeEvent.EventType.GET_KNOWLEDGE_BY_ID_SUCCESS事件,其中包括有宝典的详情
     * 请求失败,触发KnowledgeEvent.EventType.GET_KNOWLEDGE_BY_ID_FAILED事件,其中包括有失败原因
     *
     * @param knowledgeId 宝典文章的ID
     */
    void getKnowledgeById(long knowledgeId);

    /***
     * 获取推荐的专家的列表
     * 请求成功,触发ExpertEvent.EventType.GET_TOP_HOT_EXPERT_SUCCESS事件,其中包括有专家的详情
     * 请求失败,触发ExpertEvent.EventType.GET_TOP_HOT_EXPERT_FAILED事件,其中包括有失败原因
     */
    void getTopHotExperts();

    /***
     * 获取专家列表
     * 请求成功,触发ExpertEvent.EventType.GET_EXPERT_SUCCESS事件,其中包括有专家的详情
     * 请求失败,触发ExpertEvent.EventType.GET_EXPERT_FAILED事件,其中包括有失败原因
     *
     * @param page 页码
     */
    void getExperts(int page);

    /***
     * 获取专家详情
     * 请求成功,触发ExpertEvent.EventType.GET_EXPERT_DETAIL_SUCCESS事件,其中包括有专家的详情
     * 请求失败,触发ExpertEvent.EventType.GET_EXPERT_DETAIL_FAILED事件,其中包括有失败原因
     *
     * @param expertId 专家ID
     */
    void getExpertDetail(long expertId);

    /***
     * 获取最新的教师帮信息
     * 请求成功, 触发CommunionMessageEvent.EventType.FETCH_LATEST_MESSAGE_SUCCESS事件, 其中包括有消息的详情
     * 请求失败, 触发CommunionMessageEvent.EventType.FETCH_LATEST_MESSAGE_FAILED事件,其中包括有失败原因
     */
    void getLatestMessages();


    //CommunionMessage getMessageLastOneFromLocal();

    /***
     * 获取最新的教师帮信息
     * 请求成功, 触发CommunionMessageEvent.EventType.FETCH_HISTORY_MESSAGE_SUCCESS事件, 其中包括有消息的详情
     * 请求失败, 触发CommunionMessageEvent.EventType.FETCH_HISTORY_MESSAGE_FAILED事件,其中包括有失败原因
     *
     * @param maxMessageId 上次返回的最大的信息的ID
     */
    void getHistoryMessage(long maxMessageId);

    /**
     * 根据关键字搜索问题
     * 请求成功,触发SearchQuestionEvent.EventType.SEARCH_QUESTION_SUCCESS,其中包括查询结果
     * 请求失败,触发SearchQuestionEvent.EventType.SEARCH_QUESTION_FAILED,其中包括失败msg
     *
     * @param keywords 搜索关键字
     * @param pagenum  页数
     */
    void searchQuestion(String keywords, int pagenum);


}
