package ch.harmen.echo.request;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Component
class RequestSubscriptionConfiguration {

  @Bean
  Sinks.Many<Request> requestSink() {
    return Sinks
      .many()
      .multicast()
      .onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
  }

  @Bean
  Flux<Request> requestFlux() {
    return requestSink().asFlux();
  }
}
