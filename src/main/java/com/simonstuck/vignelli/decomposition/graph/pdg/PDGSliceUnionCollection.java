package com.simonstuck.vignelli.decomposition.graph.pdg;

import com.google.gson.Gson;
import com.intellij.psi.PsiLocalVariable;
import com.simonstuck.vignelli.decomposition.graph.cfg.BasicBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PDGSliceUnionCollection implements Iterable<PDGSliceUnion> {

    private Map<BasicBlock, PDGSliceUnion> sliceUnionMap;

    public PDGSliceUnionCollection(ProgramDependenceGraph pdg, PsiLocalVariable variableCriterion) {
        this.sliceUnionMap = new LinkedHashMap<BasicBlock, PDGSliceUnion>();

        Set<PDGNode> nodeCriteria = pdg.getAssignmentNodesForVariable(variableCriterion);
        Map<PDGNode, Set<BasicBlock>> boundaryBlockMap = new LinkedHashMap<PDGNode, Set<BasicBlock>>();

        for (PDGNode nodeCriterion : nodeCriteria) {
            Set<BasicBlock> boundaryBlocks = pdg.getBoundaryBlocks(nodeCriterion);
            boundaryBlockMap.put(nodeCriterion, boundaryBlocks);
        }

        List<Set<BasicBlock>> list = new ArrayList<Set<BasicBlock>>(boundaryBlockMap.values());

        if (!list.isEmpty()) {
            Set<BasicBlock> basicBlockIntersection = new LinkedHashSet<BasicBlock>(list.get(0));
            for (int i = 1; i < list.size(); i++) {
                basicBlockIntersection.retainAll(list.get(i));
            }

            for (BasicBlock basicBlock : basicBlockIntersection) {
                //FIXME: This needs to be better, factory method?
                PDGSliceUnion sliceUnion = new PDGSliceUnion(pdg, basicBlock, nodeCriteria, variableCriterion);
                if (sliceUnion.isValid()) {
                    sliceUnionMap.put(basicBlock, sliceUnion);
                }
            }
        }

    }

    @Override
    public Iterator<PDGSliceUnion> iterator() {
        return sliceUnionMap.values().iterator();
    }

    @Override
    public String toString() {
        return "PDGSliceUnionCollection{" +
                "sliceUnionMap=" + sliceUnionMap +
                '}';
    }
}
