package ext.zybio;

import java.io.Serializable;

/**
 * 系统相关的角色常量接口
 * @author wyu
 *
 */
public interface ZybioRoleIfc extends Serializable {

	/**
	 * 项目经理角色
	 */
	public static final String ROLE_PROJECT_MANAGER = "zy_ivd_projectmanager";
	
	/**
	 * 项目管理者
	 */
	public static final String ROLE_PROJECT_ADMIN = "PIProjectContainer Administrators";
	
	/**
	 * 产品经理
	 */
	public static final String ROLE_PRODUCT_MANAGER = "zy_ivd_Productmanager";
	
	/**
	 * 产品线总经理
	 */
	public static final String ROLE_PRODUCTLINEGENERAL_MANAGER = "zy_ivd_ProductLineGeneralManager";
	/**
	 * 产品线总经理
	 */
	public static final String ROLE_DQA = "zy_ivd_DQA";
	/**
	 * 系统工程师
	 */
	public static final String ROLE_SYSTEMS_ENGINEER = "zy_ivd_systemsengineer";
	
	/**
	 * QA
	 */
	public static final String ROLE_QA = "zy_ivd_QA";
	
	
	/**
	 * 需求接口人
	 */
	public static final String ROLE_REQ_INTERFACE_PERSION = "REQ_Interface_Person";
	
	/**
	 * 设计转换工程师
	 */
	public static final String ROLE_DESIGN_CONVERSIONENGINNER = "zy_ivd_Designconversionengineer";


	/**
	 * 会签者
	 */
	public static final String ROLE_SIGNER = "Signer";
	
	/**
	 * 审阅者
	 */
	public static final String ROLE_REVIEWER = "REVIEWER";
	/**
	 * bom工程师
	 */
	public static final String ZY_IVD_BOMENGINEER = "zy_ivd_BOMengineer";
	
}
