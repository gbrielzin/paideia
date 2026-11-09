"""Compara a nota estimada com a nota oficial do INEP em participantes dos microdados publicos.

Uso: python data/validate_against_inep.py RESULTADOS_2025.csv [maximo_de_participantes] [passo]
Com passo N, usa um participante a cada N da prova (espalha a amostra pelo arquivo).
O arquivo bruto fica fora do repositorio. Nada individual e impresso, so numeros agregados.
"""
import csv
import os
import statistics
import sys

import tri_reference as tri

EXAM = os.path.join(os.path.dirname(__file__), "..", "server", "src", "main", "resources",
                    "exams", "enem-2025-mt-azul.json")


def main(path, limit=4000, step=1):
    exam = tri.load_exam(EXAM)
    code = str(exam["examCode"])
    diffs = []
    seen = 0
    with open(path, encoding="utf-8", errors="replace", newline="") as f:
        for row in csv.DictReader(f, delimiter=";"):
            if row.get("CO_PROVA_MT") != code or not row.get("NU_NOTA_MT"):
                continue
            answers = row["TX_RESPOSTAS_MT"]
            if len(answers) != 45:
                continue
            seen += 1
            if seen % int(step):
                continue
            est, _ = tri.estimate(tri.correctness(exam, answers))
            diffs.append(est - float(row["NU_NOTA_MT"]))
            if len(diffs) >= int(limit):
                break
    abs_d = sorted(abs(d) for d in diffs)
    n = len(abs_d)
    print(f"participantes comparados: {n}")
    print(f"vies medio (estimada menos oficial): {statistics.mean(diffs):.3f}")
    print(f"mediana do desvio absoluto: {abs_d[n // 2]:.3f}")
    print(f"percentil 99 do desvio absoluto: {abs_d[int(n * 0.99)]:.3f}")
    print(f"maior desvio: {abs_d[-1]:.1f}")
    print(f"participantes com desvio acima de 2 pontos: {sum(d > 2 for d in abs_d)}")


if __name__ == "__main__":
    main(*sys.argv[1:4])
