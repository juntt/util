
# 1 任务执行机制(/executor)
![状态图](/dev-book/uml/ExecutorStatus.png)<br />
同步、异步化执行能力+重试、超时策略
## 1.1 任务
生命周期：
- IN_PROGRESS: 执行中
- FAILED: 失败。先触发重试逻辑，否则任务终止
- COMPLETED: 成功

重试逻辑：
- RETRY_FIXED: retryDelaySeconds后重试
- RETRY_BACKOFF: (retryDelaySeconds * 剩余重试次数)后重试

超时策略：
- TERMINATED: 超时终止
- RETRY: 超时重试

## 1.2 执行模式
- 同步执行sync()
- 执行超时/失败后异步重试策略async()
- 延迟异步执行delay()

# 2 分布式事务TCC模式(/tcc)
TCC模式(Try, Confirm, Cancel)是应用层的两阶段提交(2 Phase Commit)，适用强隔离性、严格一致性要求的，执行时间较短的业务。
## 2.1 Try尝试执行业务
完成所有业务检查(一致性)，预留必需业务资源(准隔离性)。如订单状态置中间状态ING、冻结数据等
## 2.2 Confirm确认执行业务
执行业务，不做任何业务检查，只使用Try预留的业务资源，需满足幂等性。如订单状态置已处理ED、冻结数据提交到业务数据等
## 2.3 Cancel取消执行业务
释放Try预留的业务资源或业务补偿(逆向操作)，需满足幂等性。如取消冻结数据等
# 3 MQ Broker故障降级机制(/mq)
![MQ Broker故障降级时序图](/dev-book/uml/MqFallbackSequence.png)<br />
适用高可用业务，不适用高并发业务。
## 3.1 MQ Broker故障和恢复的感知
使用ZooKeeper, Spring Cloud Config或Redis管理降级开关；线程run checkHealth()，周期自检MQ发布订阅消息功能：
- 降级开关为关闭状态，且checkHealth()发现MQ Broker故障累计到达指定阈值，则开启降级开关；
- 降级开关为开启状态，且checkHealth()发现MQ Broker恢复累计到达指定阈值，则关闭降级开关。

## 3.2 MQ Broker故障降级
使用Redis LIST列表数据结构(FIFO, RPUSH/LPOP)替代MQ，key: 消息topic，value: 消息载体JSON序列化。<br />
降级开关为开启状态：
- 发布者降级，向FIFO PUSH键值对；
- 消费者降级，多线程轮询的方式从FIFO POP键值对。

降级开关为关闭状态：
- 发布者直接调用MQ发布消息；
- 消费者先多线程处理完FIFO中的所有数据，再切换到MQ消费消息。

