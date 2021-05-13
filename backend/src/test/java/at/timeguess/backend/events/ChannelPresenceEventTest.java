package at.timeguess.backend.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Set;
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

/**
 * Tests for {@link ChannelPresenceEvent}, {@link ChannelPresenceEventPublisher}, {@link ChannelPresenceEventListener}
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class ChannelPresenceEventTest {

    @InjectMocks
    private ChannelPresenceEventPublisher publisher;
    @Mock
    ApplicationEventPublisher publisherApplication;
    @Mock
    private Consumer<ChannelPresenceEvent> consumer1;

    private String expectedChannel = "somechannel";
    private Set<Long> expectedUserIds = Set.of(1L, 2L, 3L);

    @Test
    public void testEvent() {
        ChannelPresenceEvent event = new ChannelPresenceEvent(this, expectedChannel, expectedUserIds);

        assertEquals(expectedChannel, event.getChannel());
        assertEquals(expectedUserIds, event.getUserIds());
    }

    @Test
    public void testPublisher() {
        ArgumentCaptor<ChannelPresenceEvent> arg = ArgumentCaptor.forClass(ChannelPresenceEvent.class);

        publisher.publishChannelPresenceEvent(expectedChannel, expectedUserIds);

        verify(publisherApplication).publishEvent(arg.capture());
        ChannelPresenceEvent result = arg.getValue();
        assertEquals(expectedChannel, result.getChannel());
        assertEquals(expectedUserIds, result.getUserIds());
    }

    @Test
    public void testListener() {
        ChannelPresenceEvent event = new ChannelPresenceEvent(this, expectedChannel, expectedUserIds);
        Consumer<ChannelPresenceEvent> consumer2 = e -> {
            assertEquals(expectedChannel, e.getChannel());
            assertEquals(expectedUserIds, e.getUserIds());
        };
        ChannelPresenceEventListener listener = new ChannelPresenceEventListener();

        assertDoesNotThrow(() -> listener.onApplicationEvent(event));

        List<Consumer<ChannelPresenceEvent>> expectedSubscribers = List.of(consumer1, consumer2);
        for (Consumer<ChannelPresenceEvent> consumer : expectedSubscribers) {
            listener.subscribe(expectedChannel, consumer);
        }
        listener.onApplicationEvent(event);

        verify(consumer1).accept(event);

        listener.unsubscribe(expectedChannel, consumer1);
        listener.onApplicationEvent(event);

        verifyNoMoreInteractions(consumer1);
    }
}
