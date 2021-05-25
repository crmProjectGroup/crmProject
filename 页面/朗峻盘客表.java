<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" isELIgnored="false" />
<cc!>
/*
description:朗峻广场盘客
version: 1.0
    2021A6974399AE59RKpF  20210428 调整新的公寓属性与布局   202106851B81E2CJ2pos 新公寓进线类型
date:20190725
author:tom
log:
1.20210428 新增页面，展示朗峻广场盘客的信息

*/

/** * 将ISO-8859-1编码格式的字符串转码为UTF-8 
 * * * @param parameterValue 
 * * @return 
 * * @throws UnsupportedEncodingException 
 * */ 
private static String encodeParameters(String parameterValue) throws UnsupportedEncodingException { 
	if (parameterValue != null && parameterValue.length()> 0) { 
		byte[] iso = parameterValue.getBytes("ISO-8859-1"); 
		if (parameterValue.equals(new String(iso, "ISO-8859-1"))) { 
			parameterValue= new String(iso, "UTF-8"); 
			return parameterValue; 
		} 
	} 
	return parameterValue; 
}
</cc>
<cc>
	CCService cs = new CCService(userInfo); 
	String uid = userInfo.getUserId();//当前登录用户id 
	// String uid = "0052019BBC3F9A4s63Lo"; //柳晖d 测试用 
    //String uid = "00520184563249EuLvVg"; //许畅d 测试用
	List<CCObject> ccuserl = cs.cqlQuery("ccuser","SELECT loginname as name FROM ccuser WHERE id = '"+uid+"'"); //登录人的账号
	String userName =ccuserl.get(0).get("name")==null?"":ccuserl.get(0).get("name")+""; 
	
	String profid = userInfo.getProfileId();//getProfileId当前登录用户的简档id 
    //String userName = userInfo.getUserName();//getUserName 
    String roleid = userInfo.getRoleId();
	
	//获取binding
	 
	
	//日期的处理
	java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
    String nowday = df.format(new Date());
    String ksrq = request.getParameter("startTime")==null?nowday:request.getParameter("startTime")+"";//开始日期
    String jsrq = request.getParameter("endTime")==null?nowday:request.getParameter("endTime")+"";//结束日期
    String datetime = " and a.createdate >= str_to_date('"+ksrq+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND a.createdate <= str_to_date('"+jsrq+" 23:59:59', '%Y-%m-%d %H:%i:%s') ";
  
  //进入页面的用户控制
  List<CCObject> projectlist = new ArrayList();
  Boolean fenpeibut=false;
  //out.print(profid);
  // 根据简档权限，获取需要展示的项目 （ 此页面只针对朗俊广场项目使用 ）
  if("aaa2018A38306ED9syVe".equals(profid) || "aaa2018E46BFCF90SnzU".equals(profid) || "aaa201854696184hq4oN".equals(profid) || "aaa000001".equals(profid) || "aaa20180681351FmekUG".equals(profid)){
  	projectlist = cs.cqlQuery("Project","SELECT id, name FROM Project WHERE ssbm in ('代理一部','代理二部') and xmzt='代理中' and is_deleted='0' and id='a0520206562E5ECNVIbC'"); //只有朗峻广场
	fenpeibut=true;
  }  
 else{
    projectlist = cs.cqlQuery("Project","select id,name from project where xmjl in (select id from ccuser where profile='aaa2018A38306ED9syVe' and role in (select roleid from role where parentrole_id='"+ roleid +"' and gap > 0)) and xmzt='代理中'"); 
  }
// 遍历项目，获取项目中每个业务员创建的客户信息
String projectid =""; //项目id 
String projectname =""; //项目名称 
String ywyid = ""; //业务员id 
String ywyname =""; //业务员姓名
int ywyacnum =0; //客户总数 
int ywylineacnum =0; //进线客户数
int ywysmacnum =0; //上门客户数
int ywyflnum =0; //业务员跟进数 
int ywysmacnum_qd =0; //业务员渠道
int ywysmacnum_ldzj =0; //业务员转介
int uidaccount = 0; // 业务员所有的客户
int ywysmacnum_qdcount =0; //总业务员渠道
int ywysmacnum_ldzjcount =0; //总业务员转介
int ywysmacount = 0; // 业务员上门客户总数
int accountall = 0; // 项目中所有的客户
JSONObject projectjson = new JSONObject(); // 存放一个项目的所有数据
JSONArray projectjsonarr = new JSONArray(); // 存放所有项目的所有数据

