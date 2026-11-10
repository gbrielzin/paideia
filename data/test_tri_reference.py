"""Testes simples da referencia em Python. Rodar: python -m unittest discover -s data"""
import json
import os
import unittest

import tri_reference as tri

HERE = os.path.dirname(__file__)
EXAM = tri.load_exam(os.path.join(HERE, "..", "server", "src", "main", "resources", "exams",
                                  "enem-2025-mt-azul.json"))


def answers_from(fn):
    return "".join(fn(i["answerKey"]) for i in EXAM["items"])


class TriTest(unittest.TestCase):
    def test_more_correct_gives_higher_score(self):
        right = tri.analyze(EXAM, answers_from(lambda k: k if k in "ABCDE" else "A"))
        wrong = tri.analyze(EXAM, answers_from(lambda k: "A" if k != "A" else "B"))
        self.assertGreater(right["score"], wrong["score"])

    def test_error_map_sorted_by_cost(self):
        res = tri.analyze(EXAM, "." * 45)
        costs = [e["cost"] for e in res["errorMap"]]
        self.assertEqual(costs, sorted(costs, reverse=True))
        self.assertEqual(len(costs), 43)
        self.assertTrue(all(c > 0 for c in costs))

    def test_annulled_item_does_not_change_score(self):
        base = list("." * 45)
        other = list(base)
        for n, item in enumerate(EXAM["items"]):
            if item["annulled"]:
                other[n] = "C"
        self.assertEqual(tri.analyze(EXAM, "".join(base))["score"],
                         tri.analyze(EXAM, "".join(other))["score"])

    def test_saved_cases_are_up_to_date(self):
        with open(os.path.join(HERE, "test_cases_mt_2025_azul.json"), encoding="utf-8") as f:
            saved = json.load(f)
        for case in saved["cases"]:
            fresh = tri.analyze(EXAM, case["answers"])
            self.assertAlmostEqual(fresh["score"], case["expected"]["score"], places=9)


if __name__ == "__main__":
    unittest.main()
