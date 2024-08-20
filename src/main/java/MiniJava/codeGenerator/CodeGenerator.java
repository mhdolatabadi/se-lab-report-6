package MiniJava.codeGenerator;

import MiniJava.Log.Log;
import MiniJava.errorHandler.ErrorHandler;
import MiniJava.scanner.token.Token;
import MiniJava.semantic.symbol.Symbol;
import MiniJava.semantic.symbol.SymbolTable;
import MiniJava.semantic.symbol.SymbolType;

import java.util.Stack;

/**
 * Created by Alireza on 6/27/2015.
 */
public class CodeGenerator {
    private final Memory memory = new Memory();
    private final Stack<Address> ss = new Stack<>();
    private final Stack<String> symbolStack = new Stack<>();
    private final Stack<String> callStack = new Stack<>();
    private final SymbolTable symbolTable;

    public CodeGenerator() {
        symbolTable = new SymbolTable(memory);
        //TODO
    }

    public void printMemory() {
        memory.pintCodeBlock();
    }

    public void semanticFunction(int func, Token next) {
        Log.print("code generator : " + func);
        switch (func) {
            case 0 -> {}
            case 1 -> checkID();
            case 2 -> pid(next);
            case 3 -> fpid();
            case 4 -> kpid(next);
            case 5 -> intpid(next);
            case 6 -> startCall();
            case 7 -> call();
            case 8 -> arg();
            case 9 -> assign();
            case 10 -> add();
            case 11 -> sub();
            case 12 -> mult();
            case 13 -> label();
            case 14 -> save();
            case 15 -> _while();
            case 16 -> jpf_save();
            case 17 -> jpHere();
            case 18 -> print();
            case 19 -> equal();
            case 20 -> less_than();
            case 21 -> and();
            case 22 -> not();
            case 23 -> defClass();
            case 24 -> defMethod();
            case 25 -> popClass();
            case 26 -> extend();
            case 27 -> defField();
            case 28 -> defVar();
            case 29 -> methodReturn();
            case 30 -> defParam();
            case 31 -> lastTypeBool();
            case 32 -> lastTypeInt();
            case 33 -> defMain();
        }
    }

    private void defMain() {
        memory.add3AddressCode(ss.pop().num, Operation.JP, new Address(memory.getCurrentCodeBlockAddress(), VarType.Address), null, null);
        String methodName = "main";
        String className = symbolStack.pop();

        symbolTable.addMethod(className, methodName, memory.getCurrentCodeBlockAddress());

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void checkID() {
        symbolStack.pop();
        if (ss.peek().varType == VarType.Non) {
            //TODO : error
        }
    }

    public void pid(Token next) {
        if (symbolStack.size() > 1) {
            String methodName = symbolStack.pop();
            String className = symbolStack.pop();
            try {

                Symbol s = symbolTable.get(className, methodName, next.value);
                VarType t = switch (s.type) {
                    case Bool -> VarType.Bool;
                    case Int -> VarType.Int;
                };
                ss.push(new Address(s.address, t));


            } catch (Exception e) {
                ss.push(new Address(0, VarType.Non));
            }
            symbolStack.push(className);
            symbolStack.push(methodName);
        } else {
            ss.push(new Address(0, VarType.Non));
        }
        symbolStack.push(next.value);
    }

    public void fpid() {
        ss.pop();
        ss.pop();

        Symbol s = symbolTable.get(symbolStack.pop(), symbolStack.pop());
        VarType t = switch (s.type) {
            case Bool -> VarType.Bool;
            case Int -> VarType.Int;
        };
        ss.push(new Address(s.address, t));

    }

    public void kpid(Token next) {
        ss.push(symbolTable.get(next.value));
    }

    public void intpid(Token next) {
        ss.push(new Address(Integer.parseInt(next.value), VarType.Int, TypeAddress.Immediate));
    }

    public void startCall() {
        //TODO: method ok
        ss.pop();
        ss.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();
        symbolTable.startCall(className, methodName);
        callStack.push(className);
        callStack.push(methodName);
    }

    public void call() throws IndexOutOfBoundsException {
        //TODO: method ok
        String methodName = callStack.pop();
        String className = callStack.pop();
        symbolTable.getNextParam(className, methodName);
        ErrorHandler.printError("The few argument pass for method");
        VarType t = switch (symbolTable.getMethodReturnType(className, methodName)) {
            case Int -> VarType.Int;
            case Bool -> VarType.Bool;
        };
        Address temp = new Address(memory.getTemp(), t);
        ss.push(temp);
        memory.add3AddressCode(Operation.ASSIGN, new Address(temp.num, VarType.Address, TypeAddress.Immediate),
                new Address(symbolTable.getMethodReturnAddress(className, methodName), VarType.Address), null);
        memory.add3AddressCode(Operation.ASSIGN,
                new Address(memory.getCurrentCodeBlockAddress() + 2, VarType.Address, TypeAddress.Immediate),
                new Address(symbolTable.getMethodCallerAddress(className, methodName), VarType.Address), null);
        memory.add3AddressCode(Operation.JP,
                new Address(symbolTable.getMethodAddress(className, methodName), VarType.Address), null, null);
    }

    public void arg() {
        //TODO: method ok

        String methodName = callStack.pop();
        try {
            Symbol s = symbolTable.getNextParam(callStack.peek(), methodName);
            VarType t = switch (s.type) {
                case Bool -> VarType.Bool;
                case Int -> VarType.Int;
            };
            Address param = ss.pop();
            if (param.varType != t) {
                ErrorHandler.printError("The argument type isn't match");
            }
            memory.add3AddressCode(Operation.ASSIGN, param, new Address(s.address, t), null);

        } catch (IndexOutOfBoundsException e) {
            ErrorHandler.printError("Too many arguments pass for method");
        }
        callStack.push(methodName);

    }

    public void assign() {
        Address s1 = ss.pop();
        Address s2 = ss.pop();
        if (s1.varType != s2.varType) {
            ErrorHandler.printError("The type of operands in assign is different ");
        }
        memory.add3AddressCode(Operation.ASSIGN, s1, s2, null);
    }

    public void add() {
        Address temp = new Address(memory.getTemp(), VarType.Int);
        Address s2 = ss.pop();
        Address s1 = ss.pop();

        if (s1.varType != VarType.Int || s2.varType != VarType.Int) {
            ErrorHandler.printError("In add two operands must be integer");
        }
        memory.add3AddressCode(Operation.ADD, s1, s2, temp);
        ss.push(temp);
    }

    public void sub() {
        Address temp = new Address(memory.getTemp(), VarType.Int);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Int || s2.varType != VarType.Int) {
            ErrorHandler.printError("In sub two operands must be integer");
        }
        memory.add3AddressCode(Operation.SUB, s1, s2, temp);
        ss.push(temp);
    }

