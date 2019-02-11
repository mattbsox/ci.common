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

public class AppStatusUtil {
	
	private static MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

	public static String getAppStatus(String appName) {
		try {
			ObjectName myAppMBean = new ObjectName(
			"WebSphere:service=com.ibm.websphere.application.ApplicationMBean,name=" + appName);
			if (mbs.isRegistered(myAppMBean)) {
				String state = (String) mbs.getAttribute(myAppMBean, "State");	
				// alternatively, obtain a proxy object
				ApplicationMBean app = JMX.newMBeanProxy(mbs, myAppMBean, ApplicationMBean.class);
				state = app.getState();
				return state;
			}
		} catch (MalformedObjectNameException | MBeanException me) {
			System.out.println("Could not connect to the MBeanServer while checking the application status.\n" + me.getMessage());
		} catch (AttributeNotFoundException | InstanceNotFoundException | ReflectionException me) {
			System.out.println("Could not determine application status. Either the attribute was missing or the instance was not found.\n" + me.getMessage());	
		}
		return null;
	}
}