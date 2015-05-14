package com.simonstuck.vignelli.psi.util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiModifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ClassUtil {

    /**
     * Returns non-private static members of a class, excluding all inherited ones.
     * @param clazz The class for which to find the members.
     * @return A new collection with all static, non-private members.
     */
    public static Collection<PsiMember> getNonPrivateStaticMembers(PsiClass clazz) {
        Collection<PsiMember> allMembers = new HashSet<PsiMember>();
        allMembers.addAll(Arrays.asList(clazz.getMethods()));
        allMembers.addAll(Arrays.asList(clazz.getFields()));
        Collection<PsiMember> staticMembers = new HashSet<PsiMember>();
        for (PsiMember member : allMembers) {
            if (member.hasModifierProperty(PsiModifier.STATIC) && !member.hasModifierProperty(PsiModifier.PRIVATE)) {
                staticMembers.add(member);
            }
        }
        return staticMembers;
    }
}
