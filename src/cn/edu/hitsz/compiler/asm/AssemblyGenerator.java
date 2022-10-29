package cn.edu.hitsz.compiler.asm;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * TODO: 实验四: 实现汇编生成
 * <br>
 * 在编译器的整体框架中, 代码生成可以称作后端, 而前面的所有工作都可称为前端.
 * <br>
 * 在前端完成的所有工作中, 都是与目标平台无关的, 而后端的工作为将前端生成的目标平台无关信息
 * 根据目标平台生成汇编代码. 前后端的分离有利于实现编译器面向不同平台生成汇编代码. 由于前后
 * 端分离的原因, 有可能前端生成的中间代码并不符合目标平台的汇编代码特点. 具体到本项目你可以
 * 尝试加入一个方法将中间代码调整为更接近 risc-v 汇编的形式, 这样会有利于汇编代码的生成.
 * <br>
 * 为保证实现上的自由, 框架中并未对后端提供基建, 在具体实现时可自行设计相关数据结构.
 *
 * @see AssemblyGenerator#run() 代码生成与寄存器分配
 */
public class AssemblyGenerator {
    /**记录预处理后的目标代码**/
    List<Instruction> instructionList=new ArrayList<>();
    /**记录变量的使用情况**/
    FutureUseVariable futureUseVariable=new FutureUseVariable();
    /**记录变量和寄存器之间的映射情况**/
    BMap<IRVariable,String> bMap=new BMap<>();
    /**记录寄存器**/
    HashSet<String> regSet=new HashSet<>();
    /**记录汇编语言的链表**/
    List<Assemble> assembleList=new ArrayList<>();
    /**
     * 加载前端提供的中间代码
     * <br>
     * 视具体实现而定, 在加载中或加载后会生成一些在代码生成中会用到的信息. 如变量的引用
     * 信息. 这些信息可以通过简单的映射维护, 或者自行增加记录信息的数据结构.
     *
     * @param originInstructions 前端提供的中间代码
     */
    public void loadIR(List<Instruction> originInstructions) {
        // TODO: 读入前端提供的中间代码并生成所需要的信息
        this.preprocessing(originInstructions);

        //初始化寄存器集合
        for(int i=0;i<7;i++){
            regSet.add("t"+i);
        }
    }

    /**
     * 预处理中间代码
     * */
     public void preprocessing(List<Instruction> originInstructions){
         for (Instruction originInstruction : originInstructions) {
             InstructionKind kind = originInstruction.getKind();
             if (kind.equals(InstructionKind.ADD) || kind.equals(InstructionKind.MUL)) {
                 //加法和乘法
                 IRVariable res = originInstruction.getResult();
                 IRValue lhs = originInstruction.getLHS();
                 IRValue rhs = originInstruction.getRHS();
                 if (lhs.isImmediate()) {
                     Instruction tmp = Instruction.createAdd(res, rhs, lhs);
                     this.instructionList.add(tmp);
                 } else {
                     this.instructionList.add(originInstruction);
                 }
                 this.setFutureUseVariable(lhs,rhs);
             }
             else if (kind.equals(InstructionKind.SUB)) {
                 //减法
                 IRVariable res = originInstruction.getResult();
                 IRValue lhs = originInstruction.getLHS();
                 IRValue rhs = originInstruction.getRHS();
                 if (lhs.isImmediate()) {
                     Instruction tmp1=Instruction.createMov(IRVariable.temp(),lhs);
                     this.instructionList.add(tmp1);
                     this.setFutureUseVariable(lhs,IRImmediate.of(0));
                     Instruction tmp2=Instruction.createSub(res,tmp1.getResult(),rhs);
                     this.instructionList.add(tmp2);
                     this.setFutureUseVariable(tmp1.getResult(),rhs);
                 } else {
                     this.instructionList.add(originInstruction);
                     this.setFutureUseVariable(lhs,rhs);
                 }
             }
             else{//其他照常记录
                 this.instructionList.add(originInstruction);
                 IRValue lhs;
                 if(kind.equals(InstructionKind.MOV)){
                     lhs = originInstruction.getFrom();
                 }else{
                     lhs = originInstruction.getReturnValue();
                 }
                 this.setFutureUseVariable(lhs, IRImmediate.of(0));
             }
         }
     }