String jsa ="";
  for (CCObject project:projectlist) {  
    JSONObject ywytojson = new JSONObject(); // 存放 获取的对象
    JSONArray ywytojsonarr = new JSONArray();  // 存放 所有 对象的集合
    ywysmacnum_qdcount=0; // 项目所有的渠道
    ywysmacnum_ldzjcount=0;// 项目所有的转介
    accountall = 0; // 项目所有的客户
    projectid = project.get("id")==null?"":project.get("id")+ "";  // 项目的id
    projectname = project.get("name")==null?"":project.get("name") + ""; // 项目名称
    // out.print(projectname+">>");
    // 获取该项目下，所有销售组 的成员
    List<CCObject> SaleGrouplist = cs.cqlQuery("ProjectSaleGroup","SELECT xmxsy FROM ProjectSaleGroup WHERE xmmc = '"+projectid+"' and is_deleted='0'");
    // 遍历每个业务员
    for (CCObject sale:SaleGrouplist) {
        ywyacnum =0; //客户总数 
        ywylineacnum =0; //进线客户数
        ywysmacnum =0; //上门客户数
        ywyflnum =0; //业务员跟进数 
        ywysmacnum_qd =0; //业务员渠道
        ywysmacnum_ldzj =0; //业务员转介
        uidaccount = 0; // 业务员所有的客户
        ywyid = sale.get("xmxsy")==null?"":sale.get("xmxsy") + ""; // 业务员的id
        String sql1 = "SELECT count(*) as num FROM Account a WHERE ownerid = '"+ywyid+"' "+datetime+"and is_deleted='0' ";
        List<CCObject> Accountnum = cs.cqlQuery("Account",sql1);//客户总数量
        // 时间范围内，客户总数
        ywyacnum = Accountnum.get(0).get("num")==null?0:Integer.parseInt(Accountnum.get(0).get("num")+""); 
        // 时间范围内，进线客户数量
        String sql2 = "SELECT count(*) as num FROM Account a WHERE ownerid = '"+ywyid+"' "+datetime+"and is_deleted='0' and recordtype = '2018525F215221DtlTXV' ";
        List<CCObject> Accountnum1 = cs.cqlQuery("Account",sql2);
        //进线客户数量
		ywylineacnum = Accountnum1.get(0).get("num")==null?0:Integer.parseInt(Accountnum1.get(0).get("num")+"");
        //上门客户数=客户总数-进线客户数
        ywysmacnum = ywyacnum - ywylineacnum; 

        //渠道客户,联动转介客户和自然上门
        List<CCObject> Accountnum_qd = cs.cqlQuery("Account","SELECT count(*) as num FROM Account a WHERE ownerid = '"+ywyid+"' "+datetime+"and is_deleted='0' and (rztj2 ='瑞信行渠道转介' or rztj2 ='甲方渠道转介') and recordtype <> '2018525F215221DtlTXV'"); //渠道转介
        ywysmacnum_qd = Accountnum_qd.get(0).get("num")==null?0:Integer.parseInt(Accountnum_qd.get(0).get("num")+""); 
        List<CCObject> Accountnum_ldzj = cs.cqlQuery("Account","SELECT count(*) as num FROM Account a WHERE ownerid = '"+ywyid+"' "+datetime+"and is_deleted='0' and rztj2 ='瑞信行联动转介' and recordtype <> '2018525F215221DtlTXV'"); //渠道转介
        ywysmacnum_ldzj = Accountnum_ldzj.get(0).get("num")==null?0:Integer.parseInt(Accountnum_ldzj.get(0).get("num")+"");
        // 获取单个业务员所有的客户， 在时间范围内
        String uidaccsql = "SELECT count(*) as num FROM Account a WHERE ownerid = '"+ywyid+"' "+datetime+"and is_deleted='0'";
        List<CCObject> uidaccountlist = cs.cqlQuery("Account",uidaccsql); 
        uidaccount = uidaccountlist.get(0).get("num")==null?0:Integer.parseInt(uidaccountlist.get(0).get("num")+"");


        // 所有业务员渠道客户汇总
        ywysmacnum_qdcount += ywysmacnum_qd; 
        // 所有业务员 转介汇总
        ywysmacnum_ldzjcount =+ ywysmacnum_ldzj; 
        // 所有业务员上门客户总数
        ywysmacount += ywysmacnum;
        // 项目所有客户数
        accountall += uidaccount;
        List<CCObject> ccuserlist = cs.cqlQuery("ccuser","SELECT name as name FROM ccuser WHERE id = '"+ywyid+"'"); //业务员姓名 
        //ywyname =ccuserlist.get(0).get("name")==null?"":ccuserlist.get(0).get("name")+""; 
        ywyname = ccuserlist.get(0).get("name")==null?"":ccuserlist.get(0).get("name")+"";
        //out.print(ywyname+"/"); 
        // 获取业务员，时间范围内的 跟进数
        String gjsql = "select count(*) as num from ywjhgjmx a inner join Account b on a.kh=b.id where a.ownerid='"+ywyid+"' and a.is_deleted='0' "+datetime+"";
        List<CCObject> ywjhgjmxnum = cs.cqlQuery("ywjhgjmx",gjsql);//ywjhgjmx跟进数 
        //out.print(gjsql);
        ywyflnum = ywjhgjmxnum.get(0).get("num")==null?0:Integer.parseInt(ywjhgjmxnum.get(0).get("num")+"");

        ywytojson.put("ywyid",ywyid); // 业务员id
        ywytojson.put("ywyname",ywyname); // 业务员姓名
        ywytojson.put("ywysmacnum",ywysmacnum); // 业务员上门数
        ywytojson.put("ywylineacnum",ywylineacnum); // 业务员进线数
        ywytojson.put("ywyflnum",ywyflnum); // y业务员跟进数
        ywytojson.put("uidaccount",uidaccount); // 业务员客户数
        
        ywytojson.put("ywyqdnum",ywysmacnum_qd); // 业务员渠道数
        ywytojson.put("ywyzrnum",ywysmacnum-ywysmacnum_qd-ywysmacnum_ldzj); //  上门数  - 渠道数 - 联动数 = 上门数
        ywytojson.put("ywyldzjnum",ywysmacnum_ldzj); // 业务员联动数

        //获取业务员的具体客户信息 begin 公寓没有取到需求面积的值  多查询 xqmjGy 字段
        List<CCObject> Accountlist = cs.cqlQuery("Account","SELECT id,name,lxrxm,lxrdh,sex1,yzkhqk,recordtype,gyNewCoChannel,gzcpmjd,jgyq FROM Account a WHERE ownerid ='"+ywyid+"' "+datetime+" and is_deleted='0'"); //客户数量 
        JSONArray csjsonArray = JSONArray.fromObject(Accountlist); 
        String csjsa=csjsonArray.toString();
        ywytojson.put("cslist",csjsa);
        ywytojsonarr.add(ywytojson); 
    }
    jsa=ywytojsonarr.toString(); 
    projectjson.put("projectname",projectname); // 项目名称
    projectjson.put("projectid",projectid); // 项目id
    projectjson.put("qdnum",ywysmacnum_qdcount); // 总渠道数
    projectjson.put("ldzjnum",ywysmacnum_ldzjcount); // 总转介数
    projectjson.put("accountall",accountall); // 总客户数
    projectjson.put("zrnum",ywysmacount-ywysmacnum_qdcount-ywysmacnum_ldzjcount); // 上门总数（除去渠道和转介）
    projectjson.put("ywy",jsa); // 表数据
    projectjsonarr.add(projectjson); 
  }
  jsa=projectjsonarr.toString();
  // out.print(jsa);
