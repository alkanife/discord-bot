package fr.alkanife.alkabot.configuration.json.notifications;

public class JSONNotificationsGuildRole {

    private boolean role_create, role_delete, role_update, member_role_update;

    public JSONNotificationsGuildRole() {}

    public JSONNotificationsGuildRole(boolean role_create, boolean role_delete, boolean role_update, boolean member_role_update) {
        this.role_create = role_create;
        this.role_delete = role_delete;
        this.role_update = role_update;
        this.member_role_update = member_role_update;
    }

    public boolean isRole_create() {
        return role_create;
    }

    public void setRole_create(boolean role_create) {
        this.role_create = role_create;
    }

    public boolean isRole_delete() {
        return role_delete;
    }

    public void setRole_delete(boolean role_delete) {
        this.role_delete = role_delete;
    }

    public boolean isRole_update() {
        return role_update;
    }

    public void setRole_update(boolean role_update) {
        this.role_update = role_update;
    }

    public boolean isMember_role_update() {
        return member_role_update;
    }

    public void setMember_role_update(boolean member_role_update) {
        this.member_role_update = member_role_update;
    }
}
