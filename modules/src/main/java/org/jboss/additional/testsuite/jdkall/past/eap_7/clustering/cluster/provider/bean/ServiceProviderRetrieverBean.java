package org.jboss.additional.testsuite.jdkall.past.eap_7.clustering.cluster.provider.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.wildfly.clustering.group.Node;
import org.wildfly.clustering.provider.ServiceProviderRegistration;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@Stateless
@Remote(ServiceProviderRetriever.class)
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap70x/clustering/src/main/java","modules/testcases/jdkAll/Eap70x-Proposed/clustering/src/main/java"})
public class ServiceProviderRetrieverBean implements ServiceProviderRetriever {

    @EJB
    private ServiceProviderRegistration<String> registration;

    @Override
    public Collection<String> getProviders() {
        Set<Node> nodes = this.registration.getProviders();
        List<String> result = new ArrayList<>(nodes.size());
        for (Node node: nodes) {
            result.add(node.getName());
        }
        return result;
    }
}
