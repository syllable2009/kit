# 同步与异步
分布式网络系统中，各个参与方节点的运行是相互独立的，没有共享内存，没有全局时钟。
各节点通过消息来进行沟通。在传统的理念中，我们会把这样的网络根据他们通信方式描述成同步和异步的。

同步网络是对消息的到达时间有限定要求（time bounded），以便保证网络活动的确定性。
异步的网络，则对消息的到达没有任何限制。即使发出的消息丢失了，也不会损害网络的活性。

# 响应式编程
它是一种基于事件模式的模型。在上面的异步编程模式中，我们描述了两种获得上一个任务执行结果的方式，一个就是主动轮训，我们把它称为 Proactive 方式。
另一个就是被动接收反馈，我们称为 Reactive。简单来说，在 Reactive 方式中，上一个任务的结果的反馈就是一个事件，这个事件的到来将会触发下一个任务的执行。

响应式编程基于reactor（Reactor 是一个运行在 Java8 之上的响应式框架）的思想，当你做一个带有一定延迟的才能够返回的io操作时，不会阻塞，而是立刻返回一个流，并且订阅这个流，当这个流上产生了返回数据，可以立刻得到通知并调用回调函数处理数据。
订阅Flux序列，只有进行订阅后才回触发数据流，不订阅就什么都不会发生。

这也就是 Reactive 的内涵。我们把处理和发出事件的主体称为 Reactor，它可以接受事件并处理，也可以在处理完事件后，发出下一个事件给其他 Reactor。
两个 Reactors 之间没有必然的强耦合，他们之间通过消息管道来传递消息。Reactor 可以定义一些事件处理函数，根据接收到的事件不同类型来进行不同的处理。
如果我们的系统复杂，我们还可以专门定义不同功能类别的 Reactors，分别处理不同类型的事件。而在每个 Reactor 中对事件又进行细分处理。

Reactor 是一个响应式编程的基础类库，其中有两个很关键的类：Flux 和 Mono。

Flux是一个标准的Publisher，表示一个异步的0到N个发出的项目序列，可选择终止于完成信号或错误信号。根据Reactive Streams规范，这三种类型的信号转换为对下游Subscriber的onNext、onComplete和onError方法的调用。
Mono是一种特殊的Publisher，通过onNext信号发出最多一个项目，然后通过onComplete信号终止（成功的Mono，有或没有值），或者只发出一个onError信号（失败的Mono）。

