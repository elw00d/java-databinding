package binding;

import binding.BindingSettingsBase;
import binding.EditTextAdapter;
import binding.TextViewAdapter;

/**
 * @author igor.kostromin
 *         10.07.13 16:27
 */
public class BindingSettings extends BindingSettingsBase {
    public static BindingSettings ANDROID_DEFAULT_SETTINGS;

    static  {
        ANDROID_DEFAULT_SETTINGS = new BindingSettings();
        ANDROID_DEFAULT_SETTINGS.initializeDefault();
    }

    @Override
    public void initializeDefault() {
        super.initializeDefault();

        addAdapter( new TextViewAdapter() );
        addAdapter( new EditTextAdapter() );
    }
}
