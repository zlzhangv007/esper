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
package com.espertech.esper.common.internal.compile.stage1.spec;

import com.espertech.esper.common.internal.epl.expression.core.ExprNode;

/**
 * Specification for the merge statement insert/update/delete-part.
 */
public abstract class OnTriggerMergeAction {
    private ExprNode optionalWhereClause;

    protected OnTriggerMergeAction(ExprNode optionalWhereClause) {
        this.optionalWhereClause = optionalWhereClause;
    }

    public ExprNode getOptionalWhereClause() {
        return optionalWhereClause;
    }

    public void setOptionalWhereClause(ExprNode optionalWhereClause) {
        this.optionalWhereClause = optionalWhereClause;
    }
}

