/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.common.internal.epl.output.polled;


import com.espertech.esper.common.client.type.EPTypeClass;

public interface OutputConditionPolled {
    EPTypeClass EPTYPE = new EPTypeClass(OutputConditionPolled.class);

    public OutputConditionPolledState getState();

    /**
     * Update the output condition.
     *
     * @param newEventsCount - number of new events incoming
     * @param oldEventsCount - number of old events incoming
     * @return indicator whether to output
     */
    public boolean updateOutputCondition(int newEventsCount, int oldEventsCount);
}