String ksrq = record_new.get("ksrq") == null ? "" : String.valueOf(record_new.get("ksrq"));// 开始日期
		String jsrq = record_new.get("jsrq") == null ? "" : String.valueOf(record_new.get("jsrq"));// 结束日期
		String pyzt = record_new.get("pyzt") == null ? "" : String.valueOf(record_new.get("pyzt"));// 批阅状态
		String ownerid = record_new.get("ownerid") == null ? "" : String.valueOf(record_new.get("ownerid"));// 记录id
		String id = "";
		if (record_old != null) {
			id = record_new.get("id") == null ? "" : String.valueOf(record_new.get("id"));// 记录id
		}
		if ("草稿".equals(pyzt)) {
			// 赢单金额
			List<CCObject> ydList = cqlQuery("Opportunity",
					"select sum(jine) jine from Opportunity where knx = 100 and is_deleted = '0' and ownerid = '"
							+ ownerid + "' and to_date(to_char(jdzjgxsj,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('"
							+ ksrq + "','yyyy-mm-dd') and to_date('" + jsrq + "','yyyy-mm-dd')");
			if (null != ydList && ydList.size() > 0) {
				BigDecimal jine = new BigDecimal(String.valueOf(ydList.get(0).get("jine")));
				record_new.put("bydje", jine.toString());
			}
			// 合同金额
			List<CCObject> htList = cqlQuery("",
					"select sum(htje) htje from contract where is_deleted = '0' and (zhuangtai = '已启用' or zhuangtai = '审批通过') and ownerid = '"
							+ ownerid + "' and to_date(to_char(qyrq1,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('"
							+ ksrq + "','yyyy-mm-dd') and to_date('" + jsrq + "','yyyy-mm-dd')");

			if (null != htList && htList.size() > 0) {
				BigDecimal htje = new BigDecimal(String.valueOf(htList.get(0).get("htje")));
				record_new.put("bhtje", htje.toString());
			}

			// 回款金额
			List<CCObject> hkList = cqlQuery("",
					"select sum(hkje) hkje from hkjl where hkqrzt = '已确认' and is_deleted = '0' and ownerid = '"
							+ ownerid + "' and to_date(to_char(khrq,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('"
							+ ksrq + "','yyyy-mm-dd') and to_date('" + jsrq + "','yyyy-mm-dd')");
			if (null != hkList && hkList.size() > 0) {
				BigDecimal hkje = new BigDecimal(String.valueOf(hkList.get(0).get("hkje")));
				record_new.put("bhkje", hkje.toString());
			}

			// 丢单金额
			List<CCObject> ddList = cqlQuery("Opportunity",
					"select sum(jine) jine from Opportunity where knx = 0 and is_deleted = '0' and ownerid = '"
							+ ownerid + "' and to_date(to_char(jdzjgxsj,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('"
							+ ksrq + "','yyyy-mm-dd') and to_date('" + jsrq + "','yyyy-mm-dd')");

			if (null != ddList && ddList.size() > 0) {
				BigDecimal jine = new BigDecimal(String.valueOf(ddList.get(0).get("jine")));
				record_new.put("bddje", jine.toString());
			}

			// 签到拜访
			List<CCObject> qdList = cqlQuery("Event",
					"select count(*) cnt from Event where type = '签到拜访' and is_deleted = '0' and ownerid = '" + ownerid
							+ "' and to_date(to_char(begintime,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('" + ksrq
							+ "','yyyy-mm-dd') and to_date('" + jsrq
							+ "','yyyy-mm-dd') and to_date(to_char(endtime,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('"
							+ ksrq + "','yyyy-mm-dd') and to_date('" + jsrq + "','yyyy-mm-dd')");
			if (null != qdList && qdList.size() > 0) {
				int count = Integer.parseInt(String.valueOf(qdList.get(0).get("cnt")));
				record_new.put("bqdbf", String.valueOf(count));
			}

			// 电话数量
			List<CCObject> dhList = cqlQuery("Event",
					"select count(*) cnt from Event where type = '电话' and is_deleted = '0' and ownerid = '" + ownerid
							+ "' and to_date(to_char(begintime,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('" + ksrq
							+ "','yyyy-mm-dd') and to_date('" + jsrq
							+ "','yyyy-mm-dd') and to_date(to_char(endtime,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('"
							+ ksrq + "','yyyy-mm-dd') and to_date('" + jsrq + "','yyyy-mm-dd')");
			if (null != dhList && dhList.size() > 0) {
				int count = Integer.parseInt(String.valueOf(dhList.get(0).get("cnt")));
				record_new.put("bdh", String.valueOf(count));
			}

			// 新增潜客数量
			List<CCObject> qkList = cqlQuery("Lead",
					"select count(*) cnt from Lead where is_deleted = '0' and ownerid = '" + ownerid
							+ "' and to_date(to_char(createdate,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('" + ksrq
							+ "','yyyy-mm-dd') and to_date('" + jsrq + "','yyyy-mm-dd')");
			if (null != qkList && qkList.size() > 0) {
				int count = Integer.parseInt(String.valueOf(qkList.get(0).get("cnt")));
				record_new.put("bxzqksl", String.valueOf(count));
			}

			// 新增客户数量
			List<CCObject> khList = cqlQuery("Account",
					"select count(*) cnt from Account where is_deleted = '0' and ownerid = '" + ownerid
							+ "' and to_date(to_char(createdate,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('" + ksrq
							+ "','yyyy-mm-dd') and to_date('" + jsrq + "','yyyy-mm-dd')");
			if (null != khList && khList.size() > 0) {
				int count = Integer.parseInt(String.valueOf(khList.get(0).get("cnt")));
				record_new.put("bxzkhsl", String.valueOf(count));
			}

			// 新增业务机会数量
			List<CCObject> ywjhList = cqlQuery("Opportunity",
					"select count(*) cnt from Opportunity where is_deleted = '0' and ownerid = '" + ownerid
							+ "' and to_date(to_char(createdate,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date('" + ksrq
							+ "','yyyy-mm-dd') and to_date('" + jsrq + "','yyyy-mm-dd')");
			if (null != ywjhList && ywjhList.size() > 0) {
				int count = Integer.parseInt(String.valueOf(ywjhList.get(0).get("cnt")));
				record_new.put("bxzywjhsl", String.valueOf(count));
			}
		}