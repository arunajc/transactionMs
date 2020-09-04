package com.mybank.transaction.config;

import com.mybank.transaction.model.TransactionDetails;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.spring.TracingConsumerFactory;
import io.opentracing.contrib.kafka.spring.TracingProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

	@Value("${spring.application.name}")
	private String applicationName;

	@Component
	@ConfigurationProperties("mybank.kafka.consumer")
	public static class KafkaConsumerConfigProps{

		private final Map<String, String> props = new HashMap<>();
		public Map<String, String> getProps(){
			return props;
		}
	}

	@Component
	@ConfigurationProperties("mybank.kafka.producer")
	public static class KafkaProducerConfigProps{

		private final Map<String, String> props = new HashMap<>();
		public Map<String, String> getProps(){
			return props;
		}
	}

	@Bean
	public Tracer tracer() {
		return io.jaegertracing.Configuration.fromEnv(applicationName)
				.withSampler(
						io.jaegertracing.Configuration.SamplerConfiguration.fromEnv()
								.withType(ConstSampler.TYPE)
								.withParam(1))
				.withReporter(
						io.jaegertracing.Configuration.ReporterConfiguration.fromEnv()
								.withLogSpans(true)
								.withFlushInterval(1000)
								.withMaxQueueSize(10000))
				.getTracer();
	}

	@Autowired
	KafkaConsumerConfigProps consumerConfigProps;

	@Autowired
	KafkaProducerConfigProps producerConfigProps;

	@Bean
	public ProducerFactory<Long, TransactionDetails> producerFactory() {
		return new TracingProducerFactory<>(
				new DefaultKafkaProducerFactory<>(
						(Map)producerConfigProps.getProps()), tracer());
	}

	@Bean
	KafkaTemplate<Long, TransactionDetails> kafkaTemplate(){
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public ConsumerFactory<Long, TransactionDetails> consumerFactory() {
		return new TracingConsumerFactory<>(
				new DefaultKafkaConsumerFactory<>(
						(Map)consumerConfigProps.getProps()), tracer());
	}

	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, TransactionDetails>>
	kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<Long, TransactionDetails> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}
