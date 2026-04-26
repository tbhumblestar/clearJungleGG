package backend.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(private val props: RiotApiProperties) {

    private val largeBufferStrategies = ExchangeStrategies.builder()
        .codecs { it.defaultCodecs().maxInMemorySize(5 * 1024 * 1024) }
        .build()

    @Bean
    @Qualifier("krWebClient")
    fun krWebClient(): WebClient =
        WebClient.builder()
            .baseUrl(props.krBaseUrl)
            .defaultHeader("X-Riot-Token", props.apiKey)
            .build()

    @Bean
    @Qualifier("asiaWebClient")
    fun asiaWebClient(): WebClient =
        WebClient.builder()
            .baseUrl(props.asiaBaseUrl)
            .defaultHeader("X-Riot-Token", props.apiKey)
            .exchangeStrategies(largeBufferStrategies)
            .build()

    @Bean
    @Qualifier("ddragonWebClient")
    fun ddragonWebClient(): WebClient =
        WebClient.builder()
            .baseUrl(props.ddragonBaseUrl)
            .build()
}
