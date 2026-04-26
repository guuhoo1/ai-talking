package com.aitalking.model;

import java.util.Date;

/**
 * AI知识实体类，用于存储从聊天内容中提取的AI知识条目。
 *
 * @author AI Talking
 * @date 2026-04-26
 */
public class AiKnowledge {
    /**
     * 知识条目唯一标识符
     */
    private Long id;

    /**
     * 知识标题，概括知识主题
     */
    private String title;

    /**
     * 知识摘要，简要描述知识内容
     */
    private String summary;

    /**
     * 核心代码，知识相关的代码示例
     */
    private String coreCode;

    /**
     * 技术标签，用于分类和检索（如用逗号分隔）
     */
    private String techTags;

    /**
     * 应用场景，知识适用的业务场景
     */
    private String scene;

    /**
     * 实现原理，技术实现的核心原理说明
     */
    private String principle;

    /**
     * 备注说明，补充注意事项或使用建议
     */
    private String notes;

    /**
     * 是否可复用，标识该知识是否可以在其他场景中复用
     */
    private Boolean reusable;

    /**
     * 来源消息ID，提取该知识的原始消息ID
     */
    private Long sourceMessageId;

    /**
     * 知识创建时间
     */
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