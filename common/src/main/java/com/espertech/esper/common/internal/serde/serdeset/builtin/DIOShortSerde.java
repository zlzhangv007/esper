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
package com.espertech.esper.common.internal.serde.serdeset.builtin;


import com.espertech.esper.common.client.serde.DataInputOutputSerde;
import com.espertech.esper.common.client.serde.EventBeanCollatedWriter;
import com.espertech.esper.common.client.type.EPTypeClass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Binding for non-null short values.
 */
public class DIOShortSerde implements DataInputOutputSerde<Short> {
    public final static EPTypeClass EPTYPE = new EPTypeClass(DIOShortSerde.class);

    public final static DIOShortSerde INSTANCE = new DIOShortSerde();

    private DIOShortSerde() {
    }

    public void write(Short object, DataOutput output, byte[] pageFullKey, EventBeanCollatedWriter writer) throws IOException {
        output.writeShort(object);
    }

    public void write(Short object, DataOutput stream) throws IOException {
        stream.writeShort(object);
    }

    public Short read(DataInput input, byte[] resourceKey) throws IOException {
        return input.readShort();
    }

    public Short read(DataInput input) throws IOException {
        return input.readShort();
    }
}
