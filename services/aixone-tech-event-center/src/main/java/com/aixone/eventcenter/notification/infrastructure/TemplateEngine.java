package com.aixone.eventcenter.notification.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板引擎
 * 负责模板变量的替换和渲染
 */
@Component
public class TemplateEngine {
    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");
    
    /**
     * 渲染模板
     * @param template 模板内容，支持 {{variableName}} 格式的变量
     * @param variablesJson 变量JSON字符串，如 {"variableName": "value"}
     * @return 渲染后的内容
     */
    public String render(String template, String variablesJson) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        
        if (variablesJson == null || variablesJson.isEmpty()) {
            return template;
        }
        
        try {
            // 解析变量JSON
            JsonNode variables = objectMapper.readTree(variablesJson);
            
            // 替换模板中的变量
            StringBuffer result = new StringBuffer();
            Matcher matcher = VARIABLE_PATTERN.matcher(template);
            
            while (matcher.find()) {
                String variableName = matcher.group(1).trim();
                JsonNode value = variables.get(variableName);
                
                if (value != null) {
                    String replacement = value.isTextual() ? value.asText() : value.toString();
                    matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
                } else {
                    // 变量不存在，保留原样
                    logger.warn("模板变量不存在: {}", variableName);
                    matcher.appendReplacement(result, matcher.group(0));
                }
            }
            matcher.appendTail(result);
            
            return result.toString();
            
        } catch (Exception e) {
            logger.error("模板渲染失败: {}", e.getMessage(), e);
            return template; // 渲染失败时返回原模板
        }
    }
}

