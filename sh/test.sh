  #!/bin/bash
  shopt -s dotglob  # 启用隐藏文件匹配

   for item in "$1"/*r; do
      echo "$item"
    done
echo "======="
for file in "$1"/**/"data"; do
      echo "$file"
done

shopt -u dotglob  # 恢复默认设置（可选）