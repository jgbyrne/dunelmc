use std::io;
use std::io::BufRead;
use std::collections::HashMap;
use std::fs::File;
use std::io::Read;

static B32 : [char; 32] = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V'];

#[derive(Debug)]
struct AsmError {
    lno: usize,
    message: String,
}

fn lookup(mnemonic: &str, lno: usize) -> Result<usize, AsmError> {
    match mnemonic {
        "ADD" => Ok(100),
        "SUB" => Ok(200),
        "STA" | "STO" => Ok(300),
        "LDA" => Ok(500),
        "BRA" | "BR" => Ok(600),
        "BRZ" => Ok(700),
        "BRP" => Ok(800),
        "INP" | "IN" => Ok(901),
        "OUT" => Ok(902),
        "HLT" | "COB" | "DAT" => Ok(000),
        _ => Err(AsmError { lno: lno, message: "Bad Mnemonic".to_string() } ),
    }
}

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
enum Address {
    STANDARD ( usize ),
    OVERFLOW ( String ), 
}

struct Assembler {
    mem : Vec<Mailbox>,
    labels: HashMap<String, usize>, // In this context, the Mailbox is relly more of a pointer than an extant mailbox.
    links: HashMap<usize  , String>,
    ptr: usize,
}

impl Assembler {
    fn create() -> Assembler {
        Assembler {
            mem: vec![Mailbox::STANDARD { dat: 000, lno: 0 }; 100],
            labels: HashMap::new(),
            links: HashMap::new(),
            ptr: 0,
        }
    }

    fn assemble(&mut self, lines: Vec<&str>) -> Result<(), AsmError> {
        let mut lno = 0;
        for line in lines.iter() {
            lno += 1;
            let mut parts = line.split_whitespace();
            let mut toks : Vec<&str> = vec![];
            for part in parts {
                if part.starts_with("#") || part.starts_with("//") || part.starts_with(";") {
                    break;
                }
                else {
                    toks.push(part);
                }
            }

            let mut label: Option<&str> = None;
            let mut op   : Option<&str> = None;
            let mut val  : Option<&str> = None;
            
            match toks.len() {
                0 => { continue; },
                1 => { op = Some(toks[0]); },
                2 => {
                    if lookup(toks[0], lno).is_err() {
                        label = Some(toks[0]);
                        op    = Some(toks[1]);
                    }
                    else {
                        op    = Some(toks[0]);
                        val   = Some(toks[1]);
                    }
                },
                3 => {
                    label = Some(toks[0]);
                    op    = Some(toks[1]);
                    val   = Some(toks[2]);
                },
                _ => {
                    return Err(AsmError { message: "Malformed command".to_string(), lno } );
                }
            }

            match label {
                Some(l) => {
                    if self.labels.contains_key(l) {
                        return Err(AsmError { message: format!("Encountered same label twice: {}", l), lno } );
                    }
                    self.labels.insert(l.to_string(), self.ptr);
                },
                None    => { },
            }

            match op {
                Some(o) => { 
                    let base = match lookup(o, lno) {
                        Ok(val) => val,
                        Err(e)  => { return Err(e); },
                    };
                    /* sketchy */
                    self.mem[self.ptr] = Mailbox::STANDARD { dat: base, lno };
                },
                None    => { return Err(AsmError { message: "No mnemonic found".to_string(), lno } ) },
            }

            match val {
                Some(v) => {
                    if lookup(op.unwrap(), lno).unwrap() == 0 {
                        match v.parse::<usize>() {
                            Ok(dat) => { let mut mb = &self.mem[self.ptr];
                                         match mb {
                                             Mailbox::STANDARD { mut dat, .. } => { dat = dat },
                                             Mailbox::OVERFLOW { .. } => { },
                                         }
                                       }
                            Err(_)  => { return Err(AsmError { message: format!("Bad Numeric: {}", v), lno } ); },
                        }
                    }
                    else {
                        if self.labels.contains_key(v) {
                            let lptr = self.labels.get(v).unwrap();
                            if *lptr > 99 {
                                // Handle overflow
                            }
                            else {
                                let mut mb = &self.mem[self.ptr];
                                match mb {
                                    Mailbox::STANDARD { mut dat, .. } => { dat += lptr; },
                                    Mailbox::OVERFLOW { .. } => { },
                                }
                            }
                        }
                        else {
                            self.links.insert(self.ptr, v.to_string());
                        }
                    }
                },
                None    => { },
            }
            self.ptr += 1;
        }

        println!("{:?}", &self.links);

        for (lnptr, lnval) in &self.links {
            if !self.labels.contains_key(lnval) {
                return Err(AsmError { message: "Bad Link Value".to_string(), lno: 0 });
            }
            match &self.mem[*lnptr] {
                Mailbox::STANDARD { mut dat, .. } => { dat += self.labels.get(lnval).unwrap(); },
                Mailbox::OVERFLOW { .. } => { },
            }
        }

        Ok(())
    }
}


