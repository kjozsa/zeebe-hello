package hu.dpc.zeebeehello;

import org.apache.camel.CamelContext;
import org.apache.camel.component.jetty9.JettyHttpComponent9;
import org.apache.camel.component.xslt.XsltComponent;
import org.apache.camel.spi.RestConfiguration;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class CamelContextConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    CamelContextConfiguration contextConfiguration() {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {
                camelContext.setTracing(false);
                camelContext.setMessageHistory(false);
                camelContext.setStreamCaching(false);

                // rest config
                RestConfiguration rest = new RestConfiguration();
                camelContext.setRestConfiguration(rest);
                rest.setComponent("jetty");
                rest.setPort(5000);
                rest.setBindingMode(RestConfiguration.RestBindingMode.json);
                rest.setDataFormatProperties(new HashMap<>());
                rest.getDataFormatProperties().put("prettyPrint", "true");
                rest.setScheme("http");
            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {
                // empty
            }
        };
    }
}
