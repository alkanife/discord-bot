package fr.alkanife.alkabot.commands;

import java.lang.reflect.Method;

public class BotCommand {

    private final String name;
    private final boolean administrative;
    private final Object object;
    private final Method method;

    public BotCommand(String name, boolean administrative, Object object, Method method) {
        super();
        this.name = name;
        this.administrative = administrative;
        this.object = object;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public boolean isAdministrative() {
        return administrative;
    }

    public Object getObject() {
        return object;
    }

    public Method getMethod() {
        return method;
    }
}