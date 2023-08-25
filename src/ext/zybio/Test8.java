package ext.zybio;

import com.pisx.pmgt.change.PIProjectIssue;
import com.pisx.pmgt.project.PIProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;

/**
 * @author wyu
 */
public class Test8 implements RemoteAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test1.class.getName());


    /**
     * windchill ext.zybio.Test1 wcadmin
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        RemoteMethodServer rm = RemoteMethodServer.getDefault();
        rm.setUserName("wcadmin");
        rm.setPassword(args[0]);
        rm.invoke("ProjectTest", Test1.class.getName(), null, new Class[]{String[].class}, new Object[]{args});
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


}
