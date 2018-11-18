## DuneLMC

DuneLMC (or DunelmC) is an intepreter and editing environment for the LMC (Little Man Computer) computer architecture.

Build instructions:

* Run the backend with `cargo run` within the `rsback` folder. You will need to have a nightly Rust toolchain installed.
* Run the frontend by opening the Kotlin project `LMC` in IntelliJ IDEA and executing it from within the application. You will need a modern Java SDK installed.

The front and backends communicate over port `10122`. Currently, the front-end is not fully tied into the back-end API so programs cannot run on the emulator.
