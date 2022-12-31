package fr.alkanife.alkabot.configuration.json;

public class JSONAutoRole {

    private boolean enable;
    private String role_id;

    public JSONAutoRole() {}

    public JSONAutoRole(boolean enable, String role_id) {
        this.enable = enable;
        this.role_id = role_id;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }
}