</cc>
<html>

        <head>
		<!--<meta http-equiv="refresh" content="5">-->
		<!-- <script src="//cdn.jsdelivr.net/npm/vue@2.6.11/dist/vue.js"></script> -->
		<script type="text/javascript" src="/staticResource.action?m=getResource&resourceId=1610358808662R8dsqYJG"></script> <!--引入内部vue -->
		<script src="https://unpkg.com/muse-ui/dist/muse-ui.js"></script>
        <script src="//unpkg.com/element-ui@2.13.1/lib/index.js"></script>
		<script type="text/javascript" src="/staticResource.action?m=getResource&resourceId=1610359371060HP6AYHCZ"></script> <!--引入内部axios -->
		<!-- <script src="https://unpkg.com/axios/dist/axios.min.js"></script> -->
		<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/muse-ui@3.0.2/dist/muse-ui.css">
        <link rel="stylesheet" href="//unpkg.com/element-ui@2.15.1/lib/theme-chalk/index.css">
		<link rel="stylesheet" href="https://cdn.bootcss.com/material-design-icons/3.0.1/iconfont/material-icons.css">
		</head>
        <style>
            .grid-content {
                border-radius: 4px;
                min-height: 36px;
            }
            .bg-purple-dark {
                background: #99a9bf;
            }
            .bg-purple {
                background: #d3dce6;
            }
        </style>
		<body>

		<form action="" method="post" name="theform" id="theform">
    <h2 align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</h2>

   &nbsp;&nbsp;&nbsp;
开始日期&nbsp;&nbsp;<input id="startTime" name="startTime" class="datepicker" type="text" value="<cc:outprint>ksrq</cc:outprint>">
&nbsp;&nbsp;截止日期&nbsp;&nbsp;<input id="endTime" name="endTime" class="datepicker" type="text" value="<cc:outprint>jsrq</cc:outprint>">
&nbsp;&nbsp;<input type="submit"  value=" 查 询 " class="input01"/><br><br>   
    <br>

</form>

<div id="app">
<mu-container>
    <mu-tabs :value.sync="active">
        <mu-tab>案场项目</mu-tab>
        <mu-tab :change="getljacc()">朗峻公客池</mu-tab>
		<mu-tab :change="getthacc()">同行展示</mu-tab>
    </mu-tabs>
<div class="demo-text" v-if="active === 0">
  <mu-expansion-panel v-for="(item,index) in collapselist1" :key="index" >
    <div slot="header"><mu-icon size="15" value="location_city"></mu-icon>{{item.projectname}} 渠道{{item.qdnum}} 自然上门{{item.zrnum}} 内部联动{{item.ldzjnum}} <mu-button flat color="primary" style="min-width: 66px; padding: 0" @click="getaccbyproid(item.projectid)">所有客户详情{{item.accountall}}</mu-button></div>
    	<mu-expansion-panel v-for="(item1,index) in item.ywy" :key="item1.ywyname +'.'+index" :name="item1.ywyname+'.'+index">
    		<div slot="header">
				<mu-icon size="15" value="person"></mu-icon>{{item1.ywyname}} 上门{{item1.ywysmacnum}} 进线{{item1.ywylineacnum}}
                <mu-button flat color="primary" style="min-width: 66px; padding: 0" @click="getFlByTime(item1.ywyid)">跟进数{{item1.ywyflnum}}</mu-button> 
                <mu-button flat color="primary" style="min-width: 66px; padding: 0" @click="getaccbyuid(item1.ywyid)">客户详情{{item1.uidaccount}}</mu-button>
                </div>
                <div>渠道{{item1.ywyqdnum}} 自然上门{{item1.ywyzrnum}} 内部联动{{item1.ywyldzjnum}}</div>
				<mu-data-table :columns="columns_cs" :data="item1.cslist">
      		<template slot-scope="scope">
                <!-- <td>{{scope.row.name}}</td> -->
                <td><a @click="getCusInfo(scope.row.id)">{{scope.row.name}}</a></td>
        		<td>{{scope.row.lxrxm}}</td>
        		<td>{{scope.row.sex1}}</td>
				<!-- <td>{{scope.row.lxrdh}}</td> -->
				<td>{{scope.row.yzkhqk}}</td>
				<td>{{scope.row.gzcpmjd}}</td>
                <td>{{scope.row.jgyq}}</td>                                               
				<td>{{scope.row.recordtype=='20186166515AE4A8ZfOc' ? ((scope.row.rztj2=='瑞信行渠道转介' || scope.row.rztj2=='甲方渠道转介') ? '渠道客户':scope.row.rztj2=='瑞信行联动转介' ? '内部转介':'自然上门' ): scope.row.recordtype=='2018496272C934EtLhWs' ? ((scope.row.rztj2=='瑞信行渠道转介' || scope.row.rztj2=='甲方渠道转介') ? '渠道客户':scope.row.rztj2=='瑞信行联动转介' ? '内部转介':'自然上门') : scope.row.recordtype=='2020F8FFFACC18DmPXQ1' ? ((scope.row.rztj2=='瑞信行渠道转介' || scope.row.rztj2=='甲方渠道转介') ? '渠道客户':scope.row.rztj2=='瑞信行联动转介' ? '内部转介':'自然上门') : scope.row.recordtype=='2021A6974399AE59RKpF' ? ((scope.row.rztj2=='瑞信行渠道转介' || scope.row.rztj2=='甲方渠道转介') ? '渠道客户':scope.row.rztj2=='瑞信行联动转介' ? '内部转介':'自然上门') : '进线'}}</td>
        		<td><mu-button color="primary" @click="getFollws(scope.row.id)">跟进详情</mu-button></td>
      		</template>
    	</mu-data-table>
    	</mu-expansion-panel>
  </mu-expansion-panel>
  </div>
  <mu-drawer :open.sync="open" :docked="docked" :right="position === 'right'" width="80%">
    <mu-paper :z-depth="1">
    <mu-data-table :columns="columns" :data="gridData1">
      <template slot-scope="scope">
        <td>{{scope.row.createdate}}</td>
        <td>{{scope.row.name}}</td>
        <td>{{scope.row.fltype}}</td>
        <td>{{scope.row.nr}}</td>
      </template>
    </mu-data-table>
  </mu-paper>
  </mu-drawer>
  
  <mu-drawer :open.sync="open1" :docked="docked1" :right="position1 === 'right'" width="80%">
    <mu-paper :z-depth="1">
    <mu-data-table :columns="columns1" :data="dataForMessage">
      <template slot-scope="scope">
        <td>{{scope.row.fltype}}</td>
        <td>{{scope.row.createdate}}</td>
        <td>{{scope.row.nr}}</td>
      </template>
    </mu-data-table>
  </mu-paper>
  </mu-drawer>
