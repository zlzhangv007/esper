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
package com.espertech.esper.regressionlib.suite.client.extension;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.compiler.ConfigurationCompilerPlugInSingleRowFunction;
import com.espertech.esper.common.client.fireandforget.EPFireAndForgetQueryResult;
import com.espertech.esper.common.client.hook.singlerowfunc.ExtensionSingleRowFunction;
import com.espertech.esper.common.client.scopetest.EPAssertionUtil;
import com.espertech.esper.common.internal.support.SupportBean;
import com.espertech.esper.regressionlib.framework.RegressionEnvironment;
import com.espertech.esper.regressionlib.framework.RegressionExecution;
import com.espertech.esper.regressionlib.framework.RegressionPath;
import com.espertech.esper.runtime.client.EPDeploymentDependencyConsumed;
import com.espertech.esper.runtime.client.EPDeploymentDependencyProvided;
import com.espertech.esper.runtime.client.util.EPObjectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.espertech.esper.common.client.scopetest.EPAssertionUtil.assertEqualsAnyOrder;
import static com.espertech.esper.regressionlib.framework.SupportMessageAssertUtil.tryInvalidCompile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientExtendUDFInlinedClass {
    public static Collection<RegressionExecution> executions() {
        ArrayList<RegressionExecution> execs = new ArrayList<>();
        execs.add(new ClientExtendUDFInlinedLocalClass(false));
        execs.add(new ClientExtendUDFInlinedLocalClass(true));
        execs.add(new ClientExtendUDFInlinedInvalid());
        execs.add(new ClientExtendUDFInlinedFAF());
        execs.add(new ClientExtendUDFCreateInlinedSameModule());
        execs.add(new ClientExtendUDFCreateInlinedOtherModule());
        execs.add(new ClientExtendUDFInlinedWOptions());
        execs.add(new ClientExtendUDFOverloaded());
        return execs;
    }

    // Note: Janino 3.1.0 does not support @Repeatable and does not support @Annos({@Anno, @Anno})
    private static class ClientExtendUDFOverloaded implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') inlined_class \"\"\"\n" +
                "  import com.espertech.esper.common.client.hook.singlerowfunc.*;\n" +
                "  @ExtensionSingleRowFunction(name=\"multiply\", methodName=\"multiplyIt\")\n" +
                "  public class MultiplyHelper {\n" +
                "    public static int multiplyIt(int a, int b) {\n" +
                "      return a*b;\n" +
                "    }\n" +
                "    public static int multiplyIt(int a, int b, int c) {\n" +
                "      return a*b*c;\n" +
                "    }\n" +
                "  }\n" +
                "\"\"\" " +
                "select multiply(intPrimitive,intPrimitive) as c0, multiply(intPrimitive,intPrimitive,intPrimitive) as c1 \n" +
                " from SupportBean";
            env.compileDeploy(epl).addListener("s0");

            env.sendEventBean(new SupportBean("E1", 4));
            EPAssertionUtil.assertProps(env.listener("s0").assertOneGetNewAndReset(), "c0,c1".split(","), new Object[]{16, 64});

            env.undeployAll();
        }
    }

    private static class ClientExtendUDFInlinedWOptions implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') inlined_class \"\"\"\n" +
                "  @" + ExtensionSingleRowFunction.class.getName() + "(" +
                "      name=\"multiply\", methodName=\"multiplyIfPositive\",\n" +
                "      valueCache=" + ConfigurationCompilerPlugInSingleRowFunction.class.getName() + ".ValueCache.DISABLED,\n" +
                "      filterOptimizable=" + ConfigurationCompilerPlugInSingleRowFunction.class.getName() + ".FilterOptimizable.DISABLED,\n" +
                "      rethrowExceptions=false,\n" +
                "      eventTypeName=\"abc\"\n" +
                "      )\n" +
                "  public class MultiplyHelper {\n" +
                "    public static int multiplyIfPositive(int a, int b) {\n" +
                "      return a*b;\n" +
                "    }\n" +
                "  }\n" +
                "\"\"\" " +
                "select multiply(intPrimitive,intPrimitive) as c0 from SupportBean";
            env.compileDeploy(epl).addListener("s0");

            sendAssertIntMultiply(env, 5, 25);

            env.undeployAll();
        }
    }

    private static class ClientExtendUDFCreateInlinedOtherModule implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String eplCreateInlined = "@name('clazz') @public create inlined_class \"\"\"\n" +
                "  @" + ExtensionSingleRowFunction.class.getName() + "(name=\"multiply\", methodName=\"multiply\")\n" +
                "  public class MultiplyHelper {\n" +
                "    public static int multiply(int a, int b) {\n" +
                "      %BEHAVIOR%\n" +
                "    }\n" +
                "  }\n" +
                "\"\"\"\n;";
            RegressionPath path = new RegressionPath();
            env.compile(eplCreateInlined.replace("%BEHAVIOR%", "return -1;"), path);

            String eplSelect = "@name('s0') select multiply(intPrimitive,intPrimitive) as c0 from SupportBean";
            EPCompiled compiledSelect = env.compile(eplSelect, path);

            env.compileDeploy(eplCreateInlined.replace("%BEHAVIOR%", "return a*b;"));
            env.deploy(compiledSelect).addListener("s0");

            sendAssertIntMultiply(env, 3, 9);

            env.milestone(0);

            sendAssertIntMultiply(env, 13, 13 * 13);

            // assert dependencies
            String deploymentIdSelect = env.deploymentId("s0");
            String deploymentIdClazz = env.deploymentId("clazz");
            EPDeploymentDependencyConsumed consumed = env.runtime().getDeploymentService().getDeploymentDependenciesConsumed(deploymentIdSelect);
            assertEqualsAnyOrder(new EPDeploymentDependencyConsumed.Item[]{new EPDeploymentDependencyConsumed.Item(deploymentIdClazz, EPObjectType.CLASSPROVIDED, "MultiplyHelper")}, consumed.getDependencies().toArray());
            EPDeploymentDependencyProvided provided = env.runtime().getDeploymentService().getDeploymentDependenciesProvided(deploymentIdClazz);
            assertEqualsAnyOrder(new EPDeploymentDependencyProvided.Item[]{new EPDeploymentDependencyProvided.Item(EPObjectType.CLASSPROVIDED, "MultiplyHelper", Collections.singleton(deploymentIdSelect))}, provided.getDependencies().toArray());

            env.undeployAll();
        }
    }

    private static class ClientExtendUDFCreateInlinedSameModule implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "create inlined_class \"\"\"\n" +
                "  @" + ExtensionSingleRowFunction.class.getName() + "(name=\"multiply\", methodName=\"multiply\")\n" +
                "  public class MultiplyHelper {\n" +
                "    public static int multiply(int a, int b) {\n" +
                "      return a*b;\n" +
                "    }\n" +
                "  }\n" +
                "\"\"\"\n;" +
                "@name('s0') select multiply(intPrimitive,intPrimitive) as c0 from SupportBean;\n";
            env.compileDeploy(epl).addListener("s0");

            sendAssertIntMultiply(env, 5, 25);

            env.milestone(0);

            sendAssertIntMultiply(env, 6, 36);

            String deploymentId = env.deploymentId("s0");
            EPDeploymentDependencyConsumed consumed = env.runtime().getDeploymentService().getDeploymentDependenciesConsumed(deploymentId);
            assertTrue(consumed.getDependencies().isEmpty());
            EPDeploymentDependencyProvided provided = env.runtime().getDeploymentService().getDeploymentDependenciesProvided(deploymentId);
            assertTrue(provided.getDependencies().isEmpty());

            env.undeployAll();
        }
    }

    private static class ClientExtendUDFInlinedFAF implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            RegressionPath path = new RegressionPath();
            String eplWindow = "create window MyWindow#keepall as (theString string);\n" +
                "on SupportBean merge MyWindow insert select theString;\n";
            env.compileDeploy(eplWindow, path);

            env.sendEventBean(new SupportBean("E1", 1));

            String eplFAF = "inlined_class \"\"\"\n" +
                "  @" + ExtensionSingleRowFunction.class.getName() + "(name=\"appendDelimiters\", methodName=\"doIt\")\n" +
                "  public class MyClass {\n" +
                "    public static String doIt(String parameter) {\n" +
                "      return '>' + parameter + '<';\n" +
                "    }\n" +
                "  }\n" +
                "\"\"\"\n select appendDelimiters(theString) as c0 from MyWindow";
            EPFireAndForgetQueryResult result = env.compileExecuteFAF(eplFAF, path);
            assertEquals(">E1<", result.getArray()[0].get("c0"));

            env.milestone(0);

            result = env.compileExecuteFAF(eplFAF, path);
            assertEquals(">E1<", result.getArray()[0].get("c0"));

            env.undeployAll();
        }
    }

    private static class ClientExtendUDFInlinedInvalid implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') inlined_class \"\"\"\n" +
                "  @" + ExtensionSingleRowFunction.class.getName() + "(name=\"multiply\", methodName=\"multiply\")\n" +
                "  public class MultiplyHelperOne {\n" +
                "    public static int multiply(int a, int b) { return 0; }\n" +
                "  }\n" +
                "  @" + ExtensionSingleRowFunction.class.getName() + "(name=\"multiply\", methodName=\"multiply\")\n" +
                "  public class MultiplyHelperTwo {\n" +
                "    public static int multiply(int a, int b, int c) { return 0; }\n" +
                "  }\n" +
                "\"\"\" " +
                "select multiply(intPrimitive,intPrimitive) as c0 from SupportBean";
            tryInvalidCompile(env, epl,
                "The plug-in single-row function 'multiply' occurs multiple times");
        }
    }

    private static class ClientExtendUDFInlinedLocalClass implements RegressionExecution {
        private final boolean soda;

        public ClientExtendUDFInlinedLocalClass(boolean soda) {
            this.soda = soda;
        }

        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') inlined_class \"\"\"\n" +
                "  @" + ExtensionSingleRowFunction.class.getName() + "(name=\"multiply\", methodName=\"multiply\")\n" +
                "  public class MultiplyHelper {\n" +
                "    public static int multiply(int a, int b) {\n" +
                "      return a*b;\n" +
                "    }\n" +
                "  }\n" +
                "\"\"\" " +
                "select multiply(intPrimitive,intPrimitive) as c0 from SupportBean";
            env.compileDeploy(soda, epl).addListener("s0");

            sendAssertIntMultiply(env, 5, 25);

            env.milestone(0);

            sendAssertIntMultiply(env, 6, 36);

            env.undeployAll();
        }
    }

    private static void sendAssertIntMultiply(RegressionEnvironment env, int intPrimitive, int expected) {
        env.sendEventBean(new SupportBean("E1", intPrimitive));
        assertEquals(expected, env.listener("s0").assertOneGetNewAndReset().get("c0"));
    }
}