## API Documentation

This document describes the HTTP API which facilitates between the interpreter and the frontend.

### Compile

Endpoint : POST /compile
Body     : LMC Program Text (json?)
Response : 200 - Intitial Machine State JSON
           418 - Compilation Error JSON 

### Configure

Endpoint : POST /configure
Body     : Semantic Flags
Response : 200 or 418