<!-- 展示单个业务员的客户详情-->
  <mu-drawer :open.sync="open2" :docked="docked2" :right="position2 === 'right'" width="80%">
    <mu-paper :z-depth="1">
    <mu-data-table :columns="columns2" :data="uidaccountdata">
      <template slot-scope="scope">
        <td>{{scope.row.name}}</td>
        <td>{{scope.row.smsj}}</td>
        <td>{{scope.row.ybgqy}}</td>
        <td>{{scope.row.lxrxm}}</td>
        <td>{{scope.row.xjzxq}}</td>
        <td>{{scope.row.gyclienttype}}</td>
        <td>{{scope.row.yzkhqk}}</td>
        <td>{{scope.row.gjfk}}</td>
        <td>{{scope.row.sfrg}}</td>
        <td>{{scope.row.familystructure}}</td>
        <td>{{scope.row.gzqy}}</td>
        <td>{{scope.row.gzdd}}</td>
        <td>{{scope.row.gzqybz}}</td>
        <td>{{scope.row.lfcs}}</td>
        <td>{{scope.row.jzqy}}</td>
        <td>{{scope.row.jzdd}}</td>
        <td>{{scope.row.jzqybz}}</td>
        <td>{{scope.row.lcyx}}</td>
        <td>{{scope.row.accouana}}</td>
        <td>{{scope.row.sfgzwlxx}}</td>
        <td>{{scope.row.xqah}}</td>
        <td>{{scope.row.gynewcochannel}}</td>
        <td>{{scope.row.rtdian}}</td>
        <td>{{scope.row.rentongdian}}</td>
        <td>{{scope.row.jgyq}}</td>
        <td>{{scope.row.gyresistance}}</td>
        <td>{{scope.row.gzcpmjd}}</td>
        <td>{{scope.row.zymd}}</td>
        <td><mu-button color="primary" @click="gotoaccount(scope.row.id)">编辑</mu-button></td>
      </template>
    </mu-data-table>
  </mu-paper>
  </mu-drawer>

  <!-- 展示所有业务员的客户详情-->
  <mu-drawer :open.sync="open3" :docked="docked3" :right="position3 === 'right'" width="80%">
    <mu-paper :z-depth="1">
    <mu-data-table :columns="columns3" :data="alluidaccount">
      <template slot-scope="scope">
        <td>{{scope.row.aname}}</td>
        <td>{{scope.row.uname}}</td>
        <td>{{scope.row.smsj}}</td>
        <td>{{scope.row.ybgqy}}</td>
        <td>{{scope.row.lxrxm}}</td>
        <td>{{scope.row.xjzxq}}</td>
        <td>{{scope.row.gyclienttype}}</td>
        <td>{{scope.row.yzkhqk}}</td>
        <td>{{scope.row.gjfk}}</td>
        <td>{{scope.row.sfrg}}</td>
        <td>{{scope.row.familystructure}}</td>
        <td>{{scope.row.gzqy}}</td>
        <td>{{scope.row.gzdd}}</td>
        <td>{{scope.row.gzqybz}}</td>
        <td>{{scope.row.lfcs}}</td>
        <td>{{scope.row.jzqy}}</td>
        <td>{{scope.row.jzdd}}</td>
        <td>{{scope.row.jzqybz}}</td>
        <td>{{scope.row.lcyx}}</td>
        <td>{{scope.row.accouana}}</td>
        <td>{{scope.row.sfgzwlxx}}</td>
        <td>{{scope.row.xqah}}</td>
        <td>{{scope.row.gynewcochannel}}</td>
        <td>{{scope.row.rtdian}}</td>
        <td>{{scope.row.rentongdian}}</td>
        <td>{{scope.row.jgyq}}</td>
        <td>{{scope.row.gyresistance}}</td>
        <td>{{scope.row.gzcpmjd}}</td>
        <td>{{scope.row.zymd}}</td>
        <td><mu-button color="primary" @click="gotoaccount(scope.row.aid)">编辑</mu-button></td>
      </template>
    </mu-data-table>
  </mu-paper>
  </mu-drawer>
  <!--朗峻公客池表-->
  <div class="demo-text" v-if="active === 1">
    <mu-container>
        <mu-paper :z-depth="1">
          <mu-data-table selectable select-all :selects.sync="selects" checkbox :loading="istrue" :columns="columns4" :data="getljacctable">
            <template slot-scope="scope">
                <td>{{scope.row.name}}</td>
                <td>{{scope.row.smsj}}</td>
                <td>{{scope.row.ybgqy}}</td>
                <td>{{scope.row.lxrxm}}</td>
                <td>{{scope.row.xjzxq}}</td>
                <td>{{scope.row.gyclienttype}}</td>
                <td>{{scope.row.yzkhqk}}</td>
                <td>{{scope.row.gjfk}}</td>
                <td>{{scope.row.sfrg}}</td>
                <td>{{scope.row.familystructure}}</td>
                <td>{{scope.row.gzqy}}</td>
                <td>{{scope.row.gzdd}}</td>
                <td>{{scope.row.gzqybz}}</td>
                <td>{{scope.row.lfcs}}</td>
                <td>{{scope.row.jzqy}}</td>
                <td>{{scope.row.jzdd}}</td>
                <td>{{scope.row.jzqybz}}</td>
                <td>{{scope.row.lcyx}}</td>
                <td>{{scope.row.accouana}}</td>
                <td>{{scope.row.sfgzwlxx}}</td>
                <td>{{scope.row.xqah}}</td>
                <td>{{scope.row.gynewcochannel}}</td>
                <td>{{scope.row.rtdian}}</td>
                <td>{{scope.row.rentongdian}}</td>
                <td>{{scope.row.jgyq}}</td>
                <td>{{scope.row.gyresistance}}</td>
                <td>{{scope.row.gzcpmjd}}</td>
                <td>{{scope.row.zymd}}</td>
            </template>
          </mu-data-table>
          <mu-flex align-items="center" style="padding: 8px;" wrap="wrap">
            已选中的客户: <mu-chip delete v-for="selectIndex in selects" @delete="removeSelect(selectIndex)" style="margin: 8px;" color="green" :key="selectIndex">{{getljacctable[selectIndex].name}}</mu-chip>
          </mu-flex>
        </mu-paper>
      </mu-container>
      <div>
        <template>
            <el-select v-model="uservalue" filterable placeholder="请选择">
            <el-option style="height: 45px;"
                v-for="item in userlisttable"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
            </el-select>
			<cc>
				if(fenpeibut) {
			</cc>
				<el-button type="primary" @click="setljgklist(selects)">立即分配</el-button>
			<cc>}</cc>
        </template>
      </div>
