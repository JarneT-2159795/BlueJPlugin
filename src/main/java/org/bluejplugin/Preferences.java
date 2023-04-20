package org.bluejplugin;

import bluej.extensions2.BlueJ;
import bluej.extensions2.PreferenceGenerator;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * This class is used to create the preferences panel for the extension.
 * It is a singleton class, so that there is only one instance of the
 * preferences panel.
 *
 * @author Jarne Thys
 */
public final class Preferences implements PreferenceGenerator
{
    public static final String JAVADOC_LABEL = "Javadoc-Action";
    public static final String SEARCH_VARIABLES_LABEL = "Search-Variables-Action";
    public static final String GET_LABEL = "Get-Action";
    public static final String SET_LABEL = "Set-Action";
    public static final String PUBLIC_LABEL = "Public-Action";
    public static final String CAMEL_CASE_LABEL = "CamelCase-Action";
    public static final String IF_LABEL = "If-Action";
    public static final String EQUAL_LABEL = "Equal-Action";
    public static final String PMD_LABEL = "PMD-Action";
    private static Preferences instance = null;
    private final CheckBox Javadoc;
    private final CheckBox SearchVariables;
    private final CheckBox Get;
    private final CheckBox Set;
    private final CheckBox Public;
    private final CheckBox CamelCase;
    private final CheckBox If;
    private final CheckBox Pmd;
    private final CheckBox Equal;
    private final Pane myPane;
    private final BlueJ bluej;

    /**
     * Create the preferences panel and initialise the fields.
     *
     * @param bluej The BlueJ object
     */
    private Preferences(BlueJ bluej)
    {
        this.bluej = bluej;
        myPane = new Pane();
        VBox vBoxContainer = new VBox();

        Javadoc = new CheckBox();
        vBoxContainer.getChildren().add(new HBox(Javadoc, new Label("  Check Javadocs")));

        SearchVariables = new CheckBox();
        vBoxContainer.getChildren().add(new HBox(SearchVariables, new Label("  Search for variables")));

        Get = new CheckBox();
        vBoxContainer.getChildren().add(new HBox(Get, new Label("  Check Getters")));

        Set = new CheckBox();
        vBoxContainer.getChildren().add(new HBox(Set, new Label("  Check Setters")));

        Public = new CheckBox();
        vBoxContainer.getChildren().add(new HBox(Public, new Label("  Check Public")));

        CamelCase = new CheckBox();
        vBoxContainer.getChildren().add(new HBox(CamelCase, new Label("  Check CamelCase")));

        If = new CheckBox();
        vBoxContainer.getChildren().add(new HBox(If, new Label("  Check If")));

        Equal = new CheckBox();
        vBoxContainer.getChildren().add(new HBox(Equal, new Label("  Check Equal")));

        Pmd = new CheckBox();
        vBoxContainer.getChildren().add(new HBox(Pmd, new Label("  Use PMD")));

        myPane.getChildren().add(vBoxContainer);
        // Load the default value
        loadValues();
    }

    /**
     * Get the singleton instance of the preferences panel.
     *
     * @param bluej The BlueJ object
     * @return The singleton instance of the preferences panel
     */
    public static synchronized Preferences getInstance(BlueJ bluej)
    {
        if (instance == null)
        {
            instance = new Preferences(bluej);
        }
        return instance;
    }

    /**
     * Get the singleton instance of the preferences panel.
     *
     * @return The singleton instance of the preferences panel
     * @throws IllegalStateException if the preferences panel has not been initialised
     */
    public static synchronized Preferences getInstance()
    {
        if (instance == null)
        {
            throw new IllegalStateException("Preferences not initialised");
        }
        return instance;
    }

    /**
     * Get the preferences panel.
     *
     * @return The preferences panel
     */
    @Override
    public Pane getWindow()
    {
        return myPane;
    }

