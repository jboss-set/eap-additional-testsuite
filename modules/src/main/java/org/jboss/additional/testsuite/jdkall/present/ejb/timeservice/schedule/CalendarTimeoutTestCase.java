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

package org.jboss.additional.testsuite.jdkall.present.ejb.timeservice.schedule;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.ejb.ScheduleExpression;
import org.jboss.as.ejb3.timerservice.schedule.CalendarBasedTimeout;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;
import org.jboss.eap.additional.testsuite.annotations.ATTest;
import org.jboss.eap.additional.testsuite.annotations.ATFeature;
import org.junit.Assert;
import org.junit.Test;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/WildflyRelease-13.0.0.Final/ejb/src/main/java","modules/testcases/jdkAll/Wildfly/ejb/src/main/java#12.0.0.Alpha1","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/ejb/src/main/java"})
public class CalendarTimeoutTestCase {
        /**
     * Change CET winter time to CEST summer time.
     * The timer should be fired every 15 minutes (absolutely).
     * The calendar time will jump from 2:00CET to 3:00CEST
     * The test should be run similar in any OS/JVM default timezone
     * This is a test to ensure WFLY-9537 will not break this.
     */
    @ATTest({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java#12.0.0.Alpha1","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/ejb/src/main/java"})
    public void testChangeCET2CEST() {
        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
        start.clear();
        // set half an hour before the CET->CEST DST switch 2017
        start.set(2017, Calendar.MARCH, 26, 1, 30, 0);

        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour("*")
                .minute("0/15")
                .second("0")
                .timezone("Europe/Berlin")  // don't fail the check below if running in a not default TZ
                .start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        // assert first timeout result
        Assert.assertNotNull(firstTimeout);
        if(firstTimeout.get(Calendar.YEAR) != 2017 ||
                firstTimeout.get(Calendar.MONTH) != Calendar.MARCH ||
                firstTimeout.get(Calendar.DAY_OF_MONTH) != 26 ||
                firstTimeout.get(Calendar.HOUR_OF_DAY) != 1 ||
                firstTimeout.get(Calendar.MINUTE) != 30 ||
                firstTimeout.get(Calendar.SECOND) != 0 ||
                firstTimeout.get(Calendar.DST_OFFSET) != 0) {
            Assert.fail("Start time unexpected : " + firstTimeout.toString());
        }
        Calendar current = firstTimeout;
        for(int i = 0 ; i<3 ; i++) {
            Calendar next = calendarTimeout.getNextTimeout(current);
            if(current.getTimeInMillis() != (next.getTimeInMillis() - 900000)) {
                Assert.fail("Schedule is more than 15 minutes from " + current.getTime() + " to " + next.getTime());
            }
            current = next;
        }
        if(current.get(Calendar.YEAR) != 2017 ||
                current.get(Calendar.MONTH) != Calendar.MARCH ||
                current.get(Calendar.DAY_OF_MONTH) != 26 ||
                current.get(Calendar.HOUR_OF_DAY) != 3 ||
                current.get(Calendar.MINUTE) != 15 ||
                current.get(Calendar.DST_OFFSET) != 3600000) {
            Assert.fail("End time unexpected : " + current.toString());
        }
    }

    /**
     * Change CEST summer time to CEST winter time.
     * The timer should be fired every 15 minutes (absolutely).
     * The calendar time will jump from 3:00CEST back to 2:00CET
     * but the timer must run within 2:00-3:00 CEST and 2:00-3:00CET!
     * The test should be run similar in any OS/JVM default timezone
     * This is a test for WFLY-9537
     */
    @Test
    @ATFeature(feature={"feature1,feature2,feature3"},minVersion={"1.2.3,1,1"},maxVersion={"2.3.4,12,null"})
    public void testChangeCEST2CET() {
        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
        start.clear();
        // set half an hour before the CEST->CET DST switch 2017
        start.set(2017, Calendar.OCTOBER, 29, 1, 30, 0);

        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour("*")
                .minute("5/15")
                .second("0")
                .timezone("Europe/Berlin")  // don't fail the check below if running in a not default TZ
                .start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        // assert first timeout result
        Assert.assertNotNull(firstTimeout);
        if(firstTimeout.get(Calendar.YEAR) != 2017 ||
                firstTimeout.get(Calendar.MONTH) != Calendar.OCTOBER ||
                firstTimeout.get(Calendar.DAY_OF_MONTH) != 29 ||
                firstTimeout.get(Calendar.HOUR_OF_DAY) != 1 ||
                firstTimeout.get(Calendar.MINUTE) != 35 ||
                firstTimeout.get(Calendar.SECOND) != 0 ||
                firstTimeout.get(Calendar.DST_OFFSET) != 3600000) {
            Assert.fail("Start time unexpected : " + firstTimeout.toString());
        }
        Calendar current = firstTimeout;
        for(int i = 0 ; i<7 ; i++) {
            Calendar next = calendarTimeout.getNextTimeout(current);
            if(current.getTimeInMillis() != (next.getTimeInMillis() - 900000)) {
                Assert.fail("Schedule is more than 15 minutes from " + current.getTime() + " to " + next.getTime());
            }
            current = next;
        }
        if(current.get(Calendar.YEAR) != 2017 ||
                current.get(Calendar.MONTH) != Calendar.OCTOBER ||
                current.get(Calendar.DAY_OF_MONTH) != 29 ||
                current.get(Calendar.HOUR_OF_DAY) != 2 ||
                current.get(Calendar.MINUTE) != 20 ||
                current.get(Calendar.DST_OFFSET) != 0) {
            Assert.fail("End time unexpected : " + current.toString());
        }
    }

    /**
     * Change PST winter time to PST summer time.
     * The timer should be fired every 15 minutes (absolutely).
     * This is a test to ensure WFLY-9537 will not break this.
     */
    @ATTest({"modules/testcases/jdkAll/Wildfly/ejb/src/main/java#12.0.0.Alpha1","modules/testcases/jdkAll/WildflyRelease-17.0.0.Final/ejb/src/main/java"})
    public void testChangeUS2Summer() {
        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        start.clear();
        // set half an hour before Los Angeles summer time switch
        start.set(2017, Calendar.MARCH, 12, 1, 30, 0);

        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour("*")
                .minute("0/15")
                .second("0")
                .timezone("America/Los_Angeles")  // don't fail the check below if running in a not default TZ
                .start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        // assert first timeout result
        Assert.assertNotNull(firstTimeout);
        if(firstTimeout.get(Calendar.YEAR) != 2017 ||
                firstTimeout.get(Calendar.MONTH) != Calendar.MARCH ||
                firstTimeout.get(Calendar.DAY_OF_MONTH) != 12 ||
                firstTimeout.get(Calendar.HOUR_OF_DAY) != 1 ||
                firstTimeout.get(Calendar.MINUTE) != 30 ||
                firstTimeout.get(Calendar.SECOND) != 0 ||
                firstTimeout.get(Calendar.DST_OFFSET) != 0) {
            Assert.fail("Start time unexpected : " + firstTimeout.toString());
        }
        Calendar current = firstTimeout;
        for(int i = 0 ; i<3 ; i++) {
            Calendar next = calendarTimeout.getNextTimeout(current);
            if(current.getTimeInMillis() != (next.getTimeInMillis() - 900000)) {
                Assert.fail("Schedule is more than 15 minutes from " + current.getTime() + " to " + next.getTime());
            }
            current = next;
        }
        if(current.get(Calendar.YEAR) != 2017 ||
                current.get(Calendar.MONTH) != Calendar.MARCH ||
                current.get(Calendar.DAY_OF_MONTH) != 12 ||
                current.get(Calendar.HOUR_OF_DAY) != 3 ||
                current.get(Calendar.MINUTE) != 15 ||
                current.get(Calendar.DST_OFFSET) != 3600000) {
            Assert.fail("End time unexpected : " + current.toString());
        }
    }

    /**
     * Change PST summer time to PST winter time.
     * The timer should be fired every 15 minutes (absolutely).
     * This is a test for WFLY-9537
     */
    @Test
    public void testChangeUS2Winter() {
        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        start.clear();
        // set half an hour before Los Angeles time switch to winter time
        start.set(2017, Calendar.NOVEMBER, 5, 0, 30, 0);

        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour("*")
                .minute("0/15")
                .second("0")
                .timezone("America/Los_Angeles")  // don't fail the check below if running in a not default TZ
                .start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        // assert first timeout result
        Assert.assertNotNull(firstTimeout);
        if(firstTimeout.get(Calendar.YEAR) != 2017 ||
                firstTimeout.get(Calendar.MONTH) != Calendar.NOVEMBER ||
                firstTimeout.get(Calendar.DAY_OF_MONTH) != 5 ||
                firstTimeout.get(Calendar.HOUR_OF_DAY) != 0 ||
                firstTimeout.get(Calendar.MINUTE) != 30 ||
                firstTimeout.get(Calendar.SECOND) != 0 ||
                firstTimeout.get(Calendar.DST_OFFSET) != 3600000) {
            Assert.fail("Start time unexpected : " + firstTimeout.toString());
        }
        Calendar current = firstTimeout;
        for(int i = 0 ; i<7 ; i++) {
            Calendar next = calendarTimeout.getNextTimeout(current);
            if(current.getTimeInMillis() != (next.getTimeInMillis() - 900000)) {
                Assert.fail("Schedule is more than 15 minutes from " + current.getTime() + " to " + next.getTime());
            }
            current = next;
        }
        if(current.get(Calendar.YEAR) != 2017 ||
                current.get(Calendar.MONTH) != Calendar.NOVEMBER ||
                current.get(Calendar.DAY_OF_MONTH) != 5 ||
                current.get(Calendar.HOUR_OF_DAY) != 1 ||
                current.get(Calendar.MINUTE) != 15 ||
                current.get(Calendar.DST_OFFSET) != 0) {
            Assert.fail("End time unexpected : " + current.toString());
        }
    }
}
