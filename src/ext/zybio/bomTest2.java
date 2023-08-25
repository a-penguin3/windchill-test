package ext.zybio;

import com.pisx.pmgt.pmgtActionsRB;
import com.pisx.pmgt.project.forms.RenameProjectProcessor;
import com.pisx.pmgt.project.validators.EditProjectFilter;

import ext.pi.PIException;
import ext.pi.core.PIAccessHelper;
import ext.pi.core.PIAttributeHelper;
import ext.pi.core.PIPartHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wt.fc.PersistenceHelper;
import wt.fc.collections.WTCollection;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.Baselineable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class bomTest2 implements RemoteAccess {

    public static void main(String[] args) throws Exception {
        RemoteMethodServer rm = RemoteMethodServer.getDefault();
        rm.setUserName("wcadmin");
        rm.setPassword(args[0]);
        rm.invoke("bomTest", bomTest2.class.getName(), null, new Class[]{String[].class}, new Object[]{args});
    }

    public static void bomTest(String[] args) throws PIException {
//        WTPartHelper
        try {
            WTPartMaster master = PIPartHelper.service.findWTPartMaster("132");
            System.out.println("<<<<<<<<<<<<<<<<查询到的部件MASTER数据为：" + master + "<<<<<<<<<<<<<<<<<<<<");
            WTPart wtPart = PIPartHelper.service.findWTPart("132", "Design");
            System.out.println("<<<<<<<<<<<<<<<<<<查询到的最新部件信息为：" + wtPart + "<<<<<<<<<<<<<<<<<<<<<");
            WTPart wtPart1 = PIPartHelper.service.findWTPart(master, "Design");
            System.out.println("<<<<<<<<<<<<<<<<<<通过master获取到的部件信息为：" + wtPart1 + "<<<<<<<<<<<<<<<<<<");
            WTCollection children = PIPartHelper.service.findChildren(wtPart);
            Iterator parts = children.stream().iterator();
            while (parts.hasNext()) {
                WTPart child = (WTPart) parts.next();
                System.out.println("子部件为：" + child);

            }
            WTCollection childLinks = PIPartHelper.service.findChildrenLinks(wtPart);
            Iterator links = childLinks.stream().iterator();
            while (links.hasNext()) {
                System.out.println("子部件连接为：" + links.next());
            }

            //版本对象改名称和编号、设置模型属性、设置扩展属性（IBA）

            //设置模型属性
//            WTPart a = null;
//            a.setSource("123");
//
//            PersistenceHelper.manager.save(a);

            //设置扩展属性
//            PIAttributeHelper.service.forceUpdateSoftAttribute(a,"test","www");
            //获取扩展属性
//            PIAttributeHelper.service.getValue(persistable, name)
            //判断当前大版本的最新小版本
//            part.isLatestIteration();
            //获取最新大版本的小版本
//            PICoreHelper.service.getLatestIterationOfLatestVersion();
        }catch (Exception e){
            e.printStackTrace();
        }





    }
}
