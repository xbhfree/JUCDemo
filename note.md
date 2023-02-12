# JUC
## java多线程相关概念
### 1把锁
synchronized
### 2个并
1. 并行(parallel)
   * 概念：不同实体上的多个事件，多台处理器上同时处理多个任务，同一时刻，大家各做各的事情
   * 举例：泡面，一边烧水一边撕开包装
2. 并发(concurrent)
   * 概念：同一实体上的多个事件，在一台处理器上“同时”处理多个任务，同一时刻，其实只有一个事件在发生
   * 举例：抢票
### 3个程序
1. 进程
   * 概念：系统中每一个应用程序就是一个进程，每个进程有自己的内存空间和系统资源
2. 线程
   * 概念：轻量级进程，同一个进程会有1个或多个线程，是大多数操作系统进行时序调度的基本单元
3. 管程
   * 概念：monitor（监视器），也就是平时所说的锁
## CompletableFuture
### FutureTask
* 功能：异步并行计算
* 特点：多线程、有返回、异步任务
* 优点：异步多线程，充分利用cpu资源
* 缺点：
   1. get()阻塞线程，调用即会等待至结果出现
   2. isDone()轮询会造成cpu资源损失
### CompletionStage
* 功能：提供异步线程的多个方法，类似管道
### CompletableFuture创建的四个静态方法
1. `public static CompletableFuture<Void> runAsync(Runnable runnable)`
2. `public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)`
3. `public static<U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)`
4. `public static<U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)`
### CompletableFuture常用API
#### .handle((v, e)
* 可以携带异常传递结果
#### thenRun、thenApply、thenAccept
1. `thenRun` A步骤与B步骤无关
2. `thenAccept` 执行完A，A返回，B无返回，B可以用A的数据 accpet类似流的终结语句
3. `thenApply` 执行完A，与B结合返回值  apply类似流管道
#### applyToEither
* `future.applyToEither(future2, f - > {})` future与future2比较返回执行更快的任务结果为f
#### thenCombine
* `future.thenCombine(future2, (x, y) -> { return x + y; });`
  future与future2返回x,y结果进行结果结合，返回新的CompletableFuture对象

## 锁
### 悲观锁
* 概念：认为使用资源时，一定有别的线程抢占资源，多用于写操作
* 常用方式：`synchronized`、`Lock`
* `synchromized`:实例锁，一个对象有多个`synchromized`方法，某一时刻，只要有一个线程调用任一`synchromized`方法，其他线程只能等待。锁的对象是this
* `static synchromized` 类锁，锁定类创建的所有对象
### 乐观锁
* 概念；认为使用资源时，不会有别的线程抢占资源，多用于读操作
* 常用方式：
  1. 版本号机制
  2. cas（compare and swap）算法