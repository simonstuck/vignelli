package com.simonstuck.vignelli.inspection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.simonstuck.vignelli.inspection.identification.IdentificationCollection;
import com.simonstuck.vignelli.inspection.identification.ProblemIdentification;
import com.simonstuck.vignelli.providers.CollectionOfMocksProvider;

import org.junit.Before;
import org.junit.Test;

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
        IdentificationCollection<ProblemIdentification> problems = createSampleProblemIdentificationCollection();

        cache.updateFileProblems(file, problems);
        cache.selectFile(file);
        assertEquals(problems, cache.selectedFileProblems());
    }

    @Test
    public void shouldOnlyReturnProblemsForCurrentlySelectedFile() throws Exception {
        VirtualFile fileA = mock(VirtualFile.class);
        VirtualFile fileB = mock(VirtualFile.class);
        IdentificationCollection<ProblemIdentification> problemsA = createSampleProblemIdentificationCollection();
        IdentificationCollection<ProblemIdentification> problemsB = createSampleProblemIdentificationCollection();

        cache.updateFileProblems(fileA, problemsA);
        cache.updateFileProblems(fileB, problemsB);

        cache.selectFile(fileA);
        assertEquals(problemsA, cache.selectedFileProblems());
    }

    @Test
    public void shouldNotBroadcastChangesInFileThatIsNotCurrentlySelected() throws Exception {
        VirtualFile file = mock(VirtualFile.class);
        IdentificationCollection<ProblemIdentification> problems = createSampleProblemIdentificationCollection();

        cache.updateFileProblems(file, problems);
        assertEquals(null, cache.lastBroadcast);
    }

    @Test
    public void shouldBroadcastNewProblemsOnUpdateOfSelectedFile() throws Exception {
        VirtualFile file = mock(VirtualFile.class);
        IdentificationCollection<ProblemIdentification> problems = createSampleProblemIdentificationCollection();

        cache.selectFile(file);
        cache.updateFileProblems(file, problems);
        assertEquals(problems, cache.lastBroadcast);
    }

    protected IdentificationCollection<ProblemIdentification> createSampleProblemIdentificationCollection() {
        Collection<ProblemIdentification> mockedIds = CollectionOfMocksProvider.collectionOfSize(3, ProblemIdentification.class);
        IdentificationCollection<ProblemIdentification> problems = new IdentificationCollection<ProblemIdentification>();
        problems.addAll(mockedIds);
        return problems;
    }
}