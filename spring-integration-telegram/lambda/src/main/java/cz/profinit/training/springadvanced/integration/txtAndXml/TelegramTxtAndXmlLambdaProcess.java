package cz.profinit.training.springadvanced.integration.txtAndXml;

import cz.profinit.training.springadvanced.integration.CountingChannelInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.support.Transformers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.splitter.DefaultMessageSplitter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@SpringBootApplication
@IntegrationComponentScan
public class TelegramTxtAndXmlLambdaProcess {

    public static void main(final String[] args) {
        SpringApplication.run(TelegramTxtAndXmlLambdaProcess.class, args);
    }

    @Bean
    public IntegrationFlow txtFlow(final FlowConfiguration configuration) {
        return IntegrationFlows
                // MessageSourcesFunction sources, Consumer<SourcePollingChannelAdapterSpec> endpointConfigurer
                // MessageSources s
                .from(s -> s.file(new File(configuration.getInputFolder()))
                                .patternFilter("*.txt")
                                .preventDuplicates(),
                        e -> e.poller(Pollers.fixedDelay(configuration.getPollerDelay())))
                // TODO
                .get();
    }

    @Bean
    public IntegrationFlow xmlFlow(final FlowConfiguration configuration) {
        return IntegrationFlows
                .from(s -> s.file(new File(configuration.getInputFolder()))
                                .patternFilter("*.xml")
                                .preventDuplicates(),
                        e -> e.poller(Pollers.fixedDelay(configuration.getPollerDelay())))
                // TODO
                .get();
    }

    @Bean
    public IntegrationFlow telegramFlow(final FlowConfiguration configuration) {
        return f -> f
                .<String>filter(s -> s.trim().length() > 0)
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
    public FlowConfiguration configuration(final Environment environment) {
        return new FlowConfiguration(
                environment.getProperty("input", "c:/temp/telegram-input"),
                environment.getProperty("output", "c:/temp/telegram-output"),
                environment.getProperty("delay", Integer.class, 1000));
    }

    @Bean
    public DefaultMessageSplitter txtSplitter() {
        final DefaultMessageSplitter splitter = new DefaultMessageSplitter();
        splitter.setDelimiters("\r\n");
        return splitter;
    }

    @Bean
    public LoggingHandler loggingHandler() {
        final LoggingHandler loggingHandler =  new LoggingHandler(LoggingHandler.Level.INFO.name());
        loggingHandler.setLoggerName("Telegrams");
        return loggingHandler;
    }

    @Bean
    public ChannelInterceptor countingInterceptor() {
        return new CountingChannelInterceptor();
    }

    private static class FlowConfiguration {

        private final String inputFolder;
        private final String outputFolder;
        private final int pollerDelay;

        private FlowConfiguration(final String inputFolder, final String outputFolder, final int pollerDelay) {
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

        private int getPollerDelay() {
            return pollerDelay;
        }
    }
}
