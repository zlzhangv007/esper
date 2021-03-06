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
package com.espertech.esper.common.internal.epl.datetime.interval.deltaexpr;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.type.EPTypePremade;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethodScope;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;
import com.espertech.esper.common.internal.epl.datetime.interval.IntervalDeltaExprEvaluator;
import com.espertech.esper.common.internal.epl.datetime.interval.IntervalDeltaExprForge;
import com.espertech.esper.common.internal.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.common.internal.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.common.internal.epl.expression.time.abacus.TimeAbacus;
import com.espertech.esper.common.internal.epl.expression.time.node.ExprTimePeriod;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.ref;

public class IntervalDeltaExprTimePeriodNonConstForge implements IntervalDeltaExprForge, IntervalDeltaExprEvaluator {

    private final ExprTimePeriod timePeriod;
    private final TimeAbacus timeAbacus;

    public IntervalDeltaExprTimePeriodNonConstForge(ExprTimePeriod timePeriod, TimeAbacus timeAbacus) {
        this.timePeriod = timePeriod;
        this.timeAbacus = timeAbacus;
    }

    public IntervalDeltaExprEvaluator makeEvaluator() {
        return this;
    }

    public long evaluate(long reference, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        double sec = timePeriod.evaluateAsSeconds(eventsPerStream, isNewData, context);
        return timeAbacus.deltaForSecondsDouble(sec);
    }

    public CodegenExpression codegen(CodegenExpression reference, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethod methodNode = codegenMethodScope.makeChild(EPTypePremade.LONGPRIMITIVE.getEPType(), IntervalDeltaExprTimePeriodNonConstForge.class, codegenClassScope).addParam(EPTypePremade.LONGPRIMITIVE.getEPType(), "reference");

        methodNode.getBlock().declareVar(EPTypePremade.DOUBLEPRIMITIVE.getEPType(), "sec", timePeriod.evaluateAsSecondsCodegen(methodNode, exprSymbol, codegenClassScope))
                .methodReturn(timeAbacus.deltaForSecondsDoubleCodegen(ref("sec"), codegenClassScope));
        return localMethod(methodNode, reference);
    }
}
