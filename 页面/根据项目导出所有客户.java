<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
    /**
     * 管理员工具: 必须管理员账户导出所有客户信息
     * 20201203 根据项目名称导出所有的客户 和 跟进信息(包含无跟进信息)
     * https://k8mm1amta1700adb471ba12b.cloudcc.com/customize/page/9291p1140/dcAllAcount.jsp?name=dcAllAcount&nameproject=广田国际
     * 
     * 使用方式: 将url后的项目名称 更改 为你需要导出的项目名称(注意:url中不能加空格)
     * 1: 登陆CRM系统
     * 2: 将改好的url,放到浏览器的地址中, 回车, 下方导出excel
     */
    private static boolean isNull(String obj){
        if(obj == null || obj.length() <= 0) {
            return true;
        }
    return false;
    }

    private static String queryBjdNumber(String jsdbh,CCService cs){
        String bjdName = "";
        if(!isNull(jsdbh)){
            List<CCObject> jsd = cs.cquery("jlwgjsd","name = '"+jsdbh+"'");
            if(jsd != null && jsd.size() > 0){
                String bjdjh = jsd.get(0).get("bjdjh") == null ? "" : String.valueOf(jsd.get(0).get("bjdjh"));
                if(!isNull(bjdjh)){
                    return splitQueryBJd(bjdjh,cs);
                }
            }
        }
        return "";
    }

    private static String splitQueryBJd(String bjdId,CCService cs){
        String bjd[] = bjdId.split(";");
        String bjdName = "";
        for(String s:bjd){
            List<CCObject> bjdList = cs.cquery("t_bjd_info","id = '"+s+"'");
            if(bjdList != null && bjdList.size() > 0){
                bjdName += bjdList.get(0).get("name") == null ? "" : String.valueOf(bjdList.get(0).get("name")) + "，";
            }
        }
        return bjdName.substring(0,bjdName.length()-1);
    }


</cc>
<cc>
    // 涉及表： account 客户表 id   ywjhgjmx 客户跟进表 kh    关联 id = kh
    CCService cs = new CCService(userInfo);
    String userid = userInfo.getUserId();
    String profid = userInfo.getProfileId();//getProfileId当前登录用户的简档id 
	//时间范围
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	String ksrq = request.getParameter("ksrq") == null ? "" :request.getParameter("ksrq");
	String jsrq = request.getParameter("jsrq") == null ? "" :request.getParameter("jsrq");
    String projectId = request.getParameter("projectId") == null ? "" :request.getParameter("projectId"); // 项目id
    String nameproject = request.getParameter("nameproject") == null ? "" :request.getParameter("nameproject");// 项目名称
	//String datetime = "and TO_CHAR(a.createdate,'YYYY-MM-DD')>=TO_CHAR(TO_DATE('"+ksrq+"','YYYY-MM-DD'),'YYYY-MM-DD') and TO_CHAR(a.createdate,'YYYY-MM-DD')<=TO_CHAR(TO_DATE('"+jsrq+"','YYYY-MM-DD'),'YYYY-MM-DD')";
    // String sql = "select a.id,a.recordtype,a.xqmj_gy,a.name,a.lxrxm,a.khdj,a.smsj,a.lxrdh,a.khlb,a.szxy,a.xbgqy,a.xbgdx,a.rztj1,a.rztj2,a.zlyy,a.xqmj,a.zjyszl,a.khyxlx,a.kxwt,a.zlkxwt from account a where a.xmmc = '"+projectId+"' and a.is_deleted='0' "+datetime+" order by a.createbyid desc";
    if(!"aaa000001".equals(profid)) {
        return;
    }
    if ("".equals(nameproject)) { // 如果项目名称为空, 就用项目id去查
        // out.print("1^"+projectId);
        String projectnamesql = "select name from Project where id = '"+projectId+"' and is_deleted = '0'";
        List<CCObject>  pronamelist = cs.cqlQuery("Project", projectnamesql); // 执行sql语句
        for(CCObject proname:pronamelist) {
        nameproject = proname.get("name")== null?"":proname.get("name")+""; // 项目的名字
        break;
        }
    } else if ("".equals(projectId)) { //如果项目id为空, 就用项目名称去查
        // out.print("2^"+nameproject);
        String projectidsql = "select id from Project where name = '"+nameproject+"'  and is_deleted = '0'";
        List<CCObject>  proidlist = cs.cqlQuery("Project", projectidsql); // 执行sql语句
        for(CCObject proid:proidlist) {
            projectId = proid.get("id")== null?"":proid.get("id")+""; // 项目的id
            break;
         }
    }
    String sql = "select a.id,a.recordtype,a.xqmj_gy,a.name,a.lxrxm,a.khdj,a.smsj,a.lxrdh,a.khlb,a.szxy,a.xbgqy,a.xbgdx,a.rztj1,a.rztj2,a.zlyy,a.xqmj,a.zjyszl,a.khyxlx,a.kxwt,a.zlkxwt from account a where a.xmmc = '"+projectId+"' and a.is_deleted='0' order by a.createbyid desc";
    List<CCObject> list = new ArrayList<CCObject>();
    list = cs.cqlQuery("scpy", sql); // 执行sql语句
	String file_name = new String("客户跟进数据.xls".getBytes(), "ISO-8859-1");
	response.setHeader("Content-disposition","attachment;filename="+file_name);
	response.setContentType("application/vnd.ms-excel;charset=UTF-8");
