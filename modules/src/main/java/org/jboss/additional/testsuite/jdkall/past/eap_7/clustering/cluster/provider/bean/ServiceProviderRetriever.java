package org.jboss.additional.testsuite.jdkall.past.eap_7.clustering.cluster.provider.bean;

import java.util.Collection;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap70x/clustering/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/clustering/src/main/java"})
public interface ServiceProviderRetriever {

    Collection<String> getProviders();
}
