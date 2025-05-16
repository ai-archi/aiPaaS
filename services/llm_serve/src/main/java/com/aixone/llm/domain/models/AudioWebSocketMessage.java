package com.aixone.llm.domain.models;

import java.util.Map;

public class AudioWebSocketMessage {
    private Header header;
    private Map<String, Object> payload;

    public Header getHeader() { return header; }
    public void setHeader(Header header) { this.header = header; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    /**
     * header结构，兼容阿里云WebSocket协议所有字段，全部可选
     */
    public static class Header {
        private String action;         // run-task/finish-task（请求时有）
        private String event;          // task-started/result-generated/task-finished/task-failed（响应时有）
        private String task_id;
        private String streaming;      // run-task/finish-task 时有
        private String error_code;     // task-failed 时有
        private String error_message;  // task-failed 时有
        private Map<String, Object> attributes; // 事件响应时有

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public String getEvent() { return event; }
        public void setEvent(String event) { this.event = event; }

        public String getTask_id() { return task_id; }
        public void setTask_id(String task_id) { this.task_id = task_id; }

        public String getStreaming() { return streaming; }
        public void setStreaming(String streaming) { this.streaming = streaming; }

        public String getError_code() { return error_code; }
        public void setError_code(String error_code) { this.error_code = error_code; }

        public String getError_message() { return error_message; }
        public void setError_message(String error_message) { this.error_message = error_message; }

        public Map<String, Object> getAttributes() { return attributes; }
        public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    }
} 