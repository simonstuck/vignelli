package com.simonstuck.vignelli.inspection.identification;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.testFramework.LightIdeaTestCase;
import com.simonstuck.vignelli.inspection.identification.engine.impl.DirectSingletonUseIdentificationEngine;
import com.simonstuck.vignelli.inspection.identification.impl.DirectSingletonUseIdentification;
import com.simonstuck.vignelli.testutils.IOUtils;

import org.junit.Before;

import java.util.Collection;

public class DirectSingletonUseIdentificationEngineTest extends LightIdeaTestCase {

    private DirectSingletonUseIdentificationEngine engine;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        engine = new DirectSingletonUseIdentificationEngine();
    }

    public void testReturnNoStaticCallsForEmptyMethod() throws Exception {
        String emptyMethod = IOUtils.readFile("src/test/resources/psi/method/emptyMethod.txt");
        PsiMethod method = getJavaFacade().getElementFactory().createMethodFromText(emptyMethod, null);
        assertEmpty(engine.process(method));
    }

    public void testReturnNoStaticCallsForMethodWithNoStaticCalls() throws Exception {
        String methodWithoutStaticCalls = IOUtils.readFile("src/test/resources/psi/method/methodWithoutStaticCalls.txt");
        PsiMethod method = getJavaFacade().getElementFactory().createMethodFromText(methodWithoutStaticCalls, null);
        assertEmpty(engine.process(method));
    }

    public void testReturnOneStaticGetInstanceCallWhenMethodContainsIt() throws Exception {
        String classWithStaticMethod = IOUtils.readFile("src/test/resources/psi/class/classWithStaticMethod.txt");
        PsiClass clazz = getJavaFacade().getElementFactory().createClassFromText(classWithStaticMethod, null);
        PsiMethod method = clazz.getMethods()[0];
        Collection<DirectSingletonUseIdentification> instanceCalls = engine.process(method);
        assertEquals(1, instanceCalls.size());
    }

    public void testReturnNoStaticCallsForMethodWithStaticCallThatIsNotCalledGetInstance() throws Exception {
        String classWithStaticMethod = IOUtils.readFile("src/test/resources/psi/class/classWithStaticNonGetInstanceMethod.txt");
        PsiClass clazz = getJavaFacade().getElementFactory().createClassFromText(classWithStaticMethod, null);
        PsiMethod method = clazz.getMethods()[0];
        assertEmpty(engine.process(method));
    }
}