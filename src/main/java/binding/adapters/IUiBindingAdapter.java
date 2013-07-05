package binding.adapters;

import binding.UpdateSourceTrigger;

/**
 * Inheritor if IBindingAdapter, adds specific functionality for UI adapters.
 *
 * @author igor.kostromin
 *         27.06.13 18:56
 */
public interface IUiBindingAdapter<TTarget> extends IBindingAdapter<TTarget> {
    /**
     * Returns default UpdateSourceTrigger mode for this Target class.
     * You cannot return UpdateSourceTrigger.Default from this method.
     */
    UpdateSourceTrigger getDefaultUpdateSourceTrigger();
}
