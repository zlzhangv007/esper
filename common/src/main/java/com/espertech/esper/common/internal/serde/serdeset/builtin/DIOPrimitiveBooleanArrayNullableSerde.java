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

public class DIOPrimitiveBooleanArrayNullableSerde implements DataInputOutputSerde<boolean[]> {
    public final static EPTypeClass EPTYPE = new EPTypeClass(DIOPrimitiveBooleanArrayNullableSerde.class);

    public final static DIOPrimitiveBooleanArrayNullableSerde INSTANCE = new DIOPrimitiveBooleanArrayNullableSerde();

    private DIOPrimitiveBooleanArrayNullableSerde() {
    }

    public void write(boolean[] object, DataOutput output) throws IOException {
        writeInternal(object, output);
    }

    public boolean[] read(DataInput input) throws IOException {
        return readInternal(input);
    }

    public void write(boolean[] object, DataOutput output, byte[] unitKey, EventBeanCollatedWriter writer) throws IOException {
        writeInternal(object, output);
    }

    public boolean[] read(DataInput input, byte[] unitKey) throws IOException {
        return readInternal(input);
    }

    private void writeInternal(boolean[] object, DataOutput output) throws IOException {
        if (object == null) {
            output.writeInt(-1);
            return;
        }
        output.writeInt(object.length);
        for (boolean i : object) {
            output.writeBoolean(i);
        }
    }

    private boolean[] readInternal(DataInput input) throws IOException {
        int len = input.readInt();
        if (len == -1) {
            return null;
        }
        boolean[] array = new boolean[len];
        for (int i = 0; i < len; i++) {
            array[i] = input.readBoolean();
        }
        return array;
    }
}
