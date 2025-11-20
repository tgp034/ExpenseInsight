package com.expenseinsight.api.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration to provide an `OpenAIClient` bean from environment.
 *
 * The SDK reads credentials from environment variables such as
 * `OPENAI_API_KEY`. Do NOT copy your API key into source control. Set it in
 * your shell or CI environment before running the application.
 */
@Configuration
@ConditionalOnProperty(prefix = "openai", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OpenAiConfig {

    @Bean
    public OpenAIClient openAIClient() {
        // Creates a client configured from environment variables.
        // Ensure you set OPENAI_API_KEY (and optionally OPENAI_BASE_URL) in your
        // environment. When running tests or in CI you can set `openai.enabled=false`
        // to avoid creating the real SDK client.
        return OpenAIOkHttpClient.fromEnv();
    }
}
