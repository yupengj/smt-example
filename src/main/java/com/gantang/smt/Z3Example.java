package com.gantang.smt;

import com.google.common.collect.ImmutableList;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.NumeralFormula;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.RationalFormulaManager;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverException;

import java.util.ArrayList;
import java.util.List;

public class Z3Example {
    public static void main(String[] args) throws InvalidConfigurationException, InterruptedException, SolverException {
        Configuration config = Configuration.defaultConfiguration();
        LogManager logger = BasicLogManager.create(config);
        ShutdownNotifier notifier = ShutdownNotifier.createDummy();
        SolverContextFactory.Solvers solvers = SolverContextFactory.Solvers.Z3;
//        SolverContextFactory.Solvers solvers = SolverContextFactory.Solvers.SMTINTERPOL;

        SolverContext context = SolverContextFactory.createSolverContext(config, logger, notifier, solvers);
        ProverEnvironment prover = context.newProverEnvironment(SolverContext.ProverOptions.GENERATE_MODELS
                , SolverContext.ProverOptions.GENERATE_UNSAT_CORE, SolverContext.ProverOptions.GENERATE_ALL_SAT);
        FormulaManager formulaManager = context.getFormulaManager();
        IntegerFormulaManager intFm = formulaManager.getIntegerFormulaManager();
        BooleanFormulaManager boolFm = formulaManager.getBooleanFormulaManager();

//        FloatingPointFormulaManager floatingPointFormulaManager = formulaManager.getFloatingPointFormulaManager();
//        FloatingPointFormula xa = floatingPointFormulaManager.makeNumber("4.2", FormulaType.FloatingPointType.getSinglePrecisionFloatingPointType());
//        FloatingPointFormula xb = floatingPointFormulaManager.makeNumber("4.2", FormulaType.FloatingPointType.getSinglePrecisionFloatingPointType());
//        FloatingPointFormula xc = floatingPointFormulaManager.makeNumber("5.2", FormulaType.FloatingPointType.getSinglePrecisionFloatingPointType());

        RationalFormulaManager rationalFormulaManager = formulaManager.getRationalFormulaManager();
        NumeralFormula.RationalFormula xa = rationalFormulaManager.makeNumber("4.2");
        NumeralFormula.RationalFormula xb = rationalFormulaManager.makeNumber("4.2");
        NumeralFormula.RationalFormula xc = rationalFormulaManager.makeNumber("5.2");
        NumeralFormula.RationalFormula add = rationalFormulaManager.add(xa, xb);
        prover.addConstraint(rationalFormulaManager.greaterOrEquals(add, xc));
        isUnsat(prover);

        // 创建x变量
        NumeralFormula.IntegerFormula x = intFm.makeVariable("x");
        // 创建y变量
        NumeralFormula.IntegerFormula y = intFm.makeVariable("y");
        // 创建表达式约束 x+y >= 10
        BooleanFormula constraint1 = intFm.greaterOrEquals(intFm.add(x, y), intFm.makeNumber(10));
        // 创建表达式约束 x+y <= 50
        BooleanFormula constraint2 = intFm.lessOrEquals(intFm.add(x, y), intFm.makeNumber(50));

        prover.addConstraint(constraint1);
        prover.addConstraint(constraint2);
        prover.push();

        // 检查 x=3，y=5 不满足约束
        prover.addConstraint(intFm.equal(x, intFm.makeNumber(3)));
        prover.addConstraint(intFm.equal(y, intFm.makeNumber(5)));
        isUnsat(prover);
        prover.pop();
        prover.push();

        // 检查 x=30，y=40 不满足约束
        prover.addConstraint(intFm.equal(x, intFm.makeNumber(30)));
        prover.addConstraint(intFm.equal(y, intFm.makeNumber(40)));
        isUnsat(prover);
        prover.pop();
        prover.push();

        // 检查 x=20，y=10 满足约束
        prover.addConstraint(intFm.equal(x, intFm.makeNumber(20)));
        prover.addConstraint(intFm.equal(y, intFm.makeNumber(10)));
        isUnsat(prover);
        prover.pop();
        prover.push();

        // 增加约束 x>10 => y>30
        BooleanFormula xgl10 = intFm.greaterThan(x, intFm.makeNumber(10));
        BooleanFormula ygl30 = intFm.greaterThan(y, intFm.makeNumber(30));
        prover.addConstraint(boolFm.implication(xgl10, ygl30));
        prover.push();

        // x=11,y=20 不满足约束， x>10 => y>30
        prover.addConstraint(intFm.equal(x, intFm.makeNumber(11)));
        prover.addConstraint(intFm.equal(y, intFm.makeNumber(20)));
        isUnsat(prover);
        prover.pop();
        prover.push();

        // x=11,y=41 不满足约束， x+y <= 50
        prover.addConstraint(intFm.equal(x, intFm.makeNumber(11)));
        prover.addConstraint(intFm.equal(y, intFm.makeNumber(41)));
        isUnsat(prover);
        prover.pop();
        prover.push();

        // x = 15 给出一个满足 x+y >= 10, x+y <= 50, x>10 => y>30 可行解
        prover.addConstraint(intFm.equal(x, intFm.makeNumber(15)));
        isUnsat(prover);
        model(prover);
        prover.pop();
        prover.push();

        // 给出一个满足 x+y >= 10,x+y <= 50， 10 < x < 30 , 5< y <32 , x>10 => y>30 可行解
        prover.addConstraint(intFm.greaterThan(x, intFm.makeNumber(10)));
        prover.addConstraint(intFm.lessThan(x, intFm.makeNumber(30)));
        prover.addConstraint(intFm.greaterThan(y, intFm.makeNumber(5)));
        prover.addConstraint(intFm.lessThan(y, intFm.makeNumber(32)));
        isUnsat(prover);
        model(prover);
        prover.pop();
        prover.push();

        // 给出一个满足 x+y >= 10,x+y <= 50， 10 < x < 30 , 5< y <32 , x>10 => y>30 可行解
        prover.addConstraint(intFm.greaterThan(x, intFm.makeNumber(10)));
        prover.addConstraint(intFm.lessThan(x, intFm.makeNumber(30)));
        prover.addConstraint(intFm.greaterThan(y, intFm.makeNumber(5)));
        prover.addConstraint(intFm.lessThan(y, intFm.makeNumber(32)));
        allSat(prover, boolFm);
        prover.pop();
        prover.push();
    }

