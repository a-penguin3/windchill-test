package ext.generic.folder.processors;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.generic.folder.ZybioPdmUtil;
import ext.pi.core.PIAttributeHelper;
import ext.pi.core.PIPartHelper;
import ext.zybio.ZybioAttributesIfc;
import org.apache.log4j.Logger;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceServerHelper;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartReferenceLink;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SoftDocProcessor extends DefaultObjectFormProcessor implements ZybioAttributesIfc {

    private static final Logger logger = LogR.getLogger(SoftDocProcessor.class.getName());

    @Override
    public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> beans) throws WTException {

        logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  进入导入软件版本片段  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        FormResult formResult = super.doOperation(nmcommandbean, beans);
        //// TODO: 2023/4/13  
        //获取浏览器语言
        Locale locale = SessionHelper.manager.getLocale();
        HashMap<String, String> map = nmcommandbean.getText();
        File file = (File) nmcommandbean.getRequest().getAttribute("file");
        String importFileName = nmcommandbean.getTextParameter("file");
        logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  文件名称为：" + importFileName + "上传得到的file的文件名称为：" + file.getName());
        if (!file.exists()){
            FeedbackMessage feedbackMessage = new FeedbackMessage(FeedbackType.FAILURE, locale, null, null,
                    "未获取到上传软件，请检查"
            );
            formResult.addFeedbackMessage(feedbackMessage);
            return formResult;
        }

        String partNumber = map.get("part");
        WTPart part = PIPartHelper.service.findWTPart(partNumber,"Design");
        if (part == null){
            FeedbackMessage feedbackMessage = new FeedbackMessage(FeedbackType.FAILURE, locale, null, null,
                    "未查询到相应物料，请检查物料号"
            );
            formResult.addFeedbackMessage(feedbackMessage);
            return formResult;
        }
        String version = map.get("version");
        logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  软件版本为：" + version);

        // 获取part所在文件夹
        String folderPath = "";
        try {
            folderPath = part.getFolderPath();
            folderPath = folderPath.substring(0, folderPath.lastIndexOf("/"));
        } catch (Exception e) {
            folderPath = "/Default";
        }
        logger.debug("createRefDoc() folderPath=" + folderPath);
//        WTPartReferenceLink link = WTPartReferenceLink.newWTPartReferenceLink(part,
//                (WTDocumentMaster) newDoc.getMaster());
        InputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            FeedbackMessage feedbackMessage = new FeedbackMessage(FeedbackType.FAILURE, locale, null, null,
                    "文件流存入失败 " + e.getMessage()
            );
            formResult.addFeedbackMessage(feedbackMessage);
            logger.error(e.getMessage());
            return formResult;
        }
        WTDocument doc = ZybioPdmUtil.createDoc(DOC_SOFT,part.getContainer(),folderPath,fileInputStream,importFileName);

        logger.debug("createRefDoc() newDoc=" + doc);


        // 设置IBA属性和生命周期状态
        PIAttributeHelper.service.forceUpdateSoftAttribute(doc, IBA_DATAVERSION, version);
        // 与部件建立参考关系
        WTPartReferenceLink link = WTPartReferenceLink.newWTPartReferenceLink(part,
                (WTDocumentMaster) doc.getMaster());
        PersistenceServerHelper.manager.insert(link);

        FeedbackMessage feedbackMessage = new FeedbackMessage(FeedbackType.SUCCESS, locale, null, null,
                "操作成功"
        );
        formResult.addFeedbackMessage(feedbackMessage);
//        QueryResult qr = VersionControlHelper.service.allVersionsOf(part.getMaster());

        return formResult;
    }


}
