package org.sigmah.client.page.project.logframe.grid;

import java.util.ArrayList;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.project.logframe.CodePolicy;
import org.sigmah.client.page.project.logframe.SelectWindow;
import org.sigmah.client.page.project.logframe.SelectWindow.SelectListener;
import org.sigmah.shared.domain.logframe.LogFrameGroupType;
import org.sigmah.shared.dto.logframe.ExpectedResultDTO;
import org.sigmah.shared.dto.logframe.LogFrameActivityDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.logframe.LogFrameGroupDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;
import org.sigmah.shared.dto.logframe.PrerequisiteDTO;
import org.sigmah.shared.dto.logframe.SpecificObjectiveDTO;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a log frame grid.
 * 
 * @author tmi
 * 
 */
public class ProjectLogFrameGrid {

    /**
     * Manages log frame events.
     * 
     * @author tmi
     * 
     */
    public static interface LogFrameGridListener {

        /**
         * Method called when the log frame has been edited.
         */
        public void logFrameEdited();
    }

    /**
     * CSS style name for the entire grid.
     */
    private static final String CSS_LOG_FRAME_GRID_STYLE_NAME = "logframe-grid";

    /**
     * CSS style name for the action button which add elements.
     */
    private static final String CSS_ADD_ACTION_STYLE_NAME = CSS_LOG_FRAME_GRID_STYLE_NAME + "-add-action";

    /**
     * CSS style name for the action button which add groups.
     */
    private static final String CSS_ADD_GROUP_ACTION_STYLE_NAME = CSS_LOG_FRAME_GRID_STYLE_NAME + "-add-group-action";

    /**
     * CSS style name for the labels which display codes.
     */
    private static final String CSS_CODE_LABEL_STYLE_NAME = CSS_LOG_FRAME_GRID_STYLE_NAME + "-code-label";

    /**
     * CSS style name for the menus buttons.
     */
    private static final String CSS_MENU_BUTTON_STYLE_NAME = CSS_LOG_FRAME_GRID_STYLE_NAME + "-menu-button";

    /**
     * Listeners.
     */
    private final ArrayList<LogFrameGridListener> listeners;

    /**
     * The current displayed log frame.
     */
    private LogFrameDTO logFrame;

    /**
     * The current displayed log frame model.
     */
    private LogFrameModelDTO logFrameModel;

    /**
     * The grid used to manage the log frame.
     */
    public final FlexTable table;

    /**
     * The number of the columns in the log frame grid.
     */
    private int columnsCount = 0;

    /**
     * The selection window for adding elements.
     */
    private final SelectWindow selectionWindow;

    /**
     * A view of the flex table in charge of the specific objectives.
     */
    private FlexTableView specificObjectivesView;

    /**
     * A view of the flex table in charge of the expected results.
     */
    private FlexTableView expectedResultsView;

    /**
     * A view of the flex table in charge of the activities.
     */
    private FlexTableView activitiesView;

    /**
     * A view of the flex table in charge of the prerequisites.
     */
    private FlexTableView prerequisitesView;

    /**
     * The codes displayer for the specific objectives.
     */
    private CodePolicy<SpecificObjectiveDTO> specificObjectivesPolicy;

    /**
     * The codes displayer for the expected results.
     */
    private CodePolicy<ExpectedResultDTO> expectedResultsPolicy;

    /**
     * The codes displayer for the activities.
     */
    private CodePolicy<LogFrameActivityDTO> activitiesPolicy;

    /**
     * The codes displayer for the prerequisites.
     */
    private CodePolicy<PrerequisiteDTO> prerequisitesPolicy;

    /**
     * Builds an empty grid.
     */
    public ProjectLogFrameGrid() {
        listeners = new ArrayList<LogFrameGridListener>();
        table = new FlexTable();
        selectionWindow = new SelectWindow();
    }

    /**
     * Registers a listener.
     * 
     * @param l
     *            The new listener.
     */
    public void addListener(LogFrameGridListener l) {
        this.listeners.add(l);
    }

    /**
     * Unregisters a listener.
     * 
     * @param l
     *            The old listener.
     */
    public void removeListener(LogFrameGridListener l) {
        this.listeners.remove(l);
    }

    /**
     * Informs the view that the log frame has been edited.
     */
    protected void fireLogFrameEdited() {
        for (final LogFrameGridListener l : listeners) {
            l.logFrameEdited();
        }
    }

    /**
     * Returns the main widget.
     * 
     * @return the main widget.
     */
    public Widget getWidget() {
        return new ScrollPanel(table);
    }

    /**
     * Clears table content.
     */
    protected void resetTable() {
        table.clear(true);
        table.removeAllRows();
    }

    /**
     * Checks if a log frame is currently displayed in the grid. If not, an
     * exception is thrown.
     */
    protected void ensureLogFrame() {

        if (logFrame == null) {
            throw new IllegalStateException(
                    "No log frame currently displayed. Specify a log frame before adding an element.");
        }
    }

