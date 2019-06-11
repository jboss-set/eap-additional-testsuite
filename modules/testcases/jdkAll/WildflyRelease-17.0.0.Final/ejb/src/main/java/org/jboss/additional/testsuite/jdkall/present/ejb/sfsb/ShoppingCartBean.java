/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.ejb.sfsb;


import java.io.Serializable;
import java.util.HashMap;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Wildfly/ejb/src/main/java","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Eap72x/ejb/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap7/ejb/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/ejb/src/main/java","modules/testcases/jdkAll/Eap71x/ejb/src/main/java"})
@org.jboss.ejb3.annotation.Clustered 
@org.jboss.ejb3.annotation.Cache("distributable")
@Stateful
@LocalBean
@Local(ShoppingCart.class)
public class ShoppingCartBean implements ShoppingCart, Serializable {
	
	private HashMap<String, Integer> cart;
        
        public ShoppingCartBean(){
            cart = new HashMap<String, Integer>();
        }

	public void buy(String product, int quantity) {
		if (cart.containsKey(product)) {
			int currq = cart.get(product);
			currq += quantity;
			cart.put(product, currq);
		} else {
			cart.put(product, quantity);
		}
	}

	public HashMap<String, Integer> getCartContents() {
		return cart;
	}

	@Remove
	public void checkout() {
		System.out.println("To be implemented");
	}
}
