import re
def check(line1,line2):
    if line1!=line2:
        print("error")
        exit(0)
if __name__=='__main__':

    name=input("输入要检查的文件名")
    with open("data/std/"+name,"r") as f1:
        line1=f1.readlines()
    with open("data/out/"+name,"r") as f2:
        line2=f2.readlines()
    if(len(line1)==len(line2)):
        for i in range(len(line1)):
            check(line1[i],line2[i])
    else:
        print(f"error,长度不不匹配")
        print(f"len1{len(line1)}  len2{len(line2)}")
        exit(0)
    print("all right")