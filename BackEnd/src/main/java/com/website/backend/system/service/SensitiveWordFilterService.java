package com.website.backend.system.service;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感词过滤服务，用于过滤评论中的敏感词
 */
@Service
public class SensitiveWordFilterService {
    
    // 敏感词集合
    private Set<String> sensitiveWords = new HashSet<>();
    
    public Set<String> getSensitiveWords() {
        return sensitiveWords;
    }
    
    public void setSensitiveWords(Set<String> sensitiveWords) {
        this.sensitiveWords = sensitiveWords;
    }
    
    // 敏感词替换字符
    private static final String REPLACEMENT = "***";
    
    /**
     * 构造函数，初始化敏感词库
     */
    public SensitiveWordFilterService() {
        // 这里可以从配置文件或数据库加载敏感词
        // 为了演示，这里直接初始化一些常见的敏感词
        initializeSensitiveWords();
    }
    
    /**
     * 初始化敏感词库
     */
    private void initializeSensitiveWords() {
        sensitiveWords.add("敏感词1");
        sensitiveWords.add("敏感词2");
        sensitiveWords.add("不良内容");
        sensitiveWords.add("违法信息");
        // 可以根据实际需求添加更多敏感词
    }
    
    /**
     * 过滤文本中的敏感词
     * 
     * @param text 原始文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (text == null || text.isEmpty() || sensitiveWords.isEmpty()) {
            return text;
        }
        
        String filteredText = text;
        for (String sensitiveWord : sensitiveWords) {
            // 使用正则表达式进行不区分大小写的替换
            String regex = "(?i)" + Pattern.quote(sensitiveWord);
            filteredText = filteredText.replaceAll(regex, REPLACEMENT);
        }
        
        return filteredText;
    }
    
    /**
     * 检查文本中是否包含敏感词
     * 
     * @param text 要检查的文本
     * @return 是否包含敏感词
     */
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty() || sensitiveWords.isEmpty()) {
            return false;
        }
        
        for (String sensitiveWord : sensitiveWords) {
            // 使用正则表达式进行不区分大小写的匹配
            String regex = "(?i)" + Pattern.quote(sensitiveWord);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 添加敏感词到过滤库
     * 
     * @param word 要添加的敏感词
     */
    public void addSensitiveWord(String word) {
        if (word != null && !word.isEmpty()) {
            sensitiveWords.add(word);
        }
    }
    
    /**
     * 从过滤库中移除敏感词
     * 
     * @param word 要移除的敏感词
     */
    public void removeSensitiveWord(String word) {
        if (word != null) {
            sensitiveWords.remove(word);
        }
    }
}
