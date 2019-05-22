package DateTimePickDialogUtil;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;


public class DateTimeControl {
	/**
	  * 获取现在时间
	  * 
	  * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	  */
	public static Date getNowDate() {
	  Date currentTime = new Date();
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String dateString = formatter.format(currentTime);
	  ParsePosition pos = new ParsePosition(8);
	  Date currentTime_2 = formatter.parse(dateString, pos);
	  return currentTime_2;
	}
	 
	/**
	  * 获取现在时间
	  * 
	  * @return返回短时间格�?yyyy-MM-dd
	  */
	public static Date getNowDateShort() {
	  Date currentTime = new Date();
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	  String dateString = formatter.format(currentTime);
	  ParsePosition pos = new ParsePosition(8);
	  Date currentTime_2 = formatter.parse(dateString, pos);
	  return currentTime_2;
	}
	 
	/**
	  * 获取现在时间
	  * 
	  * @return返回字符串格�?yyyy-MM-dd HH:mm:ss
	  */
	public static String getStringDate() {
	  Date currentTime = new Date();
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String dateString = formatter.format(currentTime);
	  return dateString;
	}
	 
	/**
	  * 获取现在时间
	  * 
	  * @return 返回短时间字符串格式yyyy-MM-dd
	  */
	public static String getStringDateShort() {
	  Date currentTime = new Date();
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	  String dateString = formatter.format(currentTime);
	  return dateString;
	}
	 
	/**
	  * 获取时间 小时:�?�?HH:mm:ss
	  * 
	  * @return
	  */
	public static String getTimeShort() {
	  SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	  Date currentTime = new Date();
	  String dateString = formatter.format(currentTime);
	  return dateString;
	}
	 
	/**
	  * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	  * 
	  * @param strDate
	  * @return
	  */
	public static Date strToDateLong(String strDate) {
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  ParsePosition pos = new ParsePosition(0);
	  Date strtodate = formatter.parse(strDate, pos);
	  return strtodate;
	}
	 
	/**  * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss  *   * @param dateDate  * @return  */
	public static String dateToStrLong(java.util.Date dateDate) {
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String dateString = formatter.format(dateDate);
	  return dateString;
	}
	 
	/**
	  * 将短时间格式时间转换为字符串 yyyy-MM-dd
	  * 
	  * @param dateDate
	  * @param k
	  * @return
	  */
	public static String dateToStr(java.util.Date dateDate) {
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	  String dateString = formatter.format(dateDate);
	  return dateString;
	}
	 
	/**
	  * 将短时间格式字符串转换为时间 yyyy-MM-dd 
	  * 
	  * @param strDate
	  * @return
	  */
	public static Date strToDate(String strDate) {
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	  ParsePosition pos = new ParsePosition(0);
	  Date strtodate = formatter.parse(strDate, pos);
	  return strtodate;
	}
	 
	/**
	  * 得到现在时间
	  * 
	  * @return
	  */
	public static Date getNow() {
	  Date currentTime = new Date();
	  return currentTime;
	}
	 
	/**
	  * 提取�?��月中的最后一�?
	  * 
	  * @param day
	  * @return
	  */
	public static Date getLastDate(long day) {
	  Date date = new Date();
	  long date_3_hm = date.getTime() - 3600000 * 34 * day;
	  Date date_3_hm_date = new Date(date_3_hm);
	  return date_3_hm_date;
	}
	 
	/**
	  * 得到现在时间
	  * 
	  * @return 字符�?yyyyMMdd HHmmss
	  */
	public static String getStringToday() {
	  Date currentTime = new Date();
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
	  String dateString = formatter.format(currentTime);
	  return dateString;
	}
	 
	/**
	  * 得到现在小时
	  */
	public static String getHour() {
	  Date currentTime = new Date();
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String dateString = formatter.format(currentTime);
	  String hour;
	  hour = dateString.substring(11, 13);
	  return hour;
	}
	 
	/**
	  * 得到现在分钟
	  * 
	  * @return
	  */
	public static String getTime() {
	  Date currentTime = new Date();
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String dateString = formatter.format(currentTime);
	  String min;
	  min = dateString.substring(14, 16);
	  return min;
	}
	 
