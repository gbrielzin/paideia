"""Implementacao de referencia da TRI (modelo logistico de 3 parametros), escrita do zero.

O motor Java em server/ precisa reproduzir estes numeros (ver docs/tri-formula.md).
So usa a biblioteca padrao do Python.
"""
import json
import math

# Constantes do modelo. Precisam ser iguais as do Java.
GRID_MIN = -4.0
GRID_MAX = 6.0
GRID_STEP = 0.01
# Escala do ENEM para Matematica 2025: nota = 500 + 129,7 * theta. O 129,7 foi medido contra
# 4000 notas oficiais publicas (ver data/README.md e data/validate_against_inep.py).
SCALE_MEAN = 500.0
SCALE_SD = 129.7
D = 1.0


def load_exam(path):
    with open(path, encoding="utf-8") as f:
        return json.load(f)


def prob_correct(theta, a, b, c):
    """Chance de acertar o item para quem tem habilidade theta."""
    return c + (1.0 - c) / (1.0 + math.exp(-D * a * (theta - b)))


def _grid():
    n = int(round((GRID_MAX - GRID_MIN) / GRID_STEP)) + 1
    return [GRID_MIN + k * GRID_STEP for k in range(n)]


GRID = _grid()
# Prior: normal padrao (media 0, desvio 1) na escala theta.
LOG_PRIOR = [-0.5 * t * t for t in GRID]


def scored_items(exam):
    return [i for i in exam["items"] if not i["annulled"]]


def correctness(exam, answers):
    """Lista de (item, acertou) so dos itens que contam. answers: 45 letras."""
    result = []
    for idx, item in enumerate(exam["items"]):
        if item["annulled"]:
            continue
        result.append((item, answers[idx].upper() == item["answerKey"]))
    return result


_TABLES = {}


def _log_table(a, b, c, ok):
    """log da chance de ter acertado (ok) ou errado em cada ponto da grade; guardado para nao recalcular."""
    key = (a, b, c, ok)
    if key not in _TABLES:
        vals = []
        for t in GRID:
            p = prob_correct(t, a, b, c)
            vals.append(math.log(p if ok else 1.0 - p))
        _TABLES[key] = vals
    return _TABLES[key]


def estimate(items_correct):
    """Devolve (nota, erro) na escala do ENEM, pela media da distribuicao a posteriori (EAP)."""
    log_post = list(LOG_PRIOR)
    for item, ok in items_correct:
        table = _log_table(item["a"], item["b"], item["c"], ok)
        for k in range(len(GRID)):
            log_post[k] += table[k]
    m = max(log_post)
    w = [math.exp(v - m) for v in log_post]
    total = sum(w)
    mean = sum(wi * t for wi, t in zip(w, GRID)) / total
    var = sum(wi * (t - mean) ** 2 for wi, t in zip(w, GRID)) / total
    return SCALE_MEAN + SCALE_SD * mean, SCALE_SD * math.sqrt(var)


def analyze(exam, answers):
    """Nota, erro, mapa de erros e lista 'revise primeiro'."""
    ic = correctness(exam, answers)
    score, error = estimate(ic)
    error_map = []
    for idx, (item, ok) in enumerate(ic):
        if ok:
            continue
        flipped = [(it, True if j == idx else o) for j, (it, o) in enumerate(ic)]
        gain = estimate(flipped)[0] - score
        error_map.append({
            "position": item["position"],
            "skill": item["skill"],
            "scoreIfCorrect": score + gain,
            "cost": gain,
        })
    error_map.sort(key=lambda e: (-e["cost"], e["position"]))
    return {"score": score, "error": error, "errorMap": error_map}