</div>
<!--同行表展示-->
  <div class="demo-text" v-if="active === 2">
    <mu-container>
        <mu-paper :z-depth="1">
          <mu-data-table :loading="isthtrue" :columns="columns5" :data="getthacctable">
            <template slot-scope="scope">
                <td>{{scope.row.aname}}</td>
				<td>{{scope.row.uname}}</td>
				<td>{{scope.row.smsj}}</td>
				<td>{{scope.row.ybgqy}}</td>
				<td>{{scope.row.lxrxm}}</td>
				<td>{{scope.row.xjzxq}}</td>
				<td>{{scope.row.gyclienttype}}</td>
				<td>{{scope.row.yzkhqk}}</td>
				<td>{{scope.row.gjfk}}</td>
				<td>{{scope.row.sfrg}}</td>
				<td>{{scope.row.familystructure}}</td>
				<td>{{scope.row.gzqy}}</td>
				<td>{{scope.row.gzdd}}</td>
				<td>{{scope.row.gzqybz}}</td>
				<td>{{scope.row.lfcs}}</td>
				<td>{{scope.row.jzqy}}</td>
				<td>{{scope.row.jzdd}}</td>
				<td>{{scope.row.jzqybz}}</td>
				<td>{{scope.row.lcyx}}</td>
				<td>{{scope.row.accouana}}</td>
				<td>{{scope.row.sfgzwlxx}}</td>
				<td>{{scope.row.xqah}}</td>
				<td>{{scope.row.gynewcochannel}}</td>
				<td>{{scope.row.rtdian}}</td>
				<td>{{scope.row.rentongdian}}</td>
				<td>{{scope.row.jgyq}}</td>
				<td>{{scope.row.gyresistance}}</td>
				<td>{{scope.row.gzcpmjd}}</td>
				<td>{{scope.row.zymd}}</td>
				<!--<td><mu-button color="primary" @click="gotoaccount(scope.row.aid)">编辑</mu-button></td>-->
            </template>
          </mu-data-table>
        </mu-paper>
      </mu-container>
</div>
</mu-container>
</div>

		<br>
		<br>
		<br>
		

		<script>
