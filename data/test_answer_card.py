"""O PDF do cartao-resposta commitado precisa ser o que o script gera hoje. Rodar: python -m unittest discover -s data"""
import json
import os
import unittest

import make_answer_card as card


class AnswerCardTest(unittest.TestCase):
    def test_committed_pdf_matches_script(self):
        with open(card.LAYOUT, encoding="utf-8") as f:
            layout = json.load(f)
        fresh = card.pdf_bytes(layout, card.build(layout))
        with open(card.OUT, "rb") as f:
            self.assertEqual(f.read(), fresh)
        self.assertTrue(fresh.startswith(b"%PDF-1.4"))

    def test_layout_has_room_for_all_questions(self):
        with open(card.LAYOUT, encoding="utf-8") as f:
            layout = json.load(f)
        self.assertEqual(layout["questions"], layout["rowsPerBlock"] * len(layout["blockOriginsXMm"]))


if __name__ == "__main__":
    unittest.main()