    /**
     * Save the values of the preferences panel.
     */
    @Override
    public void saveValues()
    {
        // Save the preference value in the BlueJ properties file
        bluej.setExtensionPropertyString(JAVADOC_LABEL, String.valueOf(Javadoc.isSelected()));
        bluej.setExtensionPropertyString(SEARCH_VARIABLES_LABEL, String.valueOf(SearchVariables.isSelected()));
        bluej.setExtensionPropertyString(GET_LABEL, String.valueOf(Get.isSelected()));
        bluej.setExtensionPropertyString(SET_LABEL, String.valueOf(Set.isSelected()));
        bluej.setExtensionPropertyString(PUBLIC_LABEL, String.valueOf(Public.isSelected()));
        bluej.setExtensionPropertyString(CAMEL_CASE_LABEL, String.valueOf(CamelCase.isSelected()));
        bluej.setExtensionPropertyString(IF_LABEL, String.valueOf(If.isSelected()));
        bluej.setExtensionPropertyString(EQUAL_LABEL, String.valueOf(Equal.isSelected()));
        bluej.setExtensionPropertyString(PMD_LABEL, String.valueOf(Pmd.isSelected()));
    }

    /**
     * Load the values of the preferences panel.
     */
    @Override
    public void loadValues()
    {
        // Load the property value from the BlueJ properties file,
        // default to an empty string
        Javadoc.setSelected(Boolean.parseBoolean(bluej.getExtensionPropertyString(JAVADOC_LABEL, "true")));
        SearchVariables.setSelected(Boolean.parseBoolean(bluej.getExtensionPropertyString(SEARCH_VARIABLES_LABEL, "true")));
        Get.setSelected(Boolean.parseBoolean(bluej.getExtensionPropertyString(GET_LABEL, "true")));
        Set.setSelected(Boolean.parseBoolean(bluej.getExtensionPropertyString(SET_LABEL, "true")));
        Public.setSelected(Boolean.parseBoolean(bluej.getExtensionPropertyString(PUBLIC_LABEL, "true")));
        CamelCase.setSelected(Boolean.parseBoolean(bluej.getExtensionPropertyString(CAMEL_CASE_LABEL, "true")));
        If.setSelected(Boolean.parseBoolean(bluej.getExtensionPropertyString(IF_LABEL, "true")));
        Equal.setSelected(Boolean.parseBoolean(bluej.getExtensionPropertyString(EQUAL_LABEL, "true")));
        Pmd.setSelected(Boolean.parseBoolean(bluej.getExtensionPropertyString(PMD_LABEL, "true")));
    }

    /**
     * Get the selection state of the Javadoc checkbox.
     *
     * @return Boolean value of the Javadoc checkbox
     */
    public boolean getJavadoc()
    {
        return Javadoc.isSelected();
    }

    /**
     * Get the selection state of the SearchVariables checkbox.
     *
     * @return Boolean value of the SearchVariables checkbox
     */
    public boolean getSearchVariables()
    {
        return SearchVariables.isSelected();
    }

    /**
     * Get the selection state of the Get checkbox.
     *
     * @return Boolean value of the Get checkbox
     */
    public boolean getGet()
    {
        return Get.isSelected();
    }

    /**
     * Get the selection state of the Set checkbox.
     *
     * @return Boolean value of the Set checkbox
     */
    public boolean getSet()
    {
        return Set.isSelected();
    }

    /**
     * Get the selection state of the Public checkbox.
     *
     * @return Boolean value of the Public checkbox
     */
    public boolean getPublic()
    {
        return Public.isSelected();
    }

    /**
     * Get the selection state of the CamelCase checkbox.
     *
     * @return Boolean value of the CamelCase checkbox
     */
    public boolean getCamelCase()
    {
        return CamelCase.isSelected();
    }

    /**
     * Get the selection state of the If checkbox.
     *
     * @return Boolean value of the If checkbox
     */
    public boolean getIf()
    {
        return If.isSelected();
    }

    /**
     * Get the selection state of the Equal checkbox.
     *
     * @return Boolean value of the Equal checkbox
     */
    public boolean getEqual()
    {
        return Equal.isSelected();
    }

    /**
     * Get the selection state of the Pmd checkbox.
     *
     * @return Boolean value of the Pmd checkbox
     */
    public boolean getPmd()
    {
        return Pmd.isSelected();
    }
}