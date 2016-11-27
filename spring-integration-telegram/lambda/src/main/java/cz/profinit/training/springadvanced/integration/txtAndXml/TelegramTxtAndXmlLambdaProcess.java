package cz.profinit.training.springadvanced.integration.txtAndXml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.support.Transformers;
import org.springframework.integration.splitter.DefaultMessageSplitter;
import org.springframework.integration.xml.splitter.XPathMessageSplitter;
import org.springframework.integration.xml.transformer.XPathTransformer;
import org.springframework.messaging.MessageHeaders;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@SpringBootApplication
@IntegrationComponentScan
public class TelegramTxtAndXmlLambdaProcess {

    public static void main(String[] args) {
        SpringApplication.run(TelegramTxtAndXmlLambdaProcess.class, args);
    }

    @Bean
    public IntegrationFlow txtFlow(FlowConfiguration configuration) {
        DefaultMessageSplitter splitter = new DefaultMessageSplitter();
        splitter.setDelimiters("\r\n");

        return IntegrationFlows
                // MessageSourcesFunction sources, Consumer<SourcePollingChannelAdapterSpec> endpointConfigurer
                // MessageSources s
                .from(s -> s.file(new File(configuration.getInputFolder()))
                                .patternFilter("*.txt")
                                .preventDuplicates(),
                        e -> e.poller(Pollers.fixedDelay(configuration.getPollerDelay())))
                .transform(Transformers.fileToString())
                .split(splitter)
                .channel("telegramFlow.input")
                .get();
    }

    @Bean
    public IntegrationFlow xmlFlow(FlowConfiguration configuration) {
        return IntegrationFlows
                .from(s -> s.file(new File(configuration.getInputFolder()))
                                .patternFilter("*.xml")
                                .preventDuplicates(),
                        e -> e.poller(Pollers.fixedDelay(configuration.getPollerDelay())))
                .transform(Transformers.fileToString())
                .split(new XPathMessageSplitter("/telegrams/telegram"))
                .transform(new XPathTransformer("/telegram/text()"))
                .channel("telegramFlow.input")
                .get();
    }

    @Bean
    public IntegrationFlow telegramFlow(FlowConfiguration configuration) {
        return f -> f
                .transform(Transformers.<String, String>converter(payload ->
                        Arrays.stream(payload.split(" "))
                                .map(String::toUpperCase)
                                .collect(Collectors.joining(" - "))))
                // Function<Adapters, MessageHandlerSpec<?, H>> adapters
                // Adapters a
                .handleWithAdapter(a -> a.file(new File(configuration.getOutputFolder()))
                        .deleteSourceFiles(true)
                        .fileNameGenerator(m ->
                                new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
                                        + "." + m.getHeaders().get(MessageHeaders.ID) + ".txt"));
    }

    @Bean
    public FlowConfiguration configuration(Environment environment) {
        return new FlowConfiguration(
                environment.getProperty("input", "c:/temp/telegram-input"),
                environment.getProperty("output", "c:/temp/telegram-output"),
                environment.getProperty("delay", Integer.class, 1000));
    }

    private static class FlowConfiguration {

        private final String inputFolder;
        private final String outputFolder;
        private final int pollerDelay;

        private FlowConfiguration(String inputFolder, String outputFolder, int pollerDelay) {
            this.inputFolder = inputFolder;
            this.outputFolder = outputFolder;
            this.pollerDelay = pollerDelay;
        }

        private String getInputFolder() {
            return inputFolder;
        }

        private String getOutputFolder() {
            return outputFolder;
        }

        public int getPollerDelay() {
            return pollerDelay;
        }
    }
}
