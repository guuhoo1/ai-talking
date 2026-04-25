package com.aitalking.dto;

import java.util.Date;

public class KnowledgeResponse {
    private Long id;
    private String title;
    private String summary;
    private String coreCode;
    private String techTags;
    private String scene;
    private String principle;
    private String notes;
    private Boolean reusable;
    private Long sourceMessageId;
    private Date createTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoreCode() {
        return coreCode;
    }

    public void setCoreCode(String coreCode) {
        this.coreCode = coreCode;
    }

    public String getTechTags() {
        return techTags;
    }

    public void setTechTags(String techTags) {
        this.techTags = techTags;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getPrinciple() {
        return principle;
    }

    public void setPrinciple(String principle) {
        this.principle = principle;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getReusable() {
        return reusable;
    }

    public void setReusable(Boolean reusable) {
        this.reusable = reusable;
    }

    public Long getSourceMessageId() {
        return sourceMessageId;
    }

    public void setSourceMessageId(Long sourceMessageId) {
        this.sourceMessageId = sourceMessageId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}