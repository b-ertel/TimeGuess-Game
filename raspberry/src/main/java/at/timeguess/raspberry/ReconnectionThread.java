package at.timeguess.raspberry;

import java.util.concurrent.TimeUnit;
import java.util.logging.*;

/**
 * A thread that regularly checks if a TimeFlip device
 * is still connected and tries to reconnect if this is no longer
 * the case.
 */
public class ReconnectionThread extends Thread {

    private static final int MONITORING_INTERVAL = 1;

    private static final Logger LOGGER = Logger.getLogger("at.timeguess.raspberry");

    private final TimeFlipWrapper timeFlipWrapper;

    public ReconnectionThread(final TimeFlipWrapper timeFlipWrapper) {
        this.timeFlipWrapper = timeFlipWrapper;
    }

    @Override
    public void run() {
        LOGGER.info("Connection monitoring started.");
        while (!isInterrupted()) {
            if (!timeFlipWrapper.getConnected()) {
                LOGGER.info("Starting attempt to reconnect ...");
                if (timeFlipWrapper.connect() && timeFlipWrapper.writePassword()) {
                    LOGGER.info("Reconnection attempt successful.");
                }
                else {
                    LOGGER.warning("Reconnection attempt unsuccessful.");
                }
            }
            try {
                TimeUnit.SECONDS.sleep(MONITORING_INTERVAL);
            }
            catch (InterruptedException e) {
                interrupt();
            }
        }
        LOGGER.info("Connection monitoring stopped.");
    }

}
