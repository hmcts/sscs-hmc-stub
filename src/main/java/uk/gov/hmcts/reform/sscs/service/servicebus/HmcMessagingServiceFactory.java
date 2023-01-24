package uk.gov.hmcts.reform.sscs.service.servicebus;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.sscs.model.servicebus.SessionAwareMessagingService;

@Component
@RequiredArgsConstructor
public class HmcMessagingServiceFactory {

    private final JmsTemplate jmsTemplate;

    @Value("${azure.service-bus.hmc-to-hearings-api.topicName}")
    private String topicName;


    public SessionAwareMessagingService getMessagingService() {
        return new JmsMessagingService(jmsTemplate, topicName);
    }
}