    public void mult() {
        Address temp = new Address(memory.getTemp(), VarType.Int);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Int || s2.varType != VarType.Int) {
            ErrorHandler.printError("In mult two operands must be integer");
        }
        memory.add3AddressCode(Operation.MULT, s1, s2, temp);
        ss.push(temp);
    }

    public void label() {
        ss.push(new Address(memory.getCurrentCodeBlockAddress(), VarType.Address));
    }

    public void save() {
        ss.push(new Address(memory.saveMemory(), VarType.Address));
    }

    public void _while() {
        memory.add3AddressCode(ss.pop().num, Operation.JPF, ss.pop(),
                new Address(memory.getCurrentCodeBlockAddress() + 1, VarType.Address), null);
        memory.add3AddressCode(Operation.JP, ss.pop(), null, null);
    }

    public void jpf_save() {
        Address save = new Address(memory.saveMemory(), VarType.Address);
        memory.add3AddressCode(ss.pop().num, Operation.JPF, ss.pop(),
                new Address(memory.getCurrentCodeBlockAddress(), VarType.Address), null);
        ss.push(save);
    }

    public void jpHere() {
        memory.add3AddressCode(ss.pop().num, Operation.JP,
                new Address(memory.getCurrentCodeBlockAddress(), VarType.Address), null, null);
    }

    public void print() {
        memory.add3AddressCode(Operation.PRINT, ss.pop(), null, null);
    }

    public void equal() {
        Address temp = new Address(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != s2.varType) {
            ErrorHandler.printError("The type of operands in equal operator is different");
        }
        memory.add3AddressCode(Operation.EQ, s1, s2, temp);
        ss.push(temp);
    }

    public void less_than() {
        Address temp = new Address(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Int || s2.varType != VarType.Int) {
            ErrorHandler.printError("The type of operands in less than operator is different");
        }
        memory.add3AddressCode(Operation.LT, s1, s2, temp);
        ss.push(temp);
    }

    public void and() {
        Address temp = new Address(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Bool || s2.varType != VarType.Bool) {
            ErrorHandler.printError("In and operator the operands must be boolean");
        }
        memory.add3AddressCode(Operation.AND, s1, s2, temp);
        ss.push(temp);
    }

    public void not() {
        Address temp = new Address(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Bool) {
            ErrorHandler.printError("In not operator the operand must be boolean");
        }
        memory.add3AddressCode(Operation.NOT, s1, s2, temp);
        ss.push(temp);
    }

    public void defClass() {
        ss.pop();
        symbolTable.addClass(symbolStack.peek());
    }

    public void defMethod() {
        ss.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethod(className, methodName, memory.getCurrentCodeBlockAddress());

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void popClass() {
        symbolStack.pop();
    }

    public void extend() {
        ss.pop();
        symbolTable.setSuperClass(symbolStack.pop(), symbolStack.peek());
    }

    public void defField() {
        ss.pop();
        symbolTable.addField(symbolStack.pop(), symbolStack.peek());
    }

    public void defVar() {
        ss.pop();

        String var = symbolStack.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethodLocalVariable(className, methodName, var);

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void methodReturn() {
        //TODO : call ok

        String methodName = symbolStack.pop();
        Address s = ss.pop();
        SymbolType t = symbolTable.getMethodReturnType(symbolStack.peek(), methodName);
        VarType temp = VarType.Int;
        switch (t) {
            case Int:
                break;
            case Bool:
                temp = VarType.Bool;
        }
        if (s.varType != temp) {
            ErrorHandler.printError("The type of method and return address was not match");
        }
        memory.add3AddressCode(Operation.ASSIGN, s,
                new Address(symbolTable.getMethodReturnAddress(symbolStack.peek(), methodName), VarType.Address,
                        TypeAddress.Indirect), null);
        memory.add3AddressCode(Operation.JP,
                new Address(symbolTable.getMethodCallerAddress(symbolStack.peek(), methodName), VarType.Address), null,
                null);
    }

    public void defParam() {
        //TODO : call Ok
        ss.pop();
        String param = symbolStack.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethodParameter(className, methodName, param);

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void lastTypeBool() {
        symbolTable.setLastType(SymbolType.Bool);
    }

    public void lastTypeInt() {
        symbolTable.setLastType(SymbolType.Int);
    }

    public void main() {

    }
}
