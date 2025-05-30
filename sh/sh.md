命令行的集合，一般用于自动化
sh一般需要参数校验，参数解析，模块化功能，进程状态监控，用户身份切换等

#代表注释

# 变量
$ 用于引用变量的值,$() 语法可以进行命令替换
$# 是传给脚本的参数个数
$0 是脚本本身的名字
$1 是传递给该shell脚本的第一个参数
$2 是传递给该shell脚本的第二个参数
$@ 是传给脚本的所有参数的列表
$* 是以一个单字符串显示所有向脚本传递的参数，与位置变量不同，参数可超过9个
$$ 是脚本运行的当前进程ID号
$? 是显示最后命令的退出状态，0表示没有错误，其他表示有错误


分号;的作用：用于在同一行中分隔多条命令，是无条件执行，不依赖前序命令的结果。ls /tmp; touch test.txt; echo "Done"
&&: 前一条命令成功时执行下一条命令。make && make install
||: 前一条命令失败时执行下一条命令。ping host || echo "Host down"

Shell脚本换行的两种方式‌：
\ 必须放在行尾，且后面直接换行（不可有空格或其他字符），\ 前需加空格，最后一行无需添加 \
在管道符 |、逻辑运算符 &&/|| 等符号后可直接换行，无需 \
docker run -it --rm \
-v /data:/host_data \
-p 8080:80 \
nginx:latest

echo "Process started" |
tee -a log.txt &&
python main.py

# 运算符
假定变量 a 为 10，变量 b 为 20：
关系运算符	说明	 举例
+	加法	expr $a + $b 结果为 30。
-	减法	expr $a - $b 结果为 -10。
*	乘法	expr $a \* $b 结果为 200。
/	除法	expr $b / $a 结果为 2。
%	取余	expr $b % $a 结果为 0。
=	赋值	a=$b 将把变量 b 的值赋给 a。
==	相等。用于比较两个数字，相同则返回 true。	[ $a == $b ] 返回 false。
!=	不相等。用于比较两个数字，不相同则返回 true。	[ $a != $b ] 返回 true。

布尔运算符	说明	举例
-eq	检测两个数是否相等，相等返回 true。	[ $a -eq $b ] 返回 false。
-ne	检测两个数是否不相等，不相等返回 true。	[ $a -ne $b ] 返回 true。
-gt	检测左边的数是否大于右边的，如果是，则返回 true。	[ $a -gt $b ] 返回 false。
-lt	检测左边的数是否小于右边的，如果是，则返回 true。	[ $a -lt $b ] 返回 true。
-ge	检测左边的数是否大于等于右边的，如果是，则返回 true。	[ $a -ge $b ] 返回 false。
-le	检测左边的数是否小于等于右边的，如果是，则返回 true。	[ $a -le $b ] 返回 true。
# 注意：**条件表达式要放在方括号之间，并且要有空格，例如: [a = = a==a==b] 是错误的，必须写成 [ $a == $b ]。

逻辑运算符	说明	举例
!	非运算，用于逻辑非运算符，表示取反。	[ ! false ] 返回 true。
-o	或运算，有一个表达式为 true 则返回 true。	[ $a -lt 20 -o $b -gt 100 ] 返回 true。
-a	与运算，两个表达式都为 true 才返回 true。	[ $a -lt 20 -a $b -gt 100 ] 返回 false。

假定变量 a 为 “abc”，变量 b 为 “efg”：
字符串运算符	说明	举例
=	检测两个字符串是否相等，相等返回 true。	[ $a = $b ] 返回 false。
!=	检测两个字符串是否相等，不相等返回 true。	[ $a != $b ] 返回 true。
-z	检测字符串长度是否为0，为0返回 true。	[ -z $a ] 返回 false。
-n	检测字符串长度是否为0，不为0返回 true。	[ -n “$a” ] 返回 true。
$	检测字符串是否为空，不为空返回 true。	[ $a ] 返回 true。

操作符	说明	举例
-b file	检测文件是否是块设备文件，如果是，则返回 true。	[ -b $file ] 返回 false。
-c file	检测文件是否是字符设备文件，如果是，则返回 true。	[ -c $file ] 返回 false。
-d file	检测文件是否是目录，如果是，则返回 true。	[ -d $file ] 返回 false。
-f file	检测文件是否是普通文件（既不是目录，也不是设备文件），如果是，则返回 true。	[ -f $file ] 返回 true。
-g file	检测文件是否设置了 SGID 位，如果是，则返回 true。	[ -g $file ] 返回 false。
-k file	检测文件是否设置了粘着位(Sticky Bit)，如果是，则返回 true。	[ -k $file ] 返回 false。
-p file	检测文件是否是有名管道，如果是，则返回 true。	[ -p $file ] 返回 false。
-u file	检测文件是否设置了 SUID 位，如果是，则返回 true。	[ -u $file ] 返回 false。
-r file	检测文件是否可读，如果是，则返回 true。	[ -r $file ] 返回 true。
-w file	检测文件是否可写，如果是，则返回 true。	[ -w $file ] 返回 true。
-x file	检测文件是否可执行，如果是，则返回 true。	[ -x $file ] 返回 true。
-s file	检测文件是否为空（文件大小是否大于0），不为空返回 true。	[ -s $file ] 返回 true。
-e file	检测文件（包括目录）是否存在，如果是，则返回 true。	[ -e $file ] 返回 true。

算术运算操作符与运算命令	意义
(())	用于整数运算的常用运算符，效率很高
let	用于整数运算，类似(())
expr	可用于整数运算，但还有其它很多功能
bc	linux下的一个计算器程序（适合整数以及小数运算）
$[]	用于整数计算
awk	awk即可用于整数运算，也可用于小数运算
declare	定义变量值和属性，-i参数可以用于定义整形变量，做运算

# 条件语句
if [ expression 1 ]
then
Statement(s) to be executed if expression 1 is true
elif [ expression 2 ]
then
Statement(s) to be executed if expression 2 is true
elif [ expression 3 ]
then
Statement(s) to be executed if expression 3 is true
else
Statement(s) to be executed if no expression is true
fi

# 循环
for file in *.txt; do
echo "Processing file: $file"
done


