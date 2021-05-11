package at.timeguess.backend.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext context = null;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        ApplicationContextProvider.context = context;
    }

    /**
     * Tries to autowire the specified instance of the class if at least one of the specified beans
     * which need to be autowired is null.
     * @param classToAutowire        instance of the class holding @Autowire annotations
     * @param beansToAutowireInClass beans having @Autowire annotation in the specified {#classToAutowire}
     */
    public static void autowire(Object classToAutowire, Object... beansToAutowireInClass) {
        for (Object bean : beansToAutowireInClass) {
            if (bean == null) {
                context.getAutowireCapableBeanFactory().autowireBean(classToAutowire);
            }
        }
    }
}