var Main = {
  data () {
    return {
      panel: '',
      collapselist1: <cc:outprint>jsa</cc:outprint>,
      docked: false,
      open: false,
      position: 'right',
      
      docked1: false,
      open1: false,
      position1: 'right',

      docked2: false,
      open2: false,
      position2: 'left',

      docked3: false,
      open3: false,
      position3: 'left',
      
      bindingcode:'',
			activeNames: ['1'],
			userName:'<cc:outprint>userName</cc:outprint>',
			url1: 'https://k8mm1amta1700adb471ba12b.cloudcc.com/distributor.action?serviceName=cssoLogin',
			datetime: "<cc:outprint>datetime</cc:outprint>",
			dataForMessage:[],
			gridData1:[],
            uidaccountdata:[],
            alluidaccount:[],
			columns: [
          { title: '日期', width: 120, name: 'createdate' },
          { title: '客户', name: 'name', width: 100 },
          { title: '跟进方式', name: 'fltype', width: 80},
          { title: '跟进内容', name: 'nr', width: 200}
      ],
      columns1: [
          { title: '跟进方式', name: 'fltype', width: 80},
          { title: '日期', width: 120, name: 'createdate' },
          { title: '跟进内容', name: 'nr', width: 200}
      ],
      columns_cs: [
          { title: '企业名称', width: 100, name: 'name' },
          { title: '联系人', name: 'lxrxm', width: 100},
          { title: '性别', name: 'sex1', width: 100},
        //   { title: '电话', name: 'lxrdh', width: 120},
          { title: '验资客户情况', name: 'yzkhqk', width: 100},
		  { title: '关注产品面积段', name: 'gzcpmjd', width: 100},
		  { title: '价格预期', name: 'jgyq', width: 100},
		  { title: '认知途径', name: 'gyNewCoChannel', width: 100},
          { title: '操作', width: 120}
      ],
      active: 0,
    // 展示单个业务员的客户详情
      columns2: [
        { title: '企业名称', width: 100, name: 'name' },
        { title: '上门时间', name: 'smsj', width: 100},
        { title: '工作区域(主要事业)', name: 'ybgqy', width: 150},
        { title: '联系人姓名', name: 'lxrxm', width: 100},
        { title: '现居住小区', name: 'xjzxq', width: 100},
        { title: '客户类型', name: 'gyclienttype', width: 100},
        { title: '验资客户情况', name: 'yzkhqk', width: 120},
        { title: '跟进反馈', width: 100, name: 'gjfk' },
        { title: '是否认购', name: 'sfrg', width: 100},
        { title: '家庭结构', name: 'familystructure', width: 100},
        { title: '具体工作区域', name: 'gzqy', width: 100},
        { title: '具体工作地点', name: 'gzdd', width: 100},
        { title: '具体工作区域备注', name: 'gzqybz', width: 100},
        { title: '来访次数', name: 'lfcs', width: 120},
        { title: '居住区域', width: 100, name: 'jzqy' },
        { title: '居住地点', name: 'jzdd', width: 100},
        { title: '居住区域备注', name: 'jzqybz', width: 100},
        { title: '楼层意向', name: 'lcyx', width: 100},
        { title: '客户语录', name: 'accouana', width: 100},
        { title: '是否关注项目未来信息', name: 'sfgzwlxx', width: 150},
        { title: '兴趣爱好(活动)', name: 'xqah', width: 100},
        { title: '认知途径3', name: 'gynewcochannel', width: 120},
        { title: '认同点（价值）', width: 100, name: 'rtdian' },
        { title: '认同点', name: 'rentongdian', width: 100},
        { title: '价格预期', name: 'jgyq', width: 100},
        { title: '抗性点(公寓)', name: 'gyresistance', width: 100},
        { title: '关注产品面积段', name: 'gzcpmjd', width: 100},
        { title: '置业目的', name: 'zymd', width: 100},
        { title: '操作', width: 120}
      ],
// 展示所有业务员的客户详情
      columns3: [
        { title: '企业名称', width: 100, name: 'aname' },
        { title: '创建人', name: 'uname', width: 100},
        { title: '上门时间', name: 'smsj', width: 100},
        { title: '工作区域(主要事业)', name: 'ybgqy', width: 150},
        { title: '联系人姓名', name: 'lxrxm', width: 100},
        { title: '现居住小区', name: 'xjzxq', width: 100},
        { title: '客户类型', name: 'gyclienttype', width: 100},
        { title: '验资客户情况', name: 'yzkhqk', width: 120},
        { title: '跟进反馈', width: 100, name: 'gjfk' },
        { title: '是否认购', name: 'sfrg', width: 100},
        { title: '家庭结构', name: 'familystructure', width: 100},
        { title: '具体工作区域', name: 'gzqy', width: 100},
        { title: '具体工作地点', name: 'gzdd', width: 100},
        { title: '具体工作区域备注', name: 'gzqybz', width: 100},
        { title: '来访次数', name: 'lfcs', width: 120},
        { title: '居住区域', width: 100, name: 'jzqy' },
        { title: '居住地点', name: 'jzdd', width: 100},
        { title: '居住区域备注', name: 'jzqybz', width: 100},
        { title: '楼层意向', name: 'lcyx', width: 100},
        { title: '客户语录', name: 'accouana', width: 100},
        { title: '是否关注项目未来信息', name: 'sfgzwlxx', width: 150},
        { title: '兴趣爱好(活动)', name: 'xqah', width: 100},
        { title: '认知途径3', name: 'gynewcochannel', width: 120},
        { title: '认同点（价值）', width: 100, name: 'rtdian' },
        { title: '认同点', name: 'rentongdian', width: 100},
        { title: '价格预期', name: 'jgyq', width: 100},
        { title: '抗性点(公寓)', name: 'gyresistance', width: 100},
        { title: '关注产品面积段', name: 'gzcpmjd', width: 100},
        { title: '置业目的', name: 'zymd', width: 100},
        { title: '操作', width: 120}
      ],
      // 朗峻公客池展示 begin
      columns4: [
        { title: '企业名称', width: 100, name: 'aname' },
        { title: '上门时间', name: 'smsj', width: 100},
        { title: '工作区域(主要事业)', name: 'ybgqy', width: 150},
        { title: '联系人姓名', name: 'lxrxm', width: 100},
        { title: '现居住小区', name: 'xjzxq', width: 100},
        { title: '客户类型', name: 'gyclienttype', width: 100},
        { title: '验资客户情况', name: 'yzkhqk', width: 120},
        { title: '跟进反馈', width: 100, name: 'gjfk' },
        { title: '是否认购', name: 'sfrg', width: 100},
        { title: '家庭结构', name: 'familystructure', width: 100},
        { title: '具体工作区域', name: 'gzqy', width: 100},
        { title: '具体工作地点', name: 'gzdd', width: 100},
        { title: '具体工作区域备注', name: 'gzqybz', width: 100},
        { title: '来访次数', name: 'lfcs', width: 120},
        { title: '居住区域', width: 100, name: 'jzqy' },
        { title: '居住地点', name: 'jzdd', width: 100},
        { title: '居住区域备注', name: 'jzqybz', width: 100},
        { title: '楼层意向', name: 'lcyx', width: 100},
        { title: '客户语录', name: 'accouana', width: 100},
        { title: '是否关注项目未来信息', name: 'sfgzwlxx', width: 150},
        { title: '兴趣爱好(活动)', name: 'xqah', width: 100},
        { title: '认知途径3', name: 'gynewcochannel', width: 120},
        { title: '认同点（价值）', width: 100, name: 'rtdian' },
        { title: '认同点', name: 'rentongdian', width: 100},
        { title: '价格预期', name: 'jgyq', width: 100},
        { title: '抗性点(公寓)', name: 'gyresistance', width: 100},
        { title: '关注产品面积段', name: 'gzcpmjd', width: 100},
        { title: '置业目的', name: 'zymd', width: 100}
      ],
      getljacctable: [], // 朗峻公客数据
      istrue:true, // 避免重复提交
      selects:[], // 已选的客户
      userlisttable:[],// 业务员数据
      uservalue:'',//选择分配的业务员的id
	  
	  isthtrue:true,// 同行类加载一次
	  getthacctable:[],// 同行表table数据
	  columns5: [ // 同行表展示
        { title: '企业名称', width: 100, name: 'aname' },
        { title: '创建人', name: 'uname', width: 100},
        { title: '上门时间', name: 'smsj', width: 100},
        { title: '工作区域(主要事业)', name: 'ybgqy', width: 150},
        { title: '联系人姓名', name: 'lxrxm', width: 100},
        { title: '现居住小区', name: 'xjzxq', width: 100},
        { title: '客户类型', name: 'gyclienttype', width: 100},
        { title: '验资客户情况', name: 'yzkhqk', width: 120},
        { title: '跟进反馈', width: 100, name: 'gjfk' },
        { title: '是否认购', name: 'sfrg', width: 100},
        { title: '家庭结构', name: 'familystructure', width: 100},
        { title: '具体工作区域', name: 'gzqy', width: 100},
        { title: '具体工作地点', name: 'gzdd', width: 100},
        { title: '具体工作区域备注', name: 'gzqybz', width: 100},
        { title: '来访次数', name: 'lfcs', width: 120},
        { title: '居住区域', width: 100, name: 'jzqy' },
        { title: '居住地点', name: 'jzdd', width: 100},
        { title: '居住区域备注', name: 'jzqybz', width: 100},
        { title: '楼层意向', name: 'lcyx', width: 100},
        { title: '客户语录', name: 'accouana', width: 100},
        { title: '是否关注项目未来信息', name: 'sfgzwlxx', width: 150},
        { title: '兴趣爱好(活动)', name: 'xqah', width: 100},
        { title: '认知途径3', name: 'gynewcochannel', width: 120},
        { title: '认同点（价值）', width: 100, name: 'rtdian' },
        { title: '认同点', name: 'rentongdian', width: 100},
        { title: '价格预期', name: 'jgyq', width: 100},
        { title: '抗性点(公寓)', name: 'gyresistance', width: 100},
        { title: '关注产品面积段', name: 'gzcpmjd', width: 100},
        { title: '置业目的', name: 'zymd', width: 100},
        //{ title: '操作', width: 120}
      ],
    }
  },
  created: function(){

 		this.getBinding()
				
        //this.getFollws()       //定义方法

  },
  methods: {
    toggle (panel) {
      this.panel = panel === this.panel ? '' : panel;
    },
        getBinding:function(){
            var that=this;
            axios.get('/distributor.action', {
                params: {
                    serviceName: 'cssoLogin',
                    userName:this.userName,
                    authCode:'6b42a467259407218c0e6acdfc978c01'
                    
                }
            })
            .then(function (response) {
                that.bindingcode=response.data.binding;
                //console.log(this.bindingcode);
            })
            .catch(function (error) {
                console.log(error);
            });
        },
        getFlByTime:function(val){ //传入业务员的id， 查看个人所有的跟进信息
            this.open=!this.open;
            console.log(val);
            this.dialogTableVisible = true;
            var that = this;
            var datetime= this.datetime;
            var expresssql="select date_format(a.createdate, '%Y-%m-%d %H:%i:%s') as createdate ,b.name as name ,a.gjlx as fltype, a.nr as nr from ywjhgjmx a inner join Account b on a.kh=b.id where a.ownerid='"+val+"' and a.is_deleted='0' "+datetime+" order by createdate desc";
            axios.get('/controller.action', {
                params: {
                    name: 'pankapi',
                    type: 'genjinAll', // 获取单个人，所有的跟进信息
                    expressions:expresssql,
                    binding:this.bindingcode
                }
            })
            .then(function (response) {
                debugger
                that.gridData1 = response.data.data;
                console.log(that.gridData1);
                
            })
            .catch(function (error) {
                console.log(error);
            });
        /**/
        },
        getFollws:function(val){ //传入客户id , 跟进详情接口
            this.open1=!this.open1;
            var that = this;
            var expresssql="select date_format(a.createdate, '%Y-%m-%d %H:%i:%s') as createdate,a.gjlx as fltype, a.nr as nr from ywjhgjmx a where kh='"+val+"' and is_deleted='0' order by createdate desc";
            //alert(expresssql);
            axios.get('/controller.action', {
                params: {
                    name: 'pankapi',
                    type: 'genjin', // 查看单个客户的跟进信息
                    expressions:expresssql,
                    binding:this.bindingcode
                }
            })
            .then(function (response) {
                debugger
                that.dataForMessage=response.data.data;
            })
            .catch(function (error) {
                console.log(error);
            });

        },
//传入userid , 获取单个业务员客户详情
        getaccbyuid:function(val){ 
            this.open2=!this.open2;
            var that = this;
            axios.get('/controller.action', {
                params: {
                    name: 'pankapi',
                    type: 'accbyuid', // 根据业务员id，查看业务员所有的客户
                    sinuid:val, // 单个userid
                    datetime:this.datetime,
                    binding:this.bindingcode
                }
            })
            .then(function (response) {
                debugger
                that.uidaccountdata=response.data.data;
            })
            .catch(function (error) {
                console.log(error);
            });

        },
// 传项目id，获取时间范围内所有客户详情
        getaccbyproid:function(val){ 
            this.open3=!this.open3;
            var that = this;
            axios.get('/controller.action', {
                params: {
                    name: 'pankapi',
                    type: 'accbyproid', // 根据项目id，获取项目所有的客户
                    projectid:val, // 项目id
                    datetime:this.datetime,
                    binding:this.bindingcode
                }
            })
            .then(function (response) {
                debugger
                that.alluidaccount=response.data.data;
            })
            .catch(function (error) {
                console.log(error);
            });

        },
        getCusInfo:function(val){ // 根据id，进入客户详情
            window.open("/query.action?m=query&id="+val);
        },
        // 朗峻去跳转到客户页面编辑
        gotoaccount:function(val){ // 根据id，进入客户详情
            // window.open("/query.action?m=query&id="+val);
            window.location.href = ("/query.action?m=query&id="+val);
        },
// 公客池部分 begin
    getljacc: function(val){ // 获取公客池数据
        var that = this;
        if(this.active==1&&this.istrue){
            this.getuserlist(); // 获取userlist
            axios.get('/controller.action', {
                params: {
                    name: 'pankapi',
                    type: 'ljghacc', // 获取朗峻公海客户
                    binding:this.bindingcode
                }
            })
            .then(function (response) {
                //alert("ok!");
                that.getljacctable=response.data.data;
                that.istrue=false;
                that.$forceUpdate();
                //console.log(this.bindingcode);
            })
            .catch(function (error) {
                console.log(error);
            });
        }
    },
    // 删除已选的客户
    removeSelect (selectIndex) {
      const index = this.selects.indexOf(selectIndex);
      this.selects.splice(index, 1);
    },
    // 获取朗峻广场业务员信息
    getuserlist: function(val) {
        var that = this;
        axios.get('/controller.action', {
                params: {
                    name: 'pankapi',
                    type: 'getuserlist', // 获取朗峻公海客户
                    binding:this.bindingcode
                }
            })
            .then(function (response) {
                //alert("ok!");
                that.userlisttable=response.data.data;
                that.$forceUpdate();
                //console.log(this.bindingcode);
            })
            .catch(function (error) {
                console.log(error);
            });
    },
    // 将公客池里的数据分给业务员（参数：分配哪几个客户）
    setljgklist: function(numberarr) { // 客户行数的编号
        debugger
        var that = this;
        var userid = that.uservalue; // 获取分配人的id
        if (userid=="" || userid==null || userid==undefined) {
            alert("请选择分配人！");
            return;
        }
        if (confirm("你确定提交吗？")) {  
        } else {  
            return; 
        }
        var  accid="";
        var accidsql = "";
        //getljacctable[selectIndex].name
        for(j = 0,len=numberarr.length; j < len; j++) {
            accid = that.getljacctable[j].id;
            if (j==0) {
                accidsql = "('"+accid;
            } else if (j!=0) {
                accidsql = accidsql+"','"+accid;
            }
        }
        // 拿到分配人的id  和  需要分配项目的id，传到sql中处理
        axios.get('/controller.action', {
                params: {
                    name: 'pankapi',
                    type: 'setljgkacc', // 分配朗峻公客池客户
                    userid:userid,
                    accidlist:accidsql,
                    binding:this.bindingcode
                }
            })
            .then(function (response) {
                if (response.data.success) {
                    alert("分配成功！");
                    that.getljacc("1");
                }
                history.go(0);
                submit();
                //console.log(this.bindingcode);
            })
            .catch(function (error) {
                console.log(error);
            });
    },
	// 获取同行数据
	getthacc: function(val){ 
        var that = this;
        if(this.active==2&&this.isthtrue){
            axios.get('/controller.action', {
                params: {
                    name: 'pankapi',
                    type: 'thacc', // 获取同行表数据
                    binding:this.bindingcode
                }
            })
            .then(function (response) {
                //alert("ok!");
                that.getthacctable=response.data.data;
                that.isthtrue=false;
                that.$forceUpdate();
                //console.log(this.bindingcode);
            })
            .catch(function (error) {
                console.log(error);
            });
        }
    },
  }
};
var Ctor = Vue.extend(Main)
new Ctor().$mount('#app')
		</script>
		</body>

		</html>