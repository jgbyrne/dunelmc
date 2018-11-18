## API Documentation

This document describes the HTTP API which facilitates between the interpreter and the frontend.

### Compile

```
Endpoint : POST /compile  
Body     : LMC Program Text (json?)  
Response : 200 - Intitial Machine State JSON  
           418 - Compilation Error JSON   
```

### Step

```
Endpoint: GET /step
Query   : exec_id - execution id  
Response: 200  - Good execution (Body is Program State)
          201  - Output (Body is Output Value)
          202  - Halted (Body is Program State)
          404  - No such Exec ID
          412  - Requiring Input (Body is Program State)
          422  - Bad input
```

### Run

```
Endpoint: GET /run
Query   : exec_id - execution id  
Response: 200  - Good execution (Body is Program State)
          201  - Output (Body is Output Value)
          202  - Halted (Body is Program State)
          404  - No such Exec ID
          412  - Requiring Input (Body is Program State)
          422  - Bad input
```

### Input

```
Endpoint : POST /input
Query    : exec_id - execution id
Body     : Input Value
Response : 200 - OK
           404 - Bad Exec ID
```

### Configure (Unimpl!)

```
Endpoint : POST /configure  
Body     : Semantic Flags  
Response : 200 or 418  
```

----

### Machine State JSON

Internally, mailboxes are indexed by an arbritrarily large integer, starting from zero. This index is equal to the index of each 'mailbox' datastructure in the "asm" list.

```json
{
  "exec"      : 1337,
  "asm"       : [ { "addr" : "000", "data" : "622", "lno" : 1, "line" : "zero HLT ; end" }, ... ],
  "labels"    : { "zero" : 0, ... },
  "registers" : { "PC" : 1, "IP" : 0, "NEG" : false, ... }
}
```
