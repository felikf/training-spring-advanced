package cz.profinit.training.springadvanced.springrest.chat.lifecycle;

import java.util.Collections;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import cz.profinit.training.springadvanced.springrest.chat.model.ChatMessage;
import cz.profinit.training.springadvanced.springrest.chat.model.ChatRating;
import cz.profinit.training.springadvanced.springrest.chat.model.ChatRatingResponse;
import cz.profinit.training.springadvanced.springrest.chat.model.ChatUpdate;

import static cz.profinit.training.springadvanced.springrest.chat.model.ChatMessageDirectionType.INCOMING;
import static cz.profinit.training.springadvanced.springrest.chat.model.ChatMessageDirectionType.OUTGOING;
import static cz.profinit.training.springadvanced.springrest.chat.model.ChatStatusType.AVAILABLE;
import static cz.profinit.training.springadvanced.springrest.chat.model.ChatStatusType.RUNNING;
import static java.util.Collections.singletonList;

/**
 * @author tpinos@csob.cz Tomas Pinos (JD71691)
 */
@Component
public class ChatLifecycle {

    static final String[] WELCOME_MESSAGES = {
            "Welcome!",
            "Here we go!",
            "What can I do you for?",
            "Identify <yourself>!"
    };

    static final String[] INCOMING_MESSAGES = {
            "Ok",
            "Very well!",
            "Well done!",
            "Really?",
            "I'm sorry to hear that..."
    };

    static final String[] GOODBYE_MESSAGES = {
            "Thanks!",
            "Enough is enough"
    };

    private Random random = new Random();

    public ChatUpdate status() {
        System.out.println("ChatLifecycle.status");
        return new ChatUpdate(AVAILABLE);
    }

    public ChatUpdate start() {
        System.out.println("ChatLifecycle.start");
        return new ChatUpdate(RUNNING, randomSessionId(), singletonList(new ChatMessage(INCOMING, randomWelcomeMessage())));
    }

    public ChatUpdate send(String sessionId, String text) {
        System.out.println("ChatLifecycle.send");
        return new ChatUpdate(RUNNING, sessionId, singletonList(new ChatMessage(OUTGOING, text)), randomMessageId());
    }

    public ChatUpdate modify(String sessionId, String messageId, String text) {
        System.out.println("ChatLifecycle.modify");
        return new ChatUpdate(RUNNING, sessionId, singletonList(new ChatMessage(OUTGOING, text)), messageId);
    }

    public ChatUpdate delete(String sessionId, String messageId) {
        System.out.println("ChatLifecycle.delete");
        return new ChatUpdate(RUNNING, sessionId, Collections.emptyList(), messageId);
    }

    public ChatUpdate refresh(String sessionId) {
        System.out.println("ChatLifecycle.refresh");
        return new ChatUpdate(RUNNING, sessionId, singletonList(new ChatMessage(INCOMING, randomIncomingMessage())));
    }

    public ChatUpdate finish(String sessionId) {
        System.out.println("ChatLifecycle.finish");
        return new ChatUpdate(RUNNING, sessionId, singletonList(new ChatMessage(INCOMING, randomGoodbyeMessage())));
    }

    public ChatRatingResponse rating(String sessionId, ChatRating rating) {
        System.out.println("ChatLifecycle.rating");
        return new ChatRatingResponse(sessionId, rating, randomGoodbyeMessage());
    }

    protected String randomSessionId() {
        return RandomStringUtils.randomAlphanumeric(12).toUpperCase();
    }

    protected String randomMessageId() {
        return RandomStringUtils.randomAlphanumeric(16).toUpperCase();
    }

    protected String randomWelcomeMessage() {
        return WELCOME_MESSAGES[random.nextInt(WELCOME_MESSAGES.length)];
    }

    protected String randomIncomingMessage() {
        return INCOMING_MESSAGES[random.nextInt(INCOMING_MESSAGES.length)];
    }

    protected String randomGoodbyeMessage() {
        return GOODBYE_MESSAGES[random.nextInt(GOODBYE_MESSAGES.length)];
    }
}
