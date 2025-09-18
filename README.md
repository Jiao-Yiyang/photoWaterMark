# 图片水印工具

一个简单的Java命令行工具，用于给图片添加基于拍摄日期的水印。

## 功能特点

- 自动读取图片EXIF信息中的拍摄日期作为水印文本
- 支持自定义水印文本、字体大小、颜色和位置
- 将添加水印后的图片保存到原目录的`_watermark`子目录中

## 技术栈

- Java 8
- Maven
- metadata-extractor (用于读取EXIF信息)

## 安装说明

1. **安装Java环境**（必需）
   - 需要安装Java 8或更高版本的JDK(Java开发工具包)
   - 安装完成后，请确保java和javac命令已添加到系统PATH中
   
   **安装建议：**
   - **macOS**: 使用Homebrew安装 `brew install openjdk@11`
   - **Windows**: 从Oracle官网下载安装包并安装
   - **Linux**: Ubuntu/Debian: `sudo apt install openjdk-11-jdk`; CentOS/RHEL: `sudo yum install java-11-openjdk-devel`

2. 克隆项目到本地

```bash
git clone <repository-url>
cd photo-watermark
```

## 使用方法

### 方法一：使用提供的脚本（推荐）

1. 为脚本添加执行权限

```bash
chmod +x run_watermark.sh
```

2. 运行脚本

```bash
./run_watermark.sh
```

脚本会自动下载必要的依赖库、编译代码并运行程序。

### 方法二：使用Maven（如果已安装）

1. 确保已安装Maven
2. 构建项目

```bash
mvn clean package
```

3. 运行生成的jar文件

```bash
java -jar target/photo-watermark-1.0-SNAPSHOT-jar-with-dependencies.jar
```

2. 根据提示输入以下信息：
   - 图片文件路径
   - 是否使用自定义水印文本（可选）
   - 字体大小（默认：30）
   - 字体颜色（默认：白色，格式：R,G,B,透明度，例如：255,255,255,150）
   - 水印位置（默认：居中）

3. 程序会自动添加水印并保存到原目录的`_watermark`子目录中

## 水印位置选项

1. 左上角
2. 右上角
3. 左下角
4. 右下角
5. 居中

## 示例

```
图片水印工具 v1.0
====================
请输入图片文件路径: /path/to/your/photo.jpg
默认水印文本: 2023-10-15
是否使用自定义水印文本？(y/n): n
请输入字体大小 (默认: 30): 40
请输入字体颜色 (默认: 白色, 格式: R,G,B,透明度，例如: 255,255,255,150): 255,255,255,180
请选择水印位置:
1. 左上角
2. 右上角
3. 左下角
4. 右下角
5. 居中
请输入选项 (默认: 5): 4
水印添加成功！
保存路径: /path/to/your/directory_watermark/photo_watermark.jpg
```

## 注意事项

- 请确保有足够的文件读写权限
- 如果图片没有EXIF信息或无法读取拍摄日期，程序会使用当前日期作为水印
- 目前支持处理常见的图片格式如JPG、PNG等
- 添加水印后的图片会保存为JPG格式

## 版本信息

当前版本：1.0