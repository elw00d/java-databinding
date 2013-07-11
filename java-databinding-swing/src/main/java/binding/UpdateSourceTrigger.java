package binding;

/**
 * UI controls mode to determine when trigger source to be updated.
 *
 * @author igor.kostromin
 *         27.06.13 18:51
 */
public enum UpdateSourceTrigger {
    /**
     * Determined by adapter to UI target.
     */
    Default,
    /**
     * You should explicitly call {@link BindingBase#updateSource()} to update source property.
     */
    Explicit,
    /**
     * Value will be synchronized when control loses focus.
     */
    LostFocus,
    /**
     * Value will be synchronized when property will change.
     */
    PropertyChanged
}
