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
2. `thenAccept` 执行完A，A返回，B无返回，B可以用A的数据 accept类似流的终结语句
3. `thenApply` 执行完A，与B结合返回值  apply类似流管道
#### applyToEither
* `future.applyToEither(future2, f - > {})` future与future2比较返回执行更快的任务结果为f
#### thenCombine
* `future.thenCombine(future2, (x, y) -> { return x + y; });`
  future与future2返回x,y结果进行结果结合，返回新的CompletableFuture对象
### 线程唤醒方法
1. 使用Object中的wait()方法让线程等待，notify()唤醒
2. 使用JUC包中的await()方法让线程等待，signal()唤醒
3. LockSupport类

## 锁
### 悲观锁
* 概念：认为使用资源时，一定有别的线程抢占资源，多用于写操作
* 常用方式：`synchronized`、`Lock`
* `synchromized`:
  1. 本质：实例锁
  2. 解释：一个对象有多个`synchromized`方法，某一时刻，只要有一个线程调用任一`synchronized`方法，其他线程只能等待。锁的对象是this
  3. 原理：
     1. 代码块锁 `javap -c */*.class`反编译看字节码由`monitorenter`和`monitorexit`实现锁功能
     2. 对象锁  `javap -v */*.class`查看字节码附加信息，有ACC_SYNCHRONIZED标识
     3. 类锁 有ACC_STATIC和ACC_SYNCHRONIZED标识
  4. 问答：为什么每一个对象都可以成为一个锁?<br/>底层c++创建对象时，会创建`objectMonitor()`，里面有`_owner`属性指向持有锁的对象

* `static synchromized` 类锁，锁定类创建的所有对象
### 乐观锁
* 概念；认为使用资源时，不会有别的线程抢占资源，多用于读操作
* 常用方式：
  1. 版本号机制
  2. cas（compare and swap）算法
### 公平锁和非公平锁
* 定义：
  1. 公平锁：多个线程按照申请锁的顺序获取锁，类似排队买票<br/>
  `ReentrantLock lock = new ReentrantLock(true);`
  2. 非公平锁：多个线程并不按照申请锁的顺序获取锁，高并发下有可能造成优先级反转或者饥饿的状态（某个线程一直得不到锁）
  <br/> `ReentrantLock lock = new ReentrantLock();`<br/>`ReentrantLock lock = new ReentrantLock(false);`
* 问答
  1. 为什么有公平，非公平的设计？<br/> 恢复挂起的锁到真正锁的获取有时间差，非公平锁能够更充分利用cpu的时间片，尽量减少cpu空闲时间
  2. 什么时候用公平，用非公平？<br/>注重效率用非公平，注重业务吞吐量用公平
### 可重入锁（递归锁）
* 定义：同一个线程在外层方法获取锁的时候，在进入该线程的内层方法会自动获取锁（前提，锁对象是同一个对象），不会因为之前已经获取过还没释放而阻塞
* 类型：synchronized、ReentrantLock
### 死锁
* 定义：死锁是指两个或两个以上的进程在执行过程中，由于竞争资源或者由于彼此通信而造成的一种阻塞的现象，若无外力作用，它们都将无法推进下去。此时称系统处于死锁状态或系统产生了死锁，这些永远在互相等待的进程称为死锁进程。
* 产生原因：1.系统资源不足；2.进程推进顺序不对；3.资源分配不当
* 发现死锁方式：<br/>1.纯命令`jps -l`查看类进程号 `jstack 进程编号`打印死锁类；2.图形化 jconsole，在jdk包里面
## 中断协商机制
* 定义：
  1. 调用线程`interrupt`方法，将活动的线程中断标志设置为true，再自己实现中断线程操作
     2. 如果线程处于阻塞状态（sleep，join，wait等），在别的线程调用当前线程的`interrupt`方法则会立刻退出阻塞状态，并抛出`InterruptedException`
* 实现：
    1. `public void interrupt()` 设置线程中断状态为true，发起一个协商而不会立刻停止线程
  2. `public static boolean interrupted()` 判断线程是否被中断并清楚当前中断状态
  3. `public boolean isInterrupted()` 判断当前线程是否被中断（通过检查中断标志位）
### LockSupport
