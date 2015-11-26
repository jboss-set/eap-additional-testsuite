/*
 * Copyleft 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.test.integration.automatedmetrics.standalone.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.ejb.EJB;
import org.jboss.metrics.automatedmetricsapi.MetricsPropertiesApi;
import org.jboss.metrics.jbossautomatedmetricsproperties.MetricProperties;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.metrics.automatedmetricsapi.MetricsCacheApi;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Panagiotis Sotiropoulos
 */
@RunWith(Arquillian.class)
public class CacheStorageTestCase {

    @EJB
    private MetricsApiSessionBean metricsApiSessionBean;
    
    private String groupName = "myTestGroup";

    @Deployment
    public static Archive<?> getDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);
        archive.addClass(CacheStorageTestCase.class);
        archive.addClass(MetricsApiSessionBean.class);
        archive.addPackage("org.jboss.metrics.jbossautomatedmetricsproperties");
        archive.addPackage("org.jboss.metrics.automatedmetricsapi");
        archive.addPackage("org.jboss.metrics.jbossautomatedmetricslibrary");
        archive.addPackage("org.jboss.metrics.automatedmetrics");
        archive.addAsResource("META-INF/beans.xml");
        return archive;
    }
    
    @Test
    public void testServerStart() {
        initializeMetricProperties();

        try {
            metricsApiSessionBean.countMethod();
            Set<String> metricNames = MetricsCacheApi.getMetricsCache(groupName).keySet();
            Iterator<String> iob = metricNames.iterator();
            while (iob.hasNext()) {
                String key = iob.next();
                if (key.contains("count2")) {
                    ArrayList<Object> comparableObject = new ArrayList<>();
                    comparableObject.add(2.0);
                    boolean correct = MetricsCacheApi.compareMetricsCacheValuesByKey(groupName, key, comparableObject);
                    assertTrue("Data are not contained in cache ... ", correct);
                }else if (key.contains("count2")) {
                    ArrayList<Object> comparableObject = new ArrayList<>();
                    comparableObject.add(1.0);
                    boolean correct = MetricsCacheApi.compareMetricsCacheValuesByKey(groupName, key, comparableObject);
                    assertTrue("Data are not contained in cache ... ", correct);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            assertFalse("No data in the cache store ... ", true);
        }
    }

    private void initializeMetricProperties() {
        MetricProperties metricProperties = new MetricProperties();
        metricProperties.setCacheStore("true");
        metricProperties.setRhqServerUrl("lz-panos-jon33.bc.jonqe.lab.eng.bos.redhat.com");
        MetricsPropertiesApi.storeProperties(groupName, metricProperties);
    }
}


