package com.server.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
//@Component
@Slf4j
public class LogAop {
    // 적용할 곳
    @Pointcut("execution(* com.server.domain..*.*(..))")
    private void cut() {}

    //메서드 호출 전 적용
    @Before("cut()")
    public void beforeParameterLog(JoinPoint joinPoint) {
        String method = getMethod(joinPoint);
        log.info("=========Method name : {}=========", method);

        Object[] args = joinPoint.getArgs();
        if(args.length <= 0) log.info("no parameter");
        for(Object arg : args) {
            log.info("parameter type : {}", arg.getClass().getSimpleName());
            log.info("parameter value : {}", arg);
        }
    }

    @AfterReturning(value = "cut()", returning = "returnObj")
    public void afterReturning(JoinPoint joinPoint, Object returnObj) {
        String method = getMethod(joinPoint);
        log.info("=========Method name : {}=========", method);


        if(returnObj == null) {
            log.info("return parameter is null");
        } else {
            log.info("return parameter type : {}", returnObj.getClass().getSimpleName());
            log.info("return parameter value : {}", returnObj);
        }

    }

    private String getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getName();
    }
}
