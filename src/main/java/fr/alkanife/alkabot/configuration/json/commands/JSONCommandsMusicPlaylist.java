package fr.alkanife.alkabot.configuration.json.commands;

public class JSONCommandsMusicPlaylist {

    private boolean add, remove, list, info;

    public JSONCommandsMusicPlaylist() {}

    public JSONCommandsMusicPlaylist(boolean add, boolean remove, boolean list, boolean info) {
        this.add = add;
        this.remove = remove;
        this.list = list;
        this.info = info;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
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
