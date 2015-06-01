package com.simonstuck.vignelli.inspection;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;

import java.io.File;

public class MethodChainingInspectionToolTest extends LightCodeInsightFixtureTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(new MethodChainingInspectionTool());
        myFixture.getModule();
    }


    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new DefaultLightProjectDescriptor() {
            @Override
            public void configureModule(Module module, ModifiableRootModel model, ContentEntry contentEntry) {
                super.configureModule(module, model, contentEntry);
                Sdk jdk = JavaAwareProjectJdkTableImpl.getInstanceEx().getInternalJdk();
                model.setSdk(jdk);
            }
        };
    }

    @Override
    protected String getTestDataPath() {
        return new File("").getAbsolutePath() + "/src/test/resources/";
    }

    public void testZipCodeExamples() throws Exception {
        final PsiFile[] psiFiles = myFixture.configureByFiles(
                "testcode/ex1/ZipCodeExample.java",
                "testcode/ex2/ZipCodeExample.java",
                "testcode/ex3/ZipCodeExample.java",
                "testcode/ex4/ZipCodeExample.java",
                "testcode/ex5/ZipCodeExample.java",
                "testcode/ex6/ZipCodeExample.java"
        );
        for (PsiFile file : psiFiles) {
            myFixture.openFileInEditor(file.getVirtualFile());
            myFixture.checkHighlighting();
        }
    }
}