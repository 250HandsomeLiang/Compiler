package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;
import cn.edu.hitsz.compiler.symtab.SymbolTable;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.List;

// TODO: 实验三: 实现语义分析
public class SemanticAnalyzer implements ActionObserver {
    //定义符号表
    private  SymbolTable symbolTable;
    //定义分析栈
    private List<SourceTypes> analysisArrayList=new ArrayList<>();
    @Override
    public void whenAccept(Status currentStatus) {
        // TODO: 该过程在遇到 Accept 时要采取的代码动作
        System.out.println("SemanticAnalysis  over");
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO: 该过程在遇到 reduce production 时要采取的代码动作
        int index= production.index();
        switch (index){
            case 4 ->{
                SourceTypes id;
                SourceTypes D;
                id=analysisArrayList.remove(analysisArrayList.size()-1);
                D=analysisArrayList.remove(analysisArrayList.size()-1);
                id.setTypes(D.getTypes());
                id.setCodeType(D.getCodeType());
                analysisArrayList.add(new SourceTypes("",new NonTerminal("Empty"),null));
                //更新符号表
                this.symbolTable.add(id.getToken().getText(),SourceCodeType.Int);
            }

            case 5 ->{
                SourceTypes body;
                SourceTypes D;
                body=analysisArrayList.remove(analysisArrayList.size()-1);
                D=new SourceTypes(body.getTypes(), new NonTerminal("D"),body.getCodeType());
                analysisArrayList.add(D);
            }
            default -> {
            }
        }
    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO: 该过程在遇到 shift 时要采取的代码动作
        String idName=currentToken.getKindId();
        if(("int".equals(idName))){
            analysisArrayList.add(new SourceTypes(idName,currentToken,SourceCodeType.Int));
        }
        else{
            analysisArrayList.add(new SourceTypes("",currentToken,null));
        }
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO: 设计你可能需要的符号表存储结构
        // 如果需要使用符号表的话, 可以将它或者它的一部分信息存起来, 比如使用一个成员变量存储
        this.symbolTable=table;
    }
}

