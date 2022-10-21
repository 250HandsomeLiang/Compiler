package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;

public class IRSymbol {
    private IRValue irValue;
    private Token token;
    private NonTerminal nonTerminal;
    private int val;
    public IRSymbol(Token token, IRValue irValue,int val){
        this.token=token;
        this.irValue=irValue;
        this.val=val;
    }
    public IRSymbol(NonTerminal nonTerminal, IRValue irValue,int val){
        this.nonTerminal=nonTerminal;
        this.irValue=irValue;
        this.val=val;
    }
    public Token getToken(){
        return this.token;
    }
    public NonTerminal getNonTerminal(){
        return this.nonTerminal;
    }
    public IRValue getIrValue() {
        return irValue;
    }
    public void setIrValue(IRValue irValue) {
        this.irValue = irValue;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
