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
package com.espertech.esper.regressionlib.support.extend.aggfunc;

import com.espertech.esper.common.client.hook.aggfunc.AggregationFunctionForge;
import com.espertech.esper.common.client.hook.aggfunc.AggregationFunctionMode;
import com.espertech.esper.common.client.hook.aggfunc.AggregationFunctionModeManaged;
import com.espertech.esper.common.client.hook.aggfunc.AggregationFunctionValidationContext;
import com.espertech.esper.common.client.hook.forgeinject.InjectionStrategyClassNewInstance;
import com.espertech.esper.common.client.type.EPType;
import com.espertech.esper.common.client.type.EPTypeClass;
import com.espertech.esper.common.client.type.EPTypePremade;
import com.espertech.esper.common.internal.epl.expression.core.ExprValidationException;
import com.espertech.esper.common.internal.support.SupportBean;

import static com.espertech.esper.common.internal.util.JavaClassHelper.isType;
import static com.espertech.esper.common.internal.util.JavaClassHelper.isTypeString;

public class SupportConcatWManagedAggregationFunctionForge implements AggregationFunctionForge {
    public void setFunctionName(String functionName) {
    }

    public void validate(AggregationFunctionValidationContext validationContext) throws ExprValidationException {
        EPType paramType = validationContext.getParameterTypes()[0];
        if (!isTypeString(paramType) && !isType(paramType, SupportBean.class)) {
            throw new ExprValidationException("Invalid parameter type '" + paramType + "'");
        }
    }

    public EPTypeClass getValueType() {
        return EPTypePremade.STRING.getEPType();
    }

    public AggregationFunctionMode getAggregationFunctionMode() {
        AggregationFunctionModeManaged mode = new AggregationFunctionModeManaged();
        mode.setHasHA(true);
        mode.setSerde(SupportConcatWManagedAggregationFunctionSerde.class);
        mode.setInjectionStrategyAggregationFunctionFactory(new InjectionStrategyClassNewInstance(SupportConcatWManagedAggregationFunctionFactory.class.getName()));
        return mode;
    }
}