    /**
     * Initializes table content.
     */
    protected void initTable() {

        resetTable();

        // Table parameters.
        table.setWidth("100%");
        table.setCellPadding(0);
        table.setCellSpacing(0);

        // Columns sizes.
        table.getColumnFormatter().setWidth(0, "100px");
        table.getColumnFormatter().setWidth(1, "50px");
        table.getColumnFormatter().setWidth(2, "50px");
        table.getColumnFormatter().setWidth(3, "25%");
        table.getColumnFormatter().setWidth(5, "20%");
        table.getColumnFormatter().setWidth(6, "20%");

        // Columns headers labels.
        final Label interventionLogicLabel = new Label(I18N.CONSTANTS.logFrameInterventionLogic());
        final Label indicatorsLabel = new Label(I18N.CONSTANTS.logFrameIndicators());
        final Label risksLabel = new Label(I18N.CONSTANTS.logFrameRisks());
        final Label assumptionsLabel = new Label(I18N.CONSTANTS.logFrameAssumptions());

        table.getFlexCellFormatter().setColSpan(0, 1, 2);
        table.setWidget(0, 2, interventionLogicLabel);
        table.setWidget(0, 3, indicatorsLabel);
        table.setWidget(0, 4, risksLabel);
        table.setWidget(0, 5, assumptionsLabel);

        // Rows headers labels (and actions).

        // Specific objectives.
        final Label specificObjectivesLabel = new Label(I18N.CONSTANTS.logFrameSpecificObjectives() + " ("
                + I18N.CONSTANTS.logFrameSpecificObjectivesCode() + ")");

        final Label specificObjectivesButton = new Label(I18N.CONSTANTS.logFrameAddRow());
        specificObjectivesButton.addStyleName(CSS_ADD_ACTION_STYLE_NAME);
        specificObjectivesButton.setTitle(I18N.CONSTANTS.logFrameAddOS());
        specificObjectivesButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent e) {
                addSpecificObjective();
            }
        });

        final Label specificObjectivesGroupsButton = new Label(I18N.CONSTANTS.logFrameAddGroup());
        specificObjectivesGroupsButton.addStyleName(CSS_ADD_GROUP_ACTION_STYLE_NAME);
        specificObjectivesGroupsButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent e) {
                addSpecificObjectivesGroup();
            }
        });

        // Are groups enabled ?
        if (!logFrameModel.getEnableSpecificObjectivesGroups()) {
            specificObjectivesGroupsButton.setVisible(false);
        }

        final Grid specificObjectivesGrid = new Grid(3, 1);
        specificObjectivesGrid.setWidth("100%");
        specificObjectivesGrid.setCellPadding(0);
        specificObjectivesGrid.setCellSpacing(0);
        specificObjectivesGrid.setWidget(0, 0, specificObjectivesLabel);
        specificObjectivesGrid.setWidget(1, 0, specificObjectivesButton);
        specificObjectivesGrid.setWidget(2, 0, specificObjectivesGroupsButton);

        // Expected results.
        final Label exceptedResultsLabel = new Label(I18N.CONSTANTS.logFrameExceptedResults() + " ("
                + I18N.CONSTANTS.logFrameExceptedResultsCode() + ")");

        final Label exceptedResultsButton = new Label(I18N.CONSTANTS.logFrameAddRow());
        exceptedResultsButton.addStyleName(CSS_ADD_ACTION_STYLE_NAME);
        exceptedResultsButton.setTitle(I18N.CONSTANTS.logFrameAddER());
        exceptedResultsButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent e) {
                addExpectedResult();
            }
        });

        final Label exceptedResultsGroupsButton = new Label(I18N.CONSTANTS.logFrameAddGroup());
        exceptedResultsGroupsButton.addStyleName(CSS_ADD_GROUP_ACTION_STYLE_NAME);
        exceptedResultsGroupsButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent e) {
                addExpectedResultsGroup();
            }
        });

        // Are groups enabled ?
        if (!logFrameModel.getEnableExpectedResultsGroups()) {
            exceptedResultsGroupsButton.setVisible(false);
        }

        final Grid exceptedResultsGrid = new Grid(3, 1);
        exceptedResultsGrid.setWidth("100%");
        exceptedResultsGrid.setCellPadding(0);
        exceptedResultsGrid.setCellSpacing(0);
        exceptedResultsGrid.setWidget(0, 0, exceptedResultsLabel);
        exceptedResultsGrid.setWidget(1, 0, exceptedResultsButton);
        exceptedResultsGrid.setWidget(2, 0, exceptedResultsGroupsButton);

        // Activities.
        final Label activitiesLabel = new Label(I18N.CONSTANTS.logFrameActivities());

        final Label activitiesButton = new Label(I18N.CONSTANTS.logFrameAddRow());
        activitiesButton.addStyleName(CSS_ADD_ACTION_STYLE_NAME);
        activitiesButton.setTitle(I18N.CONSTANTS.logFrameAddA());
        activitiesButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent e) {
                addActivity();
            }
        });

        final Label activitiesGroupsButton = new Label(I18N.CONSTANTS.logFrameAddGroup());
        activitiesGroupsButton.addStyleName(CSS_ADD_GROUP_ACTION_STYLE_NAME);
        activitiesGroupsButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent e) {
                addActivitiesGroup();
            }
        });

        // Are groups enabled ?
        if (!logFrameModel.getEnableActivitiesGroups()) {
            activitiesGroupsButton.setVisible(false);
        }

        final Grid activitiesGrid = new Grid(3, 1);
        activitiesGrid.setWidth("100%");
        activitiesGrid.setCellPadding(0);
        activitiesGrid.setCellSpacing(0);
        activitiesGrid.setWidget(0, 0, activitiesLabel);
        activitiesGrid.setWidget(1, 0, activitiesButton);
        activitiesGrid.setWidget(2, 0, activitiesGroupsButton);

        // Prerequisites.
        final Label prerequisitesLabel = new Label(I18N.CONSTANTS.logFramePrerequisites());

        final Label prerequisitesButton = new Label(I18N.CONSTANTS.logFrameAddRow());
        prerequisitesButton.addStyleName(CSS_ADD_ACTION_STYLE_NAME);
        prerequisitesButton.setTitle(I18N.CONSTANTS.logFrameAddP());
        prerequisitesButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent e) {
                addPrerequisite();
            }
        });

        final Label prerequisitesGroupButton = new Label(I18N.CONSTANTS.logFrameAddGroup());
        prerequisitesGroupButton.addStyleName(CSS_ADD_GROUP_ACTION_STYLE_NAME);
        prerequisitesGroupButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent e) {
                addPrerequisitesGroup();
            }
        });

        // Are groups enabled ?
        if (!logFrameModel.getEnablePrerequisitesGroups()) {
            prerequisitesGroupButton.setVisible(false);
        }

        final Grid prerequisitesGrid = new Grid(3, 1);
        prerequisitesGrid.setWidth("100%");
        prerequisitesGrid.setCellPadding(0);
        prerequisitesGrid.setCellSpacing(0);
        prerequisitesGrid.setWidget(0, 0, prerequisitesLabel);
        prerequisitesGrid.setWidget(1, 0, prerequisitesButton);
        prerequisitesGrid.setWidget(2, 0, prerequisitesGroupButton);

        table.setWidget(1, 0, specificObjectivesGrid);
        table.setWidget(2, 0, exceptedResultsGrid);
        table.setWidget(3, 0, activitiesGrid);
        table.setWidget(4, 0, prerequisitesGrid);

        // Header styles.
        HTMLTableUtils.applyHeaderStyles(table, true);

        // Initializes grid views.
        columnsCount = 7;

        specificObjectivesView = new FlexTableView(table, columnsCount, 1);
        expectedResultsView = new FlexTableView(table, columnsCount, 2);
        activitiesView = new FlexTableView(table, columnsCount, 3);
        prerequisitesView = new FlexTableView(table, columnsCount, 4);

        specificObjectivesView.addDependency(expectedResultsView);
        specificObjectivesView.addDependency(activitiesView);
        specificObjectivesView.addDependency(prerequisitesView);

        expectedResultsView.addDependency(activitiesView);
        expectedResultsView.addDependency(prerequisitesView);

        activitiesView.addDependency(prerequisitesView);
    }

    /**
     * Initializes the codes policies.
     */
    private void initCodePolicies() {

        // Defines how the specific objectives codes are shown.
        specificObjectivesPolicy = new CodePolicy<SpecificObjectiveDTO>() {

            @Override
            public String getCode(int code, SpecificObjectiveDTO userObject) {

                final StringBuilder sb = new StringBuilder();
                sb.append(CodePolicy.getLetter(code, true, 1));
                sb.append(".");

                return sb.toString();
            }
        };

        // Defines how the expected results codes are shown.
        expectedResultsPolicy = new CodePolicy<ExpectedResultDTO>() {

            @Override
            public String getCode(int code, ExpectedResultDTO userObject) {

                final StringBuilder sb = new StringBuilder();

                final SpecificObjectiveDTO parent;
                if ((parent = userObject.getParentSpecificObjectiveDTO()) != null) {
                    sb.append(specificObjectivesPolicy.getCode(parent.getCode(), parent));
                }

                sb.append(code);
                sb.append(".");

                return sb.toString();
            }
        };

        // Defines how the activities codes are shown.
        activitiesPolicy = new CodePolicy<LogFrameActivityDTO>() {

            @Override
            public String getCode(int code, LogFrameActivityDTO userObject) {

                final StringBuilder sb = new StringBuilder();

                final ExpectedResultDTO parent;
                if ((parent = userObject.getParentExpectedResultDTO()) != null) {
                    sb.append(expectedResultsPolicy.getCode(parent.getCode(), parent));
                }

                sb.append(code);
                sb.append(".");

                return sb.toString();
            }
        };

        // Defines how the prerequisites codes are shown.
        prerequisitesPolicy = new CodePolicy<PrerequisiteDTO>() {

            @Override
            public String getCode(int code, PrerequisiteDTO userObject) {

                final StringBuilder sb = new StringBuilder();
                sb.append(code);
                sb.append(".");

                return sb.toString();
            }
        };
    }

    /**
     * Displays the log frame content in the log frame grid (specific
     * objectives, expected results, prerequisites, activities);
     * 
     * @param table
     *            The log frame grid.
     * @param logFrame
     *            The log frame.
     */
    public void displayLogFrame(LogFrameDTO logFrame) {

        this.logFrame = logFrame;
        this.logFrameModel = logFrame.getLogFrameModelDTO();

        ensureLogFrame();

        resetTable();
        initTable();
        initCodePolicies();

        for (final SpecificObjectiveDTO objective : logFrame.getSpecificObjectivesDTO()) {
            addSpecificObjective(objective);
        }

        for (final PrerequisiteDTO prerequisite : logFrame.getPrerequisitesDTO()) {
            addPrerequisite(prerequisite);
        }
    }

    /**
     * Updates the log frame instance of the grid after an updating.
     * 
     * @param logFrame
     *            The updated log frame.
     */
    public void updateLogFrame(LogFrameDTO logFrame) {

        // For the moment, each save action requires rebuilding the whole log
        // frame. Its needed to update the ids of the new entities in the local
        // maps.
        displayLogFrame(logFrame);

        // this.logFrame = logFrame;
        // this.logFrameModel = logFrame.getLogFrameModelDTO();
    }

    // ------------------------------------------------------------------------
    // - SPECIFIC OBJECTIVES
    // ------------------------------------------------------------------------

    // ------------------------
    // -- GROUPS
    // ------------------------

    /**
     * Creates a new display group for the specific objectives.
     */
    protected void addSpecificObjectivesGroup() {

        ensureLogFrame();

        // Asks for the new group label.
        MessageBox.prompt(I18N.CONSTANTS.logFrameAddGroup(), I18N.CONSTANTS.logFrameAddGroupToOS(), false,
                new Listener<MessageBoxEvent>() {

                    @Override
                    public void handleEvent(MessageBoxEvent be) {

                        // OK.
                        if (Dialog.OK.equals(be.getButtonClicked().getItemId())) {

                            // Creates the new group.
                            final LogFrameGroupDTO group = logFrame.addGroup(be.getValue(),
                                    LogFrameGroupType.SPECIFIC_OBJECTIVE);

                            // Displays it.
                            addSpecificObjectivesGroup(group);
                        }
                    }
                });
    }

    /**
     * Adds a new display group for the specific objectives.
     * 
     * @param group
     *            The specific objectives groups.
     */
    protected void addSpecificObjectivesGroup(final LogFrameGroupDTO group) {

        ensureLogFrame();

        // Adds a new group.
        specificObjectivesView.addGroup(new RowsGroup<LogFrameGroupDTO>(group) {

            @Override
            public String getTitle(LogFrameGroupDTO userObject) {

                // Builds the title (prefix + label).
                final StringBuilder sb = new StringBuilder();
                sb.append(I18N.CONSTANTS.logFrameGroup());
                sb.append(" (");
                sb.append(I18N.CONSTANTS.logFrameSpecificObjectivesCode());
                sb.append(") - ");
                sb.append(userObject.getLabel());

                return sb.toString();
            }

            @Override
            public int getId(LogFrameGroupDTO userObject) {
                return userObject.getClientSideId();
            }

            @Override
            public int[] getMergedColumnIndexes(LogFrameGroupDTO userObject) {
                return new int[] { 1 };
            }

            @Override
            public boolean isVisible(LogFrameGroupDTO userObject) {
                return logFrameModel.getEnableSpecificObjectivesGroups();
            }
        });

        fireLogFrameEdited();
    }

    // ------------------------
    // -- ROWS
    // ------------------------

    /**
     * Adds a new specific objective empty row.
     */
    protected void addSpecificObjective() {

        ensureLogFrame();

        // Must select a group.
        if (logFrameModel.getEnableSpecificObjectivesGroups()) {

            // Sets the selection window.
            selectionWindow.clear();
            selectionWindow.addChoicesList(I18N.CONSTANTS.logFrameGroup(),
                    logFrame.getAllGroups(LogFrameGroupType.SPECIFIC_OBJECTIVE), false, "label");
            selectionWindow.addSelectListener(new SelectListener() {

                @Override
                public void elementsSelected(ModelData... elements) {

                    // Checks that the selected elements are correct.
                    final ModelData element = elements[0];
                    if (!(element instanceof LogFrameGroupDTO)) {
                        return;
                    }

                    // Retrieves the selected group.
                    final LogFrameGroupDTO group = (LogFrameGroupDTO) element;

                    // Creates and displays a new objective.
                    final SpecificObjectiveDTO objective = logFrame.addSpecificObjective();
                    objective.setLogFrameGroupDTO(group);
                    addSpecificObjective(objective);
                }
            });

            selectionWindow.show(I18N.CONSTANTS.logFrameAddOS(), I18N.CONSTANTS.logFrameSelectGroupOS());
        }
        // Groups are disabled, no need to select a group, the default one will
        // be selected.
        else {

            // Retrieves the default group.
            final LogFrameGroupDTO group = logFrame.getDefaultGroup(LogFrameGroupType.SPECIFIC_OBJECTIVE);

            // Creates and displays a new objective.
            final SpecificObjectiveDTO objective = logFrame.addSpecificObjective();
            objective.setLogFrameGroupDTO(group);
            addSpecificObjective(objective);
        }
    }

    /**
     * Adds a specific objective row.
     * 
     * @param specificObjective
     *            The specific objective. Must not be <code>null</code>.
     */
    protected void addSpecificObjective(final SpecificObjectiveDTO specificObjective) {

        // Checks if the objective is correct.
        if (specificObjective == null) {
            throw new NullPointerException("specific objective must not be null");
        }

        // Retrieves the group.
        final LogFrameGroupDTO group = specificObjective.getLogFrameGroupDTO();

        // Retrieves the equivalent rows group.
        @SuppressWarnings("unchecked")
        final RowsGroup<LogFrameGroupDTO> g = (RowsGroup<LogFrameGroupDTO>) specificObjectivesView.getGroup(group
                .getClientSideId());

        // If the rows hasn't been created already, adds it.
        if (g == null) {
            addSpecificObjectivesGroup(group);
        }

        // Sets the display label.
        final StringBuilder sb = new StringBuilder();
        sb.append(I18N.CONSTANTS.logFrameSpecificObjectivesCode());
        sb.append(" ");
        sb.append(specificObjectivesPolicy.getCode(specificObjective.getCode(), specificObjective));
        specificObjective.setLabel(sb.toString());

        // Adds the row.
        specificObjectivesView.addRow(group.getClientSideId(), new Row<SpecificObjectiveDTO>(specificObjective) {

            @Override
            public boolean isSimilar(int column, SpecificObjectiveDTO userObject, SpecificObjectiveDTO other) {

                switch (column) {
                case 1:
                    // Code.
                    return userObject.getCode() == other.getCode();
                }
                return false;
            }

            @Override
            public Widget getWidgetAt(int column, final SpecificObjectiveDTO userObject) {

                switch (column) {
                case 0:

                    // Parent code.
                    return null;

                case 1:

                    // Code.
                    final Label codeLabel = new Label();
                    codeLabel.addStyleName(CSS_CODE_LABEL_STYLE_NAME);

                    if (userObject != null) {
                        codeLabel.setText(userObject.getLabel());
                    }

                    // Up action.
                    final MenuItem upMenuItem = new MenuItem(I18N.CONSTANTS.logFrameActionUp(), IconImageBundle.ICONS
                            .up());
                    upMenuItem.addListener(Events.Select, new Listener<BaseEvent>() {

                        @Override
                        public void handleEvent(BaseEvent be) {
                            specificObjectivesView.moveRow(g.getId(), getId(userObject), +1);
                        }
                    });

                    // Down action.
                    final MenuItem downMenuItem = new MenuItem(I18N.CONSTANTS.logFrameActionDown(),
                            IconImageBundle.ICONS.down());
                    downMenuItem.addListener(Events.Select, new Listener<BaseEvent>() {

                        @Override
                        public void handleEvent(BaseEvent be) {
                            specificObjectivesView.moveRow(g.getId(), getId(userObject), -1);
                        }
                    });

                    // Delete action.
                    final MenuItem deleteMenuItem = new MenuItem(I18N.CONSTANTS.logFrameActionDelete(),
                            IconImageBundle.ICONS.delete());
                    deleteMenuItem.addListener(Events.Select, new Listener<BaseEvent>() {

                        @Override
                        public void handleEvent(BaseEvent be) {
                            Window.alert("erasor");
                        }
                    });

                    // Menu.
                    final Menu menu = new Menu();
                    menu.add(upMenuItem);
                    menu.add(downMenuItem);
                    menu.add(deleteMenuItem);

                    final Anchor anchor = new Anchor("\u25BC");
                    anchor.addStyleName(CSS_MENU_BUTTON_STYLE_NAME);
                    anchor.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            menu.show(anchor);
                        }
                    });

                    // Grid.
                    final Grid grid = new Grid(1, 2);
                    grid.setCellPadding(0);
                    grid.setCellSpacing(0);
                    grid.setWidget(0, 0, codeLabel);
                    grid.setWidget(0, 1, anchor);

                    return grid;

                case 2:

                    // Intervention logic.
                    final TextArea interventionLogicTextBox = new TextArea();
                    interventionLogicTextBox.setWidth("100%");
                    interventionLogicTextBox.setHeight("100%");
                    interventionLogicTextBox.setVisibleLines(3);
                    interventionLogicTextBox.addStyleName("html-textbox");

                    if (userObject != null) {
                        interventionLogicTextBox.setText(userObject.getInterventionLogic());
                    }

                    interventionLogicTextBox.addChangeHandler(new ChangeHandler() {
                        @Override
                        public void onChange(ChangeEvent e) {
                            userObject.setInterventionLogic(interventionLogicTextBox.getText());
                            fireLogFrameEdited();
                        }
                    });

                    return interventionLogicTextBox;

                case 3:

                    // Indicators.
                    return new Label("");

                case 4:

                    // Risks.
                    final TextArea risksTextBox = new TextArea();
                    risksTextBox.setWidth("100%");
                    risksTextBox.setHeight("100%");
                    risksTextBox.setVisibleLines(3);
                    risksTextBox.addStyleName("html-textbox");

                    if (userObject != null) {
                        risksTextBox.setText(userObject.getRisks());
                    }

                    risksTextBox.addChangeHandler(new ChangeHandler() {
                        @Override
                        public void onChange(ChangeEvent e) {
                            userObject.setRisks(risksTextBox.getText());
                            fireLogFrameEdited();
                        }
                    });

                    return risksTextBox;

                case 5:

                    // Assumptions.
                    final TextArea assumptionsTextBox = new TextArea();
                    assumptionsTextBox.setWidth("100%");
                    assumptionsTextBox.setHeight("100%");
                    assumptionsTextBox.setVisibleLines(3);
                    assumptionsTextBox.addStyleName("html-textbox");

                    if (userObject != null) {
                        assumptionsTextBox.setText(userObject.getAssumptions());
                    }

                    assumptionsTextBox.addChangeHandler(new ChangeHandler() {
                        @Override
                        public void onChange(ChangeEvent e) {
                            userObject.setAssumptions(assumptionsTextBox.getText());
                            fireLogFrameEdited();
                        }
                    });

                    return assumptionsTextBox;

                default:
                    return null;
                }
            }

            @Override
            public int getId(SpecificObjectiveDTO userObject) {
                return userObject.getId();
            }
        });

        // Adds sub expected results.
        if (specificObjective.getExpectedResultsDTO() != null) {
            for (final ExpectedResultDTO result : specificObjective.getExpectedResultsDTO()) {
                addExpectedResult(result);
            }
        }
    }

    // ------------------------------------------------------------------------
    // - EXPECTED RESULTS
    // ------------------------------------------------------------------------

    // ------------------------
    // -- GROUPS
    // ------------------------

    /**
     * Creates a new display group for the expected results.
     */
    protected void addExpectedResultsGroup() {

        ensureLogFrame();

        // Asks for the new group label.
        MessageBox.prompt(I18N.CONSTANTS.logFrameAddGroup(), I18N.CONSTANTS.logFrameAddGroupToER(), false,
                new Listener<MessageBoxEvent>() {

                    @Override
                    public void handleEvent(MessageBoxEvent be) {

                        // OK.
                        if (Dialog.OK.equals(be.getButtonClicked().getItemId())) {

                            // Creates the new group.
                            final LogFrameGroupDTO group = logFrame.addGroup(be.getValue(),
                                    LogFrameGroupType.EXPECTED_RESULT);

                            // Displays it.
                            addExpectedResultsGroup(group);
                        }
                    }
                });
    }

    /**
     * Adds a new display group for the expected results.
     * 
     * @param group
     *            The expected results groups.
     */
    protected void addExpectedResultsGroup(final LogFrameGroupDTO group) {

        ensureLogFrame();

        // Adds a new group.
        expectedResultsView.addGroup(new RowsGroup<LogFrameGroupDTO>(group) {

            @Override
            public String getTitle(LogFrameGroupDTO userObject) {

                // Builds the title (prefix + label).
                final StringBuilder sb = new StringBuilder();
                sb.append(I18N.CONSTANTS.logFrameGroup());
                sb.append(" (");
                sb.append(I18N.CONSTANTS.logFrameExceptedResultsCode());
                sb.append(") - ");
                sb.append(userObject.getLabel());

                return sb.toString();
            }

            @Override
            public int getId(LogFrameGroupDTO userObject) {
                return userObject.getClientSideId();
            }

            @Override
            public int[] getMergedColumnIndexes(LogFrameGroupDTO userObject) {
                return new int[] { 0 };
            }

            @Override
            public boolean isVisible(LogFrameGroupDTO userObject) {
                return logFrameModel.getEnableExpectedResultsGroups();
            }
        });

        fireLogFrameEdited();
    }

    // ------------------------
    // -- ROWS
    // ------------------------

    /**
     * Adds a new expected result empty row.
     */
    protected void addExpectedResult() {

        ensureLogFrame();

        // Must select a group.
        if (logFrameModel.getEnableExpectedResultsGroups()) {

            // Sets the selection window.
            selectionWindow.clear();
            selectionWindow.addChoicesList(I18N.CONSTANTS.logFrameSpecificObjective(),
                    logFrame.getSpecificObjectivesDTO(), false, "label");
            selectionWindow.addChoicesList(I18N.CONSTANTS.logFrameGroup(),
                    logFrame.getAllGroups(LogFrameGroupType.EXPECTED_RESULT), false, "label");
            selectionWindow.addSelectListener(new SelectListener() {

                @Override
                public void elementsSelected(ModelData... elements) {

                    // Checks that the selected elements are correct.
                    final ModelData element0 = elements[0];
                    if (!(element0 instanceof SpecificObjectiveDTO)) {
                        return;
                    }

                    final ModelData element1 = elements[1];
                    if (!(element1 instanceof LogFrameGroupDTO)) {
                        return;
                    }

                    // Retrieves the selected OS and group.
                    final SpecificObjectiveDTO specificObjective = (SpecificObjectiveDTO) element0;
                    final LogFrameGroupDTO group = (LogFrameGroupDTO) element1;

                    // Creates and displays a new objective.
                    final ExpectedResultDTO result = specificObjective.addExpectedResult();
                    result.setLogFrameGroupDTO(group);
                    addExpectedResult(result);
                }
            });

            selectionWindow.show(I18N.CONSTANTS.logFrameAddER(), I18N.CONSTANTS.logFrameSelectGroupER());
        }
        // Groups are disabled, no need to select a group, the default one will
        // be selected.
        else {

            // Sets the selection window.
            selectionWindow.clear();
            selectionWindow.addChoicesList(I18N.CONSTANTS.logFrameSpecificObjective(),
                    logFrame.getSpecificObjectivesDTO(), false, "label");
            selectionWindow.addSelectListener(new SelectListener() {

                @Override
                public void elementsSelected(ModelData... elements) {

                    // Checks that the selected elements are correct.
                    final ModelData element0 = elements[0];
                    if (!(element0 instanceof SpecificObjectiveDTO)) {
                        return;
                    }

                    // Retrieves the selected OS.
                    final SpecificObjectiveDTO specificObjective = (SpecificObjectiveDTO) element0;

                    // Retrieves the default group.
                    final LogFrameGroupDTO group = logFrame.getDefaultGroup(LogFrameGroupType.EXPECTED_RESULT);

                    // Creates and displays a new objective.
                    final ExpectedResultDTO result = specificObjective.addExpectedResult();
                    result.setLogFrameGroupDTO(group);
                    addExpectedResult(result);
                }
            });

            selectionWindow.show(I18N.CONSTANTS.logFrameAddER(), I18N.CONSTANTS.logFrameSelectGroup2ER());
        }
    }

    /**
     * Adds an activity row.
     * 
     * @param result
     *            The expected result. Must not be <code>null</code>.
     */
    protected void addExpectedResult(final ExpectedResultDTO result) {

        // Checks if the result is correct.
        if (result == null) {
            throw new NullPointerException("result must not be null");
        }

        // Retrieves the group.
        final LogFrameGroupDTO group = result.getLogFrameGroupDTO();

        // Retrieves the equivalent rows group.
        @SuppressWarnings("unchecked")
        final RowsGroup<LogFrameGroupDTO> g = (RowsGroup<LogFrameGroupDTO>) expectedResultsView.getGroup(group
                .getClientSideId());

        // If the rows hasn't been created already, adds it.
        if (g == null) {
            addExpectedResultsGroup(group);
        }

        // Sets the display label.
        final StringBuilder sb = new StringBuilder();
        sb.append(I18N.CONSTANTS.logFrameExceptedResultsCode());
        sb.append(" ");
        sb.append(expectedResultsPolicy.getCode(result.getCode(), result));
        result.setLabel(sb.toString());

        // Adds the row.
        expectedResultsView.addRow(group.getClientSideId(), new Row<ExpectedResultDTO>(result) {

            @Override
            public boolean isSimilar(int column, ExpectedResultDTO userObject, ExpectedResultDTO other) {

                switch (column) {
                case 0:
                    // Parent code.
                    return userObject.getParentSpecificObjectiveDTO() != null
                            && other.getParentSpecificObjectiveDTO() != null
                            && userObject.getParentSpecificObjectiveDTO().getCode() == other
                                    .getParentSpecificObjectiveDTO().getCode();
                }
                return false;
            }

            @Override
            public Widget getWidgetAt(int column, final ExpectedResultDTO userObject) {

                switch (column) {
                case 0:

                    // Parent code.
                    final Label parentCodeLabel = new Label();
                    parentCodeLabel.addStyleName(CSS_CODE_LABEL_STYLE_NAME);

                    final SpecificObjectiveDTO parent;
                    if (userObject != null && (parent = userObject.getParentSpecificObjectiveDTO()) != null) {

                        final StringBuilder sb = new StringBuilder();

                        sb.append(I18N.CONSTANTS.logFrameExceptedResultsCode());
                        sb.append(" (");
                        sb.append(I18N.CONSTANTS.logFrameSpecificObjectivesCode());
                        sb.append(" ");
                        sb.append(specificObjectivesPolicy.getCode(parent.getCode(), parent));
                        sb.append(")");

                        parentCodeLabel.setText(sb.toString());
                    }

                    return parentCodeLabel;

                case 1:

                    // Code.
                    final Label codeLabel = new Label();
                    codeLabel.addStyleName(CSS_CODE_LABEL_STYLE_NAME);

                    if (userObject != null) {
                        codeLabel.setText(userObject.getLabel());
                    }

                    return codeLabel;

                case 2:

                    // Intervention logic.
                    final TextArea interventionLogicTextBox = new TextArea();
                    interventionLogicTextBox.setWidth("100%");
                    interventionLogicTextBox.setHeight("100%");
                    interventionLogicTextBox.setVisibleLines(3);
                    interventionLogicTextBox.addStyleName("html-textbox");

                    if (userObject != null) {
                        interventionLogicTextBox.setText(userObject.getInterventionLogic());
                    }

                    interventionLogicTextBox.addChangeHandler(new ChangeHandler() {
                        @Override
                        public void onChange(ChangeEvent e) {
                            userObject.setInterventionLogic(interventionLogicTextBox.getText());
                            fireLogFrameEdited();
                        }
                    });

                    return interventionLogicTextBox;

                case 3:

                    // Indicators.
                    return new Label("");

                case 4:

                    // Risks.
                    final TextArea risksTextBox = new TextArea();
                    risksTextBox.setWidth("100%");
                    risksTextBox.setHeight("100%");
                    risksTextBox.setVisibleLines(3);
                    risksTextBox.addStyleName("html-textbox");

                    if (userObject != null) {
                        risksTextBox.setText(userObject.getRisks());
                    }

                    risksTextBox.addChangeHandler(new ChangeHandler() {
                        @Override
                        public void onChange(ChangeEvent e) {
                            userObject.setRisks(risksTextBox.getText());
                            fireLogFrameEdited();
                        }
                    });

                    return risksTextBox;

                case 5:

                    // Assumptions.
                    final TextArea assumptionsTextBox = new TextArea();
                    assumptionsTextBox.setWidth("100%");
                    assumptionsTextBox.setHeight("100%");
                    assumptionsTextBox.setVisibleLines(3);
                    assumptionsTextBox.addStyleName("html-textbox");

                    if (userObject != null) {
                        assumptionsTextBox.setText(userObject.getAssumptions());
                    }

                    assumptionsTextBox.addChangeHandler(new ChangeHandler() {
                        @Override
                        public void onChange(ChangeEvent e) {
                            userObject.setAssumptions(assumptionsTextBox.getText());
                            fireLogFrameEdited();
                        }
                    });

                    return assumptionsTextBox;

                default:
                    return null;
                }
            }

            @Override
            public int getId(ExpectedResultDTO userObject) {
                return userObject.getId();
            }
        });

        // Adds sub activities.
        if (result.getActivitiesDTO() != null) {
            for (final LogFrameActivityDTO activity : result.getActivitiesDTO()) {
                addActivity(activity);
            }
        }
    }

    // ------------------------------------------------------------------------
    // - ACTIVITIES
    // ------------------------------------------------------------------------

    // ------------------------
    // -- GROUPS
    // ------------------------

    /**
     * Creates a new display group for the activities.
     */
    protected void addActivitiesGroup() {

        ensureLogFrame();

        // Asks for the new group label.
        MessageBox.prompt(I18N.CONSTANTS.logFrameAddGroup(), I18N.CONSTANTS.logFrameAddGroupToA(), false,
                new Listener<MessageBoxEvent>() {

                    @Override
                    public void handleEvent(MessageBoxEvent be) {

                        // OK.
                        if (Dialog.OK.equals(be.getButtonClicked().getItemId())) {

                            // Creates the new group.
                            final LogFrameGroupDTO group = logFrame.addGroup(be.getValue(), LogFrameGroupType.ACTIVITY);

                            // Displays it.
                            addActivitiesGroup(group);
                        }
                    }
                });
    }

    /**
     * Adds a new display group for the activities.
     * 
     * @param group
     *            The activities group.
     */
    protected void addActivitiesGroup(final LogFrameGroupDTO group) {

        ensureLogFrame();

        // Adds a new group.
        activitiesView.addGroup(new RowsGroup<LogFrameGroupDTO>(group) {

            @Override
            public String getTitle(LogFrameGroupDTO userObject) {

                // Builds the title (prefix + label).
                final StringBuilder sb = new StringBuilder();
                sb.append(I18N.CONSTANTS.logFrameGroup());
                sb.append(" (");
                sb.append(I18N.CONSTANTS.logFrameActivitiesCode());
                sb.append(") - ");
                sb.append(userObject.getLabel());

                return sb.toString();
            }

            @Override
            public int getId(LogFrameGroupDTO userObject) {
                return userObject.getClientSideId();
            }

            @Override
            public int[] getMergedColumnIndexes(LogFrameGroupDTO userObject) {
                return new int[] { 0 };
            }

            @Override
            public boolean isVisible(LogFrameGroupDTO userObject) {
                return logFrameModel.getEnableActivitiesGroups();
            }
        });

        fireLogFrameEdited();
    }

    // ------------------------
    // -- ROWS
    // ------------------------

    /**
     * Adds a new activity empty row.
     */
    protected void addActivity() {

        ensureLogFrame();

        // Must select a group.
        if (logFrameModel.getEnableActivitiesGroups()) {

            // Sets the selection window.
            selectionWindow.clear();
            selectionWindow.addChoicesList(I18N.CONSTANTS.logFrameExceptedResult(),
                    logFrame.getAllExpectedResultsDTO(), false, "label");
            selectionWindow.addChoicesList(I18N.CONSTANTS.logFrameGroup(),
                    logFrame.getAllGroups(LogFrameGroupType.ACTIVITY), false, "label");
            selectionWindow.addSelectListener(new SelectListener() {

                @Override
                public void elementsSelected(ModelData... elements) {

                    // Checks that the selected elements are correct.
                    final ModelData element0 = elements[0];
                    if (!(element0 instanceof ExpectedResultDTO)) {
                        return;
                    }

                    final ModelData element1 = elements[1];
                    if (!(element1 instanceof LogFrameGroupDTO)) {
                        return;
                    }

                    // Retrieves the selected ER and group.
                    final ExpectedResultDTO expectedResult = (ExpectedResultDTO) element0;
                    final LogFrameGroupDTO group = (LogFrameGroupDTO) element1;

                    // Creates and displays a new activity.
                    final LogFrameActivityDTO activity = expectedResult.addActivity();
                    activity.setLogFrameGroupDTO(group);
                    addActivity(activity);
                }
            });

            selectionWindow.show(I18N.CONSTANTS.logFrameAddA(), I18N.CONSTANTS.logFrameSelectGroupA());
        }
        // Groups are disabled, no need to select a group, the default one will
        // be selected.
        else {

            // Sets the selection window.
            selectionWindow.clear();
            selectionWindow.addChoicesList(I18N.CONSTANTS.logFrameExceptedResult(),
                    logFrame.getAllExpectedResultsDTO(), false, "label");
            selectionWindow.addSelectListener(new SelectListener() {

                @Override
                public void elementsSelected(ModelData... elements) {

                    // Checks that the selected elements are correct.
                    final ModelData element0 = elements[0];
                    if (!(element0 instanceof ExpectedResultDTO)) {
                        return;
                    }

                    // Retrieves the selected ER and group.
                    final ExpectedResultDTO expectedResult = (ExpectedResultDTO) element0;

                    // Retrieves the default group.
                    final LogFrameGroupDTO group = logFrame.getDefaultGroup(LogFrameGroupType.ACTIVITY);

                    // Creates and displays a new activity.
                    final LogFrameActivityDTO activity = expectedResult.addActivity();
                    activity.setLogFrameGroupDTO(group);
                    addActivity(activity);
                }
            });

            selectionWindow.show(I18N.CONSTANTS.logFrameAddA(), I18N.CONSTANTS.logFrameSelectGroup2A());
        }
    }

    /**
     * Adds an activity row.
     * 
     * @param activity
     *            The activity. Must not be <code>null</code>.
     */
    protected void addActivity(final LogFrameActivityDTO activity) {

        // Checks if the activity is correct.
        if (activity == null) {
            throw new NullPointerException("activity must not be null");
        }

        // Retrieves the group.
        final LogFrameGroupDTO group = activity.getLogFrameGroupDTO();

        // Retrieves the equivalent rows group.
        @SuppressWarnings("unchecked")
        final RowsGroup<LogFrameGroupDTO> g = (RowsGroup<LogFrameGroupDTO>) activitiesView.getGroup(group
                .getClientSideId());

        // If the rows hasn't been created already, adds it.
        if (g == null) {
            addActivitiesGroup(group);
        }

        // Sets the display label.
        final StringBuilder sb = new StringBuilder();
        sb.append(I18N.CONSTANTS.logFrameActivitiesCode());
        sb.append(" ");
        sb.append(activitiesPolicy.getCode(activity.getCode(), activity));
        activity.setLabel(sb.toString());

        // Adds the row.
        activitiesView.addRow(group.getClientSideId(), new Row<LogFrameActivityDTO>(activity) {

            @Override
            public boolean isSimilar(int column, LogFrameActivityDTO userObject, LogFrameActivityDTO other) {

                switch (column) {
                case 0:
                    // Parent code.
                    return userObject.getParentExpectedResultDTO() != null
                            && other.getParentExpectedResultDTO() != null
                            && userObject.getParentExpectedResultDTO().getCode() == other.getParentExpectedResultDTO()
                                    .getCode();
                }
                return false;
            }

            @Override
            public Widget getWidgetAt(int column, final LogFrameActivityDTO userObject) {

                switch (column) {
                case 0:

                    // Parent code.
                    final Label parentCodeLabel = new Label();
                    parentCodeLabel.addStyleName(CSS_CODE_LABEL_STYLE_NAME);

                    final ExpectedResultDTO parent;
                    if (userObject != null && (parent = userObject.getParentExpectedResultDTO()) != null) {

                        final StringBuilder sb = new StringBuilder();

                        sb.append(I18N.CONSTANTS.logFrameActivitiesCode());
                        sb.append(" (");
                        sb.append(I18N.CONSTANTS.logFrameExceptedResultsCode());
                        sb.append(" ");
                        sb.append(expectedResultsPolicy.getCode(parent.getCode(), parent));
                        sb.append(")");

                        parentCodeLabel.setText(sb.toString());
                    }

                    return parentCodeLabel;

                case 1:

                    // Code.
                    final Label codeLabel = new Label();
                    codeLabel.addStyleName(CSS_CODE_LABEL_STYLE_NAME);

                    if (userObject != null) {
                        codeLabel.setText(userObject.getLabel());
                    }

                    return codeLabel;

                case 5:

                    // Activity content.
                    final TextArea contentTextBox = new TextArea();
                    contentTextBox.setWidth("100%");
                    contentTextBox.setHeight("100%");
                    contentTextBox.setVisibleLines(2);
                    contentTextBox.addStyleName("html-textbox");

                    if (userObject != null) {
                        contentTextBox.setText(userObject.getContent());
                    }

                    contentTextBox.addChangeHandler(new ChangeHandler() {
                        @Override
                        public void onChange(ChangeEvent e) {
                            userObject.setContent(contentTextBox.getText());
                            fireLogFrameEdited();
                        }
                    });

                    return contentTextBox;
                default:
                    return null;
                }
            }

            @Override
            public int getId(LogFrameActivityDTO userObject) {
                return userObject.getId();
            }
        });
    }

    // ------------------------------------------------------------------------
    // - PREREQUISITES
    // ------------------------------------------------------------------------

    // ------------------------
    // -- GROUPS
    // ------------------------

    /**
     * Creates a new display group for the prerequisites.
     */
    protected void addPrerequisitesGroup() {

        ensureLogFrame();

        // Asks for the new group label.
        MessageBox.prompt(I18N.CONSTANTS.logFrameAddGroup(), I18N.CONSTANTS.logFrameAddGroupToP(), false,
                new Listener<MessageBoxEvent>() {

                    @Override
                    public void handleEvent(MessageBoxEvent be) {

                        // OK.
                        if (Dialog.OK.equals(be.getButtonClicked().getItemId())) {

                            // Creates the new group.
                            final LogFrameGroupDTO group = logFrame.addGroup(be.getValue(),
                                    LogFrameGroupType.PREREQUISITE);

                            // Displays it.
                            addPrerequisitesGroup(group);
                        }
                    }
                });
    }

    /**
     * Adds a new display group for the prerequisites.
     * 
     * @param group
     *            The prerequisites group.
     */
    protected void addPrerequisitesGroup(final LogFrameGroupDTO group) {

        ensureLogFrame();

        // Adds a new group.
        prerequisitesView.addGroup(new RowsGroup<LogFrameGroupDTO>(group) {

            @Override
            public String getTitle(LogFrameGroupDTO userObject) {

                // Builds the title (prefix + label).
                final StringBuilder sb = new StringBuilder();
                sb.append(I18N.CONSTANTS.logFrameGroup());
                sb.append(" (");
                sb.append(I18N.CONSTANTS.logFramePrerequisitesCode());
                sb.append(") - ");
                sb.append(userObject.getLabel());

                return sb.toString();
            }

            @Override
            public int getId(LogFrameGroupDTO userObject) {
                return userObject.getClientSideId();
            }

            @Override
            public int[] getMergedColumnIndexes(LogFrameGroupDTO userObject) {
                return new int[0];
            }

            @Override
            public boolean isVisible(LogFrameGroupDTO userObject) {
                return logFrameModel.getEnablePrerequisitesGroups();
            }
        });

        fireLogFrameEdited();
    }

    // ------------------------
    // -- ROWS
    // ------------------------

    /**
     * Adds a new prerequisite empty row.
     */
    protected void addPrerequisite() {

        ensureLogFrame();

        // Must select a group.
        if (logFrameModel.getEnablePrerequisitesGroups()) {

            // Sets the selection window.
            selectionWindow.clear();
            selectionWindow.addChoicesList(I18N.CONSTANTS.logFrameGroup(),
                    logFrame.getAllGroups(LogFrameGroupType.PREREQUISITE), false, "label");
            selectionWindow.addSelectListener(new SelectListener() {

                @Override
                public void elementsSelected(ModelData... elements) {

                    // Checks that the selected elements are correct.
                    final ModelData element0 = elements[0];
                    if (!(element0 instanceof LogFrameGroupDTO)) {
                        return;
                    }

                    // Retrieves the group.
                    final LogFrameGroupDTO group = (LogFrameGroupDTO) element0;

                    // Creates and displays a new prerequisite.
                    final PrerequisiteDTO prerequisite = logFrame.addPrerequisite();
                    prerequisite.setLogFrameGroupDTO(group);
                    addPrerequisite(prerequisite);
                }
            });

            selectionWindow.show(I18N.CONSTANTS.logFrameAddP(), I18N.CONSTANTS.logFrameSelectGroupP());
        }
        // Groups are disabled, no need to select a group, the default one will
        // be selected.
        else {

            // Retrieves the default group.
            final LogFrameGroupDTO group = logFrame.getDefaultGroup(LogFrameGroupType.PREREQUISITE);

            // Creates and displays a new prerequisite.
            final PrerequisiteDTO prerequisite = logFrame.addPrerequisite();
            prerequisite.setLogFrameGroupDTO(group);
            addPrerequisite(prerequisite);
        }
    }

    /**
     * Adds an prerequisite row.
     * 
     * @param prerequisite
     *            The prerequisite. Must not be <code>null</code>.
     */
    protected void addPrerequisite(final PrerequisiteDTO prerequisite) {

        // Checks if the prerequisite is correct.
        if (prerequisite == null) {
            throw new NullPointerException("prerequisite must not be null");
        }

        // Retrieves the group.
        final LogFrameGroupDTO group = prerequisite.getLogFrameGroupDTO();

        // Retrieves the equivalent rows group.
        @SuppressWarnings("unchecked")
        final RowsGroup<LogFrameGroupDTO> g = (RowsGroup<LogFrameGroupDTO>) prerequisitesView.getGroup(group
                .getClientSideId());

        // If the rows hasn't been created already, adds it.
        if (g == null) {
            addPrerequisitesGroup(group);
        }

        // Sets the display label.
        final StringBuilder sb = new StringBuilder();
        sb.append(I18N.CONSTANTS.logFramePrerequisitesCode());
        sb.append(" ");
        sb.append(prerequisitesPolicy.getCode(prerequisite.getCode(), prerequisite));
        prerequisite.setLabel(sb.toString());

        // Adds the row.
        prerequisitesView.addRow(group.getClientSideId(), new Row<PrerequisiteDTO>(prerequisite) {

            @Override
            public boolean isSimilar(int column, PrerequisiteDTO userObject, PrerequisiteDTO other) {
                return false;
            }

            @Override
            public Widget getWidgetAt(int column, final PrerequisiteDTO userObject) {

                switch (column) {
                case 5:

                    // Activity content.
                    final TextArea contentTextBox = new TextArea();
                    contentTextBox.setWidth("100%");
                    contentTextBox.setHeight("100%");
                    contentTextBox.setVisibleLines(2);
                    contentTextBox.addStyleName("html-textbox");

                    if (userObject != null) {
                        contentTextBox.setText(userObject.getContent());
                    }

                    contentTextBox.addChangeHandler(new ChangeHandler() {
                        @Override
                        public void onChange(ChangeEvent e) {
                            userObject.setContent(contentTextBox.getText());
                            fireLogFrameEdited();
                        }
                    });

                    return contentTextBox;
                default:
                    return null;
                }
            }

            @Override
            public int getId(PrerequisiteDTO userObject) {
                return userObject.getId();
            }
        });
    }
}
