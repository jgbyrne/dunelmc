import requests

with open("./examples/cube.lmc") as prog:
    text = prog.read()

resp = requests.post("http://127.0.0.1:5000/compile", data=text)

print(resp.json())
