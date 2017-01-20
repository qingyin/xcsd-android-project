package com.tuxing.sdk.modle;

import com.tuxing.rpc.proto.TagType;

import java.io.Serializable;


/**
 * Created by apple on 16/4/20.
 */
public class QuestionTag implements Serializable {
    private static final long serialVersionUID = -4246801607006158510L;

    private Long tagId;
    private String name;
    private Long parentId;
    private TagType type;

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public TagType getType() {
        return type;
    }

    public void setType(TagType type) {
        this.type = type;
    }

}
