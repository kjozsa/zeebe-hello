package hu.dpc.zeebeehello;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.WorkflowInstanceEvent;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SomeRouteBuilder extends RouteBuilder {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

    @Override
    public void configure() {
        from("rest:GET:/test/start")
                .process(exchange -> zeebeStartInstance());
    }

    private void zeebeStartInstance() {
        WorkflowInstanceEvent instance = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("PaymentTest")
                .latestVersion()
                .send()
                .join();

        logger.info("zeebee workflow instance {} created", instance.getWorkflowInstanceKey());

    }
}
