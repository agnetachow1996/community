package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

//@Component
//@Aspect
public class AlphaAspect {
    //execution表示关键字，*表示返回值，后面到service是包名，*表示包下的所有类
    //*.*表示类下所有的方法，（..）表示所有的参数
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointCut(){

    }
    //这里表示在切入点运行之前
    @Before("pointCut()")
    public void before(){
        System.out.println("before");
    }

    @After("pointCut()")
    public void after(){
        System.out.println("after");
    }

    //如果是想在有返回值之后运行，可以用@AfterReturning()
    @AfterReturning("pointCut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointCut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    @Around("pointCut()")
    public Object Around(ProceedingJoinPoint point) throws Throwable{
        //调用被处理的目标组件的方法
        System.out.println("around before");
        Object oj = point.proceed();
        System.out.println("around after");
        return oj;
    }

}
