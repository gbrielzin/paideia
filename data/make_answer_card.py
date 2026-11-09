"""Gera o cartao-resposta em PDF (A4, preto e branco) a partir de server/src/main/resources/card/layout.json.

Uso: python data/make_answer_card.py
Saida: public/cartao-resposta-mt-2025.pdf (sempre o mesmo arquivo para o mesmo layout: nao grava data/hora).
So usa a biblioteca padrao do Python; escreve o PDF na mao (retas, circulos e texto Helvetica).
"""
import json
import os

HERE = os.path.dirname(os.path.abspath(__file__))
LAYOUT = os.path.join(HERE, "..", "server", "src", "main", "resources", "card", "layout.json")
OUT = os.path.join(HERE, "..", "public", "cartao-resposta-mt-2025.pdf")

PT = 72.0 / 25.4  # pontos por milimetro


def build(layout):
    page_h = layout["pageHeightMm"]
    ops = []

    def y(mm):
        return (page_h - mm) * PT

    def x(mm):
        return mm * PT

    def text(mm_x, mm_y, size, s, bold=False, align="left"):
        font = "F2" if bold else "F1"
        width = len(s) * size * 0.5  # estimativa suficiente para centralizar rotulos curtos
        px = x(mm_x) - (width / 2 if align == "center" else 0)
        data = s.encode("cp1252").replace(b"\\", b"\\\\").replace(b"(", b"\\(").replace(b")", b"\\)")
        ops.append(b"BT /" + font.encode() + b" %.2f Tf %.2f %.2f Td (" % (size, px, y(mm_y)) + data + b") Tj ET")

    def square(cx, cy, size):
        ops.append(("0 g %.2f %.2f %.2f %.2f re f" % (x(cx - size / 2), y(cy + size / 2), size * PT, size * PT)).encode())

    def circle(cx, cy, r):
        k = 0.5522847498 * r
        # curvas de Bezier; y do PDF cresce para cima, aqui em mm cresce para baixo (so muda o sinal do desvio)
        px = lambda mm: x(mm)
        py = lambda mm: y(mm)
        parts = ["%.2f %.2f m" % (px(cx + r), py(cy))]
        parts.append("%.2f %.2f %.2f %.2f %.2f %.2f c" % (px(cx + r), py(cy + k), px(cx + k), py(cy + r), px(cx), py(cy + r)))
        parts.append("%.2f %.2f %.2f %.2f %.2f %.2f c" % (px(cx - k), py(cy + r), px(cx - r), py(cy + k), px(cx - r), py(cy)))
        parts.append("%.2f %.2f %.2f %.2f %.2f %.2f c" % (px(cx - r), py(cy - k), px(cx - k), py(cy - r), px(cx), py(cy - r)))
        parts.append("%.2f %.2f %.2f %.2f %.2f %.2f c" % (px(cx + k), py(cy - r), px(cx + r), py(cy - k), px(cx + r), py(cy)))
        ops.append(("0 G 0.45 w " + " ".join(parts) + " S").encode())

    # marcas de alinhamento
    for cx, cy in layout["markCentersMm"]:
        square(cx, cy, layout["markSizeMm"])

    # titulo e instrucoes
    text(105, 27, 15, "PAIDEIA  -  Cartão-resposta", bold=True, align="center")
    text(105, 34, 10, "Matemática, ENEM 2025, prova azul, questões 136 a 180", align="center")
    instructions = [
        "1. Preencha a bolinha por completo, com caneta preta. Uma só bolinha por questão.",
        "2. Questão em branco: deixe as cinco bolinhas vazias.",
        "3. Não dobre, não risque e não cubra os quatro quadrados pretos dos cantos.",
        "4. Fotografe a folha inteira, de frente, em lugar bem iluminado, sem sombra em cima.",
        "5. Esta folha não tem nome nem dado pessoal. O resultado é uma estimativa, não a nota oficial.",
    ]
    for i, line in enumerate(instructions):
        text(24, 44 + i * 5.2, 8.5, line)

    # blocos de questoes
    letters = layout["letters"]
    rows = layout["rowsPerBlock"]
    radius = layout["bubbleDiameterMm"] / 2
    for q in range(layout["questions"]):
        block, row = divmod(q, rows)
        ox = layout["blockOriginsXMm"][block]
        cy = layout["firstRowCenterYMm"] + row * layout["rowPitchMm"]
        text(ox + 1, cy + 1.2, 9, str(layout["firstPosition"] + q), bold=True)
        for j in range(len(letters)):
            circle(ox + layout["firstBubbleOffsetXMm"] + j * layout["bubblePitchMm"], cy, radius)
    for block in range(layout["questions"] // rows):
        ox = layout["blockOriginsXMm"][block]
        for j, letter in enumerate(letters):
            cx = ox + layout["firstBubbleOffsetXMm"] + j * layout["bubblePitchMm"]
            text(cx, layout["firstRowCenterYMm"] - 7.5, 9, letter, bold=True, align="center")
            text(cx, layout["firstRowCenterYMm"] + (rows - 1) * layout["rowPitchMm"] + 10, 9, letter, bold=True, align="center")

    return b"\n".join(ops)


def pdf_bytes(layout, content):
    w, h = layout["pageWidthMm"] * PT, layout["pageHeightMm"] * PT
    objects = [
        b"<< /Type /Catalog /Pages 2 0 R >>",
        b"<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
        b"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 %.2f %.2f] /Resources << /Font << /F1 5 0 R /F2 6 0 R >> >> /Contents 4 0 R >>" % (w, h),
        b"<< /Length %d >>\nstream\n" % len(content) + content + b"\nendstream",
        b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>",
        b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>",
    ]
    out = bytearray(b"%PDF-1.4\n")
    offsets = []
    for i, body in enumerate(objects, start=1):
        offsets.append(len(out))
        out += b"%d 0 obj\n" % i + body + b"\nendobj\n"
    xref = len(out)
    out += b"xref\n0 %d\n0000000000 65535 f \n" % (len(objects) + 1)
    for off in offsets:
        out += b"%010d 00000 n \n" % off
    out += b"trailer\n<< /Size %d /Root 1 0 R >>\nstartxref\n%d\n%%%%EOF\n" % (len(objects) + 1, xref)
    return bytes(out)


def main():
    with open(LAYOUT, encoding="utf-8") as f:
        layout = json.load(f)
    data = pdf_bytes(layout, build(layout))
    with open(OUT, "wb") as f:
        f.write(data)
    print(f"{OUT}: {len(data)} bytes")


if __name__ == "__main__":
    main()
