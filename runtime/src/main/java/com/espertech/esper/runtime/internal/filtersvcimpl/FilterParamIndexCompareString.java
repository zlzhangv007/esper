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
package com.espertech.esper.runtime.internal.filtersvcimpl;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.internal.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.common.internal.epl.expression.core.ExprFilterSpecLookupable;
import com.espertech.esper.common.internal.filterspec.FilterOperator;
import com.espertech.esper.common.internal.filtersvc.FilterHandle;
import com.espertech.esper.runtime.internal.metrics.instrumentation.InstrumentationHelper;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Index for filter parameter constants for the comparison operators (less, greater, etc).
 * The implementation is based on the SortedMap implementation of TreeMap.
 * The index only accepts String constants. It keeps a lower and upper bounds of all constants in the index
 * for fast range checking, since the assumption is that frequently values fall within a range.
 */
public final class FilterParamIndexCompareString extends FilterParamIndexLookupableBase {
    private final TreeMap<Object, EventEvaluator> constantsMap;
    private final ReadWriteLock constantsMapRWLock;

    public FilterParamIndexCompareString(ExprFilterSpecLookupable lookupable, ReadWriteLock readWriteLock, FilterOperator filterOperator) {
        super(filterOperator, lookupable);

        constantsMap = new TreeMap<Object, EventEvaluator>();
        constantsMapRWLock = readWriteLock;

        if ((filterOperator != FilterOperator.GREATER) &&
                (filterOperator != FilterOperator.GREATER_OR_EQUAL) &&
                (filterOperator != FilterOperator.LESS) &&
                (filterOperator != FilterOperator.LESS_OR_EQUAL)) {
            throw new IllegalArgumentException("Invalid filter operator for index of " + filterOperator);
        }
    }

    public final EventEvaluator get(Object filterConstant) {
        return constantsMap.get(filterConstant);
    }

    public final void put(Object filterConstant, EventEvaluator matcher) {
        constantsMap.put(filterConstant, matcher);
    }

    public final void remove(Object filterConstant) {
        constantsMap.remove(filterConstant);
    }

    public final int sizeExpensive() {
        return constantsMap.size();
    }

    public boolean isEmpty() {
        return constantsMap.isEmpty();
    }

    public final ReadWriteLock getReadWriteLock() {
        return constantsMapRWLock;
    }

    public final void matchEvent(EventBean theEvent, Collection<FilterHandle> matches, ExprEvaluatorContext ctx) {
        Object propertyValue = lookupable.getEval().eval(theEvent, ctx);
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qFilterReverseIndex(this, propertyValue);
        }

        if (propertyValue == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aFilterReverseIndex(false);
            }
            return;
        }

        FilterOperator filterOperator = this.getFilterOperator();

        // Look up in table
        constantsMapRWLock.readLock().lock();
        try {

            // Get the head or tail end of the map depending on comparison type
            Map<Object, EventEvaluator> subMap;

            if ((filterOperator == FilterOperator.GREATER) ||
                    (filterOperator == FilterOperator.GREATER_OR_EQUAL)) {
                // At the head of the map are those with a lower numeric constants
                subMap = constantsMap.headMap(propertyValue);
            } else {
                subMap = constantsMap.tailMap(propertyValue);
            }

            // All entries in the subMap are elgibile, with an exception
            EventEvaluator exactEquals = null;
            if (filterOperator == FilterOperator.LESS) {
                exactEquals = constantsMap.get(propertyValue);
            }

            for (EventEvaluator matcher : subMap.values()) {
                // For the LESS comparison type we ignore the exactly equal case
                // The subMap is sorted ascending, thus the exactly equals case is the first
                if (exactEquals != null) {
                    exactEquals = null;
                    continue;
                }

                matcher.matchEvent(theEvent, matches, ctx);
            }

            if (filterOperator == FilterOperator.GREATER_OR_EQUAL) {
                EventEvaluator matcher = constantsMap.get(propertyValue);
                if (matcher != null) {
                    matcher.matchEvent(theEvent, matches, ctx);
                }
            }
        } finally {
            constantsMapRWLock.readLock().unlock();
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aFilterReverseIndex(null);
        }
    }

    public void getTraverseStatement(EventTypeIndexTraverse traverse, Set<Integer> statementIds, ArrayDeque<FilterItem> evaluatorStack) {
        for (Map.Entry<Object, EventEvaluator> entry : constantsMap.entrySet()) {
            evaluatorStack.add(new FilterItem(lookupable.getExpression(), getFilterOperator(), entry.getKey(), this));
            entry.getValue().getTraverseStatement(traverse, statementIds, evaluatorStack);
            evaluatorStack.removeLast();
        }
    }
}
