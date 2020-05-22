package org.jboss.additional.testsuite.jdkall.present.ejb.singleton.postconstruct;

import javax.ejb.*;
import java.util.logging.*;
import javax.annotation.*;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@Startup
@Singleton
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java", "modules/testcases/jdkAll/Eap7/ejb/src/main/java", "modules/testcases/jdkAll/Eap72x/ejb/src/main/java", "modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java"})
public class ExampleSingleton
{
    private static Boolean fail;
    private static Integer sleep;
    private static Logger log;
    private static boolean constructed;
    
    @PostConstruct
    public void init() {
        constructed = false;
        ExampleSingleton.log.info("init fail: " + ExampleSingleton.fail + " sleep: " + ExampleSingleton.sleep);
        if (ExampleSingleton.sleep > 0) {
            ExampleSingleton.log.info("Starting sleep: " + ExampleSingleton.sleep);
            try {
                Thread.sleep(ExampleSingleton.sleep * 1000);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            ExampleSingleton.log.info("Finished sleep: " + ExampleSingleton.sleep);
        }
        ExampleSingleton.log.info("Throwing RuntimeException");
        throw new RuntimeException("Failing the Singleton");
        
       // constructed=true;
    }
    
    static {
        ExampleSingleton.fail = Boolean.getBoolean("fail");
        ExampleSingleton.sleep = Integer.getInteger("sleep", 0);
        ExampleSingleton.log = Logger.getLogger(ExampleSingleton.class.getSimpleName());
    }
    
    public static boolean getConstucted(){
        return constructed;
    }
}
