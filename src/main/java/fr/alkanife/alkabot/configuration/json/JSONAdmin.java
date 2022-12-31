package fr.alkanife.alkabot.configuration.json;

import java.util.List;

public class JSONAdmin {

    private boolean metrics_for_nerds, admin_only;
    private List<String> administrators_id;

    public JSONAdmin() {}

    public JSONAdmin(boolean metrics_for_nerds, boolean admin_only, List<String> administrators_id) {
        this.metrics_for_nerds = metrics_for_nerds;
        this.admin_only = admin_only;
        this.administrators_id = administrators_id;
    }

    public boolean isMetrics_for_nerds() {
        return metrics_for_nerds;
    }

    public boolean isAdmin_only() {
        return admin_only;
    }

    public List<String> getAdministrators_id() {
        return administrators_id;
    }

    public void setMetrics_for_nerds(boolean metrics_for_nerds) {
        this.metrics_for_nerds = metrics_for_nerds;
    }

    public void setAdmin_only(boolean admin_only) {
        this.admin_only = admin_only;
    }

    public void setAdministrators_id(List<String> administrators_id) {
        this.administrators_id = administrators_id;
    }
}
