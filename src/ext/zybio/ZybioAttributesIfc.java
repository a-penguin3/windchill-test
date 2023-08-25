package ext.zybio;

import java.io.Serializable;

/**
 * 系统对象属性
 * 
 * @author wyu
 *
 */
public interface ZybioAttributesIfc extends Serializable {

	public static final String FOLDERPATH = "/Default/";

	public static final String PHF_SMALLDOCTYPE = "PHF";// 项目预算文档小类值

	public static String ECA_ATTR_TASKTYPE = "zyivd_taskType";// 任务类型

	public static String SOURCE_BUY = "buy";// 外购

	public static String SOURCE_OUT = "outsourcepart";// 外协

	public static String SOURCE_OUT2 = "outsourcepart2";// 委外

	public static String STANDARD_PART_VALUE = "标准件";

	public static String ERP_BOM_SUFFIX = "_VA";// ERP接口中“BOM版本”字段的后缀

	public static String DOC208 = "com.zy_ivd.doc208";// 物料规格书

	public static String DOC_SOFT = "com.zy_ivd.doc210"; //软件文档

	/**
	 * 项目属性
	 */
	public static final String IDRAWING_NO = "DRAWING_NO";

	public static final String IBA_PRODUCTLINE = "productline";// 产线代码

	public static final String IBA_PROJECTCATEGORY = "ProjectCategory";// 类别代码

	public static final String IBA_PRODUCTMANAGER = "zy_ivd_Productmanager";

	public static final String IBA_SMALLDOCTYPE = "SmallDocType";// 文档小类

	public static final String IBA_PDMLINKPRODUCTNAME = "PDMLinkProductName";// 文件

	public static final String IBA_BUSINESSSATUS = "zyivdBusinessSatus"; // 业务状态

	public static final String IBA_SYSTEMENGINEER = "zy_ivd_systemsengineer";// 系统工程师

	public static final String IBA_DQA = "zy_ivd_DQA";// DQA

	public static final String IBA_PROJECTTYPE = "ProjectType";// 项目类型

	public static final String IBA_PROJECTGRADE = "ProjectGrade";// 项目等级

	public static final String IBA_FINANCIALCODE = "FinancialCode";// 财务代号

	public static final String IBA_CURRENTSTAGEOFTHEPROJECT = "CurrentStageOfTheProject";// 项目当前阶段

	public static final String IBA_NUMBEROFPROJECTCHANGES = "NumberOfProjectChanges";// 变更次数

	public static final String IBA_REGISTRATIONTYPE = "RegistrationType";// 注册类型

	public static final String IBA_GRADUATESCHOOL = "graduateSchool";// 研究所

	public static final String IBA_PROJECTOBJECTIVES = "ProjectObjectives";// 项目目标

	public static final String IBA_CHINALPLANNEDTIME = "chinaPlannedTime";// 国内上线时间

	public static final String IBA_INTERNATIONLPLANNEDTIME = "internationalPlannerTime";// 国际上线时间

	public static final String IBA_OAWFNUMBER = "OAWFNumber";// OA流程编号

	public static final String IBA_OAWFMESSAGE = "OAWFMessage";// 返回信息

	public static final String IBA_OAWFID = "OAWFId";// OA流程ID

	public static final String IBA_PRODUCTLINEGENERALMANAGER = "zy_ivd_ProductLineGeneralManager";

	public static final String IBA_QA = "zy_ivd_QA";

	public static final String IBA_PROJECTMANAGER = "zy_ivd_projectmanager";// 项目经理

	/**
	 * 问题属性
	 */
	public static final String IBA_STAGE = "stage";// 所属阶段

	public static final String IBA_CAUSEANALYSIS = "CauseAnalysis";// 原因分析

	public static final String IBA_INTERIMMEASURES = "InterimMeasures";// 临时措施

	public static final String IBA_PERSONLIABLE = "personliable";// 责任人

	// 项目风险属性
	public static final String IBA_RISKLEVEL = "RiskLevel";// 风险等级

	public static final String IBA_PROBABILITYOFOCCURRENCE = "ProbabilityOfOccurrence";// 发生概率

	public static final String IBA_DETECTION = "Detection";// 不可探测性

	public static final String IBA_RISKINDEX = "RiskIndex";// 风险指数指数

	/**
	 * 部件IBA属性
	 */
	// public static final String IBA_OA = "OAIntegrationStatus";//OA状态

	public static final String IBA_OA_SAMPLE_STATUS = "OASampleMaterialIntegrationStatus";// OA样品状态

	public static final String IBA_OA_PRODUCT_STATUS = "OAProductionMaterialIntegrationStatus";// OA正式码状态

	public static final String IBA_OA_SAMPLE_MESSAGE = "OASampleMaterialIntegrationMessage";// OA样品错误信息

	public static final String IBA_OA_PRODUCT_MESSAGE = "OAProductionMaterialIntegrationMessage";// OA正式码错误信息

	public static final String IBA_CRMSTATUS = "CRMIntegrationStatus";// CRM状态

	public static final String IBA_SRMINTEGRATIONSTATUS = "SRMIntegrationStatus";// SRM状态

	public static final String IBA_MATERIALDESCRIPTION = "zyivdMaterialDescription";// 物料描述

	public static final String IBA_ISSPAREPARTS = "IsSpareParts";// 是否备件

	public static final String IBA_ISCONSUMABLEPART = "IsConsumablePart";// 是否损耗件

	public static final String IBA_MATERIALPRIORITY = "zyivdMaterialPriority";// 优选等级

