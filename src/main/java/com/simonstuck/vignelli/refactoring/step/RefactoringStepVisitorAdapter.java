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

/**
 * This adapter contains empty default implementations for all visit methods.
 * For practical use, override the necessary methods only.
 */
public class RefactoringStepVisitorAdapter implements RefactoringStepVisitor {
    @Override
    public void visitElement(RefactoringStep refactoringStep) {}

    @Override
    public void visitElement(ExtractMethodRefactoringStep extractMethodRefactoringStep) {}

    @Override
    public void visitElement(TrainWreckExpressionRefactoringImpl trainWreckExpressionRefactoring) {}

    @Override
    public void visitElement(IntroduceParametersForMembersRefactoringImpl introduceParametersForMembersRefactoring) {}

    @Override
    public void visitElement(IntroduceParameterRefactoringStep introduceParameterRefactoringStep) {}

    @Override
    public void visitElement(IntroduceParametersForCriticalCallsImpl introduceParametersForCriticalCalls) {}

    @Override
    public void visitElement(MoveMethodRefactoringStep moveMethodRefactoringStep) {}

    @Override
    public void visitElement(InlineMethodCallRefactoringStep inlineMethodCallRefactoringStep) {}

    @Override
    public void visitElement(ExtractInterfaceRefactoringStep extractInterfaceRefactoringStep) {}

    @Override
    public void visitElement(ConvertToConstructorAssignedFieldRefactoringStep convertToConstructorAssignedFieldRefactoringStep) {}

    @Override
    public void visitElement(RenameMethodRefactoringStep renameMethodRefactoringStep) {}

    @Override
    public void visitElement(InlineVariableRefactoringStep inlineVariableRefactoringStep) {}

    @Override
    public void visitElement(InlineMethodRefactoringStep inlineMethodRefactoringStep) {}
}
