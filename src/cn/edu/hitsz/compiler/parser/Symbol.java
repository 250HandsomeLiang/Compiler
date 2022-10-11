package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;

/**
 * 符号栈的符号类
 * **/
public class Symbol {
    Token token;
    NonTerminal nonTerminal;
    private Symbol(Token token,NonTerminal nonTerminal){
        this.token=token;
        this.nonTerminal=nonTerminal;
    }
    public Symbol(Token token){
        this(token,null);
    }
    public Symbol(NonTerminal nonTerminal){
        this(null,nonTerminal);
    }
    public  boolean isToken(){
      return this.token!=null;
    }
    public  boolean isNonterminal(){
        return this.nonTerminal!=null;
    }

    //返回token
    public Token getToken(){
        return  this.token;
    }

    //返回nonterminal
    public NonTerminal getNonTerminal() {
        return this.nonTerminal;
    }
}
