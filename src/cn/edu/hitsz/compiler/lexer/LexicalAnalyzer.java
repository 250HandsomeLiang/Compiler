package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;
import java.util.regex.*;
/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    //加载文件
    private String buffer;
    private List<Token> tokenList;
    String res="";//记录标记符
    StringBuilder num= new StringBuilder();
    int state=0;
    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.tokenList=new ArrayList<>();
    }


    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        // TODO: 词法分析前的缓冲区实现
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        this.buffer=FileUtils.readFile(path);
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() throws Exception {
        // TODO: 自动机实现的词法分析过程
        for(int i=0;i<this.buffer.length();i++){
            switch (state){
                case 0:
                    if(Pattern.matches("[\n\r\s\t]",buffer.charAt(i)+"")){
                        state=0;
                        res="";
                        num = new StringBuilder();
                    }else if(Pattern.matches("[a-zA-Z_]",buffer.charAt(i)+"")){
                        state=14;
                        res=""+buffer.charAt(i);
                    }
                    else if(Pattern.matches("[0-9]",buffer.charAt(i)+"")){
                        state=16;
                        num = new StringBuilder("" + buffer.charAt(i));
                    }else {
                        switch (buffer.charAt(i)) {
                            case '*' -> state = 18;
                            case '=' -> state = 21;
                            case '"' -> state = 24;
                            case '(' -> state = 26;
                            case ')' -> state = 27;
                            case ';' -> state = 28;
                            case '+' -> state = 29;
                            case '-' -> state = 30;
                            case '/' -> state = 31;
                            case ',' -> state = 32;
                            default -> {
                                System.err.println("error,出现错误词法");
                                throw new Exception();
                            }
                        }
                    }
                    break;
                case 14:
                    if(Pattern.matches("[a-zA-Z0-9_]",buffer.charAt(i)+"")){
                       res=res+buffer.charAt(i);
                    }else{
                        state=refreshState(buffer.charAt(i)+"");
                        if(state==16){
                            num = new StringBuilder("" + buffer.charAt(i));
                        }
                        if("int".equals(res)){
                            tokenList.add(Token.simple("int"));
                        }else if("return".equals(res)){
                            tokenList.add(Token.simple("return"));
                        }else{
                            //加入token
                            tokenList.add(Token.normal("id",res));
                            //加入符号表
                            if (!symbolTable.has(res)) {
                                symbolTable.add(res);
                            }
                        }
                        res="";
                    }
                    break;
                case  16:
                    if(Pattern.matches("[0-9]",buffer.charAt(i)+"")){
                        num.append(buffer.charAt(i));
                    }else{
                        state=refreshState(buffer.charAt(i)+"");
                        if(state==14){
                            res=""+buffer.charAt(i);
                        }
                        tokenList.add(Token.normal("IntConst",num.toString()));
                    }
                    num=new StringBuilder("");
                    break;
                case  18:
                    if(buffer.charAt(i)=='*'){
                        state=0;
                        tokenList.add(Token.simple("**"));
                    }else{
                        update(i);
                        tokenList.add(Token.simple("*"));
                    }
                    break;
                case 21:
                    if(buffer.charAt(i)=='='){
                        state=0;
                        tokenList.add(Token.simple("=="));
                    }else{
                        update(i);
                        tokenList.add(Token.simple("="));
                    }
                    break;
                case 24:
                    //TODO
                    System.out.println("引号未实现");
                    break;
                case 26:
                case 27:
                case 29:
                case 30:
                case 31:
                case 32:
                    insertSimpleToken(state);
                    update(i);
                    break;
                //分号
                case 28:
                    update(i);
                    insertSimpleToken(28);
                    break;
                default:
                    System.err.println("error,词法分析出错");
                    break;
            }
        }

        //最后一个字符一定是分号
        insertSimpleToken(28);
        tokenList.add(Token.eof());
    }

    /**
     *
     * 更新num 和 res
     * **/
    private  void  update(int i) throws Exception {
        num=new StringBuilder("");
        res="";
        state=refreshState(""+buffer.charAt(i));
        if(state==14){
            res=""+buffer.charAt(i);
        }else if(state==16){
            num=new StringBuilder(""+buffer.charAt(i));
        }
    }
    //插入simple token
    private void insertSimpleToken(int state){
        switch (state){
            case 26 -> tokenList.add(Token.simple("("));
            case 27 -> tokenList.add(Token.simple(")"));
            case 28 -> tokenList.add(Token.simple("Semicolon"));
            case 29 -> tokenList.add(Token.simple("+"));
            case 30 -> tokenList.add(Token.simple("-"));
            case 31 -> tokenList.add(Token.simple("/"));
            case 32 -> tokenList.add(Token.simple(","));
            default ->{
                System.err.println("error");
            }
        }
    }

    /**
     *
     * 更新状态
     * **/
    private int refreshState(String str) throws Exception {
        if(Pattern.matches("[\n\r\s\t]",str)){
            return 0;
        }else if(Pattern.matches("[a-zA-Z_]",str)){
            return  14;
        }
        else if(Pattern.matches("[0-9]",str)){
            return 16;
        }else {
            switch (str.charAt(0)) {
                case '*' -> {
                    return 18;
                }
                case '=' -> {
                    return 21;
                }
                case '"' -> {
                    return 24;
                }
                case '(' -> {
                    return 26;
                }
                case ')' -> {
                    return 27;
                }
                case ';' -> {
                    return 28;
                }
                case '+' -> {
                    return 29;
                }
                case '-' -> {
                    return 30;
                }
                case '/' -> {
                    return 31;
                }
                case ',' -> {
                    return 32;
                }
                default -> {
                    System.err.println("error,出现错误词法");
                    throw new Exception();
                }
            }
        }
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可
        return tokenList;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
            path,
            StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
