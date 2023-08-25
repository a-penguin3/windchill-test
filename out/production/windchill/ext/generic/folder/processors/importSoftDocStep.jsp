<%@ page pageEncoding="UTF-8"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.zybio.pdm.document.documentResource" />
<fmt:message var="DOC" key="DOC"/>
<fmt:message var="PART" key="PART"/>
<fmt:message var="VERSION" key="VERSION"/>

<fieldset class="x-fieldset x-form-label-left" id="Visualization_and_Attributes" style="width: auto; display: block;">
    <legend><font class="wizardlabel" size='2'>输入</font></legend>
    <div>
        <table border="0" cellspacing="3" cellpadding="3">
            <tr>
                <td align="left" valign="top" nowrap>
                    <font class="requiredfield">*</font>
                    <font class="wizardlabel">${DOC}</font>
                </td>
                <td align="left" valign="top" nowrap>
                    <input name="file" type="file" size="27" id="file1" onchange="validateImportFile(this)"/>
                </td>
            </tr>
            <tr>
                <td align="left" valign="top" nowrap>
                    <font class="requiredfield">*</font>
                    <font class="wizardlabel">${PART}</font>
                </td>
                <td>
                    <w:textBox propertyLabel="" id="part" name="part" size="30" value="" styleClass="required" required="true" maxlength="200"/>
                </td>
            </tr>
            <tr>
                <td align="left" valign="top" nowrap>
                    <font class="requiredfield">*</font>
                    <font class="wizardlabel">${VERSION}</font>
                </td>
                <td>
                    <w:textBox propertyLabel="" id="version" name="version" size="30" value="" styleClass="required" required="true" maxlength="200"/>
                </td>
            </tr>
        </table>
    </div>
</fieldset>
<script language="JavaScript">
// setUserSubmitFunction(validateImportFile);

function validateImportFile(obj) {
    console.log("进入文件路径效验")
    var file = obj.files[0];
    var reader = new FileReader();
    reader.readAsDataURL(file);
    console.log(reader);
    reader.onload = (e) => { // 读取成功时：
					console.log("文件路径："+
						e.target.result);
				};
    // var url = null;
    // // 下面函数执行的效果是一样的，只是需要针对不同的浏览器执行不同的 js 函数而已
    // if (window.createObjectURL !== undefined) {   // basic
    //     url = window.createObjectURL(file);
    // } else if (window.URL !== undefined) {        // mozilla(firefox)
    //     url = window.URL.createObjectURL(file);
    // } else if (window.webkitURL !== undefined) {  // webkit or chrome
    //     url = window.webkitURL.createObjectURL(file) ;
    // }
    // console.log("进入文件路径为：" + url)
    // if ( url !== "\\\\192.168.9.5\\Release\\UploadPlm") {
    //     wfalert("文件上传路径出错！！！")
    //     return false;
    // }
	// return true;
}
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>