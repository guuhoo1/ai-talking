package com.aitalking.dto;

import java.util.Date;

/**
 * 知识库响应类，用于封装知识库条目的完整信息。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class KnowledgeResponse {
    /**
     * 知识库条目的唯一标识ID
     */
    private Long id;

    /**
     * 知识条目的标题
     */
    private String title;

    /**
     * 知识条目的摘要描述
     */
    private String summary;

    /**
     * 核心代码片段
     */
    private String coreCode;

    /**
     * 技术标签，多个标签用逗号分隔
     */
    private String techTags;

    /**
     * 使用场景描述
     */
    private String scene;

    /**
     * 技术原理说明
     */
    private String principle;

    /**
     * 注意事项或备注信息
     */
    private String notes;

    /**
     * 标识该知识是否可复用
     */
    private Boolean reusable;

    /**
     * 来源消息的ID
     */
    private Long sourceMessageId;

    /**
     * 知识条目的创建时间
     */
    private Date createTime;

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