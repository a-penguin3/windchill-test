package com.zybio.pmgt.builders;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import java.util.*;

public class TestTreeHandler extends TreeHandlerAdapter {


    @Override
    public Map<Object, List> getNodes(List parents) throws WTException {
        int[] ai = new int[]{0, 1};

        Map<Object, List> result = new HashMap();

        System.out.println("<<<<<<<<<<<<<< 查询" + parents.size() + "个父节点");

        Map<Object, List> res = new HashMap<>();
        for (Object parent : parents) {
            WTPart node = (WTPart) parent;
            QuerySpec qs = new QuerySpec(WTPart.class);
            //查询link表
            QuerySpec childQs = new QuerySpec();
            //查询link——B的id
            ClassAttribute ca = new ClassAttribute(WTPartUsageLink.class, "roleBObjectRef.key.id");
            int index = childQs.addClassList(WTPartUsageLink.class, false);
            childQs.appendSelect(ca, new int[]{index}, false);
            //查询条件，roleA.key =
            SearchCondition childSc = new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key", SearchCondition.EQUAL, node.getPersistInfo().getObjectIdentifier());
            childQs.appendWhere(childSc, ai);
            System.out.println("<<<<<<<<<<<<<< 子查詢為：" + childQs);
            //加入子查询
            SubSelectExpression sse = new SubSelectExpression(childQs);

            qs.appendWhere(new SearchCondition(new ClassAttribute(WTPart.class,"masterReference.key.id"),SearchCondition.IN, sse), ai);
            qs.setAdvancedQueryEnabled(true);
            LatestConfigSpec spec = new LatestConfigSpec();
            System.out.println("<<<<<<<<<<<<<<<<<<<<< qs:" +qs);
            QueryResult qr = PersistenceServerHelper.manager.query(qs);
            System.out.println("<<<<<<<<<<<<<<<<<<<<< 最新版本前的查询条数:" +qr.size());
            qr = spec.process(qr);
            System.out.println("<<<<<<<<<<<<<<<<<<<<< 最新版本后的查询条数:" +qr.size());
            Collection children = qr.getObjectVectorIfc().getVector();
            if (children.size() > 0) {
                result.put(parent, new ArrayList(children));
            }

        }
        return result;
    }

    @Override
    public List<Object> getRootNodes() throws WTException {
        System.out.println(">>>>>>>>>>>>>>>>>>> 开始获取根节点");

        ArrayList<Object> list = new ArrayList<>();
        try {
            QuerySpec qs = new QuerySpec(WTPart.class);
            int[] ai = new int[]{0, 1};
            SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.EQUAL, "132");
            qs.appendWhere(sc, ai);
            QueryResult qr = PersistenceServerHelper.manager.query(qs);
            LatestConfigSpec spec = new LatestConfigSpec();
            qr = spec.process(qr);
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<< 查询出来的父节点有：" + qr.size());
            list.addAll(qr.getObjectVectorIfc().getVector());
        } catch (WTException e) {
            e.printStackTrace();
            throw new WTException(e);
        }

        return list;
    }
}
