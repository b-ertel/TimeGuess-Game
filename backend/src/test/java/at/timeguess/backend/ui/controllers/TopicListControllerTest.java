package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.createEntities;
import static at.timeguess.backend.utils.TestSetup.createTerm;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.services.TopicService;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.utils.TestSetup;

/**
 * Tests for {@link TopicListController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class TopicListControllerTest {

    @InjectMocks
    private TopicListController topicListController;

    @Mock
    private TopicService topicService;
    @Mock
    private TermService termService;
    @Mock
    private MessageBean messageBean;
    @Mock
    private UploadedFile uploadedFile;

    private static String testfile = "sample.json";
    private static List<Topic> expected;

    @BeforeAll
    private static void setup() {
        expected = createEntities(TestSetup::createTopic, 5);
        AtomicLong counter = new AtomicLong(1);
        expected.stream().forEach(t -> {
            // complicated on purpose...
            t.setName("Topic " + counter.get());
            Set<Term> terms = t.getTerms();
            Term term = createTerm(counter.getAndAdd(2));
            term.setName("Term " + counter.get());
            terms.add(term);
            t.setTerms(terms);
            assertEquals(1, t.getTerms().size());
        });
    }

    @Test
    public void testGetTopics() {
        when(topicService.getAllTopics()).thenReturn(expected);

        List<Topic> result = topicListController.getTopics();

        verify(topicService).getAllTopics();
        assertEquals(expected, result);
    }

    @Test
    public void testUploadTerms() throws IOException {
        testUploadTerms(true);
    }

    @Test
    public void testUploadTermsNoTerm() throws IOException {
        testUploadTerms(false);
    }

    @Test
    public void testUploadTermsNoTopic() throws IOException {
        when(uploadedFile.getFileName()).thenReturn(testfile);
        when(uploadedFile.getInputStream()).thenReturn(getSampleUploadFile());
        when(topicService.getAllTopics()).thenReturn(expected);
        when(topicService.saveTopic(any(Topic.class))).thenReturn(null);

        assertDoesNotThrow(() -> topicListController.doUploadTerms(new FileUploadEvent(new FileUpload(), uploadedFile)));

        verify(uploadedFile).getFileName();
        verify(uploadedFile).getInputStream();
        verify(topicService).getAllTopics();
        verify(topicService).saveTopic(any(Topic.class));
        verify(messageBean, times(3)).alertErrorFailValidation(anyString(), anyString());
    }

    @Test
    public void testUploadTermsFailureFile() throws IOException {
        when(uploadedFile.getFileName()).thenThrow(RuntimeException.class);

        assertDoesNotThrow(() -> topicListController.doUploadTerms(new FileUploadEvent(new FileUpload(), uploadedFile)));

        verify(uploadedFile).getFileName();
        verifyNoMoreInteractions(uploadedFile, topicService, termService);
        verify(messageBean).alertErrorFailValidation(anyString(), any());
    }

    @Test
    public void testUploadTermsFailureParse() throws IOException {
        when(uploadedFile.getFileName()).thenReturn(testfile);
        when(uploadedFile.getInputStream()).thenReturn(getSampleUploadFile());
        when(topicService.getAllTopics()).thenThrow(RuntimeException.class);

        assertDoesNotThrow(() -> topicListController.doUploadTerms(new FileUploadEvent(new FileUpload(), uploadedFile)));

        verify(uploadedFile).getFileName();
        verify(termService).setInfoOnSave(false);
        verify(uploadedFile).getInputStream();
        verify(topicService).getAllTopics();
        verify(messageBean).alertErrorFailValidation(anyString(), anyString());
        verify(termService).setInfoOnSave(true);
    }

    private void testUploadTerms(boolean success) throws IOException {
        Topic topic = expected.get(0);
        when(uploadedFile.getFileName()).thenReturn(testfile);
        when(uploadedFile.getInputStream()).thenReturn(getSampleUploadFile());
        when(topicService.getAllTopics()).thenReturn(expected);
        when(topicService.saveTopic(any(Topic.class))).thenReturn(topic);
        when(termService.saveTerm(any(Term.class))).thenReturn(success ? createTerm(5L) : null);

        assertDoesNotThrow(() -> topicListController.doUploadTerms(new FileUploadEvent(new FileUpload(), uploadedFile)));

        verify(uploadedFile).getFileName();
        verify(uploadedFile).getInputStream();
        verify(topicService).getAllTopics();
        verify(topicService).saveTopic(any(Topic.class));
        verify(termService, times(5)).saveTerm(any(Term.class));
        if (!success) verify(messageBean, times(5)).alertErrorFailValidation(anyString(), anyString());
    }

    private InputStream getSampleUploadFile() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return classLoader.getResourceAsStream(testfile);
    }
}
