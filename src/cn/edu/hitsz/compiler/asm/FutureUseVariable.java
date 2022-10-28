package cn.edu.hitsz.compiler.asm;

/**
 * @author Liang
 */

import cn.edu.hitsz.compiler.ir.IRVariable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 维护代码中未来会使用到的变量,用来辅助寄存器选择算法的执行
 * **/
public class FutureUseVariable {
    //记录临时变量
    private  HashSet<IRVariable> irVariablesHashSet=new HashSet<>();
    //记录所有指令需要用到的变量
    private  List<HashSet<IRVariable>> futureList;
    public FutureUseVariable(){
        this.futureList=new ArrayList<>();
    }
    public void addVariable(HashSet<IRVariable> hashSet){
        this.futureList.add(hashSet);
        irVariablesHashSet.addAll(hashSet);
    }
    public boolean findVar(int index,IRVariable variable){
        int size=this.futureList.size();
        for(int i=index+1;i<size;i++){
            if(this.futureList.get(i).contains(variable)){
                return true;
            }
        }
        return false;
    }

    //获取一个后续不会用到的变量
    public IRVariable getIRVar(int index,BMap<IRVariable,String> bMap) throws Exception {
        for(IRVariable obj:irVariablesHashSet){
            if(bMap.containsKey(obj)&&!this.findVar(index,obj)){
                return obj;
            }
        }
        System.err.println("无法找到合适的寄存器");
        throw new Exception();
    }

    //打印每条指令需要用到的变量
    public void testVar(){
        int i=1;
        for(HashSet<IRVariable> hashSet:futureList){
            StringBuilder str=new StringBuilder();
            for(IRVariable obj:hashSet){
                str.append(obj.getName()).append("  ");
            }
            System.out.println("index:"+i+"   "+str.toString());
            i++;
        }
    }
}
