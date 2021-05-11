package at.timeguess.backend.events;

import static at.timeguess.backend.utils.TestSetup.createCube;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Cube;

/**
 * Tests for {@link UnconfiguredFacetsEvent}, {@link UnconfiguredFacetsEventPublisher},
 * {@link UnconfiguredFacetsEventListener}
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class UnconfiguredFacetsEventTest {

    @InjectMocks
    private UnconfiguredFacetsEventPublisher publisher;
    @Mock
    ApplicationEventPublisher publisherApplication;
    @Mock
    private Consumer<UnconfiguredFacetsEvent> consumer1;

    private Cube expectedCube = createCube(5L);
    private Integer expectedFacet = 8;

    @Test
    public void testEvent() {
        UnconfiguredFacetsEvent event = new UnconfiguredFacetsEvent(this, expectedCube, expectedFacet);

        assertEquals(expectedCube, event.getCube());
        assertEquals(expectedFacet, event.getFacet());
    }

    @Test
    public void testPublisher() {
        ArgumentCaptor<UnconfiguredFacetsEvent> arg = ArgumentCaptor.forClass(UnconfiguredFacetsEvent.class);

        publisher.publishUnconfiguredFacetsEvent(expectedCube, expectedFacet);

        verify(publisherApplication).publishEvent(arg.capture());
        UnconfiguredFacetsEvent result = arg.getValue();
        assertEquals(expectedCube, result.getCube());
        assertEquals(expectedFacet, result.getFacet());
    }

    @Test
    public void testListener() {
        UnconfiguredFacetsEvent event = new UnconfiguredFacetsEvent(this, expectedCube, expectedFacet);
        Consumer<UnconfiguredFacetsEvent> consumer2 = e -> {
            assertEquals(expectedCube, e.getCube());
            assertEquals(expectedFacet, e.getFacet());
        };

        List<Consumer<UnconfiguredFacetsEvent>> expectedSubscribers = List.of(consumer1, consumer2);
        UnconfiguredFacetsEventListener listener = new UnconfiguredFacetsEventListener();

        for (Consumer<UnconfiguredFacetsEvent> consumer : expectedSubscribers) {
            listener.subscribe(consumer);
        }
        listener.onApplicationEvent(event);

        verify(consumer1).accept(event);

        listener.unsubscribe(consumer1);
        listener.onApplicationEvent(event);

        verifyNoMoreInteractions(consumer1);
    }
}
