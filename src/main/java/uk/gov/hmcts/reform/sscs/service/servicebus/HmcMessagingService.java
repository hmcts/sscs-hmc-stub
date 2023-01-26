package uk.gov.hmcts.reform.sscs.service.servicebus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.sscs.model.servicebus.SessionAwareRequest;

@Component
public class HmcMessagingService {
    private final JmsMessagingService messagingService;

    public HmcMessagingService(JmsTemplate jmsTemplate, @Value("${azure.service-bus.hmc-to-hearings-api.topicName}") String topicName) {
        messagingService = new JmsMessagingService(jmsTemplate, topicName);
    }

    public void sendMessage(SessionAwareRequest message) {
        messagingService.sendMessage(message);
    }
}
