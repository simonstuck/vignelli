package com.simonstuck.vignelli.inspection.identification;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.testFramework.LightIdeaTestCase;
import com.simonstuck.vignelli.utils.IOUtils;
import org.junit.Before;

import java.io.IOException;

public class MethodChainIdentificationEngineTest extends LightIdeaTestCase {

    private MethodChainIdentificationEngine engine;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        engine = new MethodChainIdentificationEngine();
    }

    public void testIdentifyMethodChainsReturnsIdentificationsInstance() throws Exception {
        PsiMethod method = getEmptyMethod();
        IdentificationCollection<MethodChainIdentification> ids = engine.identifyMethodChains(method);
        assertNotNull(ids);
    }

    public void testReturnsEmptyIdentificationsForEmptyMethod() throws Exception {
        assertEquals(0, engine.identifyMethodChains(getEmptyMethod()).size());
    }

    public void testReturnsEmptyIdentificationsForOneCallMethod() throws Exception {
        String oneCallMethod = IOUtils.readFile("src/test/resources/psi/method/oneCallMethod.txt");
        PsiMethod method = getJavaFacade().getElementFactory().createMethodFromText(oneCallMethod, null);
        assertEquals(0, engine.identifyMethodChains(method).size());
    }

    public void testReturnsOneIdentificationForTwoCallMethod() throws Exception {
        String twoCallMethodClass = IOUtils.readFile("src/test/resources/psi/class/methodCallChainMethodClass.txt");
        PsiClass clazz = getJavaFacade().getElementFactory().createClassFromText(twoCallMethodClass, null);
        PsiMethod method = clazz.getMethods()[0];
        assertEquals(1, engine.identifyMethodChains(method).size());
    }

    public void testDoesNotIdentifyBuilderCalls() throws Exception {
        String builderCallMethodClass = IOUtils.readFile("src/test/resources/psi/class/builderMethodCallChains.txt");
        PsiClass clazz = getJavaFacade().getElementFactory().createClassFromText(builderCallMethodClass, null);
        PsiMethod method = clazz.getMethods()[0];
        assertEquals(0, engine.identifyMethodChains(method).size());
    }

    public void testIdentifiesChainsOfMultipleBuilders() throws Exception {
        // a.attrA().attrB().bb().attrA().attrB().cc();
        //   a1      .a2    .b3  .b4     .b5     .c()
        // is collapsed chain of a.b.c
        String builderCallsNestedMethodClass = IOUtils.readFile("src/test/resources/psi/class/twoBuilderTypesCallChain.txt");
        PsiClass clazz = getJavaFacade().getElementFactory().createClassFromText(builderCallsNestedMethodClass, null);
        PsiMethod method = clazz.getMethods()[0];
        assertEquals(1, engine.identifyMethodChains(method).size());
    }

    public void testIdentifiesOneMethodChainForZipCodeInLabelFiller() throws Exception {
        String fillZipCodeLabelClass = IOUtils.readFile("src/test/resources/psi/class/fillZipCodeLabel.txt");
        PsiClass clazz = getJavaFacade().getElementFactory().createClassFromText(fillZipCodeLabelClass, null);
        assertEquals(1, engine.identifyMethodChains(clazz).size());
    }

    private PsiMethod getEmptyMethod() throws IOException {
        String emptyMethod = IOUtils.readFile("src/test/resources/psi/method/emptyMethod.txt");
        return getJavaFacade().getElementFactory().createMethodFromText(emptyMethod, null);
    }
}