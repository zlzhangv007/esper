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
package com.espertech.esper.common.internal.type;

import com.espertech.esper.common.client.annotation.Name;
import com.espertech.esper.common.client.type.EPTypeClass;

import java.lang.annotation.Annotation;

public class AnnotationName implements Name {
    public final static EPTypeClass EPTYPE = new EPTypeClass(AnnotationName.class);

    private final String name;

    public AnnotationName(String name) {
        this.name = name;
    }

    public String value() {
        return name;
    }

    public Class<? extends Annotation> annotationType() {
        return Name.class;
    }

    public String toString() {
        return "@Name(\"" + name + "\")";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotationName that = (AnnotationName) o;

        return name.equals(that.name);
    }

    public int hashCode() {
        return name.hashCode();
    }
}