</cc>
<html >
<head>
    <title>"<cc:outprint>nameproject</cc:outprint>"客户跟进数据</title>
    <style type="text/css">
<!--
td {
    background-color: #FFFFFF;
}
 
.txt
    {padding-top:1px;
    padding-right:1px;
    padding-left:1px;
    mso-ignore:padding;
    color:black;
    font-size:11.0pt;
    font-weight:400;
    font-style:normal;
    text-decoration:none;
    font-family:宋体;
    mso-generic-font-family:auto;
    mso-font-charset:134;
    mso-number-format:"@";
    text-align:general;
    vertical-align:middle;
    mso-background-source:auto;
    mso-pattern:auto;
    white-space:nowrap;}
-->
</style>


</head>
<body>
    
<div class="title" align="center">
    <h1 align="center"><font size="4px">客户跟进数据</font></h1>
    <form method="post" name="theform" id="theform">
</div> 
<div class="cloudbox3">
<br/>
    
    <table id="showtable" border="0px" class="cloudbiaoge" cellspacing="0" cellspadding="0" width="100%" style="word-break: break-all; word-wrap:break-word;">
<tr class="tabtitle" style="font-size:15px; text-align: center;">
    <td  NOWRAP="NOWRAP" class="tdTitle">客户名称</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">联系人姓名</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">客户等级</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">上门时间</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">客户类别</td>
	<td  NOWRAP="NOWRAP" class="tdTitle">所属行业</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">原办公区域</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">原办公大厦</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">认知途径1</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">认知途径2</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">租赁原因</td>
	<td  NOWRAP="NOWRAP" class="tdTitle">需求面积</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">租金预算</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">客户意向类型</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">抗性问题</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">内容</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">创建时间</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">下次跟进时间</td>
  </tr>
  <cc>
  if(list.size()>0){
    //   out.print("1^客户总数(包含无跟进记录)"+list.size());
    //   int nullnr = 0;
    //   int notnullnr=0;
       for(CCObject item:list){
        String retype = item.get("recordtype")== null?"":item.get("recordtype")+""; // 类别的id
        String khlb = "";  //客户类别
        if ("2018525F215221DtlTXV".equals(retype)) {
            khlb = "进线客户";
        } else if("2018496272C934EtLhWs".equals(retype)) {
            khlb = "销售客户";
        } else if("20186166515AE4A8ZfOc".equals(retype)) {
            khlb = "租赁客户";
        } else if("2020F8FFFACC18DmPXQ1".equals(retype)) {
            khlb = "公寓客户";
        } else {
            khlb = "类型错误";
        }
            // xcgjsj  下次跟进时间  createdate 创建时间
             String sql0= "select nr,xcgjsj,createdate from ywjhgjmx a where a.is_deleted = '0' and (nr is not null or nr !='') and a.kh = '"+item.get("id")+"' order by a.createdate desc"; // 通过客户id获取跟进内容
             List<CCObject> detaillist = cs.cqlQuery("ywjhgjmx",sql0);
             int i = 0;
			 String colspanStr = String.valueOf(detaillist.size());
             if(detaillist.isEmpty()) { // 通过客户的id，获取跟进内容，没有时，跳过
    </cc>
            <tr>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("name")==null?"":item.get("name")+""</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("lxrxm")==null?"":item.get("lxrxm")+""</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("khdj")==null?"":item.get("khdj")+""</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("smsj")==null?"":item.get("smsj")+""</cc:outprint></td>

            <!-- <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("khlb")==null?"":item.get("khlb")+""</cc:outprint></td> -->
            <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>khlb</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("szxy")==null?"":item.get("szxy")+""</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("xbgqy")==null?"":item.get("xbgqy")+""</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("xbgdx")==null?"":item.get("xbgdx")+""</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("rztj1")==null?"":item.get("rztj1")+""</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("rztj2")==null?"":item.get("rztj2")+""</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("zlyy")==null?"":item.get("zlyy")+""</cc:outprint></td>
            <!--   <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("xqmj")==null?"":item.get("xqmj")+""</cc:outprint></td> -->
            <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("recordtype").equals("2020F8FFFACC18DmPXQ1")?item.get("xqmj_gy"):item.get("xqmj")+""</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("zjyszl")==null?"":item.get("zjyszl")+""</cc:outprint></td>
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("khyxlx")==null?"":item.get("khyxlx")+""</cc:outprint></td>

                <!-- <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("kxwt")==null?"":item.get("kxwt")+""</cc:outprint></td> -->
                
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("recordtype").equals("2020F8FFFACC18DmPXQ1")?item.get("kxwt"):item.get("zlkxwt")+""</cc:outprint></td>
                <td class="dataCell_h"><cc:outprint>""</cc:outprint></td>
                <td class="dataCell_h"><cc:outprint>""</cc:outprint></td>
                <td class="dataCell_h"><cc:outprint>""</cc:outprint></td>
            </tr>
    <cc>
				continue;
			 }
             
             if(detaillist.size()>0){
                // notnullnr++;
                for(CCObject nrList:detaillist){
  </cc>
            <tr>
                <cc>
                    if (i == 0) {
                        i++;
                </cc>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("name")==null?"":item.get("name")+""</cc:outprint></td>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("lxrxm")==null?"":item.get("lxrxm")+""</cc:outprint></td>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("khdj")==null?"":item.get("khdj")+""</cc:outprint></td>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("smsj")==null?"":item.get("smsj")+""</cc:outprint></td>

                 <!-- <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("khlb")==null?"":item.get("khlb")+""</cc:outprint></td> -->
                 <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>khlb</cc:outprint></td>
				  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("szxy")==null?"":item.get("szxy")+""</cc:outprint></td>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("xbgqy")==null?"":item.get("xbgqy")+""</cc:outprint></td>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("xbgdx")==null?"":item.get("xbgdx")+""</cc:outprint></td>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("rztj1")==null?"":item.get("rztj1")+""</cc:outprint></td>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("rztj2")==null?"":item.get("rztj2")+""</cc:outprint></td>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("zlyy")==null?"":item.get("zlyy")+""</cc:outprint></td>
                <!--   <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("xqmj")==null?"":item.get("xqmj")+""</cc:outprint></td> -->
                <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("recordtype").equals("2020F8FFFACC18DmPXQ1")?item.get("xqmj_gy"):item.get("xqmj")+""</cc:outprint></td>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("zjyszl")==null?"":item.get("zjyszl")+""</cc:outprint></td>
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("khyxlx")==null?"":item.get("khyxlx")+""</cc:outprint></td>

                  <!-- <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("kxwt")==null?"":item.get("kxwt")+""</cc:outprint></td> -->
                  
                  <td  class="dataCell_h" rowspan="<cc:outprint>colspanStr</cc:outprint>"><cc:outprint>item.get("recordtype").equals("2020F8FFFACC18DmPXQ1")?item.get("kxwt"):item.get("zlkxwt")+""</cc:outprint></td>
                          <cc>}</cc>
                  <td class="dataCell_h"><cc:outprint>nrList.get("nr")</cc:outprint></td>
                  <td class="dataCell_h"><cc:outprint>nrList.get("createdate")</cc:outprint></td>
                  <td class="dataCell_h"><cc:outprint>nrList.get("xcgjsj")==null?"":nrList.get("xcgjsj")+""</cc:outprint></td>
               <cc>}
                }
                </cc>
            </tr>
             <cc>
       }
    //    out.print("2^无跟进记录的客户数"+nullnr);
    //    out.print("3^有跟进记录的客户数"+notnullnr);
  }
  </cc>
 </table>
</div>
</form>
 
</body>
</html>
