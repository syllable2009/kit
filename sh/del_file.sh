#!/bin/bash
if [ $# -ne 2 ]; then
    echo "用法: $0 <目录路径> <要删除的文件名>"
    exit 1
fi

target_dir="$1"
file_name="$2"

if [ ! -d "$target_dir" ]; then
    echo "错误: 目录不存在"
    exit 1
fi

shopt -s dotglob  # 包含隐藏文件
shopt -s nullglob # 处理空目录情况

deleted=0
# 只能找到下一级的文件
for file in "$target_dir"/**/"$file_name"; do
    if [ -f "$file" ]; then
        rm -v "$file"
        ((deleted++))
    fi
done

echo "已删除 $deleted 个匹配文件"
shopt -u dotglob