from enum import Enum
import re
import sys
import flask
from flask import Flask
import json

app = Flask(__name__)

class Op(Enum):
    ADD = 1
    SUB = 2
    STA = 3
    LDA = 5
    BRA = 6
    BRZ = 7
    BRP = 8
    IO  = 9
    HLT = 0

def lookup(op):
    if op == "ADD":
        return 100
    if op == "SUB":
        return 200
    if op == "STA" or op == "STO":
        return 300
    if op == "LDA":
        return 500
    if op == "BRA" or op == "BR":
        return 600
    if op == "BRZ":
        return 700
    if op == "BRP":
        return 800
    if op == "INP" or op == "IN":
        return 901
    if op == "OUT":
        return 902
    if op == "HLT" or op == "COB" or op == "DAT":
        return 000
    return None

class Assembler:
    comments = re.compile(".*((;|//|#).*)")

    def write_op(self, tok):
        val = lookup(tok)
        if val is not None:
            self.mem[self.ptr] += lookup(tok)
        else:
            print("Encountered Bad Token: {}".format(tok))
            sys.exit(1)

    def write_val(self, val):
        try:
            if 0 <= int(val) < (100 if self.mem[self.ptr] else 1000):
                self.mem[self.ptr] += int(val)
            else:
                print("Bad numerical value: {}".format(val))
                sys.exit(1)
        except ValueError:
            if val in self.symbols:
                self.mem[self.ptr] += self.symbols[val]
            else:
                self.links[self.ptr] = val

    def write_label(self, tok):
        if tok in self.symbols:
            print("Encountered same label twice: {}".format(tok))
            sys.exit(1)
        self.symbols[tok] = self.ptr

    def __init__(self, lines):
        self.mem = [000] * 100
        self.symbols = {}
        self.links = {}
        self.ptr = 000
        for line in lines.split("\n"):
            cmatch = Assembler.comments.match(line)
            if cmatch is not None:
                line = line[:cmatch.span(1)[0]]
            toks = line.split()
            tlen = len(toks)
            if tlen == 0:
                continue
            if tlen == 1:
                self.write_op(toks[0])
            elif tlen == 2:
                # first token is instruction
                if lookup(toks[0]) is not None:
                    self.write_op(toks[0])
                    self.write_val(toks[1])
                else:
                    self.write_label(toks[0])
                    self.write_op(toks[1])
            elif tlen == 3:
                self.write_label(toks[0])
                self.write_op(toks[1])
                self.write_val(toks[2])
            self.ptr += 1

        for lnptr, lnval in self.links.items():
            self.mem[lnptr] += self.symbols[lnval]

def curtail(n):
    if n > 999:
        return (n % 1000, False)
    elif n < 0:
        return (1000 + n, True)
    return (n, False)


