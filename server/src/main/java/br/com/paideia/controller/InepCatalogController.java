package br.com.paideia.controller;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;
import br.com.paideia.dto.response.InepExamDetailDto;
import br.com.paideia.dto.response.InepExamSummaryDto;
import br.com.paideia.exception.ExamNotFoundException;
import br.com.paideia.service.InepCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exams")
public class InepCatalogController {

    private final InepCatalogService inepCatalogService;

    public InepCatalogController(InepCatalogService inepCatalogService) {
        this.inepCatalogService = inepCatalogService;
    }

    @GetMapping
    public ResponseEntity<List<InepExamSummaryDto>> listExams() {
        List<InepExamSummaryDto> exams = inepCatalogService.listAllExams().stream()
                .map(InepExamSummaryDto::fromDomain)
                .toList();
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{year}/{knowledgeArea}/{bookletColor}")
    public ResponseEntity<InepExamDetailDto> getExamDetails(
            @PathVariable int year,
            @PathVariable KnowledgeArea knowledgeArea,
            @PathVariable BookletColor bookletColor
    ) {
        return inepCatalogService.getExam(year, knowledgeArea, bookletColor)
                .map(InepExamDetailDto::fromDomain)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ExamNotFoundException(
                        String.format("Prova não encontrada para o ano %d, área %s e caderno %s.",
                                year, knowledgeArea, bookletColor)
                ));
    }
}
