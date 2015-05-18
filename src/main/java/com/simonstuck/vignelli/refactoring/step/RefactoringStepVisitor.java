package com.simonstuck.vignelli.refactoring.step;

import com.simonstuck.vignelli.refactoring.impl.IntroduceParametersForCriticalCallsImpl;
import com.simonstuck.vignelli.refactoring.impl.IntroduceParametersForMembersRefactoringImpl;
import com.simonstuck.vignelli.refactoring.impl.TrainWreckExpressionRefactoringImpl;
import com.simonstuck.vignelli.refactoring.step.impl.ConvertToConstructorAssignedFieldRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.ExtractInterfaceRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.ExtractMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.InlineMethodCallRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.InlineMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.InlineVariableRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.IntroduceParameterRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.MoveMethodRefactoringStep;
import com.simonstuck.vignelli.refactoring.step.impl.RenameMethodRefactoringStep;

public interface RefactoringStepVisitor {
    /**
     * Visit the generic refactoring step.
     * @param refactoringStep The refactoring step to visit.
     */
    void visitElement(RefactoringStep refactoringStep);

    void visitElement(ExtractMethodRefactoringStep extractMethodRefactoringStep);

    void visitElement(TrainWreckExpressionRefactoringImpl trainWreckExpressionRefactoring);

    void visitElement(IntroduceParametersForMembersRefactoringImpl introduceParametersForMembersRefactoring);

    void visitElement(IntroduceParameterRefactoringStep introduceParameterRefactoringStep);

    void visitElement(IntroduceParametersForCriticalCallsImpl introduceParametersForCriticalCalls);


    void visitElement(MoveMethodRefactoringStep moveMethodRefactoringStep);

    void visitElement(InlineMethodCallRefactoringStep inlineMethodCallRefactoringStep);


    void visitElement(ExtractInterfaceRefactoringStep extractInterfaceRefactoringStep);


    void visitElement(ConvertToConstructorAssignedFieldRefactoringStep convertToConstructorAssignedFieldRefactoringStep);

    void visitElement(RenameMethodRefactoringStep renameMethodRefactoringStep);

    void visitElement(InlineVariableRefactoringStep inlineVariableRefactoringStep);

    void visitElement(InlineMethodRefactoringStep inlineMethodRefactoringStep);
}