class Exec:
    HALT    = 0
    SUCCESS = 2
    OUTPUT  = 3
    INPUT   = 4

    def __init__(self, prog, symbols={}):
        self.memory = [000] * 100
        for i, d in enumerate(prog):
            self.memory[i] = d
        self.symbols = symbols
        self.begun   = False
        self.inputs  = []
        self.outputs = []
        self.outbox  = 000
        self.inbox   = 000
        self.pc      = 000
        self.ip      = 000
        self.acc     = 000
        self.neg     = False

    def cycle(self, inp = None):
        if not self.begun: self.begun = True
        self.ip = self.pc
        ir = self.memory[self.ip]
        self.pc += 1
        op = Op(ir // 100)
        xx = ir - (op.value * 100)
        if op == Op.ADD:
            self.neg = False
            self.acc, _ = curtail(self.acc + self.memory[xx])

        elif op == Op.SUB:
            self.acc, self.neg = curtail(self.acc - self.memory[xx])

        elif op == Op.STA:
            self.memory[xx] = self.acc

        elif op == Op.LDA:
            self.neg = False
            self.acc = self.memory[xx]

        elif op == Op.BRA:
            self.pc = xx

        elif op == Op.BRZ:
            if not self.neg:
                if self.acc == 000:
                    self.pc = xx

        elif op == Op.BRP:
            if not self.neg:
                self.pc = xx

        elif op == Op.IO:
            if xx == 1:
                self.neg = False
                self.acc, _ = curtail(inp)
                self.inbox = self.acc
                self.inputs.append(self.acc)
                return Exec.INPUT
            elif xx == 2:
                self.outbox = self.acc
                self.outputs.append(self.outbox)
                return Exec.OUTPUT
        elif op == Op.HLT:
            return Exec.HALT

        return Exec.SUCCESS

    def needs_input(self):
        return self.memory[self.pc] == 901

class ESesh:
    def __init__(self, exec_, prog):
        self.exec_ = exec_
        self.asm = Assembler(prog)
        self.inp = None

    def execute(self):
        self.ex = Exec(self.asm.mem)

next_exec = 1
sessions  = {}

@app.route("/compile", methods=['POST'])
def compile():
    global next_exec
    global sessions

    s = ESesh(next_exec, flask.request.data.decode("utf-8"))
    asm = []
    for i, mb in enumerate(s.asm.mem):
        asm.append({"addr": i ,
                    "data": mb,
                    "lno" : int(999),
                    "line": "-----"
                    })
    registers = {"outbox": "000", "inbox": "000", "pc": 0, "ip": 0, "acc": "000", "neg": False}

    sessions[next_exec] = s
    next_exec += 1
    s.execute()
    return json.dumps({"exec_id" : s.exec_, "asm" : asm, "labels": s.asm.symbols, "registers": registers})

@app.route("/input", methods=["POST"])
def input():
    global sessions

    print(flask.request.data.decode("utf-8"))

    if flask.request.args["exec_id"] is None:
        return ("", 404, {})

    sessions[int(flask.request.args["exec_id"])].inp = flask.request.data.decode("utf-8")
    return ("", 200, {})

@app.route("/step", methods=["GET"])
def step():
    global sessions

    if flask.request.args["exec_id"] is None:
        return ("", 404, {})

    exec_id = int(flask.request.args["exec_id"])
    ex = sessions[exec_id].ex
    if ex.needs_input():
        if sessions[exec_id].inp is None:
            return (json.dumps({"exec_id" : exec_id, "asm" : asm, "labels": sessions[exec_id].asm.symbols, "registers": registers}), 412, {})
        else:
            try:
                code = ex.cycle(inp = int(sessions[exec_id].inp))
            except (EOFError, ValueError):
                return ("", 422, {})
            else:
                inp = None
    else:
        code = ex.cycle()

    asm = []
    for i, mb in enumerate(ex.memory):
        asm.append({"addr": i ,
                    "data": mb,
                    "lno" : int(999),
                    "line": "-----"
                    })

        registers = {"outbox": "{:03d}".format(ex.outbox), "inbox": "{:03d}".format(ex.inbox), "pc": ex.pc, "ip": ex.ip, "acc": "{:03d}".format(ex.acc), "neg": ex.neg}

    print(registers)

    if code == Exec.HALT:
        return (json.dumps({"exec_id" : exec_id, "asm" : asm, "labels": sessions[exec_id].asm.symbols, "registers": registers}), 202, {})
    if code == Exec.OUTPUT:
        return (str(ex.outbox), 201, {})
    else:
        return (json.dumps({"exec_id" : exec_id, "asm" : asm, "labels": sessions[exec_id].asm.symbols, "registers": registers}), 200, {})

@app.route("/run", methods=["GET"])
def run():
    global sessions

    if flask.request.args["exec_id"] is None:
        return ("", 404, {})

    exec_id = int(flask.request.args["exec_id"])
    ex = sessions[exec_id].ex
    while True:
        if ex.needs_input():
            if sessions[exec_id].inp is None:
                return ("", 412, {})
            else:
                try:
                    code = ex.cycle(inp = int(sessions[exec_id].inp))
                except (EOFError, ValueError):
                    return ("", 422, {})
                else:
                    sessions[exec_id].inp = None
        else:
            code = ex.cycle()
        if code == Exec.HALT or code == Exec.OUTPUT:
            break

    asm = []
    for i, mb in enumerate(ex.memory):
        asm.append({"addr": i,
                    "data": mb,
                    "lno" : int(999),
                    "line": "-----"
                    })

            registers = {"outbox": "{:03d}".format(ex.outbox), "inbox": "{:03d}".format(ex.inbox), "pc": ex.pc, "ip": ex.ip, "acc": "{:03d}".format(ex.acc), "neg": ex.neg}

    print(registers)

    if code == Exec.HALT:
        return ("", 202, {})
    if code == Exec.OUTPUT:
        return (str(ex.outbox), 201, {})
    else:
        return (json.dumps({"exec_id" : exec_id, "asm" : asm, "labels": sessions[exec_id].asm.symbols, "registers": registers}), 200, {})

app.run(host="172.20.10.7", port=80)#10122)
