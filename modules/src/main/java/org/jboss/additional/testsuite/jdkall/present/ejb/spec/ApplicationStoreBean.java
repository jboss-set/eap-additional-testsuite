package org.jboss.additional.testsuite.jdkall.present.ejb.spec;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.Remove;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;

import org.apache.log4j.Logger;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@Startup
@Singleton
@ApplicationScoped
@Stateful(name = "applicationStoreBean")
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java"})
public class ApplicationStoreBean implements ApplicationStore {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(ApplicationStoreBean.class);

	private static final Date date = new Date();

	{

		logger.info("++ Constructor Called. Date Creation Reference: " + getDate());

	}

	@PostConstruct
	public void postconstruct() {

		logger.info("++ Post Constructor Called. Date Creation Reference: " + getDate());
	}

	@Remove
	public void remove() {

	}

	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format(date);
	}

}
