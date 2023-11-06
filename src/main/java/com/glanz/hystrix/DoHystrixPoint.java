package com.glanz.hystrix;

import com.glanz.hystrix.annotation.DoHystrix;
import com.glanz.hystrix.valve.IValveService;
import com.glanz.hystrix.valve.IValveServiceImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Author GlanzWen
 * @Description 切面类
 * @github
 */

@Aspect
@Component
public class DoHystrixPoint {
    @Pointcut("@annotation(com.glanz.hystrix.annotation.DoHystrix)")
    public void aopPoint() {}

    @Around("aopPoint() && @annotation(doGovern)")
    public Object doRouter(ProceedingJoinPoint jp, DoHystrix doGovern) throws Throwable {
        IValveService valveService = new IValveServiceImpl();
        return valveService.access(jp, getMethod(jp), doGovern, jp.getArgs());
    }



    private Method getMethod (JoinPoint jp) throws Throwable {
        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }
}
