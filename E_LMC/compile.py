import os
import sys
import argparse
import re

SINGLE_RE = re.compile("([A-Z]{3}\([A-Z]*\))")
LABEL_RE  = re.compile("([A-Z]{3}\([A-Z]*(\,[A-Z]+)+\))")
NUM_RE    = re.compile("([A-Z]{3}\([A-Z]*(\,[0-9]+)+\))")

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
    print("text")
    print(text)

    commands = text.split(";")[:-1]
    print(commands)

    init_dict = {}
    s_vars = {}

    output = ""

    max_tvars = 0
    loop_count = 0
    temp_vars = []
    
    for command in commands:
        a = re.match(SINGLE_RE, command)
        b = re.match(LABEL_RE,  command)
        c = re.match(NUM_RE,    command)
        if a:
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
        elif b: # label
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
                
                if "SC0" not in s_vars:
                    s_vars["SC0"] = "0"
                if "SC1" not in s_vars:
                    s_vars["SC1"] = "1"
                    
                temp_vars.append("SV_TEMP" + str(len(temp_vars)))
                temp_vars.append("SV_TEMP" + str(len(temp_vars)))
                
                if len(temp_vars) > max_tvars:
                    max_tvars = len(temp_vars)
                    
                output += "\tLDA\t" + mul_vars[0] + "\n"
                output += "\tSTO\t" + temp_vars[-2] + "\n"

                output += "\tLDA\tSC0\n"
                output += "\tSTO\t" + temp_vars[-1] + "\n"

                output += "\tLDA\t"
                
                    
        elif c: # number
            instruction = c[0][:3]
            if instruction == "MOV":
                mov_vars = c[0][4:-1].split(",")
                if mov_vars[0] not in init_dict:
                    init_dict[mov_vars[0]] = mov_vars[1]
                else:
                    init_dict["SC" + str(mov_vars[1])] = mov_vars[1]
                    output += "\tLDA\tSC" + str(mov_vars[1]) + "\n\tSTO\t" + mov_vars[0] + "\n"
        else:
            #throw error
            print("printing command")
            print(command)
            print("Error: Invalid syntax")
            break

    #init all vars
    for v in init_dict:
        output += v + "\tDAT\t" + init_dict[v] + "\n"
    print(output)
    
    print(text)




"""
TO-DO
Make sure that no labels start with SV
"""
    
