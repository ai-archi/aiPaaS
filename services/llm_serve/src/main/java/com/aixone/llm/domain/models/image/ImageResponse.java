package com.aixone.llm.domain.models.image;

import java.util.List;

/**
 * 图片生成/编辑响应领域模型。
 * 包含图片结果、任务状态、错误信息等。
 */
public class ImageResponse {
    /**
     * 任务ID。
     */
    private String taskId;

    /**
     * 任务状态（如 pending、success、failed 等）。
     */
    private String status;

    /**
     * 生成或编辑后的图片URL列表。
     */
    private List<String> imageUrls;

    /**
     * 错误信息（如有）。
     */
    private String errorMessage;

    public ImageResponse() {}

    public ImageResponse(String taskId, String status, List<String> imageUrls, String errorMessage) {
        this.taskId = taskId;
        this.status = status;
        this.imageUrls = imageUrls;
        this.errorMessage = errorMessage;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
} 