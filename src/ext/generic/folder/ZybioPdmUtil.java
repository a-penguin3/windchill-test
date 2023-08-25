package ext.generic.folder;

import com.muxin.threePartylibs.apache.commons.io.FileUtils;
import com.pisx.pmgt.project.PIProject;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.lwc.common.BaseDefinitionService;
import com.ptc.core.lwc.common.view.*;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.EnumeratedSet;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.container.common.impl.ValueRequiredConstraint;
import com.ptc.windchill.enterprise.product.ProductListCommand;
import ext.gwc.base.participant.WfParticipantHelper;
import ext.gwc.base.signature.handler.impl.DefaultProcessInfoler;
import ext.lang.PICollectionUtils;
import ext.lang.PIPropUtils;
import ext.lang.PIStringUtils;
import ext.lang.prop.PropConfigBean;
import ext.pi.PIException;
import ext.pi.core.*;
import ext.zybio.ZybioAttributesIfc;
import ext.zybio.ZybioStateIfc;
import ext.zybio.ZybioTypelfc;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentUsageLink;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.fc.collections.*;
import wt.filter.NavigationCriteria;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.container.WTContainerTemplateRef;
import wt.inf.template.ContainerTemplateHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.org.WTUser;
import wt.part.*;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.PersistentObjectManager;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.*;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewReference;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ZybioPdmUtil implements ZybioAttributesIfc, ZybioTypelfc, ZybioStateIfc {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogR.getLogger(ZybioPdmUtil.class.getName());

	public static String CODEBASE;
	public static String ZYBIO_CONFIG;
	static {
		try {
			CODEBASE = PICoreHelper.service.getCodebase();
			ZYBIO_CONFIG = CODEBASE + File.separator + "ext" + File.separator + "zybio" + File.separator + "config"
					+ File.separator + "zybioConfig.properties";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从“codebase/ext/zybio/config/zybioConfig.properties”中读取配置
	 */
	public static String getZybioConfig(String key) throws WTException {
		PropConfigBean propConfig = PIPropUtils.getPropInstance(ZYBIO_CONFIG, "UTF-8");
		if (propConfig != null) {
			return propConfig.getPropValueByKey(key);
		} else {
			return "";
		}
	}

    /**
     * 判断部件是否为外协件
     * @param part
     * @return
     * @throws WTException
     */
    public static boolean isOutSourcePart(WTPart part) throws WTException {
        if (part == null) {
            return false;
        }
        Source source = part.getSource();
        if (source == null) {
            return false;
        }
        if (PIStringUtils.equals(source.toString(),SOURCE_OUT)) {
            return true;
        }
        
        if (PIStringUtils.equals(source.toString(),SOURCE_OUT2)) {
            return true;
        }
        return false;
    }
    
    
    /**
     * 判断部件是否为外购件
     * @param part
     * @return
     * @throws WTException
     */
    public static boolean isBuyPart(WTPart part) throws WTException {
        if (part == null) {
            return false;
        }
        Source source = part.getSource();
        if (source == null) {
            return false;
        }
        if (PIStringUtils.equals(source.toString(), SOURCE_BUY)) {
            return true;
        }
        return false;
    }
    
    /**
     * 是否为标准件
     * @param part
     * @return
     * @throws WTException
     */
    public static boolean isStandardPart(WTPart part) throws WTException {
        String value = (String)PIAttributeHelper.service.getValue(part, IBA_REVIEWLINK_ZYBIO_MATERIALCATEGORY);
        logger.debug("  isStandardPart value:  " + value);
        if(STANDARD_PART_VALUE.equals(value)){
        	return true;
        }
        return false;
    }
    
    /**
     * 是否为组件/半成品
     * @param part
     * @return
     * @throws WTException
     */
	public static boolean isComponentPart(WTPart part) throws WTException {
		String node = (String) PIAttributeHelper.service.getValue(part, IBA_CLASSIFICATION);
		logger.debug("  isStandardPart node:  " + node);
		if (StringUtils.isNotEmpty(node)) {
			String parentNodeName = PIClassificationHelper.service.getParentNodeName(node);
			logger.debug("  isStandardPart parentNodeName:  " + parentNodeName);
			if ("02-24".equals(parentNodeName) || "02-27".equals(parentNodeName) || "02-28".equals(parentNodeName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据项目获取产品库
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public static WTContainer getWTContainer(PIProject project) throws Exception{
		WTContainer container = null;
		if(project == null) {
			return null;
		}
		try {
			String cxdm = PIAttributeHelper.service.getValue(project,IBA_PRODUCTLINE)==null?"":(String) PIAttributeHelper.service.getValue(project,IBA_PRODUCTLINE);
			String lbdm = PIAttributeHelper.service.getValue(project,IBA_PROJECTCATEGORY)==null?"":(String) PIAttributeHelper.service.getValue(project,IBA_PROJECTCATEGORY);
			logger.debug("cxdm>>>>>>>>>"+cxdm);
			logger.debug("lbdm>>>>>>>>>"+lbdm);
			String containerName = "";
			if(lbdm.equals("2")){
				containerName  = getContainerName(cxdm,lbdm);
			}else if(lbdm.equals("1")) {
				String abbrevName = project.getProjectAbbreviation();//项目奖惩
				String projectNumber = project.getProjectShortName();
				containerName = projectNumber+"_"+abbrevName;
			}
			
			logger.debug("containerName>>>>>>>>>"+containerName);
			container = getContainer(containerName);
			if(container==null) {
				return null;
			}
		} catch (PIException e) {
			e.printStackTrace();
		}
		return container;
	}

	/**
	 * 组合产品库名称
	 * @param cxdmName
	 * @param cxdm
	 * @return
	 * @throws Exception
	 */
	public static String getContainerName(String cxdm, String lbdm) throws Exception {
		// 产品线全局枚举
		EnumeratedSet set = PIAttributeHelper.service.getEnumeratedSet("productline", Locale.CHINA);
		String cxdmDisplay = AttributeDataUtilityHelper.getDefinitionDescriptor(set.getElementByKey(cxdm), Locale.CHINA).getDisplay();
		// 项目类别全局枚举
		set = PIAttributeHelper.service.getEnumeratedSet("ProjectCategory", Locale.CHINA);
		String lbdmDisplay = AttributeDataUtilityHelper.getDefinitionDescriptor(set.getElementByKey(lbdm), Locale.CHINA).getDisplay();
		return cxdmDisplay + lbdmDisplay;
	}

	/**
	 * 根据名称获取产品库
	 * @param project
	 * @return
	 */
	public static WTContainer getContainer(String containerName) throws WTException{
		WTContainer container = null;
		QueryResult qr = PIContainerHelper.service.findWTContainer(containerName);
		if(qr.hasMoreElements()) {
			container = (WTContainer) qr.nextElement();
		}
		return container;
	}

	/**
      * 创建文件夹路径
     *
     * @param container  上下文
     * @param folderPath 文件夹全路径
     * @return
     * @throws WTException
     */
    public static Folder createFolder(WTContainer container, String folderPath) throws WTException {
        Folder subFolder = null;
        if (container == null || StringUtils.isBlank(folderPath)) {
            return subFolder;
        }

        try {
            String[] folderPathArray = folderPath.split("/");
            for (int i = 0; i < folderPathArray.length; i++) {
                String folderName = folderPathArray[i];
                if (StringUtils.isBlank(folderName)) {
                    continue;
                }
                if (folderName.equals("Default")) {
                    subFolder = FolderHelper.service.getFolder("/Default/", WTContainerRef.newWTContainerRef(container));
                } else {
                    // 是否存在
                    boolean isExist = false;
                    // 查询下层子文件夹
                    QueryResult qr = FolderHelper.service.findSubFolders(subFolder);
                    while (qr.hasMoreElements()) {
                        SubFolder sf = (SubFolder) qr.nextElement();
                        if (sf.getName().equals(folderName)) {
                            subFolder = sf;
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        SubFolder newSubFolder = SubFolder.newSubFolder(folderName, subFolder);
                        newSubFolder.setContainer(container);
                        newSubFolder = (SubFolder) PersistenceHelper.manager.save(newSubFolder);
                        subFolder = newSubFolder;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new WTException(e.getLocalizedMessage());
        }

        return subFolder;
    }

	/**
	 * 创建文档
	 */
	public static WTDocument createDoc(String type, WTContainer container, String folderPath, File file) throws WTException {
		try {
			String fileName = file.getName();
			String docName = null;
			if (fileName.contains(".")) {
				docName = fileName.substring(0, fileName.lastIndexOf("."));
			} else {
				docName = fileName;
			}
			logger.debug("createDoc() fileName>>>>>>>>" + fileName);

			// 创建文档
			WTDocument doc = WTDocument.newWTDocument();
			doc.setName(docName);
			doc.setContainer(container);
			doc.setTypeDefinitionReference(TypedUtility.getTypeDefinitionReference(type));

			Folder folder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
			if (folder != null) {
				FolderHelper.assignLocation((FolderEntry) doc, folder);
			}
			doc = (WTDocument) PersistenceHelper.manager.save(doc);

			// 设置主内容
			doc = (WTDocument) PIContentHelper.service.updatePrimary(doc, file, fileName, false);
			return doc;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 创建文档
	 */
	public static WTDocument createDoc(String type, WTContainer container, String folderPath, InputStream inputStream,
			String fileName) throws WTException {
		try {
			String docName = null;
			if (fileName.contains(".")) {
				docName = fileName.substring(0, fileName.lastIndexOf("."));
			} else {
				docName = fileName;
			}
			logger.debug("createDoc() fileName>>>>>>>>" + fileName);

			// 创建文档
			WTDocument doc = WTDocument.newWTDocument();
			doc.setName(docName);
			doc.setContainer(container);
			doc.setTypeDefinitionReference(TypedUtility.getTypeDefinitionReference(type));

			Folder folder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
			if (folder != null) {
				FolderHelper.assignLocation((FolderEntry) doc, folder);
			}
			doc = (WTDocument) PersistenceHelper.manager.save(doc);

			// 设置主内容
			ApplicationData content = ApplicationData.newApplicationData(doc);
			content.setFileName(fileName);
			content.setRole(ContentRoleType.PRIMARY);
			doc = (WTDocument) PIContentHelper.service.updatePrimary(doc, content, inputStream, false);
			return doc;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

    /**
     * 升小版本
     * @param workable
     * @param state
     * @throws WTException 
     */
    public static Workable updateIteration(Workable workable,String state) throws WTException {
    	boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
    	boolean canCommit = false;
		Transaction tx = null;
    	Workable newWorkable = null;
    	try {
    		if (!PersistentObjectManager.getPom().isTransactionActive()) {
				tx = new Transaction();
				tx.start();
				canCommit = true;
			}
    		if(!WorkInProgressHelper.isCheckedOut(workable)&& !WorkInProgressHelper.isWorkingCopy(workable)){
    			Folder coFolder = WorkInProgressHelper.service.getCheckoutFolder();
    			CheckoutLink checkoutLink = WorkInProgressHelper.service.checkout(workable,coFolder,"");
    			Workable workable1 = (Workable)checkoutLink.getWorkingCopy();
    			newWorkable =  WorkInProgressHelper.service.checkin(workable1,"");
    			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)newWorkable, State.toState(state));
    		}
    		if (canCommit) {
				tx.commit();
				tx = null;
			}
    	}catch(Exception e) {
    		e.printStackTrace();
    		throw new WTException(e);
    	}finally {
    		SessionServerHelper.manager.setAccessEnforced(flag);
    		if (canCommit && tx != null) {
				tx.rollback();
			}
    	}
    	return newWorkable;
    }

    /**
     * 更新文档主内容
     * @param doc
     * @throws IOException
     * @throws PropertyVetoException
     * @throws FileNotFoundException
     */
    public static void updateApplication(WTDocument doc,String fileName,String uploadPath) throws FileNotFoundException, PropertyVetoException, IOException {
		try {
			ApplicationData applicationdata = ApplicationData.newApplicationData(doc);
			applicationdata.setRole(ContentRoleType.PRIMARY);
			applicationdata = ContentServerHelper.service.updateContent(doc, applicationdata, uploadPath);
			applicationdata.setFileName(fileName);
			PersistenceServerHelper.manager.update(applicationdata);
			PersistenceServerHelper.manager.restore(doc);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    

	/**
	 * 部件的默认的配置规范
	 *
	 * @return NavigationCriteria
	 * @throws WTException
	 */
	public static NavigationCriteria getNavigationCriteria(WTPart part) throws WTException {
		 NavigationCriteria criteria=new NavigationCriteria();
		 List<ConfigSpec> configSpecList = new ArrayList<ConfigSpec>();
		 WTPartStandardConfigSpec configSpec = defaultStandardConfigSpec(part);
		 configSpecList.add(configSpec);
		 criteria.setConfigSpecs(configSpecList);
		 return criteria;
	}

	/**
	 *
	 * 部件的默认的配置规范
	 *
	 * @return WTPartStandardConfigSpec
	 * @throws PIException
	 */
	public static WTPartStandardConfigSpec defaultStandardConfigSpec(WTPart part) throws WTException {
		WTPartStandardConfigSpec configSpec = null;

		View view = (View)part.getView().getObject();
		if (view == null) {
			configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec();
		} else {
			configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
		}

		return configSpec;
	}

	/**
	 * 获取 BOM 结构
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static WTCollection getAllPartChildrens(WTPart part) throws WTException {
		 WTCollection list = new WTHashSet();

		 list = getAllPartChildrens(part,null);

		 return list;
	 }
	
	/**
	 * 获取 BOM 子件和关系
	 * @param part
	 * @param linkSet
	 * @return
	 * @throws WTException
	 */
	public static WTCollection getAllPartChildrens(WTPart part,WTSet linkSet) throws WTException {
		 WTCollection list = new WTHashSet();
		 try{
			 NavigationCriteria navigationCriteria = getNavigationCriteria(part);
	
			 WTList parentList = new WTArrayList();
			 parentList.add(part);
			 
			 if(linkSet == null){
				 linkSet = new WTHashSet();
			 }
	
			 getAllPartChildrens(parentList,list,linkSet,navigationCriteria);
		 }catch(Exception e){
			 e.printStackTrace();
			 throw new WTException(e);
		 }

		 return list;
	 }



	/**
	 * 获取BOM结构
	 * @param parentList
	 * @param result
	 * @param navigationCriteria
	 * @throws WTException
	 */
    public static void getAllPartChildrens(WTList parentList, WTCollection result, WTSet linkSet,NavigationCriteria navigationCriteria) throws WTException {
        if (PICollectionUtils.isEmpty(parentList)) {
            return;
        }
        WTList nextLevelList = new WTArrayList();
        WTKeyedMap childrenAndLinkMap = PIPartHelper.service.findChildrenAndLinks(parentList, navigationCriteria);
        if (childrenAndLinkMap != null && childrenAndLinkMap.size() > 0) {
            Iterator<Persistable> persistableIterator = childrenAndLinkMap.wtKeySet().persistableIterator();
            while (persistableIterator.hasNext()) {
                WTPart parent = (WTPart) persistableIterator.next();
                QueryResult childQr = (QueryResult) childrenAndLinkMap.get(parent);
                WTCollection childList = getChildren(childQr,linkSet);
                nextLevelList.addAll(childList);
                result.addAll(childList);
            }
        }

        getAllPartChildrens(nextLevelList,result,linkSet,navigationCriteria);
    }

	private static WTCollection getChildren(QueryResult qr,WTSet linkSet) throws WTException {
		WTCollection children = new WTHashSet();
		if (qr == null || qr.size() == 0) {
			return children;
		}

		qr.reset();
		while (qr.hasMoreElements()) {
			Object nextElement = qr.nextElement();
			if (nextElement != null && nextElement instanceof WTPart) {
				children.add(nextElement);
			} else if (nextElement != null && nextElement instanceof Persistable[]) {
				Persistable[] persistables = (Persistable[]) nextElement;
				if (persistables.length == 2) {
					if(persistables[0] != null && persistables[0] instanceof WTPartUsageLink){
						linkSet.add(persistables[0]);
					}
					
					if(persistables[1] != null && persistables[1] instanceof WTPart){
						children.add(persistables[1]);
					}
				}
			} else {
				logger.error(">>> getConfigurableChildren, error datatype.");
			}
		}
		return children;
	}

	/**
	 * 获取文档类型的所有子类型（包括自身）
	 * @param type 类型的内部值
	 * @return
	 * @throws PIException
	 */
	public static Collection<TypeIdentifier> getAllDocType(String type) throws PIException {
		if (logger.isDebugEnabled()) {
			logger.debug(">>>>>>>>>>>>getAllDocType begin>>>>>>>");
		}
		TypeIdentifier typeDefinition = PICoreHelper.service.getTypeDefinition(type);
		Collection<TypeIdentifier> allInstantiableSubTypes = PICoreHelper.service.getAllInstantiableSubTypes(typeDefinition);
		allInstantiableSubTypes.add(typeDefinition);
		if (logger.isDebugEnabled()) {
			logger.debug("类型数量:"+allInstantiableSubTypes.size());
		}
		return allInstantiableSubTypes;
	}
	
	
	/**
	 * 获取指定类型所有子类型
	 * @param type
	 * @return
	 * @throws PIException
	 */
	public static HashMap<String,TypeIdentifier> getAllDocTypes(String type) throws PIException {
		HashMap<String,TypeIdentifier> map = new HashMap<String,TypeIdentifier>();
		if(StringUtils.isNotEmpty(type)){
			TypeIdentifier typeDefinition = PICoreHelper.service.getTypeDefinition(type);
			Collection<TypeIdentifier> allInstantiableSubTypes = PICoreHelper.service.getAllInstantiableSubTypes(typeDefinition);
			if (logger.isDebugEnabled()) {
				logger.debug("类型数量:"+allInstantiableSubTypes.size());
			}
			allInstantiableSubTypes.add(typeDefinition);
			for(TypeIdentifier identifier : allInstantiableSubTypes){
				map.put(identifier.getTypeInternalName(), identifier);
			}
		}
		return map;
	}

	/**
	 * 递归获取所有层子文件夹
	 * @param folder
	 * @param collection
	 * @return
	 * @throws WTException
	 */
	public static Collection getAllSubfolders(Folder folder, Collection collection) throws WTException {
		QueryResult subFolders = FolderHelper.service.findSubFolders(folder);//获取文件夹直接一层的子文件夹
		while (subFolders.hasMoreElements()) {
			Object o = subFolders.nextElement();
			if (o instanceof Folder) {
				Folder folder1 = (Folder) o;
				collection.add(folder1);
				getAllSubfolders(folder1, collection);
			}
		}
		return collection;
	}

	/**
	 * 根据文件夹查询文件夹下所有文档对象
	 *
	 * @param folder
	 * @return
	 * @throws WTException
	 */
	public static QueryResult queryDocsByFolder(Folder folder) throws WTException {
		QueryResult result = null;
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE);
			qs.appendWhere(sc, new int[]{0});
			long id = folder.getPersistInfo().getObjectIdentifier().getId();//文件夹的ida2a2
			//属于特定文件夹
			qs.appendAnd();
			sc = new SearchCondition(WTDocument.class, "folderingInfo.parentFolder.key.id", SearchCondition.EQUAL, id);
			qs.appendWhere(sc, new int[]{0});
			result = PersistenceHelper.manager.find(qs);
			if (logger.isDebugEnabled()) {
				logger.debug("size=" + result.size());
			}
			result = new LatestConfigSpec().process(result);
			if (logger.isDebugEnabled()) {
				logger.debug("after process.size=" + result.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}
		return result == null ? new QueryResult() : result;
	}
	
	/**
	 * 根据产品库和文档类型查询文档对象
	 * @param container
	 * @param softType
	 * @param recursiveSubtype
	 * @return
	 * @throws WTException
	 */
	public static QueryResult queryDocsByContainerAndType(WTContainer container,String softType,boolean recursiveSubtype) throws WTException {
		long containerId = container.getPersistInfo().getObjectIdentifier().getId();
		QuerySpec qs = new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc, new int[]{0});
		//属于特定文件夹
		qs.appendAnd();
		sc = new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL, containerId);
		qs.appendWhere(sc, new int[]{0});
		if(StringUtils.isNotEmpty(softType)){
			qs.appendAnd();
			TypeDefinitionReference typedefinitionreference = TypedUtility.getTypeDefinitionReference(softType);
	        long branchId = typedefinitionreference.getKey().getBranchId();
	        	
			if(recursiveSubtype){
	            Collection<TypeIdentifier> allDocType = ZybioPdmUtil.getAllDocType(softType);
	            long [] ids = new long[allDocType.size()+1];        
	            int index = 0;
	            for(TypeIdentifier identifier : allDocType){
	            	typedefinitionreference = TypedUtility.getTypeDefinitionReference(identifier.getTypename());
	            	long id = typedefinitionreference.getKey().getBranchId();
	            	ids[index] = id;
	            	index++;
	            }
	          
	        	ids[allDocType.size()] = branchId;
	        	sc = new SearchCondition(WTDocument.class, "typeDefinitionReference.key.branchId", ids, false);
			}else{
				sc = new SearchCondition(WTDocument.class, "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, branchId);
			}           
			
			qs.appendWhere(sc, new int[]{0});						
		}
		logger.debug("  queryDocsByContainerAndType qs: " + qs);
		
		QueryResult result = PersistenceHelper.manager.find(qs);	
		result = new LatestConfigSpec().process(result);
		logger.debug("  queryDocsByContainerAndType result: " + result.size());
		
		return result ;
	}
	
	
	/**
	 * 更新对象信息
	 * @param type
	 * @param values
	 * @param usingOIR
	 * @return
	 * @throws PIException
	 */
	public static Persistable newPersistable(Persistable persistable, Map<String, Object> values) throws PIException {
		try {
             if (persistable==null) {
                 return persistable;
             }
             Locale local = SessionHelper.getLocale();
             PersistableAdapter persistableAdapter = new PersistableAdapter(persistable, null, local,null);
             
             if (values != null && values.size() > 0) {
                 persistableAdapter.load(values.keySet());
                 Iterator<String> iterator = values.keySet().iterator();
                 while (iterator.hasNext()) {
                     String attName = iterator.next();
                     Object attValue = values.get(attName);
                     persistableAdapter.set(attName, attValue);
                 }
             }
             persistable = persistableAdapter.apply();
             return persistable;
         } catch (WTException e) {
                 throw new PIException(e);
         }
	}
	
	/**
	 * 创建产品库
	 * @param libName
	 * @param libDescription
	 * @param templateName
	 * @return
	 * @throws Exception
	 */
	public static WTContainerRef createProduct(String productName, String productDescription, String templateName) throws Exception {
       
        WTUser me = (WTUser) SessionHelper.manager.getPrincipal();
        WTOrganization org = OrganizationServicesHelper.manager.getOrganization(me);
        WTContainerRef orgContainerRef = WTContainerHelper.service.getOrgContainerRef(org);

        PDMLinkProduct product = PDMLinkProduct.newPDMLinkProduct();
        WTContainerRef productRef = null;

        boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
        try {
        	product.setName(productName);
            if (productDescription != null)
            	product.setDescription(productDescription);

            WTContainerTemplateRef containerTemplateRef = ContainerTemplateHelper.service.getContainerTemplateRef(orgContainerRef, templateName, PDMLinkProduct.class);
            if (containerTemplateRef == null) {
            	throw new WTException("未找到对应的产品库模板:" + templateName);  
            }

            product.setContainerTemplateReference(containerTemplateRef);
            product.setContainerReference(orgContainerRef);
            product = (PDMLinkProduct) WTContainerHelper.service.create(product);
            product = (PDMLinkProduct) WTContainerHelper.service.makePublic(product);
            productRef = WTContainerRef.newWTContainerRef(product);
        }catch(Exception e){
        	e.printStackTrace();
        	throw new WTException(e);
        }finally {
            SessionHelper.manager.setAuthenticatedPrincipal(me.getAuthenticationName());
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }

        return productRef;
    }

    /**
     * 根据用户名查询用户，获取替代名
     * @param userName
     * @param alternateStr
     * @return
     * @throws Exception
     */
    public static String getAlternateName(String userName, String alternateStr) throws Exception {
    	logger.debug(" getAlternateName userName: " + userName + "  alternateStr: " + alternateStr);
    	String alternateName = "";
    	WTUser user = PIPrincipalHelper.service.findWTUserById(userName);
    	if(user != null){
    		alternateName = getAlternateName(user,alternateStr);
    	}
    	return alternateName;
    }
    
    /**
     * 获取替代名
     * @param user
     * @param alternateStr
     * @return
     * @throws Exception
     */
	public static String getAlternateName(WTUser user, String alternateStr) throws Exception {
		String str = "";
	    Enumeration localEnumeration = user.getAttributes().getValues("uid");

	    ArrayList localArrayList = Collections.list(localEnumeration);
	    localArrayList.remove(user.getName());
	    if ((alternateStr.equals("alternateUserName1")) && (localArrayList.size() > 0))
	      str = (String)localArrayList.get(0);
	    else if ((alternateStr.equals("alternateUserName2")) && (localArrayList.size() > 1))
	      str = (String)localArrayList.get(1);
	    else if ((alternateStr.equals("alternateUserName3")) && (localArrayList.size() > 2))
	      str = (String)localArrayList.get(2);
	    else if ((alternateStr.equals("alternateUserName4")) && (localArrayList.size() > 3)) {
	      str = (String)localArrayList.get(3);
	    }
	    
	    return str;
	}
	
	/**
	 * 获取用户工号
	 */
	public static String getUserPostalAddress(String userName) {
		try {
			WTUser user = PIPrincipalHelper.service.findWTUserById(userName);
			String postAddress = user.getPostalAddress();
			if (PIStringUtils.hasText(postAddress)) {
				return postAddress;
			}
			return user.getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取用户工号
	 */
	public static String getUserPostalAddress(WTUser user) {
		try {
			String postAddress = user.getPostalAddress();
			if (PIStringUtils.hasText(postAddress)) {
				return postAddress;
			}
			return user.getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取用户有权限的产品库
	 * @param var0
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<WTContainer> getProducts() throws Exception {
		logger.debug(" ### getProducts ");
		ArrayList<WTContainer> list=new ArrayList<WTContainer>();
		try {
			QueryResult qr= ProductListCommand.getProducts("netmarkets.product.list");
			while(qr.hasMoreElements()) {
				WTContainer container=(WTContainer)qr.nextElement();
				list.add(container);
			}
			logger.debug(" ### getProducts list: "+list.size());
		}catch(Exception e) {
			e.printStackTrace();
		}		
		return list;
	}
	
	
	/**
     * 找到系统的站点internet domain
     * from:  DMR
     * to:   com.zy_ivd.DMR
     * @throws WTException 
     */
    public static String getSiteDomain() throws WTException {
        // 取站点internet domain
        String siteDomain = WTContainerHelper.service.getExchangeContainer() .getInternetDomain();
        siteDomain = siteDomain.replace('-', '_');
        
        // 计算逆序
        String reversedDomain = "";
        String[] segs = siteDomain.split("\\.");
        for (int i = segs.length - 1; i >= 0; i--) {
            if (i < segs.length - 1)
                reversedDomain += ".";
            reversedDomain += segs[i].trim();
        }
            
        return reversedDomain;
    }
    
    public static String internetDomain = "";
    static{
    	try {
			internetDomain = getSiteDomain();
		} catch (WTException e) {
			e.printStackTrace();
		}
    }

	/**
	 * 多值属性值转换为List
	 */
	public static List<String> multipleValueToList(Object value) throws WTException {
		List<String> list = new ArrayList<String>();
		if (value == null) {
			return list;
		}
		if (value instanceof Object[]) {
			Object[] values = (Object[]) value;
			for (Object v : values) {
				list.add(v.toString());
			}
		} else {
			list.add(value.toString());
		}
		return list;
	}

	/**
	 * 去除物料编码前缀T
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String removePartNumberPrefixT(WTPart part) throws WTException {
		String number = part.getNumber();
		number = removePartNumberPrefixT(number);
		return number;
	}
	
	/**
	 * 去除物料编码前缀T
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static String removePartNumberPrefixT(String number) throws WTException {
		if(number.startsWith("T-")){
			number = number.replaceFirst("T-", "").trim();
		}
		return number;
	}
	
	/**
	 * 增加物料编码前缀T
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static String addPartNumberPrefixT(String number) throws WTException {
		if(!number.startsWith("T-")){
			number = "T-"+number.trim();
		}
		return number;
	}
	
	/**
	 * 是否样品编码
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static boolean isSamplePart(String number) throws WTException {
		if(number.startsWith("T-")){
			return true;
		}
		return false;
	}

	/**
	 * PLM系统单位转换为ERP系统单位
	 * @param unit 内部名称
	 */
	public static String toERPUnit(QuantityUnit unit) throws WTException {
		String internalName = unit.toString();
		String erpUnit = getZybioConfig("PLM_Unit_" + internalName);
		if (PIStringUtils.hasText(erpUnit)) {
			return erpUnit;
		}
		return internalName;
	}

	/**
	 * 创建文档结构关系
	 * @param parentNumber
	 * @param childNumber
	 * @throws WTException
	 */
	public static void createDocUsageLink(WTDocument parentDoc, WTDocument childDoc) throws WTException {
		try {
			if(parentDoc==null || childDoc==null) {
				return;
			}
			WTDocumentUsageLink link = WTDocumentUsageLink.newWTDocumentUsageLink(parentDoc, (WTDocumentMaster) childDoc.getMaster());
			PersistenceServerHelper.manager.insert(link);
		} catch (PIException e) {
			e.printStackTrace();
			throw new WTException(e);
		}
		
	}
	
	/**
     * 获取枚举值 key为显示值 value为内部值
     *
     * @param enumerationTypeName
     * @return
     */
    public static Map<String, String> getEnumerationValues(String enumerationTypeName) {
        Map<String, String> enumerationValueMap = new HashMap<String, String>();
        try {
            BaseDefinitionService seviec = (BaseDefinitionService) ServiceFactory
                    .getService(BaseDefinitionService.class);
            EnumerationDefinitionReadView view = seviec.getEnumDefView(enumerationTypeName);
            Map<String, EnumerationEntryReadView> map = view.getAllEnumerationEntries();
            Iterator<String> keyIte = map.keySet().iterator();
            while (keyIte.hasNext()) {
                String key = keyIte.next();
                if (key == null) {
                    continue;
                }
                EnumerationEntryReadView readview = map.get(key);
                if (readview == null) {
                    continue;
                }
                PropertyValueReadView propreadview = readview.getPropertyValueByName("displayName");
                if (propreadview == null || propreadview.getValueAsString() == null) {
                    continue;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(propreadview.getValueAsString() + " ====> " + key);
                }
                enumerationValueMap.put(propreadview.getValueAsString(), key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return enumerationValueMap;
    }
    
    
    /**
     * 判断属性是否必填
     * @param type
     * @param attribute
     * @return
     * @throws WTException
     */
	public static boolean getAttributeIsRequired(String type,String attribute) throws WTException {
		boolean required = false;
		TypeIdentifier typeIdentifier = PICoreHelper.service.getTypeDefinition(type);
		if (typeIdentifier!=null){
			TypeDefinitionReadView typeDefinitionReadView = PICoreHelper.service.getTypeDefView(typeIdentifier);
			AttributeDefinitionReadView definitionReadView = typeDefinitionReadView.getAttributeByName(attribute);
			if (definitionReadView!=null){
				Collection<ConstraintDefinitionReadView> collection = definitionReadView.getAllConstraints();
				for (ConstraintDefinitionReadView constraintDefinitionReadView:collection){
					if (!constraintDefinitionReadView.isDisabled()) {
						String ruleName = constraintDefinitionReadView.getRule().getRuleClassname();
						if (ruleName.equals(ValueRequiredConstraint.class.getName())) {
							required = true;
							break;
						}
					}
				}
			}
		}
		return required;
	}
	
   /**
    * 用户名密码加密
    * @param username
    * @param password
    * @return
    */
   public static String encodeBase64(String username, String password) {
		byte[] key = (username + ":" + password).getBytes();
		return new String(Base64.encodeBase64(key));
   }
   
   /**
    * 根据 iba 名称和值进行查询部件
    * @param ibaName
    * @param ibaValues
    * @return
    * @throws WTException
    */
	public static WTCollection getWTPartByIbaNameAndValues(String ibaName,List<String> ibaValues,String viewName) throws WTException{
		logger.debug("  getWTPartByIbaNameAndValues ibaName: " + ibaName + " ibaValues: " + ibaValues);
		WTCollection partList = new WTArrayList();
		if(ibaValues == null || ibaValues.size() <=0){
			return partList;
		}
		String [] values = ibaValues.toArray(new String [ibaValues.size()]);
		
		long ibaId = 0;	
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.setAdvancedQueryEnabled(true);

		try {
			AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
			ibaId = addv.getObjectID().getId();	
			qs.setDescendantQuery(false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}
	   
		ClassAttribute ca = new ClassAttribute(StringValue.class,StringValue.IBAHOLDER_REFERENCE + "."
				+ ObjectReference.KEY + "."+ ObjectIdentifier.ID);		
	    QuerySpec childQs = new QuerySpec();
		int index = childQs.appendClassList(StringValue.class, false);
		childQs.appendSelect(ca, new int[] { index }, false);
		childQs.appendWhere(new SearchCondition(StringValue.class,StringValue.DEFINITION_REFERENCE + "."+ 
				ObjectReference.KEY + "."+ ObjectIdentifier.ID,
				SearchCondition.EQUAL, ibaId),new int[] { index });
		childQs.appendAnd();
		CompositeWhereExpression orExpression = new CompositeWhereExpression(LogicalOperator.OR);
		orExpression.append(new SearchCondition(StringValue.class,StringValue.VALUE2, values, false), new int[] { 0 });
		childQs.appendWhere(orExpression, null);		

		SubSelectExpression sse = new SubSelectExpression(childQs);
		qs.appendWhere(new SearchCondition(new ClassAttribute(
				WTPart.class,  Persistable.PERSIST_INFO + "."+ PersistInfo.OBJECT_IDENTIFIER + "."+ ObjectIdentifier.ID), 
				SearchCondition.IN,sse),new int[] { 0 });
		
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(
				WTPart.class,  "iterationInfo.latest", SearchCondition.IS_TRUE),new int[] { 0 });
				
		View view = ViewHelper.service.getView(viewName);
		if(view != null){
			qs.appendAnd();
			String viewKey = WTPart.VIEW + "." + ViewReference.KEY;
			ObjectIdentifier viewIdentifier = PersistenceHelper.getObjectIdentifier(view);
			SearchCondition sc = new SearchCondition(WTPart.class, viewKey, SearchCondition.EQUAL, viewIdentifier);
			qs.appendWhere(sc, new int[] { 0 });
		}
		
		logger.debug(" getWTPartByIbaNameAndValues qs: " + qs);
		QueryResult  qr = PersistenceServerHelper.manager.query(qs);

		logger.debug("  getWTPartByIbaNameAndValues qr size: " + qr.size());

		partList.addAll(qr);
		
		return partList;
	}

	/**
	 * 根据iba属性值，批量查询版本管理对象
	 */
	public static WTCollection findRevisionControlledByStringIBA(Class findObject, String ibaName, List<String> ibaValues) throws WTException {
		logger.debug("findRevisionControlledByStringIBA ibaName: " + ibaName + " ibaValues: " + ibaValues);
		WTCollection reslut = new WTArrayList();
		if (ibaValues == null || ibaValues.size() == 0) {
			return reslut;
		}
		String[] values = ibaValues.toArray(new String[ibaValues.size()]);

		long ibaId = 0;
		try {
			AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
			ibaId = addv.getObjectID().getId();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}

		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int a0 = qs.appendClassList(findObject, true);

		QuerySpec childQs = new QuerySpec();
		int a1 = childQs.appendClassList(StringValue.class, false);

		// idA3A4
		childQs.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { a1 },
				false);

		// idA3A6
		childQs.appendWhere(
				new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaId),
				new int[] { a1 });

		childQs.appendAnd();
		childQs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, values, false),
				new int[] { a1 });

		SubSelectExpression sse = new SubSelectExpression(childQs);
		qs.appendWhere(new SearchCondition(new ClassAttribute(findObject, "thePersistInfo.theObjectIdentifier.id"),
				SearchCondition.IN, sse), new int[] { a0 });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(findObject, RevisionControlled.LATEST_ITERATION, SearchCondition.IS_TRUE),
				new int[] { a0 });

		logger.debug("findRevisionControlledByStringIBA qs: " + qs);
		reslut.addAll(PersistenceServerHelper.manager.query(qs));
		logger.debug("findRevisionControlledByStringIBA reslut size: " + reslut.size());
		return reslut;
	}

	/**
	 * 从部件相关的图纸或文档上查询数据版本，返回显示名称，找不到返回null
	 */
	public static String getPartDataVersion(WTPart part) throws WTException {
		String value = null;
		boolean flag = SessionServerHelper.manager.isAccessEnforced();
		try {
			// 先查找所有者图纸
			QueryResult qr = PIPartHelper.service.findOwnerLinks(part, true);
			if (qr != null && qr.hasMoreElements()) {
				value = PIAttributeHelper.service.getDisplayValue((EPMDocument) qr.nextElement(), IBA_DATAVERSION, Locale.CHINA);
				if (PIStringUtils.hasText(value)) {
					return value;
				}
			}
			// 然后再查找参考文档
			qr = PIPartHelper.service.findAssociatedReferenceDocuments(part);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				// 判断图纸类文档
				if (ZybioPdmUtil.isDrawingDoc(doc)) {
					value = PIAttributeHelper.service.getDisplayValue(doc, IBA_DATAVERSION, Locale.CHINA);
					if (PIStringUtils.hasText(value)) {
						return value;
					}
				}
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return value;
	}

	/**
	 * 判断文档软类型，是否是图纸类文档
	 */
	public static boolean isDrawingDoc(WTDocument doc) {
		try {
			String[] types = PIStringUtils.tokenizeToStringArray(getZybioConfig("drawingDocTypeInternalName"), ";", true, true);
			for (String type : types) {
				if (PICoreHelper.service.isType(doc, internetDomain + "." + type)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 将流程团队中的用户存入PBO的属性中
	 * 
	 * @param nodeName 流程活动节点名称
	 * @param iba PBO的IBA属性内部名称
	 */
	public static void setWorkflowUserToPBO(WTObject pbo, ObjectReference self, String nodeName, String iba)
			throws WTException {
		boolean flag = SessionServerHelper.manager.isAccessEnforced();
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			StringBuffer value = new StringBuffer();
			WfProcess process = PIWorkflowHelper.service.getProcess(self);
			// 获取活动节点的参与角色
			Collection<Role> roles = WfParticipantHelper.service.getProcessRoles(process, nodeName);
			for (Role role : roles) {
				WTSet userSet = PIWorkflowHelper.service.getRoleUsers(process, role);
				Iterator iterator = userSet.persistableIterator();
				while (iterator.hasNext()) {
					WTUser user = (WTUser) iterator.next();
					if (value.length() > 0) {
						value.append(",");
					}
					value.append(user.getName());
				}
			}
			if (value.length() > 0) {
				PIAttributeHelper.service.forceUpdateSoftAttribute(pbo, iba, value.toString());
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	/**
	 * 将工作流任务的签审信息存入PBO的属性中，此表达式应写在流程最后
	 * 
	 * @param nodeName 需要记录的审批节点名称
	 * @param reviewUserIBA 存储实际完成人工号的IBA属性内部名称
	 * @param commentIBA 存储任务备注意见的IBA属性内部名称
	 * @param reviewTimeIBA 存储任务完成时间的IBA属性内部名称
	 */
	public static void setWorkItemInfoToPBO(WTObject pbo, ObjectReference self, String nodeName, String reviewUserIBA,
			String commentIBA, String reviewTimeIBA) throws WTException {
		boolean flag = SessionServerHelper.manager.isAccessEnforced();
		try {
			DefaultProcessInfoler util = new DefaultProcessInfoler(pbo, PIWorkflowHelper.service.getProcess(self));
			Map<String, WorkItem> info = util.findProcessInfo();
			if (info == null || !info.containsKey(nodeName)) {
				return;
			}
			WorkItem workItem = info.get(nodeName);
			if (workItem == null) {
				return;
			}
			PIAttributeHelper.service.forceUpdateSoftAttribute(pbo, reviewUserIBA, workItem.getCompletedBy());
			PIAttributeHelper.service.forceUpdateSoftAttribute(pbo, reviewTimeIBA, workItem.getModifyTimestamp());
			PIAttributeHelper.service.forceUpdateSoftAttribute(pbo, commentIBA, workItem.getContext().getTaskComments());
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}
	
	/**
	 * 删除文件夹文件
	 * @param path
	 * @throws WTException
	 * @throws IOException
	 */
	public static void deleteDirFiles(String path) throws WTException, IOException {
		File file = new File(path);
		if(file.isFile()){
			File parentFile = file.getParentFile();
			if(parentFile.isDirectory()){
				FileUtils.deleteDirectory(parentFile);
			}
		}else if(file.isDirectory()){
			FileUtils.deleteDirectory(file);
		}
	}
}