	/**
	  * 根据用户传入的时间表示格式，返回当前时间的格�?如果是yyyyMMdd，注意字母y不能大写�?
	  * 
	  * @param sformat
	  *            yyyyMMddhhmmss
	  * @return
	  */
	public static String getUserDate(String sformat) {
	  Date currentTime = new Date();
	  SimpleDateFormat formatter = new SimpleDateFormat(sformat);
	  String dateString = formatter.format(currentTime);
	  return dateString;
	}
	 
	/**
	  * 二个小时时间间的差�?,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
	  */
	public static String getTwoHour(String st1, String st2) {
	  String[] kk = null;
	  String[] jj = null;
	  kk = st1.split(":");
	  jj = st2.split(":");
	  if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
	   return "0";
	  else {
	   double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
	   double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
	   if ((y - u) > 0)
	    return y - u + "";
	   else
	    return "0";
	  }
	}
	 
	/**
	  * 得到二个日期间的间隔天数
	  */
	public static String getTwoDay(String sj1, String sj2) {
	  SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
	  long day = 0;
	  try {
	   java.util.Date date = myFormatter.parse(sj1);
	   java.util.Date mydate = myFormatter.parse(sj2);
	   day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
	  } catch (Exception e) {
	   return "";
	  }
	  return day + "";
	}
	 
	/**
	  * 时间前推或后推分�?其中JJ表示分钟.
	  */
	public static String getPreTime(String sj1, String jj) {
	  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String mydate1 = "";
	  try {
	   Date date1 = format.parse(sj1);
	   long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
	   date1.setTime(Time * 1000);
	   mydate1 = format.format(date1);
	  } catch (Exception e) {
	  }
	  return mydate1;
	}
	 
	/**
	  * 得到�?��时间延后或前移几天的时间,nowdate为时�?delay为前移或后延的天�?
	  */
	public static String getNextDay(String nowdate, String delay) {
	  try{
	  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	  String mdate = "";
	  Date d = strToDate(nowdate);
	  long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
	  d.setTime(myTime * 1000);
	  mdate = format.format(d);
	  return mdate;
	  }catch(Exception e){
	   return "";
	  }
	}
	 
	/**
	  * 判断是否润年
	  * 
	  * @param ddate
	  * @return
	  */
	public static boolean isLeapYear(String ddate) {
	 
	  /**
	   * 详细设计�?1.�?00整除是闰年，否则�?2.不能�?整除则不是闰�?3.能被4整除同时不能�?00整除则是闰年
	   * 3.能被4整除同时能被100整除则不是闰�?
	   */
	  Date d = strToDate(ddate);
	  GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
	  gc.setTime(d);
	  int year = gc.get(Calendar.YEAR);
	  if ((year % 400) == 0)
	   return true;
	  else if ((year % 4) == 0) {
	   if ((year % 100) == 0)
	    return false;
	   else
	    return true;
	  } else
	   return false;
	}
	 
	/**
	  * 返回美国时间格式 26 Apr 2006
	  * 
	  * @param str
	  * @return
	  */
	public static String getEDate(String str) {
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	  ParsePosition pos = new ParsePosition(0);
	  Date strtodate = formatter.parse(str, pos);
	  String j = strtodate.toString();
	  String[] k = j.split(" ");
	  return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
	}
	 
