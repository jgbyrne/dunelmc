import requests

with open("./examples/cube.lmc") as prog:
    text = prog.read()

resp = requests.post("http://127.0.0.1:10122/compile", params={}, data=text)
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

