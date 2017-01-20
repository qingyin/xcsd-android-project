package com.tuxing.sdk.db.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table light_app.
 */
public class LightApp implements java.io.Serializable {

    private Long id;
    private String name;
    private String moduleName;
    private String icon;
    private Integer type;
    private String url;
    private String counterName;
    private Integer counterType;
    private Boolean isNew;
    private Integer showAt;
    private Long createdOn;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public LightApp() {
    }

    public LightApp(Long id) {
        this.id = id;
    }

    public LightApp(Long id, String name, String moduleName, String icon, Integer type, String url, String counterName, Integer counterType, Boolean isNew, Integer showAt, Long createdOn) {
        this.id = id;
        this.name = name;
        this.moduleName = moduleName;
        this.icon = icon;
        this.type = type;
        this.url = url;
        this.counterName = counterName;
        this.counterType = counterType;
        this.isNew = isNew;
        this.showAt = showAt;
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public Integer getCounterType() {
        return counterType;
    }

    public void setCounterType(Integer counterType) {
        this.counterType = counterType;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public Integer getShowAt() {
        return showAt;
    }

    public void setShowAt(Integer showAt) {
        this.showAt = showAt;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
