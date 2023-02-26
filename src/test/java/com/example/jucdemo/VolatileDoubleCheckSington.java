package com.example.jucdemo;

/**
 * 单例示例
 */
public class VolatileDoubleCheckSington {
    //禁止重排序，保证示例延迟创建，避免指向空指针，instance实例
    //1.memory=allocate(); 分配对象的内存空间
    //2.ctorInstance(memory)  初始化对象
    //3.instance=memory   设置instance指向刚分配的内存地址
    private static volatile VolatileDoubleCheckSington sington;

    private VolatileDoubleCheckSington(){}

    public static VolatileDoubleCheckSington getInstance(){
        if (sington == null){
            //多线程并发，加锁保证只有一个线程创建对象
            synchronized (VolatileDoubleCheckSington.class){
                if (sington == null){
                    //隐患，多线程下，由于重排序，该对象可能还未完成初始化就被其他线程读取
                    sington = new VolatileDoubleCheckSington();
                }
            }
        }
        //对象创建完毕，不需要获取锁，直接返回创建的对象
        return sington;
    }
}
