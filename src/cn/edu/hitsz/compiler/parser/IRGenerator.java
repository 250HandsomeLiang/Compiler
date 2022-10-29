package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// TODO: 实验三: 实现 IR 生成

/**
 *
 */
public class IRGenerator implements ActionObserver {
    //分析栈
    private List<IRSymbol> irSymbolList=new ArrayList<>();
    private List<Instruction> instructionList=new ArrayList<>();
    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO
        if("id".equals(currentToken.getKindId())){
            irSymbolList.add(new IRSymbol(currentToken,IRVariable.named(currentToken.getText()),0));
        }else if("IntConst".equals(currentToken.getKindId())){
            irSymbolList.add(new IRSymbol(currentToken, IRImmediate.of(Integer.parseInt(currentToken.getText())),Integer.parseInt(currentToken.getText())));
        }else{
            irSymbolList.add(new IRSymbol(currentToken,IRVariable.named("Empty"),0));
        }
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO
        int index= production.index();
        switch(index){
            case 6 ->{//id.val=E.val
                IRSymbol E=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol sign=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol id=irSymbolList.remove(irSymbolList.size()-1);
                irSymbolList.add(new IRSymbol(new NonTerminal("S"),null,0));
                id.setVal(E.getVal());
                instructionList.add(Instruction.createMov((IRVariable) id.getIrValue(),E.getIrValue()));
            }

            case 7 ->{//return E.val
                IRSymbol E=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol ret=irSymbolList.remove(irSymbolList.size()-1);
                irSymbolList.add(new IRSymbol(new NonTerminal("S"),null,0));
                instructionList.add(Instruction.createRet(E.getIrValue()));
            }

            case 8 ->{//E.val=E.val+A.val
                IRSymbol A=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol sign=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol E=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol res=new IRSymbol(new NonTerminal("E"),IRVariable.temp(),E.getVal()+A.getVal());
                irSymbolList.add(res);
                instructionList.add(Instruction.createAdd((IRVariable) res.getIrValue(),E.getIrValue(),A.getIrValue()));
            }

            case 9 ->{//E.val=E.val-A.val
                IRSymbol A=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol sign=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol E=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol res=new IRSymbol(new NonTerminal("E"),IRVariable.temp(),E.getVal()-A.getVal());
                irSymbolList.add(res);
                instructionList.add(Instruction.createSub((IRVariable) res.getIrValue(),E.getIrValue(),A.getIrValue()));
            }

            case 11 ->{//A.val=E.val*A.val
                IRSymbol B=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol sign=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol A=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol res=new IRSymbol(new NonTerminal("A"),IRVariable.temp(),A.getVal()*B.getVal());
                irSymbolList.add(res);
                instructionList.add(Instruction.createMul((IRVariable) res.getIrValue(),A.getIrValue(),B.getIrValue()));
            }

            case 10,12,14,15 ->{//有语义动作但无中间代码的规约
                IRSymbol body=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol head=new IRSymbol(new NonTerminal("HEAD"), body.getIrValue(), body.getVal());
                irSymbolList.add(head);
            }

            case 13 ->{//B.val=E.val
                IRSymbol right=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol body=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol left=irSymbolList.remove(irSymbolList.size()-1);
                IRSymbol head=new IRSymbol(new NonTerminal("HEAD"), body.getIrValue(), body.getVal());
                irSymbolList.add(head);
            }

            default -> {
            }
        }
    }


    @Override
    public void whenAccept(Status currentStatus) {
        // TODO
        System.out.println("IR generate over");
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO
    }

    public List<Instruction> getIR() {
        // TODO
        return this.instructionList;
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}

