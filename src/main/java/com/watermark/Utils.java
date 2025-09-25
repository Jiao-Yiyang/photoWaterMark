package com.watermark;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Utils {
    
    // 支持的图片格式
    private static final Set<String> SUPPORTED_IMAGE_FORMATS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "bmp", "gif", "tiff"));
    
    /**
     * 检查文件是否为支持的图片格式
     * @param file 要检查的文件
     * @return 如果是支持的图片格式返回true，否则返回false
     */
    public static boolean isSupportedImage(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        int lastDotIndex = fileName.lastIndexOf('.');
        
        if (lastDotIndex == -1) {
            return false;
        }
        
        String extension = fileName.substring(lastDotIndex + 1);
        return SUPPORTED_IMAGE_FORMATS.contains(extension);
    }
    
    /**
     * 确保目录存在，如果不存在则创建
     * @param dirPath 目录路径
     * @return 创建或存在的目录对象
     */
    public static File ensureDirectoryExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    
    /**
     * 获取不带扩展名的文件名
     * @param fileName 文件名
     * @return 不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }
    
    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 文件扩展名（小写）
     */
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}