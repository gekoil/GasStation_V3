package Aspects;

import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;

public aspect LoggingAspect {
    @After("@Loggable")
    public void logCall(JoinPoint joinPoint) {
    	
    }
//GasStation.getLog().log(Level.INFO, "Car #" + car.getID() + " finished fueling up, at Pump #" + pumpNum, param);
}
