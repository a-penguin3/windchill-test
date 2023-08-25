package ext.generic.folder;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.generic.folder.folderBd")
public class folderBd extends WTListResourceBundle {
        @RBEntry("Test data import")
        public static final String TEST_PROCESSOR_TITLE = "processorTest.importTest.title";

        @RBEntry("Test data import")
        public static final String TEST_PROCESSOR_DES = "processorTest.importTest.description";

        @RBEntry("Test data import")
        public static final String TEST_PROCESSOR_TOOLTIP = "processorTest.importTest.tooltip";

        @RBEntry("import_from_excel.png")
        public static final String TEST_PROCESSOR_ICON = "processorTest.importTest.icon";
}
