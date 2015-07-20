package Aspects;

import Annotations.Loggable;
import BL.GasStation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jack on 21/07/2015.
 */
@Aspect
public class LoggingAspect {

    public Object logAfter(ProceedingJoinPoint point) {
        Object result = null;
        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        String annotationMessage = "";
        MethodSignature signature = MethodSignature.class.cast(point.getSignature());
        Annotation[] annotations = signature.getMethod().getDeclaredAnnotations();
        for (Annotation a : annotations) {
            if (a.annotationType() == Loggable.class) {
                //Annotation b = signature.getMethod().getDeclaredAnnotation(a.getClass());
                 annotationMessage = ((Loggable) a).logMessage();
            }
        }
        String className = point.getClass().getName();
        String methodName = MethodSignature.class.cast(point.getSignature()).getMethod().getName();
        Logger logger = Logger.getLogger(GasStation.LOGGER_NAME);
        logger.logp(Level.INFO, className, methodName, annotationMessage, point.getArgs());
        return result;
    }
//GasStation.getLog().log(Level.INFO, "Car #" + car.getID() + " finished fueling up, at Pump #" + pumpNum, param);
}
