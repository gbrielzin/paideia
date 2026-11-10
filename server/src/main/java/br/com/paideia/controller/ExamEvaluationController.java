package br.com.paideia.controller;

import br.com.paideia.domain.model.ExamEvaluationResult;
import br.com.paideia.dto.request.EvaluationRequest;
import br.com.paideia.dto.response.EvaluationResponse;
import br.com.paideia.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/evaluations")
public class ExamEvaluationController {

    private final EvaluationService evaluationService;

    public ExamEvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<EvaluationResponse> calculateEvaluation(@Valid @RequestBody EvaluationRequest request) {
        Map<Integer, String> answersMap = request.toAnswersMap();

        ExamEvaluationResult result = evaluationService.evaluate(
                request.examYear(),
                request.knowledgeArea(),
                request.bookletColor(),
                answersMap
        );

        return ResponseEntity.ok(EvaluationResponse.fromDomain(result));
    }
}
