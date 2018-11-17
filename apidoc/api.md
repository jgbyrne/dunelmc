## API Documentation

This document describes the HTTP API which facilitates between the interpreter and the frontend.

### Compile

Endpoint : POST /compile  
Body     : LMC Program Text (json?)  
Response : 200 - Intitial Machine State JSON  
           418 - Compilation Error JSON   

### Step

Endpoint: POST /step
Query   : exec - execution id  

### Configure

Endpoint : POST /configure  
Body     : Semantic Flags  
Response : 200 or 418  


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
