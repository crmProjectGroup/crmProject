String bglx = record_new.get("bglx") == null ? "" : String.valueOf(record_new.get("bglx"));// 报告类型
		String ksrq = record_new.get("ksrq") == null ? "" : String.valueOf(record_new.get("ksrq"));// 开始日期
		String jsrq = record_new.get("jsrq") == null ? "" : String.valueOf(record_new.get("jsrq"));// 结束日期
		String ownerid = record_new.get("ownerid") == null ? "" : String.valueOf(record_new.get("ownerid"));// 记录所有人
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		MultiLanguage multiLang = new MultiLanguage(userInfo, "zh");
		String lang = "";
		try {
			/**
			 * 日期校验
			 */
			Date firstDay, lastDay;
			Calendar standardCal = Calendar.getInstance();// 标准日历
			standardCal.setTime(sdf.parse(ksrq));// 设定标准日历日期
			Calendar beginCal = Calendar.getInstance();
			beginCal.setTime(sdf.parse(ksrq));// 设定开始日期
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(sdf.parse(jsrq));// 设定结束日期
			if (bglx.equals("日报")) {
				if (!ksrq.equals(jsrq)) {
					lang = "【日报】的开始日期于结束日期必须为同一天！";
					multiLang.initLanguageResource(lang, "en",
							"[daily]The start date of the daily report must be the same day at the end date!");
					trigger.addErrorMessage(multiLang.translate(lang));
				}
			} else if (bglx.equals("周报")) {
				int day_of_week = standardCal.get(Calendar.DAY_OF_WEEK) - 1;// 判断是否周日
				if (day_of_week == 0) {
					day_of_week = 7;
				}
				standardCal.add(Calendar.DATE, -day_of_week + 1);
				firstDay = standardCal.getTime();
				standardCal.add(Calendar.DATE, 6);
				lastDay = standardCal.getTime();
				if (firstDay.compareTo(beginCal.getTime()) != 0 || lastDay.compareTo(endCal.getTime()) != 0) {
					lang = "【周报】的开始日期于结束日期必须是同一周的周一【" + sdf.format(firstDay) + "】和周日【" + sdf.format(lastDay) + "】!";
					multiLang.initLanguageResource(lang, "en",
							"[weekly] The start date of the weekly report must be the Monday of the same week["
									+ sdf.format(firstDay) + "]Sunday and [" + sdf.format(lastDay) + "]!");
					trigger.addErrorMessage(multiLang.translate(lang));
				}
			} else if (bglx.equals("月报")) {
				standardCal.set(Calendar.DAY_OF_MONTH, 1);// 设定月初
				firstDay = standardCal.getTime();
				standardCal.set(Calendar.DAY_OF_MONTH, standardCal.getActualMaximum(Calendar.DAY_OF_MONTH));// 设定月末
				lastDay = standardCal.getTime();
				if (firstDay.compareTo(beginCal.getTime()) != 0 || lastDay.compareTo(endCal.getTime()) != 0) {
					lang = "【月报】的开始日期于结束日期必须是同一月的月初【" + sdf.format(firstDay) + "】和月末【" + sdf.format(lastDay) + "】!";
					multiLang.initLanguageResource(lang, "en",
							"[monthly] report start date end date must be the same month is the first month ["
									+ sdf.format(firstDay) + "]The end of the month[" + sdf.format(lastDay) + "]!");
					trigger.addErrorMessage(multiLang.translate(lang));
				}
			}
			/**
			 *
			 * 工作报告重复校验
			 *
			 **/
			String cql = "";
			if (record_old == null) {// 新建
				cql = "select count(*) cnt from TheWorkReport where is_deleted = '0' and date_format(ksrq,'%Y-%m-%d') = '"
						+ ksrq + "' and date_format(jsrq,'%Y-%m-%d') = '" + jsrq + "' and ownerid = '" + ownerid
						+ "' and bglx = '" + bglx + "'";
			} else {// 编辑
				String id = record_new.get("id") == null ? "" : String.valueOf(record_new.get("id"));// 记录id
				cql = "select count(*) cnt from TheWorkReport where is_deleted = '0' and date_format(ksrq,'%Y-%m-%d') = '"
						+ ksrq + "' and date_format(jsrq,'%Y-%m-%d') = '" + jsrq + "' and ownerid = '" + ownerid
						+ "' and bglx = '" + bglx + "' and id <> '" + id + "'";
			}
			List<CCObject> list = this.cqlQuery("TheWorkReport", cql);
			if (null != list && list.size() > 0 && Integer.parseInt(String.valueOf(list.get(0).get("cnt"))) > 0) {
				lang = "您已创建了" + ksrq + "至" + jsrq + "的" + bglx + ",无需重复创建！";
				multiLang.initLanguageResource(lang, "en", "You have created it" + ksrq + "to" + jsrq + "The" + bglx
						+ ",No duplicate creation is required!");
				trigger.addErrorMessage(multiLang.translate(lang));

			}
			/**
			 * 自动带值上次计划
			 */
			String beginDate = "";
			String endDate = "";
			int dateType = Calendar.DATE;
			if ("日报".equals(bglx)) {
				dateType = Calendar.DAY_OF_YEAR;
			} else if ("周报".equals(bglx)) {
				dateType = Calendar.WEEK_OF_YEAR;
			} else if ("月报".equals(bglx)) {
				dateType = Calendar.MONTH;
			}
			beginCal.add(dateType, -1);
			beginDate = sdf.format(beginCal.getTime());
			endCal.add(dateType, -1);
			endDate = sdf.format(endCal.getTime());
			list = this.cquery("TheWorkReport",
					"date_format(ksrq,'%Y-%m-%d') = '" + beginDate + "' and date_format(jsrq,'%Y-%m-%d') = '" + endDate
							+ "' and ownerid = '" + ownerid + "' and bglx = '" + bglx + "' and pyzt <> '草稿'");

			if (null != list && list.size() > 0) {
				String xcjh = list.get(0).get("xcjh") == null ? "" : String.valueOf(list.get(0).get("xcjh"));// 下次计划
				if ("".equals(xcjh)) {
					lang = "上次报告中的下次计划未填写";
					multiLang.initLanguageResource(lang, "en",
							"The next plan in the last report has not been filled in");
					record_new.put("scjh", multiLang.translate(lang));
				} else {
					record_new.put("scjh", xcjh);
				}
			} else {
				lang = "没有提交上次报告";
				multiLang.initLanguageResource(lang, "en", "No last report was submitted");
				record_new.put("scjh", multiLang.translate(lang));
			}
		} catch (ParseException e) {
			if (true) {
				trigger.addErrorMessage(e.getMessage());
			}
		}