package com.simonstuck.vignelli.inspection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.providers.CollectionOfMocksProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class ProblemIdentificationCacheComponentTest {

    private ProblemIdentificationCacheComponentSeam cache;

    @Before
    public void setUp() throws Exception {
        Project project = mock(Project.class);
        cache = new ProblemIdentificationCacheComponentSeam(project);
    }

    @Test
    public void shouldReturnNoProblemsIfNoFileIsSelected() throws Exception {
        assertEquals(0, cache.selectedFileProblems().size());
    }

    @Test
    public void shouldUpdateProblemsForGivenFile() throws Exception {
        VirtualFile file = mock(VirtualFile.class);
        Collection<ProblemIdentification> problems = CollectionOfMocksProvider.collectionOfSize(3, ProblemIdentification.class);

        cache.updateFileProblems(file, this, problems);
        cache.selectFile(file);
        assertEquals(problems, cache.selectedFileProblems());
    }

    @Test
    public void shouldNotRemoveProblemsFromAnotherOwnerOnProblemUpdate() throws Exception {
        VirtualFile file = mock(VirtualFile.class);
        Object ownerA = new Object();
        Object ownerB = new Object();
        Collection<ProblemIdentification> problemsA = CollectionOfMocksProvider.collectionOfSize(3, ProblemIdentification.class);
        Collection<ProblemIdentification> problemsB = CollectionOfMocksProvider.collectionOfSize(3, ProblemIdentification.class);

        Collection<ProblemIdentification> allProblems = new ArrayList<ProblemIdentification>(problemsA);
        allProblems.addAll(problemsB);

        cache.updateFileProblems(file, ownerA, problemsA);
        cache.updateFileProblems(file, ownerB, problemsB);
        cache.selectFile(file);
        assertTrue(allProblems.containsAll(cache.selectedFileProblems()));
        assertTrue(cache.selectedFileProblems().containsAll(allProblems));
    }

    @Test
    public void shouldOnlyReturnProblemsForCurrentlySelectedFile() throws Exception {
        VirtualFile fileA = mock(VirtualFile.class);
        VirtualFile fileB = mock(VirtualFile.class);
        Collection<ProblemIdentification> problemsA = CollectionOfMocksProvider.collectionOfSize(3, ProblemIdentification.class);
        Collection<ProblemIdentification> problemsB = CollectionOfMocksProvider.collectionOfSize(3, ProblemIdentification.class);

        cache.updateFileProblems(fileA, this, problemsA);
        cache.updateFileProblems(fileB, this, problemsB);

        cache.selectFile(fileA);
        assertEquals(problemsA, cache.selectedFileProblems());
    }

    @Test
    public void shouldNotBroadcastChangesInFileThatIsNotCurrentlySelected() throws Exception {
        VirtualFile file = mock(VirtualFile.class);
        Collection<ProblemIdentification> problems = CollectionOfMocksProvider.collectionOfSize(3, ProblemIdentification.class);

        cache.updateFileProblems(file, this, problems);
        assertEquals(null, cache.lastBroadcast);
    }

    @Test
    public void shouldBroadcastNewProblemsOnUpdateOfSelectedFile() throws Exception {
        VirtualFile file = mock(VirtualFile.class);
        Collection<ProblemIdentification> problems = CollectionOfMocksProvider.collectionOfSize(3, ProblemIdentification.class);

        cache.selectFile(file);
        cache.updateFileProblems(file, this, problems);
        assertEquals(problems, cache.lastBroadcast);
    }
}