    private static void isUnsat(ProverEnvironment prover) throws SolverException, InterruptedException {
        boolean unsat = prover.isUnsat();
        if (unsat) {
            System.out.println("不满足:=======不满足原因========");
            List<BooleanFormula> unsatCore = prover.getUnsatCore();
            unsatCore.forEach(System.out::println);
        } else {
            System.out.println("满足:=====================");
        }
    }

    private static void model(ProverEnvironment prover) throws SolverException, InterruptedException {
        Model model = prover.getModel();
        ImmutableList<Model.ValueAssignment> list = model.asList();
        System.out.println("可行解:===================");
        for (Model.ValueAssignment valueAssignment : list) {
            if (valueAssignment.getName().equals("x") || valueAssignment.getName().equals("y")) {
                System.out.println(valueAssignment.getName() + " = " + valueAssignment.getValue());
            }
        }
    }

    private static void allSat(ProverEnvironment prover, BooleanFormulaManager boolFm) throws SolverException, InterruptedException {
        List<List<Model.ValueAssignment>> models = new ArrayList<>();
        while (!prover.isUnsat()) {
            final ImmutableList<Model.ValueAssignment> modelAssignments = prover.getModelAssignments();

            models.add(modelAssignments);

            final List<BooleanFormula> modelAssignmentsAsFormulas = new ArrayList<>();
            for (Model.ValueAssignment va : modelAssignments) {
                modelAssignmentsAsFormulas.add(va.getAssignmentAsFormula());
            }

            // prevent next model from using the same assignment as a previous model
            prover.addConstraint(boolFm.not(boolFm.and(modelAssignmentsAsFormulas)));
        }
        int i = 1;
        for (List<Model.ValueAssignment> model : models) {
            System.out.println("可行解:=================== " + i++);
            for (Model.ValueAssignment valueAssignment : model) {
                if (valueAssignment.getName().equals("x") || valueAssignment.getName().equals("y")) {
                    System.out.println(valueAssignment.getName() + " = " + valueAssignment.getValue());
                }
            }
        }
    }
}
