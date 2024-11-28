package org.b3log.solo.event;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;

/**
 * An abstract base listener class.
 *
 * @author Rainie
 * @version 1.0.0.1, Nov 27, 2024
 * @since 4.4.0
 */
public abstract class AbstractB3EventListener extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(AbstractB3EventListener.class);

    @Override
    public void action(final Event<JSONObject> event) {
        final JSONObject data = event.getData();
        LOGGER.log(Level.DEBUG, "Processing an event [type={}, data={}] in listener [className={}]",
                event.getType(), data, this.getClass().getName());
        pushArticleToRhy(data);
    }

    protected void pushArticleToRhy(final JSONObject data) {
        B3ArticlePusher.pushArticleToRhy(data);
    }

    @Override
    public abstract String getEventType();
}