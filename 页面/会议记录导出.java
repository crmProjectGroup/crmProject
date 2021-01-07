<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
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
    // 主要涉及表： xmhyjl ( 会议记录表 id) 关联 ,  Attachement ( 附件表 relatedid ) 
    CCService cs = new CCService(userInfo);
    String userid = userInfo.getUserId();
    String profid = userInfo.getProfileId();//getProfileId当前登录用户的简档id 
	//时间范围
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	String timebegin = request.getParameter("timebegin")==null?"":request.getParameter("timebegin")+""; // 获取起始时间
    String timeend = request.getParameter("timeend")==null?"":request.getParameter("timeend")+""; // 获取结束时间
    String timehorizon = timebegin + "  到  " + timeend;
    // 组装 时间 sql片段
    String datetime = " and a.createdate >= str_to_date('"+timebegin+"', '%Y-%m-%d %H:%i:%s')  AND a.createdate <= str_to_date('"+timeend+"', '%Y-%m-%d %H:%i:%s') order by a.createdate desc";
    String projectId = request.getParameter("projectId")==null?"":request.getParameter("projectId")+""; // 获取项目的id
    String fjvalue = request.getParameter("fjvalue")==null?"":request.getParameter("fjvalue")+""; // 获取项目经理的id
     // 组装 项目id 的sql 片段
     String prosql = "";
     if (!"".equals(projectId)) {
         prosql = " and a.xmmc = '" + projectId+"'";
     }
     // 组装 有无附件 的sql片段
     String fjsql = "";
     if (!"".equals(fjvalue)) {
         if ("有附件".equals(fjvalue)) {
             fjsql = " and att.id is not null";
         } else {
             fjsql = " and att.id is null ";
         }
     }
	//获取会议记录信息
    List<CCObject> list = new ArrayList<CCObject>();
    String xmhyjlsql = "";
    if("aaa20180D5809FBsQZab".equals(profid) || "aaa20180681351FmekUG".equals(profid) || "aaa2018E46BFCF90SnzU".equals(profid) || "aaa201854696184hq4oN".equals(profid) || "aaa000001".equals(profid) ){
        xmhyjlsql = "select a.id as hyid,a.name as hyname,att.id as fjid,a.xmmc,a.createbyid,a.createdate,a.hyztxx,a.hynr,a.hyzt,c.name as cname,c.id as cid,p.name as pname from xmhyjl a  left join Attachement att  on att.relatedid = a.id join project p on a.xmmc = p.id join ccuser c on p.xmjl = c.id where a.is_deleted='0'  and p.is_deleted='0' and c.is_deleted='0' and c.isusing='1' "+ prosql + fjsql + datetime;
    } else {
        xmhyjlsql = "select a.id as hyid,a.name as hyname,att.id as fjid,a.xmmc,a.createbyid,a.createdate,a.hyztxx,a.hynr,a.hyzt,c.name as cname,c.id as cid,p.name as pname from xmhyjl a  left join Attachement att  on att.relatedid = a.id join project p on a.xmmc = p.id join ccuser c on p.xmjl = c.id where a.is_deleted='0'  and p.is_deleted='0' and c.is_deleted='0' and c.isusing='1' and p.xmjl = '"+userid+"' "+ prosql + fjsql + datetime;
    }
    list = cs.cqlQuery("xmhyjl", xmhyjlsql); // 执行sql语句
    // out.print(sql);
	 String file_name = new String("会议纪要数据.xls".getBytes(), "ISO-8859-1");
	 response.setHeader("Content-disposition","attachment;filename="+file_name);
	 response.setContentType("application/vnd.ms-excel;charset=UTF-8");
</cc>
<html >
<head>
    <title>会议记录数据</title>
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
    <h1 align="center"><font size="4px">会议记录数据</font></h1>
    <form method="post" name="theform" id="theform">
</div> 
<div class="cloudbox3">
<br/>
    
    <table id="showtable" border="0px" class="cloudbiaoge" cellspacing="0" cellspadding="0" width="100%" style="word-break: break-all; word-wrap:break-word;">
