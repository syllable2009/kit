#!/bin/bash
set -euo pipefail
# 严格模式
# -e: 命令失败立即退出。
#-u: 使用未定义变量立即退出。
#-o pipefail: 管道中任何命令失败，整个管道视为失败。
# 参数校验
if [ $# -ne 2 ]; then
    echo "Usage: $0 <source_dir> <target_dir>"
    # exit 1 返回非零状态码表示错误退出
    exit 1
fi

# 处理路径规范化
source_dir="$(realpath "$1")"
target_dir="$(realpath "$2")"

# 目录验证
[ -d "$source_dir" ] || { echo >&2 "Error: Source dir not exist"; exit 1; }
mkdir -p "$target_dir" || { echo >&2 "Error: Target dir creation failed"; exit 1; }
[ -w "$target_dir" ] || { echo >&2 "Error: Target dir not writable"; exit 1; }

# 验证源目录存在性
#if [ ! -d "$source_dir" ]; then
#    echo "Error: Source directory does not exist"
#    exit 1
#fi

# 创建目标目录（如果不存在）
#mkdir -p "$target_dir" || {
#    echo "Error: Failed to create target directory"
#    exit 1
#}

# 计数器初始化
count=0 has_errors=0

move_file() {
    if [[ "$file" == *.mp3 ]]; then
        if mv -n "$file" "$target_dir/"; then
            ((count++))
        else
            # >&2将错误信息发送到 stderr，而不是 stdout
            echo >&2 "Error: Failed to move $file"
            ((has_errors++))
        fi
    fi
}

traverse2() {
    # 声明局部变量 dir，local确保变量仅在函数内有效
    local dir="$1"
    # IFS=：禁用分隔符，防止文件名中的空格被误解析
    # read -r：禁止反斜杠转义
    # -d ''：使用空字符（\0）作为分隔符，兼容含换行符的文件名
    while IFS= read -r -d '' file; do
        if [ -f "$file" ]; then
            move_file "$file"
        fi
    # find "$dir" -type f：递归查找 dir 下的所有普通文件
    # -print0：以 \0 分隔结果，与 read -d '' 配合解决文件名特殊字符问题
    # < <(...)：进程替换（Process Substitution），将 find 输出作为循环输入
    done < <(find "$dir" -type f -print0)
}

traverse() {
    local dir="$1"
    for item in "$dir"/*; do
        if [[ -f "$item" ]]; then
            move_file "$item"
        elif [[ -d "$item" ]]; then
            traverse "$item"  # 递归处理子目录
        fi
    done
}


# 查找并移动MP3文件（处理含空格/特殊字符的文件名）
# 主流程
traverse "$source_dir"

if [ $has_errors -eq 0 ]; then
    echo "Success: Moved $count MP3 files to $target_dir"
else
    echo >&2 "Warning: Completed with errors (moved $count files)"
    exit 1
fi
