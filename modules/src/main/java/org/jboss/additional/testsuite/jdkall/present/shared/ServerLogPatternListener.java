/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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

package org.jboss.additional.testsuite.jdkall.present.shared;

import org.apache.commons.io.input.TailerListenerAdapter;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

/**
 * Listens for occurrences of a given pattern in new lines of a given file.
 *
 * @author Peter Mackay
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/Eap72x/messaging/src/main/java","modules/testcases/jdkAll/Eap72x-Proposed/messaging/src/main/java","modules/testcases/jdkAll/Eap71x-Proposed/messaging/src/main/java#7.1.4","modules/testcases/jdkAll/Eap71x/messaging/src/main/java#7.1.4","modules/testcases/jdkAll/Wildfly/messaging/src/main/java#12.0.0"})
public class ServerLogPatternListener extends TailerListenerAdapter {

    private List<String> matchedLines = new CopyOnWriteArrayList<>();
    private final List<Pattern> patterns;

    public ServerLogPatternListener(Pattern... patterns){
        this.patterns = Arrays.asList(patterns);
    }

    public List<String> getMatchedLines() {
        return matchedLines;
    }

    @Override
    public void handle(final String line) {
        for (Pattern pattern: patterns) {
            if (pattern.matcher(line).matches()) {
                matchedLines.add(line);
                break;
            }
        }
    }
}
