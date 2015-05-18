package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.InspectionToolProvider;

public class VignelliInspectionToolProvider implements InspectionToolProvider {
    @Override
    public Class[] getInspectionClasses() {
        return new Class[] {
            ComplexMethodInspectionTool.class,
            MethodChainingInspectionTool.class,
            DirectSingletonUseInspectionTool.class,
            InternalGetterUseInspectionTool.class
        };
    }
}
