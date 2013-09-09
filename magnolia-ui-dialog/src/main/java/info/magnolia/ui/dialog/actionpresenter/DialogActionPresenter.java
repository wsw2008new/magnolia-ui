package info.magnolia.ui.dialog.actionpresenter;

import info.magnolia.ui.api.action.ActionDefinition;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.dialog.actionpresenter.definition.EditorActionPresenterDefinition;
import info.magnolia.ui.dialog.actionpresenter.view.DialogActionView;

/**
 * Created with IntelliJ IDEA.
 * User: sasha
 * Date: 9/6/13
 * Time: 1:52 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DialogActionPresenter extends ActionPresenter {
    @Override
    DialogActionView start(Iterable<ActionDefinition> actions, EditorActionPresenterDefinition definition, ActionParameterProvider parameterProvider, UiContext uiContext);
}
