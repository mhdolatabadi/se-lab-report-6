package MiniJava.semantic.symbol;

import MiniJava.codeGenerator.Address;
import MiniJava.codeGenerator.Memory;
import MiniJava.codeGenerator.TypeAddress;
import MiniJava.codeGenerator.VarType;
import MiniJava.errorHandler.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.Setter;

public class SymbolTable {
    private final Map<String, Klass> classes;
    private final Map<String, Address> keyWords;
    private final Memory mem;
    @Setter
    private SymbolType lastType;

    public SymbolTable(Memory memory) {
        mem = memory;
        classes = new HashMap<>();
        keyWords = new HashMap<>();
        keyWords.put("true", new Address(1, VarType.Bool, TypeAddress.Immediate));
        keyWords.put("false", new Address(0, VarType.Bool, TypeAddress.Immediate));
    }

    public void addClass(String className) {
        if (classes.containsKey(className)) {
            ErrorHandler.printError("This class already defined");
        }
        classes.put(className, new Klass());
    }

    public void addField(String fieldName, String className) {
        classes.get(className).fields.put(fieldName, new Symbol(lastType, mem.getDateAddress()));
    }

    public void addMethod(String className, String methodName, int address) {
        if (classes.get(className).methods.containsKey(methodName)) {
            ErrorHandler.printError("This method already defined");
        }
        classes.get(className).methods.put(methodName, new Method(address, lastType));
    }

    public void addMethodParameter(String className, String methodName, String parameterName) {
        classes.get(className).methods.get(methodName).addParameter(parameterName);
    }

    public void addMethodLocalVariable(String className, String methodName, String localVariableName) {
//        try {
        if (classes.get(className).methods.get(methodName).localVariable.containsKey(localVariableName)) {
            ErrorHandler.printError("This variable already defined");
        }
        classes.get(className).methods.get(methodName).localVariable.put(localVariableName, new Symbol(lastType, mem.getDateAddress()));
//        }catch (NullPointerException e){
//            e.printStackTrace();
//        }
    }

    public void setSuperClass(String superClass, String className) {
        classes.get(className).superClass = classes.get(superClass);
    }

    public Address get(String keywordName) {
        return keyWords.get(keywordName);
    }

    public Symbol get(String fieldName, String className) {
//        try {
        return classes.get(className).getField(fieldName);
//        }catch (NullPointerException n)
//        {
//            n.printStackTrace();
//            return null;
//        }
    }

    public Symbol get(String className, String methodName, String variable) {
        Symbol res = classes.get(className).methods.get(methodName).getVariable(variable);
        if (res == null) res = get(variable, className);
        return res;
    }

    public Symbol getNextParam(String className, String methodName) {
        return classes.get(className).methods.get(methodName).getNextParameter();
    }

    public void startCall(String className, String methodName) {
//        try {
        classes.get(className).methods.get(methodName).reset();
//        }catch (NullPointerException n)
//        {
//            n.printStackTrace();
//        }
    }

    public int getMethodCallerAddress(String className, String methodName) {
        return classes.get(className).methods.get(methodName).callerAddress;
    }

    public int getMethodReturnAddress(String className, String methodName) {
        return classes.get(className).methods.get(methodName).returnAddress;
    }

    public SymbolType getMethodReturnType(String className, String methodName) {
//        try {
        return classes.get(className).methods.get(methodName).returnType;
//        }catch (NullPointerException ed){
//            ed.printStackTrace();
//            return null;
//        }

    }

    public int getMethodAddress(String className, String methodName) {
        return classes.get(className).methods.get(methodName).codeAddress;
    }


    class Klass {
        public Map<String, Symbol> fields;
        public Map<String, Method> methods;
        public Klass superClass;

        public Klass() {
            fields = new HashMap<>();
            methods = new HashMap<>();
        }

        public Symbol getField(String fieldName) {
            if (fields.containsKey(fieldName)) {
                return fields.get(fieldName);
            }
            return superClass.getField(fieldName);

        }

    }

    class Method {
        public int codeAddress;
        public Map<String, Symbol> parameters;
        public Map<String, Symbol> localVariable;
        private final ArrayList<String> orderedParameters;
        public int callerAddress;
        public int returnAddress;
        public SymbolType returnType;
        private int index;

        public Method(int codeAddress, SymbolType returnType) {
            this.codeAddress = codeAddress;
            this.returnType = returnType;
            this.orderedParameters = new ArrayList<>();
            this.returnAddress = mem.getDateAddress();
            this.callerAddress = mem.getDateAddress();
            this.parameters = new HashMap<>();
            this.localVariable = new HashMap<>();
        }

        public Symbol getVariable(String variableName) {
            if (parameters.containsKey(variableName)) return parameters.get(variableName);
            if (localVariable.containsKey(variableName)) return localVariable.get(variableName);
            return null;
        }

        public void addParameter(String parameterName) {
            parameters.put(parameterName, new Symbol(lastType, mem.getDateAddress()));
            orderedParameters.add(parameterName);
        }

        private void reset() {
            index = 0;
        }

        private Symbol getNextParameter() {
            return parameters.get(orderedParameters.get(index++));
        }
    }

}