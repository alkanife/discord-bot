package fr.alkanife.alkabot.configuration.json.commands;

public class JSONCommandsMusicShortcut {

    private boolean bind, unbind, list, info;

    public JSONCommandsMusicShortcut() {}

    public JSONCommandsMusicShortcut(boolean bind, boolean unbind, boolean list, boolean info) {
        this.bind = bind;
        this.unbind = unbind;
        this.list = list;
        this.info = info;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public boolean isUnbind() {
        return unbind;
    }

    public void setUnbind(boolean unbind) {
        this.unbind = unbind;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public boolean isInfo() {
        return info;
    }

    public void setInfo(boolean info) {
        this.info = info;
    }
}
