package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;

public class SourceTypes {
    private  String types;
    private  Token token;
    private  NonTerminal nonTerminal;
    private  SourceCodeType codeType;
    public SourceTypes(String types, Token token, SourceCodeType codeType){
        this.types=types;
        this.token=token;
        this.codeType=codeType;
    }
    public SourceTypes(String types, NonTerminal nonTerminal, SourceCodeType codeType){
        this.types=types;
        this.nonTerminal=nonTerminal;
        this.codeType=codeType;
    }
    public String getTypes(){
        return this.types;
    }
    public  void  setTypes(String types){
        this.types=types;
    }
    public Token getToken(){
        return this.token;
    }
    public NonTerminal getNonTerminal(){
        return this.nonTerminal;
    }

    public SourceCodeType getCodeType() {
        return codeType;
    }
    public void  setCodeType(SourceCodeType codeType){
        this.codeType=codeType;
    }
}
