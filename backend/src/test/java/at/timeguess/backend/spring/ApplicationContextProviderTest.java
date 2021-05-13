package at.timeguess.backend.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests for {@link ApplicationContextProvider}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class ApplicationContextProviderTest {

    @Test
    public void testAutowire() {
        ApplicationContext context = mock(ApplicationContext.class);
        AutowireCapableBeanFactory factory = mock(AutowireCapableBeanFactory.class);
        when(context.getAutowireCapableBeanFactory()).thenReturn(factory);

        ApplicationContextProvider provider = new ApplicationContextProvider();
        provider.setApplicationContext(context);

        assertEquals(context, ApplicationContextProvider.getApplicationContext());

        ApplicationContextProvider.autowire(this, this);

        verifyNoInteractions(context);
        verifyNoInteractions(factory);

        ApplicationContextProvider.autowire(this, (Object) null);

        verify(context).getAutowireCapableBeanFactory();
        verify(factory).autowireBean(this);
    }
}
