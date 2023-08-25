package ext.generic.folder.processors;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import org.apache.log4j.Logger;
import wt.inf.container.WTContainerRef;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FolderFormProcessor extends DefaultObjectFormProcessor {
    private static final Logger log = Logger.getLogger(FolderFormProcessor.class);
    private static final String CLASSNAME = FolderFormProcessor.class.getName();

    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
        log.debug("-----import processor test-----");

        FormResult formResult = new FormResult();

        WTContainerRef currentContainerRef = new WTContainerRef();
        currentContainerRef = nmCommandBean.getContainerRef();
        //获取文件，看着是按照request的模式获取值
        File file = (File) nmCommandBean.getRequest().getAttribute("file");
        //获取浏览器语言
        Locale locale = SessionHelper.manager.getLocale();

        //获取所有参数
        HashMap<String, Object> map = nmCommandBean.getParameterMap();
        Set<String> set = map.keySet();
        for(String key : set){
            String [] values = (String []) map.get(key);
            System.out.println(">>>>>>>>>>>>>>>>>.key : " + key + "  value:  " + values[0]);
        }


        if (file != null) {
            FeedbackMessage feedbackMessage = new FeedbackMessage(FeedbackType.SUCCESS, locale, null, null,
//                    new WTMessage("资源class路径例如：ext.generic.folder.folderBd", "操作成功", null)
                    "操作成功"
            );
            formResult.addFeedbackMessage(feedbackMessage);
        }
        HashMap<String, String> textMap = nmCommandBean.getText();
        log.debug("test: " + textMap);

        String foldText = textMap.get("FOLDER_NAME");
        return formResult;
    }
}
