package hu.dpc.zeebeehello;

import io.zeebe.client.ZeebeClient;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SomeRouteBuilder extends RouteBuilder {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

    @Override
    public void configure() {
        from("rest:GET:/test/start/{txId}")
                .process(exchange -> zeebeStartInstance(exchange.getIn().getHeader("txId", String.class)));

        from("rest:GET:/test/start/{nr}/{txId}")
                .process(exchange -> {
                    Integer txId = exchange.getIn().getHeader("txId", Integer.class);
                    Integer nr = exchange.getIn().getHeader("nr", Integer.class);
                    logger.info("starting {} workflow instances", nr);
                    for (int i = 0; i < nr; i++) {
                        zeebeStartInstance(Integer.toString(txId + i));
                    }
                });

        from("rest:GET:/test/correlate/{txId}")
                .process(exchange -> zeebeCorrelate(exchange.getIn().getHeader("txId", String.class)));

        from("rest:GET:/test/correlate/{nr}/{txId}")
                .process(exchange -> {
                    Integer txId = exchange.getIn().getHeader("txId", Integer.class);
                    Integer nr = exchange.getIn().getHeader("nr", Integer.class);
                    logger.info("correlating {} workflow instances", nr);
                    for (int i = 0; i < nr; i++) {
                        zeebeCorrelate(Integer.toString(txId + i));
                    }
                });
    }

    @PostConstruct
    public void setupHandlers() {
        zeebeClient.newWorker().jobType("initate-payment").handler((client, job) -> {
            logger.info("initiating payment from process {} with key {}", job.getBpmnProcessId(), job.getKey());
            client.newCompleteCommand(job.getKey()).send();
        })
                .maxJobsActive(100_000)
                .open();

        zeebeClient.newWorker().jobType("process-payment").handler((client, job) -> {
            logger.info("processing payment from process {} with key {} and txId {}", job.getBpmnProcessId(), job.getKey(), job.getVariablesAsMap().get("txId"));
            client.newCompleteCommand(job.getKey()).send();
        })
                .maxJobsActive(100_000)
                .open();
    }

    private void zeebeStartInstance(String txId) {
        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("PaymentTest")
                .latestVersion()
                .variables("{ \"txId\": \"" + txId + "\" }")
                .send();

//        logger.info("zeebee workflow instance {} created with txId {}", instance.getWorkflowInstanceKey(), txId);
    }

    private void zeebeCorrelate(String txId) {
        logger.info("correlating to {}", txId);
        Void paymentReceived = zeebeClient.newPublishMessageCommand()
                .messageName("PaymentReceived")
                .correlationKey(txId)
                .send()
                .join();
    }
}
