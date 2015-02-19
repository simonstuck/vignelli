package com.simonstuck.vignelli.inspection.identification;

import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class MethodChainIdentification implements Identification {
    private PsiMethodCallExpression finalCall;

    private MethodChainIdentification(PsiMethodCallExpression finalCall) {
        this.finalCall = finalCall;
    }

    public static MethodChainIdentification createWithFinalCall(PsiMethodCallExpression finalCall) {
        return new MethodChainIdentification(finalCall);
    }

    /**
     * Gets the method call qualifier.
     * @return The methodcall that this identification's final call depends on, otherwise empty.
     */
    public Optional<MethodChainIdentification> getMethodCallQualifier() {
        final PsiReferenceExpression methodExpression = finalCall.getMethodExpression();
        final PsiExpression qualifierExpression = methodExpression.getQualifierExpression();

        if (qualifierExpression instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression qualifier = (PsiMethodCallExpression) qualifierExpression;
            return Optional.of(MethodChainIdentification.createWithFinalCall(qualifier));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets all method call qualifiers for this final call.
     * This recursively calls getMethodCallQualifier() recursively and collects all qualifiers.
     * @return A set of all recursive qualifiers
     */
    public Set<MethodChainIdentification> getAllMethodCallQualifiers() {
        Optional<MethodChainIdentification> directQualifier = getMethodCallQualifier();
        if (!directQualifier.isPresent()) {
            return new HashSet<MethodChainIdentification>();
        } else {
            Set<MethodChainIdentification> result = directQualifier.get().getAllMethodCallQualifiers();
            result.add(directQualifier.get());
            return result;
        }
    }

    public PsiType getMethodCallType() {
        return finalCall.getType();
    }

    @Override
    public int hashCode() {
        return finalCall.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MethodChainIdentification)) {
            return false;
        }

        MethodChainIdentification other = (MethodChainIdentification) obj;
        return other.finalCall.equals(finalCall);
    }
}
