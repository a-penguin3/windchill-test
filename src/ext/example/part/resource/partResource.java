package ext.example.part.resource;

import wt.util.resource.*;

@RBUUID("ext.example.part.resource.partResource")
public final class partResource extends WTListResourceBundle{

    @RBEntry("Customized test menu")
    public static final String EXAMPLE_NAVIGATE_TITLE = "example.myMenuAction.title";

    @RBEntry("Test menu")
    public static final String EXAMPLE_NAVIGATE_DESCRIPTION = "example.myMenuAction.description";

    @RBEntry("Test menu")
    public static final String EXAMPLE_NAVIGATE_TOOLTIP = "example.myMenuAction.tooltip";

    @RBEntry("modify identity")
    public static final String TEST_TITLE = "example.testProcessor.title";

    @RBEntry("modify identity")
    public static final String TEST_DESCRIPTION = "example.testProcessor.description";

    @RBEntry("modify identity")
    public static final String TEST_TOOLTIP = "example.testProcessor.tooltip";

    @RBEntry("name")
    public static final String TEST_NAME = "PART_NAME";

    @RBEntry("number")
    public static final String TEST_NUMBER = "PART_NUMBER";

    @RBEntry("attr")
    public static final String TEST_ATTR = "PART_ATTR";

//    @RBEntry("Customized Navigate Button")
//    public static final String EXAMPLE_MYNAVIGATEBUTTON_TITLE = "";
//
//    @RBEntry("")
//    public static final String EXAMPLE_MYNAVIGATEBUTTON_DESCRIPTION = "";
//
//    @RBEntry("")
//    public static final String EXAMPLE_MYNAVIGATEBUTTON_TOOLTIP = "";
//
//    @RBEntry("")
//    public static final String EXAMPLE_MYNAVIGATEBUTTON_ICON = "";
    
}