/// The type of operation, determined by most significant digit at evaltime
enum SigOp {
    ADD,
    SUB,
    STA,
    LDA,
    BRA,
    BRZ,
    BRP,
    IO ,
    HLT,
}

fn sig_to_op(sig: usize) -> SigOp {
    match sig {
        0 => SigOp::HLT,
        1 => SigOp::ADD,
        2 => SigOp::SUB,
        3 => SigOp::STA,
        5 => SigOp::LDA,
        6 => SigOp::BRA,
        7 => SigOp::BRZ,
        8 => SigOp::BRP,
        9 => SigOp::IO ,
        _ => unreachable!(),
    }
}

/// Represents a Data Cell
#[derive(Clone, Debug)]
enum Mailbox {
    STANDARD { dat: usize, lno: usize },
    OVERFLOW { sig: usize, duo: String, lno: usize }, 
}

#[derive(Debug)]
struct RuntimeError {
    message: String
}

/// Outcome of a Cycle
#[derive(Debug)]
enum CycleResult {
    HALT,
    SUCCESS,
    OUTPUT,
    INPUT,
    ABORT ( RuntimeError ),
}

struct Interpreter {
    ip : usize,
    pc : usize,
    acc: usize,
    neg: bool,

    outbox: usize,
    inbox : usize,

    mem : Vec<Mailbox>,
    lines: Vec<String>,
}

impl Interpreter {
    fn create(mem: Vec<Mailbox>, lines: Vec<String>) -> Interpreter {
        Interpreter {
            ip: 0,
            pc: 0,
            acc: 0,
            neg: false,
            outbox: 0,
            inbox: 0,
            mem: mem,
            lines: lines,
        }
    }

    fn load_rval(&self, duo: usize) -> Result<usize, CycleResult> {
        match self.mem.get(duo) {
            Some(mb) => match mb {
                Mailbox::STANDARD { dat, .. } => Ok(*dat),
                Mailbox::OVERFLOW { .. } => Err(CycleResult::ABORT(
                                    RuntimeError { message: "Cannot load overflow value".to_string() }
                                  )),
            },
            None     => Err(CycleResult::ABORT(
                  RuntimeError { message : "Overran Message Space".to_string() }
                )),
        }
    }

    fn cycle(&mut self, input: Option<usize>) -> CycleResult {
        self.ip = self.pc;
        let ir = match self.mem.get(self.ip) {
            Some(mb) => (*mb).clone(),
            None     => return CycleResult::ABORT(
                                   RuntimeError { message : "Overran Memory Space".to_string() }
                               ),
        };

        self.pc += 1;
        let sig = match ir {
            Mailbox::STANDARD { dat, .. } => dat / 100,
            Mailbox::OVERFLOW { sig, .. } => sig,
        };
        let duo = match ir {
            Mailbox::STANDARD { dat, .. } => dat - (sig * 100),
            Mailbox::OVERFLOW { sig, duo, .. } => {
                let chars: Vec<char> = duo.chars().collect();
                let mag2 = B32.iter().position(|&r| r == chars[0]).unwrap() * 32;
                let mag1 = B32.iter().position(|&r| r == chars[1]).unwrap();
                let b32num = mag2 + mag1;
                b32num - 220 // 320 -> 100
            }, 
        };

        match sig_to_op(sig) {
            SigOp::ADD => {
                self.neg = false;
                match self.load_rval(duo) {
                    Ok(rval) => { self.acc = (self.acc + rval) % 1000; CycleResult::SUCCESS },
                    Err(cr)  => cr,
                }
            },
            SigOp::SUB => {
                match self.load_rval(duo) {
                    Ok(rval) => { 
                        if rval > self.acc {
                            self.acc = 1000 - (rval - self.acc);
                            self.neg = true;
                        }
                        else {
                            self.acc = self.acc - rval;
                            self.neg = false;
                        };
                        CycleResult::SUCCESS
                    },
                    Err(cr) => cr,
                }
            },
            SigOp::STA => {
                if duo < self.mem.len() {
                    self.mem[duo] = Mailbox::STANDARD { dat: self.acc, lno: 0 } ;
                    CycleResult::SUCCESS
                }
                else {
                    CycleResult::ABORT( RuntimeError { message: "Overran Memory Space".to_string() } )
                }
            },
            SigOp::LDA => {
                match self.load_rval(duo) {
                    Ok(rval) => { self.neg = false; self.acc = rval; CycleResult::SUCCESS },
                    Err(cr)  => cr,
                }
            },
            SigOp::BRA => {
                if duo < self.mem.len() {
                    self.pc = duo;
                    CycleResult::SUCCESS
                }
                else {
                    CycleResult::ABORT( RuntimeError { message: "Overran Memory Space".to_string() } )
                }
            },
            SigOp::BRZ => {
                if !self.neg {
                    if self.acc == 0 {
                        if duo < self.mem.len() {
                            self.pc = duo;
                        }
                        else {
                            return CycleResult::ABORT(RuntimeError { message: "Overran Memory Space".to_string() });
                        }
                    }
                }
                CycleResult::SUCCESS
            },
            SigOp::BRP => {
                if !self.neg {
                    if duo < self.mem.len() {
                        self.pc = duo;
                    }
                    else {
                        return CycleResult::ABORT(RuntimeError { message: "Overran Memory Space".to_string() });
                    }
                }
                CycleResult::SUCCESS
            },
            SigOp::IO  => {
                match duo {
                    1 => {
                        match input {
                            Some(i) => {
                                self.neg = false;
                                if i > 999 {
                                    CycleResult::ABORT(RuntimeError { message: "Input Value >999".to_string() })
                                }
                                else {
                                   self.acc = i;
                                   self.inbox = i;
                                   CycleResult::SUCCESS
                                }
                            },
                            None => { CycleResult::ABORT(RuntimeError { message: "Missing Input".to_string() }) },
                        }
                    },
                    2 => {
                        self.outbox = self.acc;
                        CycleResult::OUTPUT
                    },
                    _ => { CycleResult::ABORT(
                                    RuntimeError { message: "Invalid IO (9XX) Instruction".to_string() }
                                             )
                         },
                }
            },
            SigOp::HLT => {
                CycleResult::HALT
            },
        }
    }

