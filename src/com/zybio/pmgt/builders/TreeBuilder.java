package com.zybio.pmgt.builders;

import com.pisx.pmgt.resource.rsrcResource;
import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaTreeConfig;
import com.ptc.mvc.components.*;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.mvc.util.ClientMessageSource;
import org.apache.poi.ss.usermodel.Workbook;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.util.*;

@ComponentBuilder(value = "com.zybio.pmgt.builders.TreeBuilder")
public class TreeBuilder extends AbstractComponentBuilder implements TreeDataBuilderAsync{
    private final ClientMessageSource messageSource = this.getMessageSource(rsrcResource.class.getName());
    TestTreeHandler treeHandler;

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        System.out.println("enter buildComponentConfig");
        ComponentConfigFactory factory = this.getComponentConfigFactory();
        TreeConfig tree = factory.newTreeConfig();
        ((JcaTreeConfig)tree).setDataSourceMode(DataSourceMode.ASYNCHRONOUS);
        tree.setLabel("部件全部信息");
        tree.setSelectable(true);
        tree.setSingleSelect(true);  //设置是否单选
        tree.setActionModel("pi-pmgtProjectContainerInfoPageTabSet"); //设置model
        tree.setShowCount(true); //设置是否显示总数
        tree.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.ICON, true));
        ColumnConfig a = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NAME, true);
        a.setDataUtilityId("");
        tree.addComponent(a);//名称
        tree.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NUMBER, true)); //编号
        tree.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.INFO_ACTION, true)); //详细信息
        tree.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.VERSION, true)); //版本
        tree.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.LAST_MODIFIED, true)); //修改时间
        tree.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.CONTAINER_NAME, true)); //所属容器

        tree. setExpansionLevel (DescriptorConstants.TableTreeProperties.ONE_EXPAND);

        //设置启用右键菜单
        ColumnConfig nmActionsColumnConfig = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NM_ACTIONS,false);
        ((JcaColumnConfig)nmActionsColumnConfig).setActionModel(""); //设置右键菜单的model名称
        tree.addComponent(nmActionsColumnConfig); //将右键菜单配置加入到表格配置
        return tree;
    }

    @Override
    public TestTreeHandler buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
        System.out.println("enter buildComponentData");
        return null;
    }

    @Override
    public void buildNodeData(Object o, ComponentResultProcessor componentResultProcessor) throws Exception {
        boolean flag = false;
        try{
            System.out.println("enter handler" + o);
            if (o == TreeNode.RootNode) {
                this.treeHandler = new TestTreeHandler();
                componentResultProcessor.addElements(this.treeHandler.getRootNodes());
            } else {
                List nodeList = new ArrayList();
                nodeList.add(o);
                Map<Object, List> map = this.treeHandler.getNodes(nodeList);
                Set keySet = map.keySet();
                Iterator var8 = keySet.iterator();

                while(var8.hasNext()) {
                    Object key = var8.next();
                    List oneList = map.get(key);
                    componentResultProcessor.addElements(oneList);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new WTException(e.toString());
        }finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }
}