	/**
	  * 获取�?��月的�?���?��
	  * 
	  * @param dat
	  * @return
	  */
	public static String getEndDateOfMonth(String dat) {// yyyy-MM-dd
	  String str = dat.substring(0, 8);
	  String month = dat.substring(5, 7);
	  int mon = Integer.parseInt(month);
	  if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
	   str += "31";
	  } else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
	   str += "30";
	  } else {
	   if (isLeapYear(dat)) {
	    str += "29";
	   } else {
	    str += "28";
	   }
	  }
	  return str;
	}
	 
	/**
	  * 判断二个时间是否在同�?���?
	  * 
	  * @param date1
	  * @param date2
	  * @return
	  */
	public static boolean isSameWeekDates(Date date1, Date date2) {
	  Calendar cal1 = Calendar.getInstance();
	  Calendar cal2 = Calendar.getInstance();
	  cal1.setTime(date1);
	  cal2.setTime(date2);
	  int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
	  if (0 == subYear) {
	   if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
	    return true;
	  } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
	   // 如果12月的�?���?��横跨来年第一周的话则�?���?��即算做来年的第一�?
	   if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
	    return true;
	  } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
	   if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
	    return true;
	  }
	  return false;
	}
	 
	/**
	  * 产生周序�?即得到当前时间所在的年度是第几周
	  * 
	  * @return
	  */
	public static String getSeqWeek() {
	  Calendar c = Calendar.getInstance(Locale.CHINA);
	  String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
	  if (week.length() == 1)
	   week = "0" + week;
	  String year = Integer.toString(c.get(Calendar.YEAR));
	  return year + week;
	}
	 
	/**
	  * 获得�?��日期�?��的周的星期几的日期，如要找出2002�?�?日所在周的星期一是几�?
	  * 
	  * @param sdate
	  * @param num
	  * @return
	  */
	/*public static String getWeek(String sdate, String num) {
	  // 再转换为时间
	  Date dd = VeDate.strToDate(sdate);
	  Calendar c = Calendar.getInstance();
	  c.setTime(dd);
	  if (num.equals("1")) // 返回星期�?��在的日期
	   c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	  else if (num.equals("2")) // 返回星期二所在的日期
	   c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
	  else if (num.equals("3")) // 返回星期三所在的日期
	   c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
	  else if (num.equals("4")) // 返回星期四所在的日期
	   c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
	  else if (num.equals("5")) // 返回星期五所在的日期
	   c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
	  else if (num.equals("6")) // 返回星期六所在的日期
	   c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
	  else if (num.equals("0")) // 返回星期日所在的日期
	   c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	  return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}
	 
	/**
	  * 根据�?��日期，返回是星期几的字符�?
	  * 
	  * @param sdate
	  * @return
	  */
	/*public static String getWeek(String sdate) {
	  // 再转换为时间
	  Date date = VeDate.strToDate(sdate);
	  Calendar c = Calendar.getInstance();
	  c.setTime(date);
	  // int hour=c.get(Calendar.DAY_OF_WEEK);
	  // hour中存的就是星期几了，其范�?1~7
	  // 1=星期�?7=星期六，其他类推
	  return new SimpleDateFormat("EEEE").format(c.getTime());
	}
	public static String getWeekStr(String sdate){
	  String str = "";
	  str = VeDate.getWeek(sdate);
	  if("1".equals(str)){
	   str = "星期�?;
	  }else if("2".equals(str)){
	   str = "星期�?;
	  }else if("3".equals(str)){
	   str = "星期�?;
	  }else if("4".equals(str)){
	   str = "星期�?;
	  }else if("5".equals(str)){
	   str = "星期�?;
	  }else if("6".equals(str)){
	   str = "星期�?;
	  }else if("7".equals(str)){
	   str = "星期�?;
	  }
	  return str;
	}
	 
	/**
	  * 两个时间之间的天�?
	  * 
	  * @param date1
	  * @param date2
	  * @return
	  */
	public static long getDays(String date1, String date2) {
	  if (date1 == null || date1.equals(""))
	   return 0;
	  if (date2 == null || date2.equals(""))
	   return 0;
	  // 转换为标准时�?
	  SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
	  java.util.Date date = null;
	  java.util.Date mydate = null;
	  try {
	   date = myFormatter.parse(date1);
	   mydate = myFormatter.parse(date2);
	  } catch (Exception e) {
	  }
	  long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
	  return day;
	}
	 
	/**
	  * 形成如下的日�?�?根据传入的一个时间返回一个结�?星期�?星期�?星期�?星期�?星期�?星期�?星期�?下面是当月的各个时间
	  * 此函数返回该日历第一行星期日�?��的日�?
	  * 
	  * @param sdate
	  * @return
	  */
	/*public static String getNowMonth(String sdate) {
	  // 取该时间�?��月的�?��
	  sdate = sdate.substring(0, 8) + "01";
	 
	  // 得到这个月的1号是星期�?
	  Date date = VeDate.strToDate(sdate);
	  Calendar c = Calendar.getInstance();
	  c.setTime(date);
	  int u = c.get(Calendar.DAY_OF_WEEK);
	  String newday = VeDate.getNextDay(sdate, (1 - u) + "");
	  return newday;
	}
	 
	/**
	  * 取得数据库主�?生成格式为yyyymmddhhmmss+k位随机数
	  * 
	  * @param k
	  *            表示是取几位随机数，可以自己�?
	  */
	 
	public static String getNo(int k) {
	 
	  return getUserDate("yyyyMMddhhmmss") + getRandom(k);
	}
	 
	/**
	  * 返回�?��随机�?
	  * 
	  * @param i
	  * @return
	  */
	public static String getRandom(int i) {
	  Random jjj = new Random();
	  // int suiJiShu = jjj.nextInt(9);
	  if (i == 0)
	   return "";
	  String jj = "";
	  for (int k = 0; k < i; k++) {
	   jj = jj + jjj.nextInt(9);
	  }
	  return jj;
	}
	 
	/***************************************************************************
	  * //nd=1表示返回的�?中包含年�?//yf=1表示返回的�?中包含月�?//rq=1表示返回的�?中包含日�?//format表示返回的格�?1
	  * 以年月日中文返回 2 以横�?返回 // 3 以斜�?返回 4 以缩写不带其它符号形式返�?// 5 以点�?返回
	  **************************************************************************/
	/*public static String getStringDateMonth(String sdate, String nd, String yf, String rq, String format) {
	  Date currentTime = new Date();
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	  String dateString = formatter.format(currentTime);
	  String s_nd = dateString.substring(0, 4); // 年份
	  String s_yf = dateString.substring(5, 7); // 月份
	  String s_rq = dateString.substring(8, 10); // 日期
	  String sreturn = "";
	  roc.util.MyChar mc = new roc.util.MyChar();
	  if (sdate == null || sdate.equals("") || !mc.Isdate(sdate)) { // 处理空�?情况
	   if (nd.equals("1")) {
	    sreturn = s_nd;
	    // 处理间隔�?
	    if (format.equals("1"))
	     sreturn = sreturn + "�?;
	    else if (format.equals("2"))
	     sreturn = sreturn + "-";
	    else if (format.equals("3"))
	     sreturn = sreturn + "/";
	    else if (format.equals("5"))
	     sreturn = sreturn + ".";
	   }
	   // 处理月份
	   if (yf.equals("1")) {
	    sreturn = sreturn + s_yf;
	    if (format.equals("1"))
	     sreturn = sreturn + "�?;
	    else if (format.equals("2"))
	     sreturn = sreturn + "-";
	    else if (format.equals("3"))
	     sreturn = sreturn + "/";
	    else if (format.equals("5"))
	     sreturn = sreturn + ".";
	   }
	   // 处理日期
	   if (rq.equals("1")) {
	    sreturn = sreturn + s_rq;
	    if (format.equals("1"))
	     sreturn = sreturn + "�?;
	   }
	  } else {
	   // 不是空�?，也是一个合法的日期值，则先将其转换为标准的时间格式
	   sdate = roc.util.RocDate.getOKDate(sdate);
	   s_nd = sdate.substring(0, 4); // 年份
	   s_yf = sdate.substring(5, 7); // 月份
	   s_rq = sdate.substring(8, 10); // 日期
	   if (nd.equals("1")) {
	    sreturn = s_nd;
	    // 处理间隔�?
	    if (format.equals("1"))
	     sreturn = sreturn + "�?;
	    else if (format.equals("2"))
	     sreturn = sreturn + "-";
	    else if (format.equals("3"))
	     sreturn = sreturn + "/";
	    else if (format.equals("5"))
	     sreturn = sreturn + ".";
	   }
	   // 处理月份
	   if (yf.equals("1")) {
	    sreturn = sreturn + s_yf;
	    if (format.equals("1"))
	     sreturn = sreturn + "�?;
	    else if (format.equals("2"))
	     sreturn = sreturn + "-";
	    else if (format.equals("3"))
	     sreturn = sreturn + "/";
	    else if (format.equals("5"))
	     sreturn = sreturn + ".";
	   }
	   // 处理日期
	   if (rq.equals("1")) {
	    sreturn = sreturn + s_rq;
	    if (format.equals("1"))
	     sreturn = sreturn + "�?;
	   }
	  }
	  return sreturn;
	}*/

}
