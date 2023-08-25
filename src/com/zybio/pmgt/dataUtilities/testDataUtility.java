package com.zybio.pmgt.dataUtilities;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.lang.PIExcelUtils;
import ext.pi.PIException;
import ext.pi.core.PIAttributeHelper;
import ext.pi.core.PICoreHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.io.File;
import java.util.ArrayList;

public class testDataUtility extends DefaultDataUtility {

    public static String CODEBASE;
    public static String FILEPATH;

    public static String excelName;

    static {
        try {
            CODEBASE = PICoreHelper.service.getCodebase();
            FILEPATH = CODEBASE + File.separator + "com" + File.separator + "zybio" + File.separator + "config" + File.separator;
            excelName = FILEPATH + "测试下拉框.xlsx";
        } catch (PIException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param componentId 列的id
     * @param datum       表格对象
     * @param mc          所在页面的信息
     * @return 封装的部件
     * @throws WTException 异常信息
     */
    public Object getDataValue(String componentId, Object datum, ModelContext mc) throws WTException {

        boolean flag = false;

        ComboBox combox = new ComboBox();
        combox.setId(componentId);
        combox.setName(componentId);
        //获取系统默认字段id
        combox.setColumnName(AttributeDataUtilityHelper.getColumnName(componentId, datum, mc));
        //展示列表
        ArrayList<String> displayList = new ArrayList<>();
        //内部值
        ArrayList<String> internalList = new ArrayList<>();
        //默认展示项
        ArrayList<String> selectList = new ArrayList<>();

        //获取当前页面视图
        ComponentMode componentMode = mc.getDescriptorMode();
        //获取当前页面对象
        NmCommandBean commandBean = mc.getNmCommandBean();

        if (ComponentMode.VIEW.equals(componentMode)) {
            return super.getDataValue(componentId, datum, mc);
        }

        try {
            flag = SessionServerHelper.manager.setAccessEnforced(flag);
            Workbook wb = PIExcelUtils.getWorkbook(new File(excelName));
            Sheet sheet = wb.getSheetAt(0);
            int rowNums = sheet.getLastRowNum();
            System.out.println("总行数为：" + rowNums);

            for (int i = 0; i <= rowNums; i++) {
                System.out.println("获取第" + i + "行数据");
                Row cur = sheet.getRow(i);
                int cellNums = cur.getLastCellNum();
                System.out.println("总列数为：" + cellNums);

                System.out.println("单元格的值为：" + cur.getCell(0));
                displayList.add(String.valueOf(cur.getCell(0)));
                internalList.add(String.valueOf(cur.getCell(0)));

            }

            System.out.println("读取的列表为：" + displayList.size());

            Object collection = null;


            //获取当前操作
            NmOid nmoid = commandBean.getPageOid();
            System.out.println("当前操作对象为：" + nmoid);
            if (nmoid != null) {
                //获取操作对象
                collection = nmoid.getRefObject();
            }

            //                Cabinet

            if (collection instanceof WTPart) {
                WTPart part = (WTPart) collection;
                //获取自定义对象的字段
                String select = PIAttributeHelper.service.getValue(part, "com.pisx.zybio.test.testattr01").toString();
                System.out.println("先前选项为：" + select);
                selectList.add(select);
            }

            combox.setValues(displayList);
            combox.setInternalValues(internalList);
            combox.setSelected(selectList);
            combox.setMultiSelect(false);
            return combox;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }
}