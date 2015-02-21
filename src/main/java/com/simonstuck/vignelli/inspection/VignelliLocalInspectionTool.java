package com.simonstuck.vignelli.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.util.messages.Topic;

public class VignelliLocalInspectionTool extends BaseJavaLocalInspectionTool {
    public static Topic<ProblemIdentificationCollectionListener> INSPECTION_IDENTIFICATION_TOPIC = Topic.create("Design Problem",ProblemIdentificationCollectionListener.class);
}
