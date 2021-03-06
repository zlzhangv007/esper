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
package com.espertech.esper.regressionlib.support.extend.aggmultifunc;

import com.espertech.esper.common.client.hook.aggmultifunc.*;
import com.espertech.esper.common.client.hook.forgeinject.InjectionStrategyClassNewInstance;
import com.espertech.esper.common.internal.epl.expression.core.ExprNode;
import com.espertech.esper.common.internal.rettype.EPChainableType;
import com.espertech.esper.common.internal.rettype.EPChainableTypeHelper;

public class SupportReferenceCountedMapRCMFunctionHandler implements AggregationMultiFunctionHandler {
    private final AggregationMultiFunctionStateKey sharedStateKey;
    private final ExprNode[] parameterExpressions;

    public SupportReferenceCountedMapRCMFunctionHandler(AggregationMultiFunctionStateKey sharedStateKey, ExprNode[] parameterExpressions) {
        this.sharedStateKey = sharedStateKey;
        this.parameterExpressions = parameterExpressions;
    }

    public EPChainableType getReturnType() {
        return EPChainableTypeHelper.nullValue();
    }

    public AggregationMultiFunctionStateKey getAggregationStateUniqueKey() {
        return sharedStateKey;
    }

    public AggregationMultiFunctionStateMode getStateMode() {
        return new AggregationMultiFunctionStateModeManaged().setInjectionStrategyAggregationStateFactory(new InjectionStrategyClassNewInstance(SupportReferenceCountedMapStateFactory.EPTYPE));
    }

    public AggregationMultiFunctionAccessorMode getAccessorMode() {
        return new AggregationMultiFunctionAccessorModeManaged().setInjectionStrategyAggregationAccessorFactory(new InjectionStrategyClassNewInstance(SupportReferenceCountedMapAccessorFactory.EPTYPE));
    }

    public AggregationMultiFunctionAgentMode getAgentMode() {
        return new AggregationMultiFunctionAgentModeManaged().setInjectionStrategyAggregationAgentFactory(
            new InjectionStrategyClassNewInstance(SupportReferenceCountedMapAgentFactory.EPTYPE).addExpression("eval", parameterExpressions[0]));
    }

    public AggregationMultiFunctionAggregationMethodMode getAggregationMethodMode(AggregationMultiFunctionAggregationMethodContext ctx) {
        throw new UnsupportedOperationException("This agregation function is not designed for use with table columns");
    }
}
