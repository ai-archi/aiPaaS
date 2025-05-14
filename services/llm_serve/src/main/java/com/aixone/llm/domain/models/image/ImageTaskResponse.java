package com.aixone.llm.domain.models.image;

import java.util.List;

import lombok.Data;

@Data
public class ImageTaskResponse {
    private String request_id;
    private Output output;
    private Usage usage;

    @Data
    public static class Output {
        private String task_id;
        private String task_status;
        private String submit_time;
        private String scheduled_time;
        private String end_time;
        private List<Result> results;
        private TaskMetrics task_metrics;
    }

    @Data
    public static class Result {
        private String url;
    }

    @Data
    public static class TaskMetrics {
        private int TOTAL;
        private int SUCCEEDED;
        private int FAILED;
    }

    @Data
    public static class Usage {
        private int image_count;
    }
} 