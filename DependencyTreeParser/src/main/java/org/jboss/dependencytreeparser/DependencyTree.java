package org.jboss.dependencytreeparser;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author panos
 */
public class DependencyTree {

    public static void main(String[] args) throws Exception {
        HashSet<String> packages = DependencyTreeMethods.listPackages();
        
        for(String s : packages){
            System.out.println(s);
        }
    }

    
}
