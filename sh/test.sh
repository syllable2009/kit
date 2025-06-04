  #!/bin/bash
  shopt -s dotglob  # 启用隐藏文件匹配
  echo $(pwd)
   for item in "$1"/*; do
      echo "$item"
    done
echo "======="
for file in "$1"/**; do
      echo "$file"
done

shopt -u dotglob  # 恢复默认设置（可选）