     /**记录中间代码中出现的变量**/
     public void setFutureUseVariable(IRValue lhs,IRValue rhs){
         HashSet<IRVariable> hashSet=new HashSet<>();
         //记录临时变量
         if(lhs.isIRVariable()){
             hashSet.add((IRVariable) lhs);
         }
         if(rhs.isIRVariable()){
             hashSet.add((IRVariable) rhs);
         }
         futureUseVariable.addVariable(hashSet);
     }
    /**
     * 执行代码生成.
     * <br>
     * 根据理论课的做法, 在代码生成时同时完成寄存器分配的工作. 若你觉得这样的做法不好,
     * 也可以将寄存器分配和代码生成分开进行.
     * <br>
     * 提示: 寄存器分配中需要的信息较多, 关于全局的与代码生成过程无关的信息建议在代码生
     * 成前完成建立, 与代码生成的过程相关的信息可自行设计数据结构进行记录并动态维护.
     */
    public void run() throws Exception {
        // TODO: 执行寄存器分配与代码生成
        int size=this.instructionList.size();
        for(int i=0;i<size;i++){
            Instruction instruction=this.instructionList.get(i);
            InstructionKind kind=instruction.getKind();
            String rd;
            String rs1;
            String rs2;
            switch (kind){
                case ADD -> {
                    IRVariable res=instruction.getResult();
                    IRValue lhs=instruction.getLHS();
                    IRValue rhs=instruction.getRHS();
                    rs1= bMap.getV((IRVariable) lhs);
                    rd=this.getReg(res,i);
                    if(rhs.isImmediate()){
                        this.assembleList.add(Assemble.createADDI(rd,rs1,((IRImmediate)rhs).getValue()+""));
                    }else{
                        rs2=bMap.getV((IRVariable) rhs);
                        this.assembleList.add(Assemble.createADD(rd,rs1,rs2));
                    }
                    bMap.putKV(res,rd);
                }

                case MOV -> {
                    IRVariable res=instruction.getResult();
                    IRValue mFrom=instruction.getFrom();
                    rd=this.getReg(res,i);
                    if(mFrom.isImmediate()){
                        this.assembleList.add(Assemble.createLI(rd,((IRImmediate)mFrom).getValue()+""));
                    }else{
                        this.assembleList.add(Assemble.createMV(rd, bMap.getV((IRVariable)mFrom )));
                    }
                    bMap.putKV(res,rd);
                }

                case MUL -> {
                    IRVariable res=instruction.getResult();
                    IRValue lhs=instruction.getLHS();
                    IRValue rhs=instruction.getRHS();
                    rd=this.getReg(res,i);
                    rs1= bMap.getV((IRVariable) lhs);
                    rs2=bMap.getV((IRVariable) rhs);
                    this.assembleList.add(Assemble.createMul(rd,rs1,rs2));
                    bMap.putKV(res,rd);
                }

                case RET -> {
                    IRValue returnValue=instruction.getReturnValue();
                    String reg=bMap.getV((IRVariable) returnValue);
                    this.assembleList.add(Assemble.createMV("a0",reg));
                }

                case SUB -> {
                    IRVariable res=instruction.getResult();
                    IRValue lhs=instruction.getLHS();
                    IRValue rhs=instruction.getRHS();
                    rd=this.getReg(res,i);
                    rs1= bMap.getV((IRVariable) lhs);
                    rs2=bMap.getV((IRVariable) rhs);
                    this.assembleList.add(Assemble.createSUB(rd,rs1,rs2));
                    bMap.putKV(res,rd);
                }

                default -> {
                    System.out.println("Assembly generate over");
                }
            }
        }
    }

    /**
     * 寄存器替换算法
     * **/
    public String getReg(IRVariable name,int index) throws Exception {
        for(String reg:regSet){
            //若发现有空闲的寄存器则直接使用该寄存器
            if(!this.bMap.containsValue(reg)){
                return reg;
            }
        }

        //如果没有空闲的寄存器则需要替换未来不会被使用变量所占据的寄存器
        IRVariable unusedVar=futureUseVariable.getIRVar(index,bMap);
        String reg=bMap.getV(unusedVar);
        return reg;
    }

    /**
     * 输出汇编代码到文件
     *
     * @param path 输出文件路径
     */
    public void dump(String path) throws IOException {
        // TODO: 输出汇编代码到文件
        if(assembleList.size()==instructionList.size()){
            int size=instructionList.size();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
            writer.write(".text:\n");
            writer.flush();
            for(int i=0;i<size;i++){
                writer.write("    "+assembleList.get(i).toString()+"    #"+instructionList.get(i).toString()+"\n");
                writer.flush();
            }
            writer.close();
            System.out.println("Assembly generate over");
        }else{
            System.err.println("汇编的长度和中间代码无法对齐");
        }
    }
}