<tr class="tabtitle" style="font-size:15px; text-align: center;">
    <td  NOWRAP="NOWRAP" class="tdTitle">项目会议编号</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">项目名称</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">会议主题</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">会议内容</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">创建时间</td>
	<td  NOWRAP="NOWRAP" class="tdTitle">创建人</td>
    <td  NOWRAP="NOWRAP" class="tdTitle">附件</td>
  </tr>
  <cc>
        Map<String, Integer> projectmap = new HashMap<>(); // 每个项目 总条数
        Map<String, Integer> fjmap = new HashMap<>(); // 每个项目 "有" 附件 总条数
        Map<String, Integer> notfjmap = new HashMap<>(); // 每个项目 "无" 附件 总条数
       for(CCObject item:list){
           String  proname = item.get("pname")==null?"":item.get("pname")+""; // 项目名
           String fjid = item.get("fjid")==null?"":"有附件"; // 有附件
           if (projectmap.get(proname) == null || "".equals(projectmap.get(proname))) {
                
               projectmap.put(proname,1); // 总条数
               if ("".equals(fjid)) { // 判断没有附件
                  fjmap.put(proname,0);
                  notfjmap.put(proname,1);
               } else {
                  fjmap.put(proname,1);
                  notfjmap.put(proname,0);
               }
            //    out.print("总"+projectmap.get(proname));
            //    out.print("附件"+fjmap.get(proname));
            //    out.print("无"+notfjmap.get(proname));
           } else {
               projectmap.put(proname,projectmap.get(proname)+1);
               if ("".equals(fjid)) { // 判断没有附件
                    notfjmap.put(proname,notfjmap.get(proname)+1);
               } else {
                    fjmap.put(proname,fjmap.get(proname)+1);
               }
            }
  </cc>
            <tr>
                  <td  class="dataCell_h" ><cc:outprint>item.get("hyname")==null?"":item.get("hyname")+""</cc:outprint></td>
                  <td  class="dataCell_h" ><cc:outprint>proname</cc:outprint></td>
                  <td  class="dataCell_h" ><cc:outprint>item.get("hyztxx")==null?"":item.get("hyztxx")+""</cc:outprint></td>
                  <td  class="dataCell_h" ><cc:outprint>item.get("hynr")==null?"":item.get("hynr")+""</cc:outprint></td>

				  <td  class="dataCell_h" ><cc:outprint>item.get("createdate")==null?"":item.get("createdate")+""</cc:outprint></td>
                  <td  class="dataCell_h" ><cc:outprint>item.get("cname")==null?"":item.get("cname")+""</cc:outprint></td>
                  <td  class="dataCell_h" ><cc:outprint>fjid</cc:outprint></td>
        <cc>
        }
        </cc>
            </tr>
            <tr class="tabtitle" style="font-size:15px; text-align: center;">
                <td  NOWRAP="NOWRAP" class="tdTitle" colspan="7">各项目会议记录情况汇总</td>   
            </tr>
 </table>
 <!-- 汇总项目的情况 -->
 <table id="showtable" border="0px" class="cloudbiaoge" cellspacing="0" cellspadding="0" width="100%" style="word-break: break-all; word-wrap:break-word;">
    <tr class="tabtitle" style="font-size:15px; text-align: center;">
        <td  NOWRAP="NOWRAP" class="tdTitle">项目名称</td>
        <td  NOWRAP="NOWRAP" class="tdTitle">会议总条数</td>
        <td  NOWRAP="NOWRAP" class="tdTitle">有附件数</td>
        <td  NOWRAP="NOWRAP" class="tdTitle">无附件数</td>
        <td  NOWRAP="NOWRAP" class="tdTitle">时间范围</td>      
    </tr>
    <cc>
        Set<String> prokeys = projectmap.keySet();
        for(String key:prokeys) {
            int proval = projectmap.get(key); // 项目的值
            int fjval = fjmap.get(key); // 有附件的值
            int nofjval = notfjmap.get(key); // 无附件的值
    </cc>
    <tr>
        <td  class="dataCell_h" ><cc:outprint>key</cc:outprint></td>
        <td  class="dataCell_h" ><cc:outprint>proval</cc:outprint></td>
        <td  class="dataCell_h" ><cc:outprint>fjval</cc:outprint></td>
        <td  class="dataCell_h" ><cc:outprint>nofjval</cc:outprint></td>
        <td  class="dataCell_h" ><cc:outprint>timehorizon</cc:outprint></td>
    </tr>
        <cc>
        }
        </cc>
</table>
</div>
</form>
 
</body>
</html>
