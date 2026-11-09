package br.com.paideia.card;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardConfig {

	@Bean
	CardLayout cardLayout() {
		return CardLayout.load();
	}

	@Bean
	CardReader cardReader(CardLayout layout) {
		return new CardReader(layout);
	}
}
