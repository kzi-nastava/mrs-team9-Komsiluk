package com.komsiluk.taxi.ui.edit;

import java.io.Serializable;
import java.util.ArrayList;

public class DriverChangeRequest implements Serializable {
    private final long id;
    private final String driverEmail;
    private final String createdAt;
    private final ArrayList<String> tags;
    private final ArrayList<FieldChange> changes;

    public DriverChangeRequest(long id, String driverEmail, String createdAt,
                               ArrayList<String> tags, ArrayList<FieldChange> changes) {
        this.id = id;
        this.driverEmail = driverEmail;
        this.createdAt = createdAt;
        this.tags = tags;
        this.changes = changes;
    }

    public long getId() { return id; }
    public String getDriverEmail() { return driverEmail; }
    public String getCreatedAt() { return createdAt; }
    public ArrayList<String> getTags() { return tags; }
    public ArrayList<FieldChange> getChanges() { return changes; }

    public static class FieldChange implements Serializable {
        private final String field;
        private final String currentValue;
        private final String requestedValue;

        public FieldChange(String field, String currentValue, String requestedValue) {
            this.field = field;
            this.currentValue = currentValue;
            this.requestedValue = requestedValue;
        }

        public String getField() { return field; }
        public String getCurrentValue() { return currentValue; }
        public String getRequestedValue() { return requestedValue; }
    }
}
