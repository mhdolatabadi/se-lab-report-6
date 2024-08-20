package MiniJava.parser;

import lombok.Getter;

@Getter
public class Action {
    private final Act action;
    //if action = shift : number is state
    //if action = reduce : number is number of rule
    private final int number;

    public Action(Act action, int number) {
        this.action = action;
        this.number = number;
    }

    public String toString() {
        return switch (action) {
            case accept -> "acc";
            case shift -> "s" + number;
            case reduce -> "r" + number;
        };
    }
}

