package MiniJava.parser;

import MiniJava.scanner.token.Token;

public class GrammarSymbol {
    public boolean isTerminal;
    public NonTerminal nonTerminal;
    public Token terminal;

    public GrammarSymbol(NonTerminal nonTerminal) {
        this.nonTerminal = nonTerminal;
        isTerminal = false;
    }

    public GrammarSymbol(Token terminal) {
        this.terminal = terminal;
        isTerminal = true;
    }
}
