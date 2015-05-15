package com.simonstuck.vignelli.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Lifts a list of PsiElements to the same context level, i.e. lifts individual statements that
 * are in nested blocks up to gain one common context for all statements in the collection.
 */
public class PsiElementLiftToCommonContext {
    private Collection<? extends PsiElement> elementsToExtract;

    public PsiElementLiftToCommonContext(Collection<? extends PsiElement> elementsToExtract) {
        this.elementsToExtract = elementsToExtract;
    }

    public Collection<? extends PsiElement> invoke() {
        if (elementsToExtract.isEmpty()) {
            return new ArrayList<PsiElement>(elementsToExtract);
        }

        PsiElement commonContext = elementsToExtract.iterator().next().getContext();

        boolean sameContext = false;
        while (!sameContext) {
            sameContext = true;
            List<PsiElement> newToExtract = new ArrayList<PsiElement>();

            Iterator<? extends PsiElement> elementIterator = elementsToExtract.iterator();
            while (elementIterator.hasNext()) {
                PsiElement element = elementIterator.next();

                if (element.getContext() == commonContext) {
                    newToExtract.add(element);
                } else {
                    sameContext = false;
                    // different context
                    //TODO: What to do if context is null?
                    if (PsiTreeUtil.isAncestor(commonContext, element.getContext(), true)) {
                        newToExtract.add(element.getParent());
                    } else {
                        commonContext = element.getContext();
                        // start over!
                        break;
                    }
                }
            }

            // if we are done though, the common context is fine and we have found
            if (!elementIterator.hasNext()) {
                elementsToExtract = newToExtract;
            }
        }
        return elementsToExtract;
    }
}
