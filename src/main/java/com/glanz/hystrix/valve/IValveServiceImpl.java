package com.glanz.hystrix.valve;

import com.alibaba.fastjson.JSON;
import com.glanz.hystrix.annotation.DoHystrix;
import com.netflix.hystrix.*;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * @Author GlanzWen
 * @Description Something
 * @github
 */
public class IValveServiceImpl extends HystrixCommand<Object> implements IValveService{

    private ProceedingJoinPoint jp;
    private Method method;

    private DoHystrix doHystrix;

    /*
    * 参数说明:
    *   该实现类封装了Hystrix熔断保护机制,继承HystrixCommand, 使用其构造器配置初始化参数
    *   包括:
    *       GroupKey：            命令所属组别
    *       CommandKey：          命令的名称
    *       CommandProperties：   命令的一些配置，包括断路器的配置，隔离策略，降级设置，以及一些监控指标等。
    *       ThreadPoolProperties：线程池的配置，包括线程池大小，等待队列的大小等
    *       ThreadPoolKey：       该命令所属线程池的名称，同样配置的命令会共享同一线程池，若不配置，会默认使用GroupKey作为线程池名称。
    *
    *
    * */

    public IValveServiceImpl() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Group")) //命令组
                .andCommandKey(HystrixCommandKey.Factory.asKey("Key"))           //命令
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ThreadPool")) //线程池
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))//线程隔离策略
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(10)));//核心线程数
    }


    @Override
    public Object access(ProceedingJoinPoint jp, Method method, DoHystrix doHystrix, Object[] args) throws Throwable {
        this.jp = jp;
        this.method = method;
        this.doHystrix = doHystrix;

        //设置超时熔断时间
        Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("group"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(doHystrix.timeOutValue()));//通过注解配置超时时间
        return this.execute();
    }

    @Override
    protected Object run() throws Exception {
        try {
            return jp.proceed();
        } catch (Throwable throwable) {
            return null;
        }
    }


    protected Object getFallBack () {
        return JSON.parseObject(doHystrix.returnJson(), method.getReturnType());
    }
}
