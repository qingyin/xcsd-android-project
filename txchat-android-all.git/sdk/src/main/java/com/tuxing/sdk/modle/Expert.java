package com.tuxing.sdk.modle;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 16/4/20.
 */
public class Expert implements Serializable {

    private static final long serialVersionUID = 4951508428865266211L;

    private Long expertId;
    private String name;
    private String title;
    private String avatar;
    private String description;
    List<QuestionTag> specialities;
    private Integer thankCount;
    private Integer answerCount;
    private Integer knowledgeCount;
    private String sign;
    private String banner;
    private List<Answer> answers;
    private Boolean hasMoreAnswer;
    private List<Knowledge> knowledges;
    private Boolean hasMoreKnowledge;

    public Long getExpertId() {
        return expertId;
    }

    public void setExpertId(Long expertId) {
        this.expertId = expertId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QuestionTag> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(List<QuestionTag> specialities) {
        this.specialities = specialities;
    }

    public Integer getThankCount() {
        return thankCount;
    }

    public void setThankCount(Integer thankCount) {
        this.thankCount = thankCount;
    }

    public Integer getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Integer answerCount) {
        this.answerCount = answerCount;
    }

    public Integer getKnowledgeCount() {
        return knowledgeCount;
    }

    public void setKnowledgeCount(Integer knowledgeCount) {
        this.knowledgeCount = knowledgeCount;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public Boolean getHasMoreAnswer() {
        return hasMoreAnswer;
    }

    public void setHasMoreAnswer(Boolean hasMoreAnswer) {
        this.hasMoreAnswer = hasMoreAnswer;
    }

    public List<Knowledge> getKnowledges() {
        return knowledges;
    }

    public void setKnowledges(List<Knowledge> knowledges) {
        this.knowledges = knowledges;
    }

    public Boolean getHasMoreKnowledge() {
        return hasMoreKnowledge;
    }

    public void setHasMoreKnowledge(Boolean hasMoreKnowledge) {
        this.hasMoreKnowledge = hasMoreKnowledge;
    }
}
