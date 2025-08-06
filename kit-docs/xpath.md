# XPath
XPath是用于在XML/HTML文档中导航和定位节点的查询语言，通过路径表达式选择节点或节点集.

# 节点类型‌
元素节点：<title>文本</title>
属性节点：<img src="...">
文本节点：标签内的文本内容
文档节点：整个文档的根节点

# 路径表达式
节点是通过沿着路径或者 step 来选取的

表达式	描述
nodename	选取此节点的所有子节点。
/	从根节点选取（取子节点）。
//	从匹配选择的当前节点选择文档中的节点，而不考虑它们的位置（取子孙节点）。
.	选取当前节点。
..	选取当前节点的父节点。
@	选取属性。
通配符	描述
*	匹配任何元素节点。
@*	匹配任何属性节点。
node()	匹配任何类型的节点。
通过在路径表达式中使用"|"运算符，您可以选取若干个路径。

# 谓语
被嵌在方括号中,用来查找某个特定的节点或者包含某个指定的值的节点。
路径表达式	结果
/bookstore/book[1]	选取属于 bookstore 子元素的第一个 book 元素。
/bookstore/book[last()]	选取属于 bookstore 子元素的最后一个 book 元素。
/bookstore/book[last()-1]	选取属于 bookstore 子元素的倒数第二个 book 元素。
/bookstore/book[position()<3]	选取最前面的两个属于 bookstore 元素的子元素的 book 元素。
//title[@lang]	选取所有拥有名为 lang 的属性的 title 元素。
//title[@lang='eng']	选取所有 title 元素，且这些元素拥有值为 eng 的 lang 属性。
/bookstore/book[price>35.00]	选取 bookstore 元素的所有 book 元素，且其中的 price 元素的值须大于 35.00。
/bookstore/book[price>35.00]//title	选取 bookstore 元素中的 book 元素的所有 title 元素，且其中的 price 元素的值须大于 35.00。
//div[contains(@class, "header")]  // 类名包含"header"的div
/bookstore/book[position()<3]      // 前两本书
//a[starts-with(@href, "https")]   // HTTPS开头的链接



# 标准函数
XPath 含有超过 100 个内建的函数。这些函数用于字符串值、数值、日期和时间比较、节点和 QName 处理、序列处理、逻辑值等等。
text() 取文本内容