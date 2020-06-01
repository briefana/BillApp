package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Utils {
    private static String TAG = "Utils:AJ";

    public static String doGet(String path) throws Exception {
        Log.i(TAG, "getHtml(),path=" + path);
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10 * 1000);
        InputStream inStream = conn.getInputStream();//通过输入流获取html数据
        byte[] data = readInputStream(inStream);//得到html的二进制数据
        String html = new String(data, "UTF-8");
        return html;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public static String doPost(String urlPath, Map<String, String> paramsMap) {
        String result = "";
        BufferedReader reader = null;
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-bocang-Authorization", "token"); //token可以是用户登录后的token等等......
            conn.setDoOutput(true);
            String parames = "";
            Log.i(TAG, "doPost(),paramsMap.size()=" + paramsMap.size());
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                parames += ("&" + entry.getKey() + "=" + entry.getValue());
            }
            Log.i(TAG, "doPost(),urlPath=" + urlPath + ",parames=" + parames);
            conn.getOutputStream().write(parames.substring(1).getBytes());
            int responseCode = conn.getResponseCode();
            Log.i(TAG, "doPost(),responseCode=" + responseCode);
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "doPost(),e=" + e);
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        Log.i(TAG, "doPost(),result=" + result);
        return result;
    }

    public static int doPostReturnResponseCode(String urlPath, Map<String, String> paramsMap) {
        String result = "";
        BufferedReader reader = null;
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-bocang-Authorization", "token"); //token可以是用户登录后的token等等......
            conn.setDoOutput(true);
            String parames = "";
            Log.i(TAG, "doPostReturnResponseCode(),paramsMap.size()=" + paramsMap.size());
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                parames += ("&" + entry.getKey() + "=" + entry.getValue());
            }
            Log.i(TAG, "doPostReturnResponseCode(),urlPath=" + urlPath + ",parames=" + parames);
            conn.getOutputStream().write(parames.substring(1).getBytes());
            int responseCode = conn.getResponseCode();
            Log.i(TAG, "doPostReturnResponseCode(),responseCode=" + responseCode);
            return responseCode;
        } catch (Exception e) {
            Log.i(TAG, "doPost(),e=" + e);
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return -1;
    }

    public static String getWeekStr(int weekIndex) {
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        return weeks[weekIndex];
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getDrawableId(String typeStr) {
        if (typeStr.equals("早午晚餐")) {
            return R.drawable.type_2;
        } else if (typeStr.equals("交通")) {
            return R.drawable.type_3;
        } else if (typeStr.equals("零食水果")) {
            return R.drawable.type_4;
        } else if (typeStr.equals("房租房贷")) {
            return R.drawable.type_5;
        } else if (typeStr.equals("服饰鞋包")) {
            return R.drawable.type_6;
        } else if (typeStr.equals("家居百货")) {
            return R.drawable.type_7;
        } else if (typeStr.equals("礼金红包")) {
            return R.drawable.type_8;
        } else if (typeStr.equals("医疗用品")) {
            return R.drawable.type_9;
        } else if (typeStr.equals("护肤美容")) {
            return R.drawable.type_10;
        } else if (typeStr.equals("话费网费")) {
            return R.drawable.type_11;
        } else if (typeStr.equals("娱乐游玩")) {
            return R.drawable.type_12;
        } else if (typeStr.equals("其他")) {
            return R.drawable.type_13;
        } else if (typeStr.equals("零碎未记")) {
            return R.drawable.type_14;
        } else {
            return R.drawable.type_13;
        }
    }

    public static String addZero(int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }

    /* 保留2位小数 */
    public static double keepTwoDecimal(double d) {
        BigDecimal bg = new BigDecimal(d);
        double d1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        Log.i(TAG, "keepTwoDecimal(),d=" + d + ",return d1=" + d1);
        return d1;
    }

    /* 保留2位小数 */
    public static String keepTwoDecimalStr(double d) {
        DecimalFormat df = new DecimalFormat(".00");
        String d1 = df.format(d);
        if (d1.startsWith(".")) {
            d1 = "0" + d1;
        }
        Log.i(TAG, "keepTwoDecimalStr(),d=" + d + ",return d1=" + d1);
        return d1;
    }

    public static String getNowDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constans.sFormatDate);
        Date today = new Date();  //当前日期
        return sdf.format(today);    // 2020/03/28
    }

    public static String url = "http://193.112.9.85:3000/";

    /*  1、查询所有用户信息。
    // http://193.112.9.85:3000/users ，返回结果如下
    [{"user_id":"an","user_name":"安静","password":"123456","familys":"my","expr_date":null},
    {"user_id":"cai","user_name":"蔡忠慧","password":"123456","familys":"my","expr_date":null}]
    */
    public static String getUsersPath() {
        return url + "users";
    }

    /*  2、查询所有支出分类信息。
        http://193.112.9.85:3000/types ，返回结果如下
       [{"type_id":2,"type_name":"早午晚餐","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":3,"type_name":"交通","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":4,"type_name":"零食水果","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":5,"type_name":"房租房贷","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":6,"type_name":"服饰鞋包","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":7,"type_name":"家居百货","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":8,"type_name":"礼金红包","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":9,"type_name":"医疗用品","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":10,"type_name":"护肤美容","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":11,"type_name":"话费网费","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":12,"type_name":"娱乐游玩","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":13,"type_name":"其他","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":14,"type_name":"零碎未记","expr_date":null,"is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
       {"type_id":15,"type_name":"工资薪水","expr_date":null,"is_consumption":0,"is_bring_into_assets":1,"is_incomes":1,"tab":1,"family_id":null},
       {"type_id":16,"type_name":"相互转账","expr_date":null,"is_consumption":0,"is_bring_into_assets":0,"is_incomes":0,"tab":2,"family_id":"my"},
       {"type_id":17,"type_name":"大额支出","expr_date":null,"is_consumption":0,"is_bring_into_assets":1,"is_incomes":0,"tab":2,"family_id":null},
       {"type_id":18,"type_name":"其他收入","expr_date":null,"is_consumption":0,"is_bring_into_assets":1,"is_incomes":1,"tab":1,"family_id":null}]
       */
    public static String getTypesPath() {
        return url + "types";
    }

    /*  5、添加一笔账单
    http://193.112.9.85:3000/，post请求，需另传 Map<String, String> 参数，
    */
    public static String getInsertPath() {
        return url;
    }

    /*  6、删除账单信息
    http://193.112.9.85:3000/deleteBill，删除指定bill_id的账目，post请求，需另传 Map<String, String> 参数，
    */
    public static String getDeletePath() {
        return url + "deleteBill";
    }

    /*  7、修改账单信息
    http://193.112.9.85:3000/updateBill，更新指定bill_id的账目，post请求，需另传 Map<String, String> 参数，
    */
    public static String getUpdatePath() {
        return url + "updateBill";
    }

    /*  8、查询账单明细。（后期加分页）
    family_id必传。month不传是获取所有的账单，传入后是获取某月的所有账目集合，例如  http://193.112.9.85:3000/showBillDetails?family_id=my&month=202002 ，返回结果
    {"bill_id":100,"time":"2020022960949","type_id":14,"money":"61.0","user_id":"cai","get_user_id":null,"note":"其他零碎未记","expr_date":null,"type_name":"零碎未记","is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
    {"bill_id":101,"time":"2020022850246","type_id":8,"money":"100","user_id":"cai","get_user_id":null,"note":"捐赠红十字会","expr_date":null,"type_name":"礼金红包","is_consumption":1,"is_bring_into_assets":1,"is_incomes":0,"tab":0,"family_id":null},
     ...]*/
    public static String getBillPathByMonth(String family_id, String month) {
        return url + "showBillDetails?family_id=" + family_id + "&month=" + month;
    }

    /*  9、查询年度账单信息
    获取现存所有月份的每月支出金额，http://193.112.9.85:3000/showBills?family_id=my ，返回结果如下
     [{"t":"202003","sumMoney":10219.77},
     {"t":"202002","sumMoney":5247.47},
     ...]*/
    public static String getMonthsPayPath(String family_id) {
        return url + "showBills?family_id=" + family_id;
    }

    /*  11、根据时间段统计账单分类消费
    获取时间段内如 20190901 到 20200229，或 20190901 到 20200229，各个类型的总支出，
     http://193.112.9.85:3000/showTypeDataByDate?family_id=my&startDate=20190901&endDate=20200229 ，返回结果
     http://193.112.9.85:3000/showTypeDataByDate?family_id=my&startDate=20190901&endDate=20200229&tab=0 ，返回结果
    [{"type_id":"5","type_name":"房租房贷","sumMoney":24174.19},
    {"type_id":"6","type_name":"服饰鞋包","sumMoney":8349.38},
    ...] */
    public static String showTypeDataByDatePath(String family_id, String startDate, String endDate) {
        return url + "showTypeDataByDate?family_id=" + family_id + "&startDate=" + startDate + "&endDate=" + endDate + "&tab=0";  //目前只分析支出
    }

    /*  12、根据时间段统计各月账单信息
    获取指定月份到指定月份的支出集合，http://193.112.9.85:3000/showMonthBillsByDate?family_id=my&startDate=201909&endDate=202002 ，返回结果
    获取指定月份到指定月份的支出集合，http://193.112.9.85:3000/showMonthBillsByDate?family_id=my&startDate=201909&endDate=202002&tab=0 ，返回结果
    [{"t":"202002","sumMoney":5247.47},
    {"t":"202001","sumMoney":13563.69},
    {"t":"201912","sumMoney":13436.76},
    {"t":"201911","sumMoney":10341.29},
    {"t":"201910","sumMoney":15247.02},
    {"t":"201909","sumMoney":15484.58}]
    */
    public static String getAppointMonthsPayPath(String family_id, String startDate, String endDate) {
        return url + "showMonthBillsByDate?family_id=" + family_id + "&startDate=" + startDate + "&endDate=" + endDate + "&tab=0";  //目前只分析支出
    }

    /*  13、根据时间段和类别查询详细账单
    获取时间段内指定类型的所有账目集合，http://193.112.9.85:3000/showTypeDetailsByDate?family_id=my&startDate=202002&endDate=202003&type_id=5
    [{"bill_id":1128,"time":"2020031242001","type_id":"5","money":"5400","user_id":"an","get_user_id":null,"note":"预交4.1--7.1这3个月的房租","expr_date":null},
    {"bill_id":1136,"time":"2020031020256","type_id":"5","money":"3018.47","user_id":"an","get_user_id":null,"note":"房贷","expr_date":null},
    ...] */
    public static String showTypeDetailsByDatePath(String family_id, String startDate, String endDate, int type_id) {
        return url + "showTypeDetailsByDate?family_id=" + family_id + "&startDate=" + startDate + "&endDate=" + endDate + "&type_id=" + type_id;
    }

    /*  14、查询家庭各成员余额
     http://193.112.9.85:3000/showUserBalance?family_id=my ，返回结果如下
    [{"user_id":"an","bmoney":14146.68},
    {"user_id":"cai","bmoney":2316.57}]
    */
    public static String getShowUserBalancePath(String family_id) {
        return url + "showUserBalance?family_id=" + family_id;
    }

    /*  15、查询上次设置家庭用户余额
    http://193.112.9.85:3000/showLastUsersBalance?family_id=my ,返回结果
    [{"user_id":"an","time":"2020040160545","balance":"41340.27","family_id":"my"},
    {"user_id":"cai","time":"2020040160545","balance":"3596.67","family_id":"my"}]
    */
    public static String getShowLastUsersBalancePath(String family_id) {
        return url + "showLastUsersBalance?family_id=" + family_id;
    }

    /*  16、修改家庭用户余额
    http://193.112.9.85:3000/updateUserBalance , post请求，需另传 Map<String, String> 参数，
    String family_id, String user_id, String time, String balance
    */
    public static String getUpdateUserBalancePath() {
        return url + "updateUserBalance";
    }

}
