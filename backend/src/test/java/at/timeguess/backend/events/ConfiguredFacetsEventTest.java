package at.timeguess.backend.events;

import static at.timeguess.backend.utils.TestSetup.createCube;
import static at.timeguess.backend.utils.TestSetup.createCubeFace;
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
import at.timeguess.backend.model.CubeFace;

/**
 * Tests for {@link ConfiguredFacetsEvent}, {@link ConfiguredFacetsEventPublisher},
 * {@link ConfiguredFacetsEventListener}
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class ConfiguredFacetsEventTest {

    @InjectMocks
    private ConfiguredFacetsEventPublisher publisher;
    @Mock
    ApplicationEventPublisher publisherApplication;
    @Mock
    private Consumer<ConfiguredFacetsEvent> consumer1;

    private Cube expectedCube = createCube(5L);
    private CubeFace expectedCubeFace = createCubeFace("someId");

    @Test
    public void testEvent() {
        ConfiguredFacetsEvent event = new ConfiguredFacetsEvent(this, expectedCube, expectedCubeFace);

        assertEquals(expectedCube, event.getCube());
        assertEquals(expectedCubeFace, event.getCubeFace());
    }

    @Test
    public void testPublisher() {
        ArgumentCaptor<ConfiguredFacetsEvent> arg = ArgumentCaptor.forClass(ConfiguredFacetsEvent.class);

        publisher.publishConfiguredFacetsEvent(expectedCube, expectedCubeFace);

        verify(publisherApplication).publishEvent(arg.capture());
        ConfiguredFacetsEvent result = arg.getValue();
        assertEquals(expectedCube, result.getCube());
        assertEquals(expectedCubeFace, result.getCubeFace());
    }

    @Test
    public void testListener() {
        ConfiguredFacetsEvent event = new ConfiguredFacetsEvent(this, expectedCube, expectedCubeFace);
        Consumer<ConfiguredFacetsEvent> consumer2 = e -> {
            assertEquals(expectedCube, e.getCube());
            assertEquals(expectedCubeFace, e.getCubeFace());
        };

        List<Consumer<ConfiguredFacetsEvent>> expectedSubscribers = List.of(consumer1, consumer2);
        ConfiguredFacetsEventListener listener = new ConfiguredFacetsEventListener();

        for (Consumer<ConfiguredFacetsEvent> consumer : expectedSubscribers) {
            listener.subscribe(consumer);
        }
        listener.onApplicationEvent(event);

        verify(consumer1).accept(event);

        listener.unsubscribe(consumer1);
        listener.onApplicationEvent(event);

        verifyNoMoreInteractions(consumer1);
    }
}
