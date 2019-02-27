package net.wasdev.wlp.common.plugins.util;

import java.lang.management.ManagementFactory;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import com.ibm.websphere.application.ApplicationMBean;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import javax.management.ObjectInstance;

public class AppStatusUtil {

    private static JMXConnector jmxConnector;
    private static MBeanServerConnection mbsc;

    public static String APP_STARTED = "STARTED";
    public static String APP_STOPPED = "STOPPED";


    public static String getAppStatus(String appName, String serverOutputDir) throws PluginExcecutionException {
        try {
            JMXServiceURL url = new JMXServiceURL(getConnectorAddress(serverOutputDir));
            jmxConnector = JMXConnectorFactory.connect(url);
            mbsc = jmxConnector.getMBeanServerConnection();

            ObjectName myAppMBean = new ObjectName(
            "WebSphere:service=com.ibm.websphere.application.ApplicationMBean,name=" + appName);
            if (mbsc.isRegistered(myAppMBean)) {
                String state = (String) mbsc.getAttribute(myAppMBean, "State");  
                return state;
            } else {
                return "Application " + appName + " is not registered.";
            }
        } catch (Exception e) {
            throw new PluginExcecutionException(e.getMessage());
        }
        return null;
    }

    private static String getConnectorAddress(String serverOutputDir) throws IOException {
        File jmxAddr = new File(serverOutputDir + "/logs/state/com.ibm.ws.jmx.local.address");
        System.out.println("Checking " + jmxAddr.getCanonicalPath());
        //Checking local connector address file for JMX address
        if (jmxAddr.exists() && jmxAddr.isFile() && jmxAddr.canRead()) {
            List<String> lines = Files.readAllLines(jmxAddr.toPath(), StandardCharsets.UTF_8);
            if (lines != null && lines.size() > 0) {
                return lines.get(0);
            }
        }
        
        return null;
    }
}