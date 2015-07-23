package Aspects;

import Annotations.Loggable;
import BL.GasStation;
import Interfaces.LoggableClass;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;

@Aspect
public class LoggingAspect {

    public Object logAfter(ProceedingJoinPoint point) {
        Object result = null;
        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Object target = point.getTarget();
        String annotationMessage = "";
        for (Class c : target.getClass().getInterfaces()) {
            if (c == LoggableClass.class) {
                annotationMessage = target.getClass().getCanonicalName() + " ID " + ((LoggableClass) target).getId() + ": ";
                break;
            }
        }

        MethodSignature signature = MethodSignature.class.cast(point.getSignature());
        Annotation[] annotations = signature.getMethod().getDeclaredAnnotations();
        for (Annotation a : annotations) {
            if (a.annotationType() == Loggable.class) {
                //Annotation b = signature.getMethod().getDeclaredAnnotation(a.getClass());
                annotationMessage += ((Loggable) a).logMessage();
                break;
            }
        }
        String className = target.getClass().getCanonicalName();//point.getThis().getClass().getName();
        String methodName = MethodSignature.class.cast(point.getSignature()).getMethod().getName();
        Logger logger = Logger.getLogger(GasStation.LOGGER_NAME);
        logger.logp(Level.INFO, className, methodName, annotationMessage, point.getArgs());
        return result;
    }
}
