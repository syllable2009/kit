# 会话的消息无论转发与否，起始id和结束id都以用户视角为主

# 延时队列的分类

1. 会话超时session-timeout sessionLastTime为主，系统消息不算，卡片操作算
2. 客服好久没有会话session-manual-timeout manulLastMessageTime为主，系统消息不算，卡片操作算
3. 用户好久没有会话session-user-timeout userLastMessageTime为主，系统消息不算，卡片操作算
4. 排队超时transfer-queue-timeout  sessionQueueTime为主
5. 留言超时leave-message-timeout  留言的状态判断，发送留言卡片->发送延时处理消息->未操作更新卡片失效
6. 

