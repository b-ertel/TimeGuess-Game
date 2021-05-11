package at.timeguess.backend.ui.websockets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.websocket.CloseReason.CloseCodes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.omnifaces.cdi.push.SocketEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.events.ChannelPresenceEventPublisher;

/**
 * Tests for {@link ChannelObserver}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class ChannelObserverTest {

    @InjectMocks
    private ChannelObserver channelObserver;

    @Mock
    private Map<String, Set<Long>> channelPresence;

    @Mock
    private ChannelPresenceEventPublisher channelPresenceEventPublisher;

    private static SocketEvent socketEvent;
    private static final String expectedChannel = "channel";
    private static final Long expectedId = 5L;

    @BeforeAll
    public static void setup() {
        socketEvent = mock(SocketEvent.class);
        when(socketEvent.getChannel()).thenReturn(expectedChannel);
        when(socketEvent.getUser()).thenReturn(expectedId);
        when(socketEvent.getPreviousUser()).thenReturn(6L);
        when(socketEvent.getCloseCode()).thenReturn(CloseCodes.CANNOT_ACCEPT);
    }

    @Test
    public void testSetup() {
        assertDoesNotThrow(() -> new ChannelObserver().setup());
        assertDoesNotThrow(() -> channelObserver.setup());
    }

    @Test
    public void testOnClose() {
        assertOnEvent(e -> channelObserver.onClose(e));
    }

    @Test
    public void testOnOpen() {
        assertOnEvent(e -> channelObserver.onOpen(e));
    }

    @Test
    public void testOnSwitch() {
        assertOnEvent(e -> channelObserver.onSwitch(e));
    }

    private void assertOnEvent(Consumer<SocketEvent> onEvent) {
        Set<Long> expectedIds = Set.of(expectedId);
        Set<Long> expectedSetOf = Set.of();
        Set<Long> expUnmodified = Collections.unmodifiableSet(expectedIds);
        when(channelPresence.getOrDefault(socketEvent.getChannel(), expectedSetOf)).thenReturn(expectedIds);
        when(socketEvent.getUser()).thenReturn(null);
        when(socketEvent.getPreviousUser()).thenReturn(null);

        onEvent.accept(socketEvent);

        verify(channelPresence).getOrDefault(expectedChannel, expectedSetOf);
        verify(channelPresenceEventPublisher).publishChannelPresenceEvent(expectedChannel, expUnmodified);

        when(socketEvent.getUser()).thenReturn(expectedId);
        when(socketEvent.getPreviousUser()).thenReturn(7L);

        onEvent.accept(socketEvent);

        verify(channelPresence, times(2)).getOrDefault(expectedChannel, expectedSetOf);
        verify(channelPresenceEventPublisher, times(2)).publishChannelPresenceEvent(expectedChannel, expUnmodified);
    }
}
