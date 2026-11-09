"""Gera o JSON de uma prova a partir do ITENS_PROVA_AAAA.csv do INEP.

Uso: python data/build_exam_json.py CAMINHO_DO_CSV CO_PROVA SAIDA.json
O CSV bruto fica fora do repositorio (data/raw/ e ignorado pelo git).
"""
import csv
import json
import sys


def main(csv_path, co_prova, out_path):
    with open(csv_path, encoding="latin-1", newline="") as f:
        rows = [r for r in csv.DictReader(f, delimiter=";") if r["CO_PROVA"] == co_prova]
    rows.sort(key=lambda r: int(r["CO_POSICAO"]))
    items = []
    for r in rows:
        annulled = r["IN_ITEM_ABAN"] == "1"
        items.append({
            "position": int(r["CO_POSICAO"]),
            "itemCode": int(r["CO_ITEM"]),
            "answerKey": r["TX_GABARITO"],
            "skill": int(r["CO_HABILIDADE"]),
            "annulled": annulled,
            "a": None if annulled else float(r["NU_PARAM_A"]),
            "b": None if annulled else float(r["NU_PARAM_B"]),
            "c": None if annulled else float(r["NU_PARAM_C"]),
        })
    exam = {
        "source": "INEP, microdados do ENEM 2025, arquivo DADOS/ITENS_PROVA_2025.csv",
        "sourceUrl": "https://download.inep.gov.br/microdados/microdados_enem_2025.zip",
        "retrievedOn": "2026-11-09",
        "edition": 2025,
        "area": "MT",
        "color": rows[0]["TX_COR"],
        "examCode": int(co_prova),
        "items": items,
    }
    with open(out_path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(exam, f, ensure_ascii=False, indent=1)
        f.write("\n")
    print(f"{len(items)} itens, {sum(i['annulled'] for i in items)} anulados/abandonados")


if __name__ == "__main__":
    main(*sys.argv[1:4])
