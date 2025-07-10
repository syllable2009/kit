# 同步与异步

分布式网络系统中，各个参与方节点的运行是相互独立的，没有共享内存，没有全局时钟。
各节点通过消息来进行沟通。在传统的理念中，我们会把这样的网络根据他们通信方式描述成同步和异步的。

同步网络是对消息的到达时间有限定要求（time bounded），以便保证网络活动的确定性。
异步的网络，则对消息的到达没有任何限制。即使发出的消息丢失了，也不会损害网络的活性。

# 响应式编程

它是一种基于事件模式的模型。在上面的异步编程模式中，我们描述了两种获得上一个任务执行结果的方式，一个就是主动轮训，我们把它称为
Proactive 方式。
另一个就是被动接收反馈，我们称为 Reactive。简单来说，在 Reactive 方式中，上一个任务的结果的反馈就是一个事件，这个事件的到来将会触发下一个任务的执行。

响应式编程基于reactor（Reactor 是一个运行在 Java8
之上的响应式框架）的思想，当你做一个带有一定延迟的才能够返回的io操作时，不会阻塞，而是立刻返回一个流，并且订阅这个流，当这个流上产生了返回数据，可以立刻得到通知并调用回调函数处理数据。
订阅Flux序列，只有进行订阅后才回触发数据流，不订阅就什么都不会发生。

这也就是 Reactive 的内涵。我们把处理和发出事件的主体称为 Reactor，它可以接受事件并处理，也可以在处理完事件后，发出下一个事件给其他
Reactor。
两个 Reactors 之间没有必然的强耦合，他们之间通过消息管道来传递消息。Reactor 可以定义一些事件处理函数，根据接收到的事件不同类型来进行不同的处理。
如果我们的系统复杂，我们还可以专门定义不同功能类别的 Reactors，分别处理不同类型的事件。而在每个 Reactor 中对事件又进行细分处理。

Reactor 是一个响应式编程的基础类库，其中有两个很关键的类：Flux 和 Mono。
Flux是一个标准的Publisher，表示一个异步的0到N个发出的项目序列，可选择终止于完成信号或错误信号。根据Reactive
Streams规范，这三种类型的信号转换为对下游Subscriber的onNext、onComplete和onError方法的调用。
Mono是一种特殊的Publisher，通过onNext信号发出最多一个项目，然后通过onComplete信号终止（成功的Mono，有或没有值），或者只发出一个onError信号（失败的Mono）。

# Spring WebFlux

随着互联网技术的发展，对高并发和低延迟的网络服务需求日益增长。Spring Boot 2.0 引入了 Spring WebFlux
模块，以支持响应式编程模型。它旨在帮助开发者构建非阻塞的、可扩展的应用程序，适用于所有类型的服务端应用程序开发。
Spring WebFlux 是 Spring 5 引入的一个新的响应式框架，提供了与 Spring MVC 类似的功能，但基于非阻塞操作并使用 Reactor
项目作为其核心库。WebFlux 支持两种编程风格：注解路由（类似于 MVC）和函数式路由。
类似于传统的 Spring MVC，我们可以使用注解来定义控制器。例如：
@RestController
public class UserController {

    @GetMapping("/users")
    public Flux<User> getAllUsers() {
        // 返回用户列表
    }

}
除了注解之外，WebFlux 还支持更灵活的函数式路由配置。这种方式更加贴近底层，允许你完全控制请求处理流程。
@Bean
public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
return route(GET("/users"), userHandler::getAllUsers);
}

Flux 与 Stream 的区别
Flux 和 Stream 都是处理数据序列的工具，但它们在设计理念、使用场景和实现机制上有显著的区别。理解这些差异有助于选择合适的技术来解决特定的问题。

1. 设计理念
   Flux：来自 Project Reactor 库，是 Spring WebFlux 的核心组件之一。它是一个响应式流（Reactive
   Streams）的实现，支持非阻塞操作，并且可以异步地处理数据流。Flux 是为了解决高并发、低延迟的需求而设计的，特别适合于需要处理大量并发连接或需要进行实时数据处理的应用场景。

Stream：Java 8 引入的一个特性，用于简化集合的操作，比如过滤、排序、映射等。它是基于集合的，主要用于同步的数据处理，提供了一种更清晰、简洁的方式来处理数据转换任务。Stream
更加适用于单机环境下的批处理任务。

2. 使用场景
   Flux：由于其非阻塞的本质，Flux 非常适合构建可扩展的网络服务或需要处理实时数据流的应用程序。例如，在微服务架构中，服务间通信通常需要快速响应和高效利用资源，这时就可以考虑使用
   Flux。

Stream：对于那些不需要考虑并发控制和异步处理的简单数据转换任务，Stream 提供了直观且易于使用的API。它非常适合用来对本地集合进行各种操作，如筛选、分组、聚合等。

3. 实现机制
   Flux：作为响应式编程的一部分，Flux 支持背压（Backpressure），这意味着消费者可以控制生产者的速率，以避免内存溢出等问题。此外，Flux
   可以通过多种方式创建，包括但不限于从集合、数组、生成器函数或者响应式库中的其他发布者。

Stream：Stream 不支持背压，因为它主要是同步执行的。一旦开始处理一个 Stream，它会持续运行直到完成所有操作。虽然 Stream
也支持并行处理（通过调用 .parallel() 方法），但这与 Flux 的异步、非阻塞特性不同。

4. 性能考量
   Flux：由于其异步和非阻塞特性，理论上在高负载情况下，Flux 能够比传统的同步方法更有效地利用系统资源，特别是在I/O密集型应用中表现尤为突出。

Stream：尽管 Stream API 在处理大数据集时提供了便利性，但在面对极高并发或需要长时间等待的任务时，可能会导致性能瓶颈，因为它的设计初衷并不是为了处理这类问题。

# 例子

public static void main(String[] args) {

// 基于java8的reactor，手动生成数据流,是一个publisher生产者，处理一定延迟的才能够返回的io操作时，不会阻塞，而是立刻返回一个流
// 并且订阅这个流，并且订阅这个流，当这个流上产生了返回数据，可以立刻得到通知并调用回调函数处理数据
final Flux<Object> objectFlux = Flux.create(new Consumer<FluxSink<Object>>() {
@Override
public void accept(FluxSink<Object> sink) {
// 阻塞耗时的io操作在这里
sink.next("ddddd");
sink.complete();
// 资源清理回调
sink.onDispose(() -> System.out.println("资源已释放"));
}
});

        // 进行consumer消费，订阅后才回触发数据流，不订阅就什么都不会发生。
        objectFlux.subscribe(new Consumer<Object>() {
                                 @Override
                                 public void accept(Object data) {
                                     System.out.println("接收数据: " + data);
                                 }
                             },  // 处理元素
                error -> {
                    System.err.println("发生异常: " + error);
                }, // 处理错误
                () -> System.out.println("流已结束"));
    }


Flux的不可变性‌:Reactor中的Flux每次操作都会返回新实例，原Flux不会被修改。

Flux的核心概念：
发布者(Publisher)：数据源
订阅者(Subscriber)：数据消费者
操作符(Operators)：用于转换和处理数据流
背压(Backpressure)：消费者控制生产者速率的能力‌5