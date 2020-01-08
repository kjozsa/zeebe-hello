package hu.dpc.zeebeehello;

import io.zeebe.client.ZeebeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZeebeConfiguration {

    @Bean
    public ZeebeClient setup() {
        return ZeebeClient.newClientBuilder()
                .brokerContactPoint("127.0.0.1:26500")
                .usePlaintext()
                .build();
    }
}
