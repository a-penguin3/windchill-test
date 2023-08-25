package ext.example.part.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.example.part.resource.partResource")
public final class partResource_zh_CN extends WTListResourceBundle {

    @RBEntry("测试菜单")
    public static final String EXAMPLE_NAVIGATE_TITLE = "example.myMenuAction.title";

    @RBEntry("测试菜单")
    public static final String EXAMPLE_NAVIGATE_DESCRIPTION = "example.myMenuAction.description";

    @RBEntry("测试菜单")
    public static final String EXAMPLE_NAVIGATE_TOOLTIP = "example.myMenuAction.tooltip";


    @RBEntry("修改标识")
    public static final String TEST_TITLE = "example.testProcessor.title";

    @RBEntry("修改标识")
    public static final String TEST_DESCRIPTION = "example.testProcessor.description";

    @RBEntry("修改标识")
    public static final String TEST_TOOLTIP = "example.testProcessor.tooltip";

    @RBEntry("名称")
    public static final String TEST_NAME = "PART_NAME";

    @RBEntry("编号")
    public static final String TEST_NUMBER = "PART_NUMBER";

    @RBEntry("自动注入")
    public static final String TEST_ATTR = "PART_ATTR";
}
