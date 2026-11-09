package br.com.paideia.tri;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TriConfig {

	@Bean
	Exam exam() {
		return ExamLoader.load(ExamLoader.DEFAULT_EXAM);
	}

	@Bean
	TriEngine triEngine(Exam exam) {
		return new TriEngine(exam);
	}
}
