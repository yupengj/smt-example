package com.gantang.smt;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Z3Exception;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.solvers.z3.Z3SolverContext;

public class Z3Example {
    static {
        System.err.println("java.library.path = " + System.getProperty("java.library.path"));
        System.err.println("trying to load lib z3java");
        try {
            System.loadLibrary("z3java");
        } catch (UnsatisfiedLinkError ex1) {
            ex1.printStackTrace();
            try {
                System.err.println("Trying to load lib libz3java");
                System.loadLibrary("libz3java");
            } catch (UnsatisfiedLinkError ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Context ctx = new Context();
        // 在上下文中创建整数变量
        IntExpr x = ctx.mkIntConst("x");
        IntExpr y = ctx.mkIntConst("y");

        // 创建 x + y = 10 的等式
        BoolExpr equation = ctx.mkEq(ctx.mkAdd(x, y), ctx.mkInt(10));

    }
}
