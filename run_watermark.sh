#!/bin/bash

# 图片水印工具运行脚本
# 此脚本用于直接编译和运行Java程序，不依赖Maven

# 设置颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# 检查Java是否安装
java_installed=false
try_java() {
    if command -v java &> /dev/null
    then
        java_installed=true
        return 0
    fi
    return 1
}

try_java

if [ "$java_installed" = false ]
then
    echo -e "${RED}错误: 未找到Java运行时环境(JRE)。${NC}"
    echo ""
    echo "请先安装Java 8或更高版本。以下是安装建议："
    echo ""
    echo "# macOS安装方法："
    echo "1. 使用Homebrew安装："
    echo "   brew install openjdk@11"
    echo "   echo 'export PATH="/usr/local/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc"
    echo "   source ~/.zshrc"
    echo ""
    echo "2. 或者从Oracle官网下载安装包："
    echo "   https://www.oracle.com/java/technologies/downloads/"
    echo ""
    echo "# Windows安装方法："
    echo "从Oracle官网下载安装包并安装，记得勾选'Add to PATH'选项。"
    echo ""
    echo "# Linux安装方法："
    echo "Ubuntu/Debian: sudo apt install openjdk-11-jdk"
    echo "CentOS/RHEL: sudo yum install java-11-openjdk-devel"
    echo ""
    echo "安装完成后，请重新运行此脚本。"
    exit 1
fi

# 检查Java版本
java_version="$(java -version 2>&1 | head -1 | cut -d'"' -f2)"
major_version="$(echo $java_version | cut -d'.' -f1)"

if [[ "$major_version" == "1" ]]; then
    # Java 8及之前版本的格式是1.x.x
    major_version="$(echo $java_version | cut -d'.' -f2)"
fi

if [ "$major_version" -lt 8 ]; then
    echo -e "${RED}错误: Java版本过低 ($java_version)。${NC}"
    echo "请安装Java 8或更高版本。"
    exit 1
fi

# 检查javac是否安装
if ! command -v javac &> /dev/null
then
    echo -e "${RED}错误: Java编译器(javac)未安装。${NC}"
    echo ""
    echo "您可能只安装了JRE(Java运行时环境)，需要安装完整的JDK(Java开发工具包)。"
    echo ""
    echo "# macOS安装JDK方法："
    echo "使用Homebrew安装："
    echo "   brew install openjdk@11"
    echo "   echo 'export PATH="/usr/local/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc"
    echo "   source ~/.zshrc"
    echo ""
    echo "# 其他系统安装方法："
    echo "请从Oracle官网或OpenJDK官网下载并安装完整的JDK。"
    echo ""
    exit 1
fi

# 创建lib目录（用于存放依赖库）
mkdir -p lib

# 下载metadata-extractor库（用于读取EXIF信息）
if [ ! -f "lib/metadata-extractor-2.16.0.jar" ]
then
    echo -e "${YELLOW}正在下载metadata-extractor库...${NC}"
    curl -L -o lib/metadata-extractor-2.16.0.jar https://repo1.maven.org/maven2/com/drewnoakes/metadata-extractor/2.16.0/metadata-extractor-2.16.0.jar
    if [ $? -ne 0 ]
    then
        echo -e "${RED}下载metadata-extractor库失败，请手动下载并放入lib目录。${NC}"
        exit 1
    fi
fi

# 下载xmpcore库（metadata-extractor的依赖）
if [ ! -f "lib/xmpcore-6.1.11.jar" ]
then
    echo -e "${YELLOW}正在下载xmpcore库...${NC}"
    curl -L -o lib/xmpcore-6.1.11.jar https://repo1.maven.org/maven2/com/adobe/xmp/xmpcore/6.1.11/xmpcore-6.1.11.jar
    if [ $? -ne 0 ]
    then
        echo -e "${RED}下载xmpcore库失败，请手动下载并放入lib目录。${NC}"
        exit 1
    fi
fi

# 创建编译输出目录
mkdir -p bin

# 编译Java源文件
echo -e "${YELLOW}正在编译Java源文件...${NC}"
javac -d bin -cp "lib/*" src/main/java/com/watermark/*.java

if [ $? -ne 0 ]
then
    echo -e "${RED}编译失败，请检查代码是否有错误。${NC}"
    exit 1
fi

# 运行程序
echo -e "${GREEN}编译成功，正在启动程序...${NC}"
echo "=================================================="
java -cp "bin:lib/*" com.watermark.WatermarkApp

# 检查程序运行状态
if [ $? -ne 0 ]
then
    echo -e "${RED}程序运行出错。${NC}"
    exit 1
fi

# 清理临时文件（可选，取消注释以启用）
# echo -e "${YELLOW}正在清理临时文件...${NC}"
# rm -rf bin

# 提示如何再次运行
cat << EOF

==================================================
${GREEN}程序已成功运行！${NC}
下次运行可直接执行：
  ./run_watermark.sh

使用说明：
1. 如果脚本无法执行，请先设置执行权限：
   chmod +x run_watermark.sh
2. 确保已安装Java 8或更高版本
3. 首次运行会自动下载必要的依赖库
EOF