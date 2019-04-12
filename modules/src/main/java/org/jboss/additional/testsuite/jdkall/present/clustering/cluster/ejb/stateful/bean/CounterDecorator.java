/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.additional.testsuite.jdkall.present.clustering.cluster.ejb.stateful.bean;

import java.io.Serializable;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

/**
 * @author Stuart Douglas
 */
@Decorator
@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/clustering/src/main/java","modules/testcases/jdkAll/Wildfly/clustering/src/main/java","modules/testcases/jdkAll/Eap72x/clustering/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap7/clustering/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/clustering/src/main/java","modules/testcases/jdkAll/Eap71x/clustering/src/main/java"})
public class CounterDecorator implements Serializable, Counter {
    private static final long serialVersionUID = -3924798173306389949L;

    @Inject
    @Delegate
    private Counter counter;

    @Override
    public int getCount() {
        return this.counter.getCount() + 10000000;
    }
}