    fn needs_input(&self) -> bool {
        match self.mem.get(self.pc) {
            Some(mb) => match mb {
                               Mailbox::STANDARD { dat, ..} => { *dat == 901 },
                               Mailbox::OVERFLOW { .. } => false,
                           },
            None     => false, // We deal with this later!
        }
    }
}


fn main() {
    //let prog1 = [901, 389, 504, 656, 605, 584, 385, 589, 386, 594, 387, 513, 670, 614, 588, 385, 593, 387, 520, 674, 621, 586, 184, 389, 526, 656, 627, 555, 387, 589, 284, 833, 643, 389, 587, 194, 387, 586, 190, 386, 285, 763, 629, 589, 285, 847, 652, 389, 587, 193, 387, 643, 587, 189, 902, 0, 378, 901, 388, 385, 386, 291, 865];
    
    //595
        
    //let prog2 = [654, 292, 863, 590, 387, 671, 378, 555, 384, 675, 378, 586, 287, 879, 600, 386, 584, 185, 384, 875, 0, 0, 0, 0, 0, 0, 1, 2, 9, 10, 100, 999, 0, 0, 0, 0];
    
    let mut asmblr = Assembler::create();
    
    let mut file = File::open("/home/jgb/prog.lmc").unwrap();
    let mut contents = String::new();
    file.read_to_string(&mut contents);
    println!("{:?}", asmblr.assemble(contents.split("\n").collect()));

    println!("{:?}", &asmblr.mem);

    let mut program : Vec<Mailbox> = asmblr.mem;

    //for dat in prog1.iter() {
    //    program.push(Mailbox::STANDARD { dat : *dat as usize, lno: 0 });
    //}

    //program.push(Mailbox::OVERFLOW { sig: 5, duo: "A0".to_string(), lno: 0 });

    //for dat in prog2.iter() {
    //    program.push(Mailbox::STANDARD { dat : *dat as usize, lno: 0 });
    //}

    //program.push(Mailbox::STANDARD { dat : 999, lno : 0 });


    let mut interp = Interpreter::create(program, vec![]);
    loop {
        let result = if interp.needs_input() {
            let stdin = io::stdin();
            let inpstr = stdin.lock().lines().next().unwrap().unwrap();
            let inp: usize = inpstr.parse().unwrap();
            interp.cycle(Some(inp))
        }
        else {
            interp.cycle(None)
        };

        match result {
            CycleResult::ABORT (error) => { println!("{}", error.message); break; },
            CycleResult::HALT   => { break; },
            CycleResult::OUTPUT => { println!("{}", interp.outbox); },
            _ => {},
        }
    }
}
