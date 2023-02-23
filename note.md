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
1. 使用Object中的`wait()`方法让线程等待，`notify()`唤醒
2. 使用JUC包中的`await()`方法让线程等待，`signal()`唤醒
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
#### 常用方法
1. `park()` 阻塞
2. `park(Thread)`阻塞
3. `unpark(Thread)` 唤醒，发放通行证（permit
#### 优点
1. 不必加锁
2. 可以先唤醒再等待
### JMM（java memory model） 
#### 出现背景
屏蔽掉各种硬件和操作系统的内存访问差异
#### 定义
JMM是一种抽象概念，并不真实存在，它仅仅描述一组规定或规范，通过这个规范定义了程序中（尤其多线程）<br/>
各个变量读写访问方式并决定一个线程对共享变量的写入时何时以及如何对另一个线程可见<br/>
关键技术点围绕多线程的`原子性`、`可见性`和`有序性`展开
#### 原则
多线程的`原子性`、`可见性`和`有序性`
#### 作用
1. 实现线程和主内存之间的抽象关系
2. 屏蔽各个`硬件平台`和`操作系统`的内存访问差异以实现在各种平台下都能达到一致的内存访问结果
#### JMM三大特性
1. 可见性
   * 定义：当一个线程修改了某一个共享变量的值，其他线程能`立刻`知道该变更,JMM规定了所有共享变量都在`主内存`中
   * 线程A --操作--> 本地内存A（共享变量的副本） --JMM控制（缓存一致性协议or总线程机制）--> 主内存（共享变量）
   * 系统主内存共享变量数据修改写入时机不确定，多线程下可能出现`脏读`，所以每个线程都有自己的`工作内存`（栈空间）
   * 主内存相当于白粥，不同的本地内存相当于甜粥，咸粥。。。
2. 原子性
   * 多个线程对于共享资源的唯一排他，在对一个资源修改时，不可以被打断
3. 有序性
   * 指令重排序：JVM能够根据cpu特性（cpu多级缓存系统、多核处理器等）适当的对机器指令进行重排序，使机器指令更符合cpu执行特性，最大限度发挥机器性能
   * 禁止指令重排序，即为有序性
#### happens-before原则
* 背景：在JMM中，如果一个操作执行的结果需要对另一个操作可见性，或者`代码重排序`，则必存在`happens-before`（先行发生）原则
* 作用：保证可见性和有序性
* 总原则：
  1. 可见：如果一个操作`happens-before`另一个操作，第一个操作的执行结果第二个操作可见，并且第一个操作要在第二个操作之前
  2. 重排序：如果重排序前后结果一致，则可以进行重排序操作 
* 八条原则：
  1. 次序规则： 先来后到，保证可见性
  2. 锁定规则： 时间上，针对锁，锁释放后才能再获取
  3. volatile变量规则： 时间上，保证可见性
  4. 传递规则 A->B->C 得出结果A先行于C
  5. 线程启动规则（Thread Start Rule） 线程先start才能执行线程内的逻辑操作
  6. 线程中断规则（Thread Interruption Rule） 先设置中断位置，才能检测到中断发生
  7. 线程终止规则（Thread Termination Rule） 线程中的所有操作发生在终止操作（`isAlice()`检测）之前
  8. 对象终结规则（Finalizer Rule） 一个对象初始化完成（构造函数执行结束）先行发生于他的`finalize()`（垃圾回收方法）的开始
#### volatile
* 特点：可见性、有序性
* 作用：
  1. 写一个`volatile`变量，JMM会把该线程对应的本地内存`立即`刷新到主内存
  2. 读一个`volatile`变量，JMM会把该线程对应的本地内存失效，重新读取主内存变量值
##### 实现：内存屏障
* 作用：
  1. 内存屏障之前，所有写操作都要写到主内存
  2. 内存屏障之后，所有读操作都能获得内存屏障之前的所有写操作的最新结果（实现可见性）
* 实现步骤：java线程 ->  工作内存 ->  内存屏障 ->  主内存
* 粗分类
  1. 读屏障：读指令之前插入读屏障，工作内存和cpu高速缓存之中的缓存数据失效，重新到主内存获取最新数据<br/>`volatile读` --LoadLoad--> --LoadStore--> 普通读 普通写
  2. 写屏障：写指令之后插入写屏障，强制把写缓冲区的数据刷到主内存中<br/>普通读写 --StoreStore--> `volatile写` --StoreLoad--> 其他操作
  3. 全屏障， 读写屏障都有
* 细分类<br/> 
* |屏障类型 | 指令示例 | 说明                           |
  | :---- | :---- |:-----------------------------|
  |LoadLoad|Load1;LoadLoad;Load2| 保证Load1的读取操作在Load2以及后续操作之前执行 |
  |StoreStore|Store1;StoreStore;Store2|在Store2之后的写操作执行前，保证Store1的写操作已经刷新到主内存中|
  |LoadStore|Load1;LoadStore;Store2|在Store2及之后的写操作执行前，保证Load1的读操作已经读取结束|
  |StoreLoad|Store1;StoreLoad;Load2|保证Store1的写操作已刷新到主内存之后，Load2及之后的读操作才执行|
* volatile变量规则
* 
| 第1个操作 | 第2个操作-普通读写 |第2个操作-volatile读| 第2个操作-volatile写 |
|-------|------------| ---- |-----------------|
|普通读写|可重排|可重排| 不可重排            |
|volatile读|不可重排|不可重排| 不可重排             |
|volatile写|可重排|不可重排| 不可重排             |

