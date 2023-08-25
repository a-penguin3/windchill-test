package com.zybio.pmgt.processers;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.queryBuilder.schema.jaxb.Object;
import ext.pi.core.PIAttributeHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TestProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {

        System.out.println("############ 进入部件修改料号processor ##########");
        FormResult formResult = new FormResult();
        Locale locale = SessionHelper.manager.getLocale();
        boolean flag = false;
        try{
            flag = SessionServerHelper.manager.setAccessEnforced(false);
            //获取浏览器语言

            WTPart part = (WTPart) nmcommandbean.getActionOid().getRefObject();
            if (part == null){
                System.out.println("未查询到部件！！！");
            }

            assert part != null;
            WTPartMaster master = part.getMaster();
            System.out.println("master为："+master);
            HashMap<String, String> map = nmcommandbean.getText();
            String name = map.get("newName");
            String number = map.get("newNumber");
            HashMap<String, ArrayList<Object>> map2 = nmcommandbean.getComboBox();
            ArrayList<Object> arrayList =  map2.get("baselineType");
            System.out.println("a");
            String select = String.valueOf(arrayList.get(0));
            System.out.println("name:" + name + ",number:" + number + ",select:" + select);

            master = (WTPartMaster) PIAttributeHelper.service.changeIdentity(master,number,name);
            System.out.println("变更后的master为：" + master);

            part = (WTPart) PIAttributeHelper.service.forceUpdateSoftAttribute(part,"com.pisx.zybio.test.testattr01",select);
            System.out.println("变更后的WTPart为：" + part);

            FeedbackMessage feedbackMessage = new FeedbackMessage(FeedbackType.SUCCESS, locale, null, null,
//                    new WTMessage("资源class路径例如：ext.generic.folder.folderBd", "操作成功", null)
                    "操作成功"
            );
            formResult.addFeedbackMessage(feedbackMessage);




        }catch (Exception e){
            e.printStackTrace();
            FeedbackMessage feedbackMessage = new FeedbackMessage(FeedbackType.FAILURE, locale, null, null,
//                    new WTMessage("资源class路径例如：ext.generic.folder.folderBd", "操作成功", null)
                    "操作失败:" + e.getMessage()
            );
            formResult.addFeedbackMessage(feedbackMessage);
        }
        return formResult;
    }
}
