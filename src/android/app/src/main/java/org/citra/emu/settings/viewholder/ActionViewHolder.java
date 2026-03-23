package org.citra.emu.settings.viewholder;

import android.view.View;
import android.widget.TextView;

import org.citra.emu.R;
import org.citra.emu.settings.SettingsAdapter;
import org.citra.emu.settings.view.ActionSetting;
import org.citra.emu.settings.view.SettingsItem;

public final class ActionViewHolder extends SettingViewHolder {
    private ActionSetting mItem;

    private TextView mTextSettingName;
    private TextView mTextSettingDescription;

    public ActionViewHolder(View itemView, SettingsAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    protected void findViews(View root) {
        mTextSettingName = root.findViewById(R.id.text_setting_name);
        mTextSettingDescription = root.findViewById(R.id.text_setting_description);
    }

    @Override
    public void bind(SettingsItem item) {
        mItem = (ActionSetting)item;
        mTextSettingName.setText(item.getNameId());
        if (!mItem.getValue().isEmpty()) {
            mTextSettingDescription.setText(mItem.getValue());
        } else if (item.getDescriptionId() > 0) {
            mTextSettingDescription.setText(item.getDescriptionId());
        } else {
            mTextSettingDescription.setText("");
        }
    }

    @Override
    public void onClick(View clicked) {
        getAdapter().onActionClick(mItem);
    }
}
