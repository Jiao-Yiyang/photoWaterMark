package com.watermark;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class WatermarkApp {
    
    // 水印位置枚举
    public enum Position {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("图片水印工具 v1.0");
        System.out.println("====================");
        
        // 1. 获取图片文件路径
        System.out.print("请输入图片文件路径: ");
        String imagePath = scanner.nextLine();
        File imageFile = new File(imagePath);
        
        if (!imageFile.exists() || !imageFile.isFile()) {
            System.out.println("错误: 图片文件不存在或不是有效的文件。");
            scanner.close();
            return;
        }
        
        if (!Utils.isSupportedImage(imageFile)) {
            System.out.println("错误: 不支持的图片格式。支持的格式: jpg, jpeg, png, bmp, gif, tiff");
            scanner.close();
            return;
        }
        
        try {
            // 2. 读取EXIF信息中的拍摄时间
            String shootingDate = getShootingDate(imageFile);
            if (shootingDate == null) {
                System.out.println("警告: 无法读取图片的拍摄时间信息，将使用当前日期作为水印。");
                shootingDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            }
            
            // 3. 设置水印参数
            System.out.println("默认水印文本: " + shootingDate);
            System.out.print("是否使用自定义水印文本？(y/n): ");
            String useCustomText = scanner.nextLine();
            String watermarkText = shootingDate;
            
            if (useCustomText.equalsIgnoreCase("y")) {
                System.out.print("请输入自定义水印文本: ");
                watermarkText = scanner.nextLine();
            }
            
            System.out.print("请输入字体大小 (默认: 30): ");
            String fontSizeStr = scanner.nextLine();
            int fontSize = fontSizeStr.isEmpty() ? 30 : Integer.parseInt(fontSizeStr);
            
            System.out.print("请输入字体颜色 (默认: 白色, 格式: R,G,B,透明度，例如: 255,255,255,150): ");
            String colorStr = scanner.nextLine();
            Color watermarkColor = Color.WHITE;
            if (!colorStr.isEmpty()) {
                try {
                    String[] colorParts = colorStr.split(",");
                    int r = Integer.parseInt(colorParts[0].trim());
                    int g = Integer.parseInt(colorParts[1].trim());
                    int b = Integer.parseInt(colorParts[2].trim());
                    int alpha = colorParts.length > 3 ? Integer.parseInt(colorParts[3].trim()) : 150;
                    watermarkColor = new Color(r, g, b, alpha);
                } catch (Exception e) {
                    System.out.println("颜色格式错误，使用默认白色。");
                }
            }
            
            System.out.println("请选择水印位置:");
            System.out.println("1. 左上角");
            System.out.println("2. 右上角");
            System.out.println("3. 左下角");
            System.out.println("4. 右下角");
            System.out.println("5. 居中");
            System.out.print("请输入选项 (默认: 5): ");
            String positionStr = scanner.nextLine();
            Position position = Position.CENTER;
            if (!positionStr.isEmpty()) {
                switch (positionStr) {
                    case "1": position = Position.TOP_LEFT; break;
                    case "2": position = Position.TOP_RIGHT; break;
                    case "3": position = Position.BOTTOM_LEFT; break;
                    case "4": position = Position.BOTTOM_RIGHT; break;
                    case "5": position = Position.CENTER; break;
                    default: System.out.println("选项错误，使用默认居中位置。");
                }
            }
            
            // 4. 添加水印并保存图片
            addWatermark(imageFile, watermarkText, fontSize, watermarkColor, position);
            
        } catch (Exception e) {
            System.out.println("处理图片时出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    // 获取图片的拍摄日期
    private static String getShootingDate(File imageFile) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            
            if (directory != null) {
                Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (date != null) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(date);
                }
            }
        } catch (ImageProcessingException | IOException e) {
            System.out.println("读取EXIF信息时出错: " + e.getMessage());
        }
        return null;
    }
    
    // 添加水印并保存图片
    private static void addWatermark(File imageFile, String text, int fontSize, Color color, Position position) throws IOException {
        // 读取原始图片
        BufferedImage originalImage = ImageIO.read(imageFile);
        BufferedImage watermarkedImage = new BufferedImage(
                originalImage.getWidth(), 
                originalImage.getHeight(), 
                BufferedImage.TYPE_INT_RGB
        );
        
        // 创建Graphics2D对象
        Graphics2D g2d = watermarkedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.setColor(color);
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        
        // 计算水印位置
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int textHeight = g2d.getFontMetrics().getHeight();
        int x = 0, y = 0;
        int padding = 20; // 边距
        
        switch (position) {
            case TOP_LEFT:
                x = padding;
                y = padding + textHeight;
                break;
            case TOP_RIGHT:
                x = originalImage.getWidth() - textWidth - padding;
                y = padding + textHeight;
                break;
            case BOTTOM_LEFT:
                x = padding;
                y = originalImage.getHeight() - padding;
                break;
            case BOTTOM_RIGHT:
                x = originalImage.getWidth() - textWidth - padding;
                y = originalImage.getHeight() - padding;
                break;
            case CENTER:
                x = (originalImage.getWidth() - textWidth) / 2;
                y = (originalImage.getHeight() + textHeight) / 2;
                break;
        }
        
        // 添加水印
        g2d.drawString(text, x, y);
        g2d.dispose();
        
        // 创建保存目录
        String parentDir = imageFile.getParent();
        String watermarkDirPath = parentDir + File.separator + imageFile.getParentFile().getName() + "_watermark";
        File watermarkDir = Utils.ensureDirectoryExists(watermarkDirPath);
        
        // 保存图片
        String baseName = Utils.getFileNameWithoutExtension(imageFile.getName());
        String outputFilePath = watermarkDirPath + File.separator + baseName + "_watermark.jpg";
        ImageIO.write(watermarkedImage, "jpg", new File(outputFilePath));
        
        System.out.println("水印添加成功！");
        System.out.println("保存路径: " + outputFilePath);
    }
}