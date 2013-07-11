package binding;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Groups the multiple bindings to one Source into one object.
 * In {@link #bind()} call all binding instances will be created and bound.
 * Typical usage scenario:
 * <p><blockquote><pre>
 *     BindingGroup group = new BindingGroup(model);
 *     group.add(jtextfield, "text", "name");
 *     group.add(jlabel, "text", "description", BindingMode.OneWay);
 *     ...
 *     group.bind();
 * </pre></blockquote></p>
 * @author igor.kostromin
 *         27.06.13 18:05
 */
public class BindingGroup {
    private static class BindingProto {
        JComponent target;
        String targetProperty;
        String sourceProperty;
        BindingMode mode;
        UpdateSourceTrigger updateSourceTrigger;

        private BindingProto( JComponent target, String targetProperty, String sourceProperty, BindingMode mode,
                UpdateSourceTrigger updateSourceTrigger) {
            this.target = target;
            this.targetProperty = targetProperty;
            this.sourceProperty = sourceProperty;
            this.mode = mode;
            this.updateSourceTrigger = updateSourceTrigger;
        }
        
        private BindingProto( JComponent target, String targetProperty, String sourceProperty, BindingMode mode ) {
            this(target, targetProperty, sourceProperty, mode, UpdateSourceTrigger.Default);
        }
    }

    private INotifyPropertyChanged source;
    private BindingSettingsBase settings;
    private List<BindingProto> protos = new ArrayList<BindingProto>(  );
    private List<BindingBase> bindings = new ArrayList<BindingBase>(  );
    private boolean bound = false;

    /**
     * Creates empty binding group object.
     */
    public BindingGroup() {
    }

    /**
     * Creates binding group object and initializes binding Source.
     * You can change it after in anytime. If you change Source in bound state,
     * all bindings will be rebound to new Source.
     */
    public BindingGroup(INotifyPropertyChanged source) {
        if (null == source) throw new IllegalArgumentException("source is null");
        this.source = source;
    }

    /**
     * Creates binding group object and initializes binding Source and binding settings.
     * You can change Source after in anytime. If you change Source in bound state,
     * all bindings will be rebound to new Source. You cannot change Settings in bound state.
     * Call {@link #unbind()} before it.
     */
    public BindingGroup(INotifyPropertyChanged source, BindingSettingsBase settings) {
        this(source);
        if (null == settings) throw new IllegalArgumentException("settings is null");
        this.settings = settings;
    }

    public BindingSettingsBase getSettings() {
        return settings;
    }

    /**
     * Sets the settings for all bindings. In settings you can setup the adapters and converters.
     */
    public void setSettings( BindingSettingsBase settings) {
        if (null == settings) throw new IllegalArgumentException( "settings is null" );
        if (bound) throw new IllegalStateException( "Cannot change binding settings in already bound group." );
        this.settings = settings;
    }

    /**
     * Adds the binding prototype to group.
     */
    public void add(JComponent target, String targetProperty, String sourceProperty) {
        protos.add( new BindingProto( target, targetProperty, sourceProperty, BindingMode.Default ) );
    }

    /**
     * Adds the binding prototype to group.
     */
    public void add(JComponent target, String targetProperty, String sourceProperty, BindingMode mode) {
        protos.add( new BindingProto( target, targetProperty, sourceProperty, mode ) );
    }

    /**
     * Adds the binding prototype to group.
     */
    public void add(JComponent target, String targetProperty, String sourceProperty, BindingMode mode,
            UpdateSourceTrigger updateSourceTrigger) {
        protos.add( new BindingProto(target, targetProperty, sourceProperty, mode, updateSourceTrigger));
    }

    /**
     * Creates all binding objects and binds them according to stored prototypes.
     */
    public void bind() {
        if (null == source) throw new IllegalStateException( "source is not defined" );
        for ( BindingProto proto : this.protos ) {
            BindingBase binding;
            if (settings == null) {
                binding = new Binding( proto.target, proto.targetProperty, source,
                        proto.sourceProperty, proto.mode, proto.updateSourceTrigger );
            } else {
                binding = new Binding( proto.target, proto.targetProperty, source,
                        proto.sourceProperty, proto.mode, proto.updateSourceTrigger, settings );
            }
            binding.bind();
        }
        bound = true;
    }

    /**
     * Unbinds all bindings.
     */
    public void unbind() {
        if (!bound) return;
        for ( BindingBase binding : bindings ) {
            binding.unbind();
        }
        bound = false;
    }

    /**
     * Returns true if group is bound.
     */
    public boolean isBound() {
        return bound;
    }

    /**
     * Returns the binding Source object.
     */
    public INotifyPropertyChanged getSource() {
        return source;
    }

    /**
     * Changes the binding Source object.
     * If you change Source in bound state, all bindings will be rebound to new Source.
     */
    public void setSource(INotifyPropertyChanged source) {
        if (null == source) throw new IllegalArgumentException( "source is null" );
        if (!bound) {
            this.source = source;
        } else {
            unbind();
            this.source =source;
            bind();
        }
    }
}
