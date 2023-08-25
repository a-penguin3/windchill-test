package ext.zybio;

import ext.generic.folder.ZybioPdmUtil;

/**
 * 软类型常量
 * @author wyu
 *
 */
public interface ZybioTypelfc {
	 
	 /**
	  * 项目
	  */
	 String PROJECT_TYPE = "com.pisx.pmgt.project.PIProject";
	 
	 /**
	  * 项目容器
	  */
	 String PROJECT_CONTAINER_TYPE = "com.pisx.pmgt.project.PIProjectContainer";
	 
	 /**
	  * 项目预算
	  */
	 String PROJECT_PHFDOC_TYPE = ZybioPdmUtil.internetDomain +".doc224";


     /**
      * 文档类型 DMR
     */
     String DOC_TYPE_DMR = ZybioPdmUtil.internetDomain + ".DMR";

     /**
      * 文档类型 DHF
     */
     String DOC_TYPE_DHF = ZybioPdmUtil.internetDomain +".DHF";

     /**
      * 文档类型 PHF,com.zy_ivd.PHF
      */
     String DOC_TYPE_PHF = ZybioPdmUtil.internetDomain +".PHF";
     
     /**
      * 原理图
     */
     String DOC_TYPE_SCHEMATIC_DIAGRAM = ZybioPdmUtil.internetDomain + ".doc204";
     
     /**
      * 标签/包装盒图纸
     */
     String DOC_TYPE_SCHEMATIC_PACKING = ZybioPdmUtil.internetDomain + ".doc229";
     
     /**
      * 机械图
     */
     String DOC_TYPE_MECHANICAL_DRAWING = ZybioPdmUtil.internetDomain + ".doc206";
     
     /**
      * 液路图
     */
     String DOC_TYPE_SCHEMATIC_FLUID = ZybioPdmUtil.internetDomain + ".doc230";
    
}
