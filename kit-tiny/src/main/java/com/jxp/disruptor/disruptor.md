Disruptor 是英国外汇交易公司 LMAX 开发的一个高性能的内存队列。
![img.png](img.png)
1.定义事件数据载体，需支持快速序列化和反序列化
@Data
@NoArgsConstructor
public class OrderEvent {
private String orderId;
private BigDecimal amount;
}
2.事件工厂实现‌
public class OrderEventFactory implements EventFactory<OrderEvent> {
@Override
public OrderEvent newInstance() { return new OrderEvent(); }
}

3.配置disruptor，线程池和异常处理

4.配置生产者

5.关闭阶段强制释放资源
@PreDestroy
public void shutdown() {
if (disruptor != null) {
disruptor.shutdown(10, TimeUnit.SECONDS);
if (ringBuffer != null) {
ringBuffer.remainingCapacity(); // 强制清空缓冲区
}
}
}





