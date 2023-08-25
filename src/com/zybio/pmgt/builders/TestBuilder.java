package com.zybio.pmgt.builders;

import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.mvc.components.*;
import com.ptc.mvc.ds.server.jmx.PerformanceConfig;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

@ComponentBuilder({"com.zybio.pmgt.builders.TestBuilder"})
public class TestBuilder extends AbstractComponentBuilder {
    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        ComponentConfigFactory factory = this.getComponentConfigFactory();
        TableConfig tableconfig = factory.newTableConfig();
        tableconfig.setLabel("部件信息"); //设置表格的标签
        tableconfig.setSelectable(true);  //设置选择框
        tableconfig.setSingleSelect(true);  //设置是否单选
        tableconfig.setActionModel(""); //设置model
        tableconfig.setShowCount(true); //设置是否显示总数
//        tableconfig.setShowCustomViewLink(false); //
        tableconfig.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.ICON, true));//设置图标
        tableconfig.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NAME, true));//名称
        tableconfig.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NUMBER, true)); //编号
        tableconfig.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.INFO_ACTION, true)); //详细信息
        tableconfig.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.VERSION, true)); //版本
        tableconfig.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.LAST_MODIFIED, true)); //修改时间
        tableconfig.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.CONTAINER_NAME, true)); //所属容器

        //设置启用右键菜单
        ColumnConfig nmActionsColumnConfig = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NM_ACTIONS,false);
        ((JcaColumnConfig)nmActionsColumnConfig).setActionModel(""); //设置右键菜单的model名称
        tableconfig.addComponent(nmActionsColumnConfig); //将右键菜单配置加入到表格配置
        return tableconfig;
    }

    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
        //查询
        QuerySpec qs = new QuerySpec(WTPart.class);
//        qs.setAdvancedQueryEnabled(true);
        PerformanceConfig performanceConfig = PerformanceConfig.getPerformanceConfig();
        int queryLimit = performanceConfig.getQueryLimit();

        System.out.println("--------------queryLimit:" + queryLimit + "----------------");

        qs.setQueryLimit(queryLimit);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        //在有版本的情况下  过滤掉旧版本
        LatestConfigSpec spec = new LatestConfigSpec();
        qr = spec.process(qr);
        System.out.println("查询结果为：" + qr);
        return qr;
    }
}
