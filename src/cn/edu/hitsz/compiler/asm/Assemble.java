package cn.edu.hitsz.compiler.asm;
//汇编语言的类
public class Assemble {
    private String op;
    private String rd;
    private String rs1;
    private String rs2;
    private Assemble(String op,String rd,String rs1,String rs2){
        this.op=op;
        this.rd=rd;
        this.rs1=rs1;
        this.rs2=rs2;
    }
    private Assemble(String op,String rd,String rs1){
        this.op=op;
        this.rd=rd;
        this.rs1=rs1;
        this.rs2="";
    }

    //构造函数
    public static Assemble createADDI(String rd,String rs1,String rs2){
        return new Assemble("addi",rd,rs1,rs2);
    }

    public static Assemble createADD(String rd,String rs1,String rs2){
        return new Assemble("add",rd,rs1,rs2);
    }

    public static Assemble createSUB(String rd,String rs1,String rs2){
        return new Assemble("sub",rd,rs1,rs2);
    }

    public static Assemble createMul(String rd,String rs1,String rs2){
        return new Assemble("mul",rd,rs1,rs2);
    }

    public static Assemble createMV(String rd,String rs1){
        return new Assemble("mv",rd,rs1);
    }

    public static Assemble createLI(String rd,String rs1){
        return new Assemble("li",rd,rs1);
    }

    @Override
    public String toString(){
        if("".equals(this.rs2)){
            return "%s %s, %s".formatted(this.op,this.rd,this.rs1);
        }else{
            return "%s %s, %s, %s".formatted(this.op,this.rd,this.rs1,this.rs2);
        }
    }
}