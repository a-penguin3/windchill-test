package ext.zybio;

import java.util.List;

import com.pisx.pmgt.change.PIProjectIssue;
import com.pisx.pmgt.project.PIProject;
import ext.pi.core.PIContentHelper;
import ext.pi.core.PICoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wt.doc.WTDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.CompositeWhereExpression;
import wt.query.LogicalOperator;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.util.WTException;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewReference;

/**
 * @author wyu
 */
public class Test1 implements RemoteAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test1.class.getName());


    /**
     * windchill ext.zybio.Test7 wcadmin
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        RemoteMethodServer rm = RemoteMethodServer.getDefault();
        rm.setUserName("wcadmin");
        rm.setPassword(args[0]);
        rm.invoke("test11", Test1.class.getName(), null, new Class[]{String[].class}, new Object[]{args});
    }


    public static void test11(String[] args) throws Exception {
        WTDocument doc = (WTDocument) PICoreHelper.service.getWTObjectByOid("OR:wt.doc.WTDocument:130320");
        System.out.println("判断是否是工作副本");
     /*   if (PICoreHelper.service.isCheckout(doc)) {
            doc = (WTDocument) PICoreHelper.service.getWorkingCopy(doc);
            System.out.println("以切换到工作副本");
        }*/

        QueryResult item = PIContentHelper.service.findSecondaryContents(doc);
        System.out.println(item == null ? "空" : item.size());
    }

    public static void test(String[] args) throws Exception {

        int ai[] = new int[]{0};

        ClassAttribute ca = new ClassAttribute(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id");

        QuerySpec childQs = new QuerySpec();
        //这也是获取查询目标的一种写法
        int index = childQs.appendClassList(WTPartMaster.class, false);
        childQs.appendSelect(ca, new int[]{index}, false);
        System.out.println(">>>>>>>>>>>>childQs: " + childQs);
        childQs.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER,
                SearchCondition.EQUAL, "1234444"), new int[]{index});
        System.out.println(">>>>>>>>>>>>childQs: " + childQs);
        SubSelectExpression sse = new SubSelectExpression(childQs);



        QuerySpec qs = new QuerySpec(WTPart.class);
        qs.setAdvancedQueryEnabled(true);
        //获取当前最新数据
        SearchCondition sc = new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE);
        qs.appendWhere(sc, ai);
        qs.appendAnd();

        qs.appendWhere(new SearchCondition(new ClassAttribute(
                WTPart.class, "masterReference.key.id"),
                SearchCondition.IN, sse), new int[]{0});

        System.out.println(">>>>>>>>>>>>qs: " + qs);
        QueryResult qr = PersistenceServerHelper.manager.query(qs);
        System.out.println(">>>>>>>>>>>>qr: " + qr.size());

        while (qr.hasMoreElements()) {
            WTPart part = (WTPart) qr.nextElement();
            System.out.println(">>>>>>>>part name: " + part.getName());
        }
    }

    public static void ProjectTest(String[] args) throws Exception{
        //查询的class对象的下标
        int[] ai = new int[]{0};

        //---------------- 子查询条件 ------------------
        ClassAttribute ca = new ClassAttribute(PIProject.class, "thePersistInfo.theObjectIdentifier.id");

        QuerySpec childQs = new QuerySpec();
        //这也是获取查询目标的一种写法
        int index = childQs.appendClassList(PIProject.class, false);
        childQs.appendSelect(ca, new int[]{index}, false);


        //查询状态为以关闭的
        childQs.appendWhere(new SearchCondition(PIProject.class, LifeCycleManaged.LIFE_CYCLE_STATE,SearchCondition.EQUAL,"CLOSED"), ai);
        System.out.println(">>>>>>>>>>>>childQr: " + childQs);
        childQs.appendAnd();
        //查询项目名称为111的项目
        childQs.appendWhere(new SearchCondition(PIProject.class, "projectName",SearchCondition.EQUAL,"111"), ai);
        SubSelectExpression sse = new SubSelectExpression(childQs);
        System.out.println(">>>>>>>>>>>>childQr: " + childQs);

        //--------------- 主查询条件 ---------------------
        QuerySpec qr = new QuerySpec(PIProjectIssue.class);
        qr.setAdvancedQueryEnabled(true);
        qr.appendWhere(new SearchCondition(new ClassAttribute(PIProjectIssue.class,"projectReference.key.id"),SearchCondition.IN, sse), ai);
        //打印查询结果
        System.out.println(">>>>>>>>>>>>qr: " + qr);
        QueryResult qs = PersistenceServerHelper.manager.query(qr);
        System.out.println(">>>>>>>>>>>>qs: " + qs.size());

        while (qs.hasMoreElements()) {
            PIProjectIssue issue = (PIProjectIssue) qs.nextElement();
            System.out.println(">>>>>>>>part name: " + issue);
        }

    }

    public static WTCollection getWTPartByIbaNameAndValues(String ibaName, List<String> ibaValues, String viewName) throws WTException {
        WTCollection partList = new WTArrayList();
        if (ibaValues == null || ibaValues.size() <= 0) {
            return partList;
        }
        String[] values = ibaValues.toArray(new String[ibaValues.size()]);

        long ibaId = 0;
        QuerySpec qs = new QuerySpec(WTPart.class);
        //高级查询？
        qs.setAdvancedQueryEnabled(true);

        try {
            AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
            ibaId = addv.getObjectID().getId();
            qs.setDescendantQuery(false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WTException(e);
        }

        ClassAttribute ca = new ClassAttribute(StringValue.class, StringValue.IBAHOLDER_REFERENCE + "."
                + ObjectReference.KEY + "." + ObjectIdentifier.ID);
        QuerySpec childQs = new QuerySpec();
        int index = childQs.appendClassList(StringValue.class, false);
        childQs.appendSelect(ca, new int[]{index}, false);
        childQs.appendWhere(new SearchCondition(StringValue.class, StringValue.DEFINITION_REFERENCE + "." +
                ObjectReference.KEY + "." + ObjectIdentifier.ID,
                SearchCondition.EQUAL, ibaId), new int[]{index});
        childQs.appendAnd();
        CompositeWhereExpression orExpression = new CompositeWhereExpression(LogicalOperator.OR);
        orExpression.append(new SearchCondition(StringValue.class, StringValue.VALUE2, values, false), new int[]{0});
        childQs.appendWhere(orExpression, null);

        SubSelectExpression sse = new SubSelectExpression(childQs);
        qs.appendWhere(new SearchCondition(new ClassAttribute(
                WTPart.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID),
                SearchCondition.IN, sse), new int[]{0});

        qs.appendAnd();
        qs.appendWhere(new SearchCondition(
                WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE), new int[]{0});

        View view = ViewHelper.service.getView(viewName);
        if (view != null) {
            qs.appendAnd();
            String viewKey = WTPart.VIEW + "." + ViewReference.KEY+".id";
            long viewIdentifier = PersistenceHelper.getObjectIdentifier(view).getId();
            SearchCondition sc = new SearchCondition(WTPart.class, viewKey, SearchCondition.EQUAL, viewIdentifier);
            qs.appendWhere(sc, new int[]{0});
        }

        QueryResult qr = PersistenceServerHelper.manager.query(qs);


        partList.addAll(qr);

        return partList;
    }


}
