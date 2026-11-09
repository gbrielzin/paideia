package br.com.paideia.api;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.paideia.tri.Analysis;
import br.com.paideia.tri.ErrorCost;
import br.com.paideia.tri.Exam;
import br.com.paideia.tri.TriEngine;
import jakarta.validation.Valid;

/**
 * Calcula a nota estimada a partir das respostas. Sem estado (decisão 0003): não grava as respostas
 * e não escreve o conteúdo delas em log.
 */
@RestController
@RequestMapping("/api")
public class AnalysisController {

	static final String NOTICE = "Estimativa, não é a nota oficial.";
	static final int REVIEW_FIRST_SIZE = 5;

	private final TriEngine engine;
	private final AnalysisResponse.ExamInfo examInfo;

	public AnalysisController(TriEngine engine, Exam exam) {
		this.engine = engine;
		int scored = (int) exam.items().stream().filter(i -> !i.annulled()).count();
		this.examInfo = new AnalysisResponse.ExamInfo(exam.edition(), exam.area(), exam.color(), scored, exam.source());
	}

	@PostMapping("/analysis")
	public AnalysisResponse analyze(@Valid @RequestBody AnalysisRequest request) {
		Analysis analysis = engine.analyze(request.answers());
		List<ErrorCost> map = analysis.errorMap();
		List<ErrorCost> first = map.subList(0, Math.min(REVIEW_FIRST_SIZE, map.size()));
		return new AnalysisResponse(NOTICE, examInfo, analysis.score(), analysis.error(), map, first);
	}
}
