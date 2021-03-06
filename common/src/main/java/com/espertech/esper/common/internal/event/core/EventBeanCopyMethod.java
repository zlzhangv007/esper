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
package com.espertech.esper.common.internal.event.core;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.type.EPTypeClass;

/**
 * Implementations copy the event object for controlled modification (shallow copy).
 */
public interface EventBeanCopyMethod {
    EPTypeClass EPTYPE = new EPTypeClass(EventBeanCopyMethod.class);

    /**
     * Copy the event bean returning a shallow copy.
     *
     * @param theEvent to copy
     * @return shallow copy
     */
    public EventBean copy(EventBean theEvent);
}
