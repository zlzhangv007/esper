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
package com.espertech.esper.common.internal.epl.pattern.and;

import com.espertech.esper.common.client.annotation.AppliesTo;
import com.espertech.esper.common.client.type.EPTypeClass;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.compile.stage2.FilterSpecCompiled;
import com.espertech.esper.common.internal.context.aifactory.core.SAIFFInitializeSymbol;
import com.espertech.esper.common.internal.epl.pattern.core.EvalFactoryNode;
import com.espertech.esper.common.internal.epl.pattern.core.EvalForgeNodeBase;
import com.espertech.esper.common.internal.epl.pattern.core.PatternExpressionPrecedenceEnum;
import com.espertech.esper.common.internal.epl.pattern.core.PatternExpressionUtil;
import com.espertech.esper.common.internal.schedule.ScheduleHandleCallbackProvider;

import java.io.StringWriter;
import java.util.List;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.*;

/**
 * This class represents an 'and' operator in the evaluation tree representing an event expressions.
 */
public class EvalAndForgeNode extends EvalForgeNodeBase {

    public EvalAndForgeNode(boolean attachPatternText) {
        super(attachPatternText);
    }

    public final String toString() {
        return "EvalAndFactoryNode children=" + this.getChildNodes().size();
    }

    public boolean isFilterChildNonQuitting() {
        return false;
    }

    public boolean isStateful() {
        return true;
    }

    public void toPrecedenceFreeEPL(StringWriter writer) {
        PatternExpressionUtil.toPrecedenceFreeEPL(writer, "and", getChildNodes(), getPrecedence());
    }

    public PatternExpressionPrecedenceEnum getPrecedence() {
        return PatternExpressionPrecedenceEnum.AND;
    }

    protected EPTypeClass typeOfFactory() {
        return EvalAndFactoryNode.EPTYPE;
    }

    protected String nameOfFactory() {
        return "and";
    }

    protected void inlineCodegen(CodegenMethod method, SAIFFInitializeSymbol symbols, CodegenClassScope classScope) {
        method.getBlock().declareVar(EvalFactoryNode.EPTYPEARRAY, "children", newArrayByLength(EvalFactoryNode.EPTYPE, constant(getChildNodes().size())));
        for (int i = 0; i < getChildNodes().size(); i++) {
            method.getBlock().assignArrayElement(ref("children"), constant(i), localMethod(getChildNodes().get(i).makeCodegen(method, symbols, classScope)));
        }
        method.getBlock().exprDotMethod(ref("node"), "setChildren", ref("children"));
    }

    public void collectSelfFilterAndSchedule(List<FilterSpecCompiled> filters, List<ScheduleHandleCallbackProvider> schedules) {
    }

    public AppliesTo appliesTo() {
        return AppliesTo.PATTERN_AND;
    }
}
