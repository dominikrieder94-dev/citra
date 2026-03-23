package org.citra.emu.settings.view;

public final class ActionSetting extends SettingsItem {
    private final String mValue;

    public ActionSetting(String key, int titleId, int descriptionId, String value) {
        super(key, null, null, titleId, descriptionId);
        mValue = value != null ? value : "";
    }

    public String getValue() {
        return mValue;
    }

    @Override
    public int getType() {
        return TYPE_ACTION;
    }
}
