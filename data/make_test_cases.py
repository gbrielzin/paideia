"""Gera os casos de teste (respostas de entrada e saida esperada) que o Java precisa reproduzir.

As respostas sao FICTICIAS: sorteadas com semente fixa, sem relacao com pessoas reais.
Uso: python data/make_test_cases.py
Escreve o mesmo arquivo em data/ e em server/src/test/resources/.
"""
import json
import os
import random

import tri_reference as tri

HERE = os.path.dirname(__file__)
EXAM = os.path.join(HERE, "..", "server", "src", "main", "resources", "exams", "enem-2025-mt-azul.json")
OUTS = [
    os.path.join(HERE, "test_cases_mt_2025_azul.json"),
    os.path.join(HERE, "..", "server", "src", "test", "resources", "test_cases_mt_2025_azul.json"),
]
LETTERS = "ABCDE"


def wrong_letter(key):
    return "A" if key != "A" else "B"


def build_cases(exam):
    keys = [i["answerKey"] for i in exam["items"]]
    rng = random.Random(2025)
    cases = []

    def add(name, answers):
        cases.append({"name": name, "answers": answers, "expected": tri.analyze(exam, answers)})

    all_right = "".join(k if k in LETTERS else "A" for k in keys)
    add("tudo certo", all_right)
    add("tudo errado", "".join(wrong_letter(k) for k in keys))
    add("tudo em branco", "." * 45)
    add("letras minusculas", all_right.lower())
    # item anulado respondido de dois jeitos tem de dar a mesma nota
    annulled = [n for n, i in enumerate(exam["items"]) if i["annulled"]]
    half = list(all_right)
    for n, i in enumerate(exam["items"]):
        if n % 2 == 0 and not i["annulled"]:
            half[n] = wrong_letter(keys[n])
    base = "".join(half)
    add("metade certa, anulados como no gabarito", base)
    changed = list(base)
    for n in annulled:
        changed[n] = "C"
    add("metade certa, anulados trocados", "".join(changed))
    for pct in (10, 20, 30, 40, 50, 60, 70, 80, 90):
        for rep in range(2):
            ans = []
            for k in keys:
                if rng.random() < pct / 100.0:
                    ans.append(k if k in LETTERS else "A")
                else:
                    ans.append(rng.choice([x for x in LETTERS if x != k]))
            add(f"sorteio {pct}% de acerto, repeticao {rep + 1}", "".join(ans))
    return cases


def main():
    exam = tri.load_exam(EXAM)
    out = {
        "examCode": exam["examCode"],
        "note": "Respostas ficticias sorteadas com semente 2025. Saida gerada por data/tri_reference.py.",
        "cases": build_cases(exam),
    }
    for path in OUTS:
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            json.dump(out, f, ensure_ascii=False, indent=1)
            f.write("\n")
    print(f"{len(out['cases'])} casos escritos")


if __name__ == "__main__":
    main()
