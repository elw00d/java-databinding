package binding;

import binding.adapters.*;

/**
 * @author igor.kostromin
 *         10.07.13 15:43
 */
public class BindingSettings extends BindingSettingsBase {
    public static BindingSettings SWING_DEFAULT_SETTINGS ;

    static  {
        SWING_DEFAULT_SETTINGS = new BindingSettings();
        SWING_DEFAULT_SETTINGS.initializeDefault();
    }

    @Override
    public void initializeDefault() {
        super.initializeDefault();

        addAdapter( new JTextFieldAdapter() );
        addAdapter( new JPasswordFieldAdapter() );
        addAdapter( new JCheckBoxAdapter() );
        addAdapter( new JListAdapter() );
        addAdapter( new JLabelAdapter() );
    }
}
