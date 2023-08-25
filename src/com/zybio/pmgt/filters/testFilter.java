package com.zybio.pmgt.filters;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import wt.fc.Persistable;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

public class testFilter extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        UIValidationStatus status = UIValidationStatus.DISABLED;
        try {
            WTPrincipal flag = SessionHelper.manager.getPrincipal();
            System.out.println("当前登录者为:" + flag);
            //获取当前对象
            Persistable pobj = criteria.getContextObject().getObject();
            System.out.println("当前对象为：" + pobj);
            //RevisionControlled rc = (RevisionControlled)pobj;  版本对象，大部分带版本的对象都可以转换成版本对象
            //由于本次练习为固定对象练习，可以直接转换成部件对象
            WTPart wtPart = (WTPart) pobj;
            WTPrincipalReference modifier = wtPart.getModifier();
            String modifierName = modifier.getPrincipal().getName();
            String localName = flag.getName();
            if (modifierName.equals(localName)){
                status = UIValidationStatus.ENABLED;
            }
        } catch (WTException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
}
