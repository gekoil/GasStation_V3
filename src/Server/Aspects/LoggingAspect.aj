package Aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;

public aspect LoggingAspect {
    @After("@Loggable")
    public void logCall(JoinPoint joinPoint) {

    }
}
