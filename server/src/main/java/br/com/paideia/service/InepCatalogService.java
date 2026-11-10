package br.com.paideia.service;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;
import br.com.paideia.domain.model.InepExam;
import br.com.paideia.domain.model.InepItem;
import tools.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class InepCatalogService {

    private final ResourcePatternResolver resourcePatternResolver;
    private final ObjectMapper objectMapper;
    private final Map<String, InepExam> examCatalog = new ConcurrentHashMap<>();

    public InepCatalogService(ResourcePatternResolver resourcePatternResolver, ObjectMapper objectMapper) {
        this.resourcePatternResolver = resourcePatternResolver;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadCatalog() {
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:data/*.json");
            log.info("Carregando catálogo de provas do INEP (encontrados {} arquivos de dados)", resources.length);

            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    InepExam exam = objectMapper.readValue(inputStream, InepExam.class);
                    validateExam(exam, resource.getFilename());
                    String key = buildCatalogKey(exam.year(), exam.knowledgeArea(), exam.bookletColor());
                    examCatalog.put(key, exam);
                    log.info("Prova carregada com sucesso: {} | {} | {} ({} itens)",
                            exam.year(), exam.knowledgeArea(), exam.bookletColor(), exam.items().size());
                } catch (Exception e) {
                    log.error("Erro ao carregar arquivo de prova: {}", resource.getFilename(), e);
                    throw new IllegalStateException("Falha ao inicializar catálogo do INEP: " + resource.getFilename(), e);
                }
            }

            log.info("Catálogo do INEP inicializado em memória com total de {} provas.", examCatalog.size());
        } catch (Exception e) {
            log.error("Falha ao buscar recursos de dados do INEP", e);
            throw new IllegalStateException("Não foi possível carregar os dados do INEP em memória", e);
        }
    }

    public Optional<InepExam> getExam(int year, KnowledgeArea knowledgeArea, BookletColor bookletColor) {
        if (knowledgeArea == null || bookletColor == null) {
            return Optional.empty();
        }
        String key = buildCatalogKey(year, knowledgeArea, bookletColor);
        return Optional.ofNullable(examCatalog.get(key));
    }

    public List<InepExam> listAllExams() {
        return examCatalog.values().stream()
                .sorted(Comparator.comparingInt(InepExam::year).reversed()
                        .thenComparing(e -> e.knowledgeArea().name())
                        .thenComparing(e -> e.bookletColor().name()))
                .toList();
    }

    public int countExams() {
        return examCatalog.size();
    }

    public int countTotalItems() {
        return examCatalog.values().stream()
                .mapToInt(e -> e.items() != null ? e.items().size() : 0)
                .sum();
    }

    private void validateExam(InepExam exam, String filename) {
        if (exam == null) {
            throw new IllegalArgumentException("Arquivo de prova nulo ou vazio: " + filename);
        }
        if (exam.year() <= 0) {
            throw new IllegalArgumentException("Ano inválido no arquivo: " + filename);
        }
        if (exam.knowledgeArea() == null || exam.bookletColor() == null) {
            throw new IllegalArgumentException("Área do conhecimento ou cor do caderno ausente em: " + filename);
        }
        if (exam.items() == null || exam.items().isEmpty()) {
            throw new IllegalArgumentException("Nenhum item encontrado na prova: " + filename);
        }

        for (InepItem item : exam.items()) {
            if (item.questionNumber() <= 0) {
                throw new IllegalArgumentException("Número de questão inválido em " + filename + ": " + item.questionNumber());
            }
            if (item.correctAlternative() == null || item.correctAlternative().isBlank()) {
                throw new IllegalArgumentException("Gabarito ausente para a questão " + item.questionNumber() + " em " + filename);
            }
            if (item.discriminationA() <= 0.0) {
                throw new IllegalArgumentException("Parâmetro 'a' (discriminação) deve ser positivo na questão " + item.questionNumber());
            }
            if (item.guessingC() < 0.0 || item.guessingC() >= 1.0) {
                throw new IllegalArgumentException("Parâmetro 'c' (chute) deve estar no intervalo [0, 1) na questão " + item.questionNumber());
            }
        }
    }

    private String buildCatalogKey(int year, KnowledgeArea knowledgeArea, BookletColor bookletColor) {
        return year + "_" + knowledgeArea.name() + "_" + bookletColor.name();
    }
}
