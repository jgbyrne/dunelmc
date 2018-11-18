import os
import sys
import argparse
import re

import socket
import requests

SINGLE_RE = re.compile("([A-Z]{3}\([A-Za-z]*\))")
LABEL_RE  = re.compile("([A-Z]{3}\([A-Za-z]*(\,[A-Za-z]+)+\))")
NUM_RE    = re.compile("([A-Z]{3}\([A-Za-z]*(\,[0-9]+)+\))")
JUMP_TO_RE= re.compile("(JTP\([A-Za-z]*\))")
BRANCH_RE = re.compile("(BRA\([A-Za-z]*\,[A-Za-z]\,[A-Za-z]\))")

def next_system_var(x):
    global s_vars
    l = len(s_vars)
    s_vars["SC"+str(l+1)] = x
    return "SC" + str(l+1)

if __name__ == "__main__":

    
    ap = argparse.ArgumentParser()
    ap.add_argument("input_file")

    args = vars(ap.parse_args())
    input_file = args["input_file"]
    text = None
    
    try:
        with open(input_file) as reader:
            text = reader.read()
    except FileNotFoundError as e:
        print(e)
        exit(-1)

    text = text.replace("\t", "").replace("\n", "").replace(" ", "")
##    print("text")
##    print(text)

    commands = text.split(";")[:-1]
##    print(commands)

    init_dict = {}
    s_vars = {}

    output = ""

    max_tvars = 0
    loop_count = 0
    temp_vars = []

##    input()
    
    for command in commands:
        a = re.match(SINGLE_RE, command)
        b = re.match(LABEL_RE,  command)
        c = re.match(NUM_RE,    command)
        d = re.match(BRANCH_RE, command)

##        print(command)
        if a:
##            print("I/O")
            instruction = a[0][:3]
            if instruction == "INP":
##                print("input")
                if a[0][4:-1] not in init_dict:
                    init_dict[a[0][4:-1]] = ""
                output += "\tIN\n\tSTO\t" + a[0][4:-1] + "\n"
                
            elif instruction == "OUT":
##                print("output")
                if a[0][4:-1] in init_dict:
                    output += "\tLDA\t" + a[0][4:-1] + "\n\tOUT\n"
                else:
                    print("Error: variable referenced before assignment")

            elif instruction == "JTP":
                if "SC0" not in s_vars:
                    s_vars["SC0"] = "0"
                output += a[0][4:-1] + "\tLDA\tSC0\n"

                
        elif b: # label
##            print("label")
            instruction = b[0][:3]
            if instruction == "MOV":
                mov_vars = b[0][4:-1].split(",")
                if mov_vars[0] not in init_dict:
                    try:
                        init_dict[mov_vars[0]] = init_dict[mov_vars[1]]
                    except KeyError:
                        print(f"{mov_vars[1]} is not defined within the system")
                        break
                else:
                    output += "\tLDA\t" + mov_vars[1] +"\n\tSTO\t" + mov_vars[0] + "\n"

            elif instruction == "INC":
                inc_vars = b[0][4:-1].split(",")
                output += "\tLDA\t" + inc_vars[0] + "\n\tADD\t" + inc_vars[1] + "\n"

            elif instruction == "DEC":
                dec_vars = b[0][4:-1].split(",")
                output += "\tLDA\t" + inc_vars[0] + "\n\tSUB\t" + inc_vars[1] + "\n"

            elif instruction == "MUL":
                mul_vars = b[0][4:-1].split(",")

                loop_count += 1
                
                if "SC0" not in s_vars:
                    s_vars["SC0"] = "0"
                if "SC1" not in s_vars:
                    s_vars["SC1"] = "1"

                temp_var1 = next_system_var(0)
                temp_var2 = next_system_var(0)


                output += "\tLDA\t" + mul_vars[0] + "\n"
                output += "\tSTO\t" + temp_var1 + "\n"
                output += "\tLDA\tSC0\n"
                output += "\tSTO\t" + temp_var2 + "\n"

                output += "l" + str(loop_count) + "\tLDA\t" + temp_var1 + "\n"
                output += "\tBRZ\te" + str(loop_count) + "\n"
                output += "\tSUB\tSC1\n"
                output += "\tSTO\t" + temp_var1 + "\n"
                output += "\tLDA\t" + temp_var2 + "\n"
                output += "\tADD\t" + mul_vars[1] + "\n"
                output += "\tSTO\t" + temp_var2 + "\n"
                output += "\tBRA\t" + "l" + str(loop_count) + "\n"
                output += "e" + str(loop_count) + "\tLDA\t" + temp_var2 + "\n"
                output += "\tSTO\t" + mul_vars[0] + "\n"

            elif instruction == "DIV":
                div_vars = b[0][4:-1].split(",")
                
                loop_count += 1

                if "SC0" not in s_vars:
                    s_vars["SC0"] = "0"
                if "SC1" not in s_vars:
                    s_vars["SC1"] = "1"

                temp_var1 = next_system_var(0)


                output += "l" + str(loop_count) + "\tLDA\t" + div_vars[0] + "\n"
                output += "\tSUB\t" + div_vars[1] + "\n"
                output += "\tBRP\tl" + str(loop_count + 1) + "\n"
                output += "\tLDA\t" + temp_var1 + "\n"
                output += "\tSTO\t" + div_vars[0] + "\n"
                output += "\tBRA\t" + "e" + str(loop_count) + "\n"
                output += "l" + str(loop_count + 1) + "\tSTO\t" + div_vars[0] + "\n"
                output += "\tLDA\t" + temp_var1 + "\n"
                output += "\tADD\tSC1\n"
                output += "\tSTO\t" + temp_var1 + "\n"
                output += "\tBRA\tl" + str(loop_count) + "\n"
                output += "e" + str(loop_count) + "\tLDA\tSC0\n"

                loop_count += 1

            elif instruction == "WHS":
                bra_vars = b[0][4:-1].split(",")
                jump = "JTP(" + bra_vars[2] + ")"
                if jump not in text:
                    print("Branch to jump point not been decleared")
                    break
                else:
                    output += "\tLDA\t" + bra_vars[0] + "\n"
                    output += "\tSUB\t" + bra_vars[1] + "\n"
                    output += "\tBRZ\t" + bra_vars[2] + "\n"

            elif instruction == "WHL":
                bra_vars = b[0][4:-1].split(",")
                jump = "JTP(" + bra_vars[2] + ")"
                if jump not in text:
                    print("Branch to jump point has not been decleared")
                    break
                else:
                    output += "\tLDA\t" + bra_vars[0] + "\n"
                    output += "\tSUB\t" + bra_vars[1] + "\n"
                    output += "\tBRP\t" + bra_vars[2] + "\n"

                
                    
        elif c: # number