	public static final String IBA_PART_TEMPORARY_CODE = "zyivdMaterialTemporaryCode";// 临时码

	public static final String IBA_CLASSIFICATION = "Classification";// 分类

	public static final String IBA_SPECIFICATION = "zyivdSpecification";

	public static final String IBA_ERPMATERIALSTATUS = "ERPMaterialIntegrationStatus";// 物料更新状态

	public static final String IBA_ERPBOMSTATUS = "ERPBOMIntegrationStatus";// ERPBOM集成状态

	public static final String IBA_MATERIAL_CERTIFICATION_STATUS = "zyivdMaterialCertificationStatus";// 认证状态

	public static final String IBA_ZYIVDSPECIFICATION = "zyivdSpecification";// 规格型号

	public static final String IBA_ERP_SAMPLE_PN = "zyivdERPSamplePN";// ERP样品料号

	public static final String IBA_ERP_PRODUCT_PN = "zyivdERProductionPN";// ERP生产料号

	/**
	 * 部件结构关系属性
	 */
	public static final String IBA_LINK_COMPONENTREMAKS = "ComponentRemaks";// BOM备注

	public static final String IBA_LINK_COMPONENTLOSSRATE = "ComponentLossRate";// 损耗率

	public static final String IBA_LINK_USAGENUMERATOR = "UsageNumerator";// 用量分子

	public static final String IBA_LINK_USAGEDENOMINATOR = "UsageDenominator";// 用量分母

	public static final String IBA_LINK_SUBPARTTYPE = "zybio_SubPartType";// 子件类型

	public static final String IBA_LINK_ALTERNATEGROUP = "zybio_AlternateGroup";// 替代组

	public static final String IBA_LINK_ALTERNATEPRIORITY = "zybio_AlternatePriority";// 替代优先级

	public static final String IBA_LINK_ALTERNATEPOLICY = "zybio_AlternatePolicy";// 替代策略

	public static final String IBA_LINK_ALTERNATEWAY = "zybio_AlternateWay";// 替代方式

	public static final String IBA_LINK_ALTERNATEMAINMATERIAL = "zybio_AlternateMainMaterial";// 替代主料

	/**
	 * 文档属性
	 */
	public static String IBA_SUPPLIER = "zybio_supplierCode";// 供应商代码

	public static String IBA_DATAVERSION = "zybio_DataVersion";// 数据版本

	/**
	 * 需求管理
	 */
	public static String IBA_RQCATEGARY = "zybio_RQCategary";

	public static String IBA_PROJECTABBREVIATION = "zybio_demandProjectabbreviation";// 项目代号

	/**
	 * 随签link属性
	 */
	public static final String IBA_REVIEWLINK_ZYBIO_MANUFACTURER = "zybio_manufacturer";// 制造商/品牌

	public static final String IBA_REVIEWLINK_ZYBIO_QUALITYWEIGHT = "zybio_QualityWeight";// 质量权重

	public static final String IBA_REVIEWLINK_ZYBIO_APPLICABLEMODEL = "zybio_ApplicableModel";// 适用机型

	public static final String IBA_REVIEWLINK_ZYBIO_INVENTORYCATEGORY = "zybio_InventoryCategory";// 存货类别

	public static final String IBA_REVIEWLINK_ZYBIO_STORAGECONDITIONS = "zybio_StorageConditions";// 贮存条件

	public static final String IBA_REVIEWLINK_ZYBIO_INVENTORYUNITCODE = "zybio_InventoryUnitCode";// 库存单位编码

	public static final String IBA_REVIEWLINK_ZYBIO_PURCHASEATTRIBUTE = "zybio_PurchaseAttribute";// 采购属性

	public static final String IBA_REVIEWLINK_ZYBIO_ENABLESHELFLIFE = "zybio_EnableShelfLife";// 启用保质期管理

	public static final String IBA_REVIEWLINK_ZYBIO_SHELFLIFEUNIT = "zybio_ShelfLifeUnit";// 保质期单位

	public static final String IBA_REVIEWLINK_ZYBIO_QUALITYGUARANTEEPERIOD = "zybio_QualityGuaranteePeriod";// 保质期

	public static final String IBA_REVIEWLINK_ZYBIO_SERIALNOUNITCODE = "zybio_SerialNoUnitCode";// 序列号单位编码

	public static final String IBA_REVIEWLINK_ZYBIO_MATERIALCATEGORY = "zybio_MaterialCategory";// 物料类别

	public static final String IBA_REVIEWLINK_ZYBIO_CERTIFICATIONCATEGORY = "zybio_CertificationCategory";// 认证类别

	public static final String IBA_REVIEWLINK_ZYBIO_RECOMMENDEDSUPPLIES = "zybio_RecommendedSuppliers";// 推荐供应商

	public static final String IBA_ECA_FORCE_TASK = "WhetherToForceTask";// 是否强制任务

	public static final String IBA_ECN_ERP_ECNNO = "zyivd_ERPECNNO";// ERP的ECN编号

	public static final String IBA_BI_PERSION_LIABLE = "BI_Person_Liable";// 责任人

	public static final String IBA_BI_APPROVAL_CONCLUSION = "BI_Approval_conclusion";// 审批结论

	public static final String IBA_BI_APPROVER = "BI_Approver";// 审批人

	public static final String IBA_BI_APPROVER_DATE = "BI_Approval_date";// 审批日期
		
	public static final String IBA_CODE = "CODE";// 图纸属性

}