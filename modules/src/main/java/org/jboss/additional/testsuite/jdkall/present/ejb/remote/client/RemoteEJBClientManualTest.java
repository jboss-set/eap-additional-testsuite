package org.jboss.additional.testsuite.jdkall.present.ejb.remote.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import ejb.HelloBeanRemote;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap71x/ejb/src/main/java"})
public class RemoteEJBClientManualTest {

    private static String result = null;

    @Test 
    public void testServerSuspentionMode() throws Exception {
        try {
            Thread t = new Thread(){
                public void run(){
              /*      Properties props = new Properties();
                    props.put("org.jboss.ejb.client.scoped.context", true);
                    props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
                    props.put("remote.connections", "main");
                    props.put("remote.connection.main.host", "localhost");
                    props.put("remote.connection.main.port", "8080");
                    props.put("remote.connection.main.connect.options.org.xnio.Options.READ_TIMEOUT", "11000");
                    props.put("remote.connection.main.connect.options.org.xnio.Options.WRITE_TIMEOUT", "11000");
                    props.put("remote.connection.main.connect.options.org.jboss.remoting3.RemotingOptions.HEARTBEAT_INTERVAL", "11000");
            */
                    InitialContext ctx = null;
                        
                    try {
                        ctx = new InitialContext();
                        
                        String lookupName = "ejb:/server/HelloBean!ejb.HelloBeanRemote";
                        // Invoke a stateless bean
                        final HelloBeanRemote statelessHelloBeanRemote = (HelloBeanRemote)ctx.lookup(lookupName);
                        statelessHelloBeanRemote.hello();
                        result = "done";
                    } catch (Exception ex) {
                        try {
                            result = "error";
                            ((Context)ctx.lookup("ejb:")).close();
                            ctx.close();
                            ex.printStackTrace();
                        } catch (NamingException ex1) {
                            ex1.printStackTrace();
                        }
                    }
                }
            };
            
            ArrayList<String> commands=new ArrayList<String>();
            commands.add("/bin/bash");
            commands.add("-c");
            commands.add("./startServer.sh");
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process p = pb.start();
            Thread.sleep(10000);
            t.start();
            Thread.sleep(5000);
            ArrayList<String> commands2=new ArrayList<String>();
            commands2.add("/bin/bash");
            commands2.add("-c");
            commands2.add("./suspendServer.sh");
            ProcessBuilder pb2 = new ProcessBuilder(commands2);
            pb2.redirectOutput(new File("output2.txt"));
            Process p2 = pb2.start();
            Thread.sleep(12000);
            assertTrue("The ejb-client side should have finished.",result!=null);
            t.interrupt();
            p2.destroy();
            p.destroy();
          
            
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
