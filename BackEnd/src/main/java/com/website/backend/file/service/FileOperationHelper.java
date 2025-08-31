package com.website.backend.file.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 文件操作辅助类，提供通用的文件操作功能
 */
@Slf4j
@Component
public class FileOperationHelper {

    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 删除是否成功
     */
    public boolean deleteFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.debug("从文件系统删除文件成功: {}", filePath);
                } else {
                    log.warn("从文件系统删除文件失败: {}", filePath);
                }
                return deleted;
            }
        }
        return false;
    }

    /**
     * 读取文件内容
     * 
     * @param filePath 文件路径
     * @return 文件内容字节数组
     * @throws IOException 文件读取异常
     */
    public byte[] readFileContent(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IOException("文件路径不能为空");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }

        return Files.readAllBytes(file.toPath());
    }
}