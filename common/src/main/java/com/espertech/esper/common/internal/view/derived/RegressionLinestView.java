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
package com.espertech.esper.common.internal.view.derived;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.EventType;
import com.espertech.esper.common.client.type.EPTypePremade;
import com.espertech.esper.common.internal.context.util.AgentInstanceContext;
import com.espertech.esper.common.internal.epl.expression.core.ExprEvaluator;
import com.espertech.esper.common.internal.event.core.EventBeanTypedEventFactory;
import com.espertech.esper.common.internal.view.core.ViewFactory;
import com.espertech.esper.common.internal.view.core.ViewForgeEnv;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A view that calculates regression on two fields. The view uses internally a {@link BaseStatisticsBean}
 * instance for the calculations, it also returns this bean as the result.
 * This class accepts most of its behaviour from its parent, {@link BaseBivariateStatisticsView}. It adds
 * the usage of the regression bean and the appropriate schema.
 */
public class RegressionLinestView extends BaseBivariateStatisticsView {
    public RegressionLinestView(ViewFactory viewFactory, AgentInstanceContext agentInstanceContext, ExprEvaluator xEval, ExprEvaluator yEval, EventType eventType, StatViewAdditionalPropsEval additionalProps) {
        super(viewFactory, agentInstanceContext, xEval, yEval, eventType, additionalProps);
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventBean populateMap(BaseStatisticsBean baseStatisticsBean,
                                 EventBeanTypedEventFactory eventAdapterService,
                                 EventType eventType,
                                 StatViewAdditionalPropsEval additionalProps,
                                 Object[] decoration) {
        return doPopulateMap(baseStatisticsBean, eventAdapterService, eventType, additionalProps, decoration);
    }

    public static EventBean doPopulateMap(BaseStatisticsBean baseStatisticsBean,
                                          EventBeanTypedEventFactory eventAdapterService,
                                          EventType eventType,
                                          StatViewAdditionalPropsEval additionalProps,
                                          Object[] decoration) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(ViewFieldEnum.REGRESSION__SLOPE.getName(), baseStatisticsBean.getSlope());
        result.put(ViewFieldEnum.REGRESSION__YINTERCEPT.getName(), baseStatisticsBean.getYIntercept());
        result.put(ViewFieldEnum.REGRESSION__XAVERAGE.getName(), baseStatisticsBean.getXAverage());
        result.put(ViewFieldEnum.REGRESSION__XSTANDARDDEVIATIONPOP.getName(), baseStatisticsBean.getXStandardDeviationPop());
        result.put(ViewFieldEnum.REGRESSION__XSTANDARDDEVIATIONSAMPLE.getName(), baseStatisticsBean.getXStandardDeviationSample());
        result.put(ViewFieldEnum.REGRESSION__XSUM.getName(), baseStatisticsBean.getXSum());
        result.put(ViewFieldEnum.REGRESSION__XVARIANCE.getName(), baseStatisticsBean.getXVariance());
        result.put(ViewFieldEnum.REGRESSION__YAVERAGE.getName(), baseStatisticsBean.getYAverage());
        result.put(ViewFieldEnum.REGRESSION__YSTANDARDDEVIATIONPOP.getName(), baseStatisticsBean.getYStandardDeviationPop());
        result.put(ViewFieldEnum.REGRESSION__YSTANDARDDEVIATIONSAMPLE.getName(), baseStatisticsBean.getYStandardDeviationSample());
        result.put(ViewFieldEnum.REGRESSION__YSUM.getName(), baseStatisticsBean.getYSum());
        result.put(ViewFieldEnum.REGRESSION__YVARIANCE.getName(), baseStatisticsBean.getYVariance());
        result.put(ViewFieldEnum.REGRESSION__DATAPOINTS.getName(), baseStatisticsBean.getDataPoints());
        result.put(ViewFieldEnum.REGRESSION__N.getName(), baseStatisticsBean.getN());
        result.put(ViewFieldEnum.REGRESSION__SUMX.getName(), baseStatisticsBean.getSumX());
        result.put(ViewFieldEnum.REGRESSION__SUMXSQ.getName(), baseStatisticsBean.getSumXSq());
        result.put(ViewFieldEnum.REGRESSION__SUMXY.getName(), baseStatisticsBean.getSumXY());
        result.put(ViewFieldEnum.REGRESSION__SUMY.getName(), baseStatisticsBean.getSumY());
        result.put(ViewFieldEnum.REGRESSION__SUMYSQ.getName(), baseStatisticsBean.getSumYSq());
        if (additionalProps != null) {
            additionalProps.addProperties(result, decoration);
        }
        return eventAdapterService.adapterForTypedMap(result, eventType);
    }

    protected static EventType createEventType(StatViewAdditionalPropsForge additionalProps, ViewForgeEnv env) {
        LinkedHashMap<String, Object> eventTypeMap = new LinkedHashMap<String, Object>();
        eventTypeMap.put(ViewFieldEnum.REGRESSION__SLOPE.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__YINTERCEPT.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__XAVERAGE.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__XSTANDARDDEVIATIONPOP.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__XSTANDARDDEVIATIONSAMPLE.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__XSUM.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__XVARIANCE.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__YAVERAGE.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__YSTANDARDDEVIATIONPOP.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__YSTANDARDDEVIATIONSAMPLE.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__YSUM.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__YVARIANCE.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__DATAPOINTS.getName(), EPTypePremade.LONGBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__N.getName(), EPTypePremade.LONGBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__SUMX.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__SUMXSQ.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__SUMXY.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__SUMY.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        eventTypeMap.put(ViewFieldEnum.REGRESSION__SUMYSQ.getName(), EPTypePremade.DOUBLEBOXED.getEPType());
        StatViewAdditionalPropsForge.addCheckDupProperties(eventTypeMap, additionalProps,
                ViewFieldEnum.REGRESSION__SLOPE, ViewFieldEnum.REGRESSION__YINTERCEPT);
        return DerivedViewTypeUtil.newType("regview", eventTypeMap, env);
    }
}