##            print("number")
            instruction = c[0][:3]
            if instruction == "MOV":
                mov_vars = c[0][4:-1].split(",")
                if mov_vars[0] not in init_dict:
                    init_dict[mov_vars[0]] = mov_vars[1]
                else:
                    init_dict["SC" + str(mov_vars[1])] = mov_vars[1]
                    output += "\tLDA\tSC" + str(mov_vars[1]) + "\n\tSTO\t" + mov_vars[0] + "\n"
            elif instruction == "INC":
                mov_vars = c[0][4:-1].split(",")
                temp = next_system_var(mov_vars[1])
                output += "\tLDA\t" + mov_vars[0] + "\n"
                output += "\tADD\t" + temp + "\n"
                output += "\tSTO\t" + mov_vars[0] + "\n"
            elif instruction == "DEC":
                mov_vars = c[0][4:-1].split(",")
                temp = next_system_var(mov_vars[1])
                output += "\tLDA\t" + mov_vars[0] + "\n"
                output += "\tSUB\t" + temp + "\n"
                output += "\tSTO\t" + mov_vars[0] + "\n"
            elif instruction == "MUL":
                mul_vars = b[0][4:-1].split(",")

                loop_count += 1
                
                if "SC0" not in s_vars:
                    s_vars["SC0"] = "0"
                if "SC1" not in s_vars:
                    s_vars["SC1"] = "1"

                temp_var1 = next_system_var(0)
                temp_var2 = next_system_var(0)

                temp = next_system_var(mul_vars[1])


                output += "\tLDA\t" + mul_vars[0] + "\n"
                output += "\tSTO\t" + temp_var1 + "\n"
                output += "\tLDA\tSC0\n"
                output += "\tSTO\t" + temp_var2 + "\n"

                output += "l" + str(loop_count) + "\tLDA\t" + temp_var1 + "\n"
                output += "\tBRZ\te" + str(loop_count) + "\n"
                output += "\tSUB\tSC1\n"
                output += "\tSTO\t" + temp_var1 + "\n"
                output += "\tLDA\t" + temp_var2 + "\n"
                output += "\tADD\t" + temp + "\n"
                output += "\tSTO\t" + temp_var2 + "\n"
                output += "\tBRA\t" + "l" + str(loop_count) + "\n"
                output += "e" + str(loop_count) + "\tLDA\t" + temp_var2 + "\n"
                output += "\tSTO\t" + mul_vars[0] + "\n"

            elif instruction == "DIV":
                div_vars = c[0][4:-1].split(",")
                
                loop_count += 1

                if "SC0" not in s_vars:
                    s_vars["SC0"] = "0"
                if "SC1" not in s_vars:
                    s_vars["SC1"] = "1"

                temp_var1 = next_system_var(0)

                temp = next_system_var(div_vars[1])

                output += "l" + str(loop_count) + "\tLDA\t" + div_vars[0] + "\n"
                output += "\tSUB\t" + temp + "\n"
                output += "\tBRP\tl" + str(loop_count + 1) + "\n"
                output += "\tLDA\t" + temp_var1 + "\n"
                output += "\tSTO\t" + div_vars[0] + "\n"
                output += "\tBRA\t" + "e" + str(loop_count) + "\n"
                output += "l" + str(loop_count + 1) + "\tSTO\t" + div_vars[0] + "\n"
                output += "\tLDA\t" + temp_var1 + "\n"
                output += "\tADD\tSC1\n"
                output += "\tSTO\t" + temp_var1 + "\n"
                output += "\tBRA\tl" + str(loop_count) + "\n"
                output += "e" + str(loop_count) + "\tLDA\tSC0\n"

                loop_count += 1

            
                
                
        else:
            if command == "LINEBREAK":
                output += "LINEBREAK\n"
            else:
                #throw error
                print("printing command")
                print(command)
                print("Error: Invalid syntax")
                break
    output += "\tHLT\n"
    #init all vars
    for v in init_dict:
        output += v + "\tDAT\t" + init_dict[v] + "\n"
    for v in s_vars:
        output += v + "\tDAT\t" + str(s_vars[v]) + "\n"

resp = requests.post("http://127.0.0.1:10122/compile", params={}, data=output)
eid = resp.json()["exec_id"]

requests.post("http://127.0.0.1:10122/input", params={"exec_id": eid}, data="5")

ctr = 0
while True:
    stepr = requests.get("http://127.0.0.1:10122/run", params={"exec_id": eid})
    if stepr.status_code != 200:
        if stepr.status_code == 201:
            print(stepr.text)
        else:
            break
    ctr += 1

"""
TO-DO
Make sure that no labels start with SV
Add free_system_var()
"""
    
