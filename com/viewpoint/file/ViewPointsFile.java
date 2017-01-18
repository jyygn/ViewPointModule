/**
 *
 */
package com.viewpoint.file;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.letv.mobile.core.log.Logger;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImgGroup;

/**
 * 视点图文件
 * @author yangn
 */
public class ViewPointsFile {

    private String TAG = ViewPointsFile.class.getSimpleName();
    private String mFromUrl;
    // 视点图数据
    private JSONObject mData = null;
    // 小图宽 高
    private int mThumWidth, mThumHeigh;

    // 小图所在大图行列数
    private int mThumRowLen, mThumColumnLen;

    // 大图信息 包含大图时间点等
    private ThumImgGroup[] mBigImgs = null;

    // 影片各张大图的起始时间和结束时间（单位毫秒）
    private int[][] mBigImgBrief = null;
    // 视频唯一ID
    private String mVideoId = "TestvideoId";
    // 小图总数量
    private int mThumImgTotalCount = 0;
    // 总毫秒数量
    private int mTotalTime = 0;
    private int mPreviouseSelectThumImgGroupIndex = 0;

    // String jsonDataBack =
    // "{\"w\":200,\"h\":112,\"c\":10,\"r\":6,\"bigimg\":[{\"step\":18000,\"item\":[200,8459,10500,41,10749,14418,1251,1542,0,1332,1667,791,11917,14626,16417,999,1791,14792,13792,1542,1291,13251,13418,1459,500,10667,18292,8500,1209,17250,667,9250,12626,14001,1459,833,16875,751,17665,12542,16583,12708,17834,1208,1208,1417,1500,1667,1000,624,708,18332,18708,876,2000,17666,15084,459,1667,18875],\"start\":200,\"end\":1511456,\"count\":60},{\"step\":18000,\"item\":[1530415,916,1917,16957,1792,1208,917,1500,541,14625,1082,1791,17543,15249,1750,12750,14918,833,1542,10001,19000,14541,11125,14042,1626,667,458,15501,1459,16667,1875,14126,1459,792,15917,18791,17624,11375,17834,17832,999,333,1583,1251,14625,14584,876,1543,10709,14834,9292,500,499,17958,917,15043,1209,1376,13709,1583],\"start\":1530415,\"end\":3046381,\"count\":60},{\"step\":18000,\"item\":[3077881,17833,17790,1001,834,917,16543,15458,12333,9500,17250,15000,1583,15332,14915,14499,10709,16167,11291,12709,792,10084,18334,209,17708,18792,584,1625,11416,1375,9000,1833,16293,1833,1625,1835,19418,17792,1792,14333,1167,15375,17001,1041,1542,16792,1166,1666,9166,14750,584,17709,542,14001,18792,18540,792,19252,417,1416],\"start\":3077881,\"end\":4699929,\"count\":60},{\"step\":18000,\"item\":[4719430,1918,500,1791,9833,959,17001,1375,1334,1666,875,18415,1375,417,14626,667,14291,917,16667,1165,1042,750,15709,1916,15917,3083,1708,1584,1250,874,1959,10625,15250,19667],\"start\":4719430,\"end\":5510556,\"count\":34}],\"brief\":{\"item\":[{\"s\":200,\"e\":1511456},{\"s\":1530415,\"e\":3046381},{\"s\":3077881,\"e\":4699929},{\"s\":4719430,\"e\":5510556}]}}";
    // String jsonData =
    // "{\"w\":200,\"h\":112,\"c\":10,\"r\":6,\"bigimg\":[{\"step\":18000,\"item\":[0,17760,1920,-1200,-800,-480,880,-4760,-3760,-1320,-320,-4000,-1800,-120,-3720,-400,640,-6920,1880,1760,1560,-4760,2000,280,-2240,1920,240,1920,-3080,-1960,1960,1920,80,1920,80,1920,-2760,1880,1920,-6440,1920,1960,-760,1920,1920,1680,1920,-1600,-7400,-880,1920,1200,2000,1800,-2080,1880,-640,-1640,1560,800],\"start\":0,\"end\":1063080,\"count\":60},{\"step\":18000,\"item\":[1081160,1920,-6880,1920,-1600,-40,1920,1600,-2560,-4440,-4080,-680,-4240,1960,200,-560,520,-2040,-960,1960,1040,1920,280,200,1120,-800,-2960,-2920,1920,-3360,-3440,240,1480,840,40,1080,-2640,-1920,560,-680,-3520,-960,720,-6080,-3080,960,-1640,-1920,-1560,360,-2080,-3840,1320,-1320,1720,-3440,-4720,1960,280,-2200],\"start\":1081160,\"end\":2090040,\"count\":60},{\"step\":18000,\"item\":[2103400,1920,-1640,-5280,-4600,1240,1920,-6840,1920,-4080,1920,-5360,400,680,-1880,240,1760,-1160,-160,-5160,1520,-1080,-1440,960,240,-4280,-6280,-5800,1560,1040,120,-3640,-3840,-720,1440,-6440,-4720,-1120,680,-5720,-5640,400,-4480,-560,600,520,-5600,-40,1880,-2320,-3200,1800,-840,-3760,-760,760,-640,-1000,-3000,-6760],\"start\":2103400,\"end\":3071080,\"count\":60},{\"step\":18000,\"item\":[3084000,640,40,-5400,-1360,1920,-4640,-2000,-6360,-5040,-920,1160,480,-2080,0,-3000,200,1360,-4560,1320,-1640,-2040,-1960,-800,1880,-3160,680,-3360,-3280,1200,-720,1960,1920,1440,1640,1320,-80,-2680,1920,-5080,-6000,1040],\"start\":3084000,\"end\":3777960,\"count\":42}],\"brief\":{\"item\":[{\"s\":0,\"e\":1063080},{\"s\":1081160,\"e\":2090040},{\"s\":2103400,\"e\":3071080},{\"s\":3084000,\"e\":3777960}]}}";

    public ViewPointsFile(String descJson, String videoId, String fromUrl) throws JSONException {
        if (null == descJson || "".equals(descJson)) {
            throw new IllegalArgumentException("video point data is null");
        }
        this.mData = new JSONObject(descJson);
        if (null == this.mData) {
            throw new NullPointerException("video point data is null");
        }
        this.mVideoId = videoId;
        this.mFromUrl = fromUrl;
        this.mThumWidth = this.getJsonInt("w");
        this.mThumHeigh = this.getJsonInt("h");
        this.mThumRowLen = this.getJsonInt("r");
        this.mThumColumnLen = this.getJsonInt("c");

        this.mBigImgBrief = this.getBigImgOfStartAndEndTime(this.mData.getJSONObject("brief"));
        this.mBigImgs = this.getBigImg();
    }

    public static ViewPointsFile createNewFile(String descJson, String videoId, String fromUrl)
            throws JSONException {
        return new ViewPointsFile(descJson, videoId, fromUrl);
    }

    int getJsonInt(String key) throws JSONException {
        if (this.mData.has(key)) {
            return this.mData.getInt(key);
        }
        throw new NullPointerException(key + " is  null");
    }

    /**
     * 获取一组大图对应的开始结束时间
     * @param brief
     * @return[0]开始时间【1】结束时间
     * @throws JSONException
     */
    int[][] getBigImgOfStartAndEndTime(JSONObject obj) throws JSONException {
        // brief =
        // "{\"item\":[{\"s\":80,\"e\":1856640},{\"s\":1877560,\"e\":3741120},{\"s\":3768480,\"e\":5608280},{\"s\":5631200,\"e\":6124720}}";
        // JSONObject obj = new JSONObject(brief);
        JSONArray array = obj.getJSONArray("item");
        int[][] table = new int[array.length()][2];
        JSONObject item = null;
        for (int i = 0; i < array.length(); i++) {
            item = array.getJSONObject(i);
            table[i][0] = item.getInt("s");
            table[i][1] = item.getInt("e");
        }
        this.mTotalTime = table[array.length() - 1][1];
        return table;
    }

    /**
     * 获取大图每一帧小图对应时间戳数组
     * @param obj
     * @return
     * @throws JSONException
     */
    ThumImgGroup getBitImgOfTimePoints(JSONObject obj) throws JSONException {
        // bigImgInfo =
        // "{\"step\":20490,\"item\":[5631200,1070,9510,9510,9510,9510,9510,9510,9510,9510,9510,9510,9510,9510,9510,9510,9510,1470],\"start\":5631200,\"end\":6124720,\"count\":18}";
        // JSONObject obj = new JSONObject(bigImgInfo);
        int step = obj.getInt("step");
        JSONArray array = obj.getJSONArray("item");
        int start = obj.getInt("start");
        int end = obj.getInt("end");
        int count = obj.getInt("count");
        int[] pts = new int[count];

        pts[0] = array.getInt(0);
        for (int i = 1; i < count; i++) {
            pts[i] = pts[i - 1] + step + array.getInt(i);

        }
        return new ThumImgGroup(step, pts, start, end, count);
    }

    /**
     * 获取小图总数量
     * @return
     */

    public int getThumImgTotalCount() {
        return this.mThumImgTotalCount;
    }

    /**
     * 获取总毫秒数
     * @return
     */

    public int getTotalTime() {
        return this.mTotalTime;
    }

    /**
     * 获取所包含大图信息
     * @return
     * @throws JSONException
     */
    ThumImgGroup[] getBigImg() throws JSONException {
        if (!this.mData.has("bigimg")) {
            throw new NullPointerException(" bigimg is null");
        }

        JSONArray bigImgArray = this.mData.getJSONArray("bigimg");

        if (null == bigImgArray || bigImgArray.length() == 0) {
            throw new NullPointerException(" bigimg length is zero");
        }

        // JSONObject obj = null;

        ThumImgGroup[] bigImgs = new ThumImgGroup[bigImgArray.length()];

        for (int i = 0; i < bigImgArray.length(); i++) {

            bigImgs[i] = this.getBitImgOfTimePoints(bigImgArray.getJSONObject(i));
            this.mThumImgTotalCount += bigImgs[i].Count;
            bigImgs[i].setVideoId(this.mVideoId);
            bigImgs[i].setBigImgListIndex(i);
            // bigImg=new BigImg(obj.getInt("step"), pointTimes, start, end,
            // count);
        }

        return bigImgs;
    }

    /**
     * @return the mThumWidth
     */
    public int getThumWidth() {
        return this.mThumWidth;
    }

    /**
     * @return the mThumHeigh
     */
    public int getThumHeigh() {
        return this.mThumHeigh;
    }

    /**
     * @return the mThumRowLen
     */
    public int getThumRowLen() {
        return this.mThumRowLen;
    }

    /**
     * @return the mThumColumnLen
     */
    public int getThumColumnLen() {
        return this.mThumColumnLen;
    }

    /**
     * @return the mVideoId
     */
    public String getVideoId() {
        return this.mVideoId;
    }

    /**
     * @return the mBigImgs
     */
    public ThumImgGroup[] getBigImgs() {
        return this.mBigImgs;
    }

    /**
     * @return the mBigImgBrief
     */
    public int[][] getBigImgBrief() {
        return this.mBigImgBrief;
    }

    public String getFromUrl() {
        return this.mFromUrl;
    }

    /**
     * 查询pointTime是否所在index缩略图组 存在返回所在缩略图组 否则返回null
     * @param index
     * @param pointTime
     * @return 未查到返回null
     */
    public ThumImgGroup indexOfThumImgGroup(int index, int pointTime) {
        if (this.getBigImgBrief()[index][0] <= pointTime
                && this.getBigImgBrief()[index][1] >= pointTime) {
            return this.getBigImgs()[index];
        } else {
            return null;
        }
    }

    public ThumImgGroup searchThumImgGroupByPointTime(int pointTime) {
        // 检查是否与上一次所在同一张大图范围
        ThumImgGroup thumImgGroup = this.indexOfThumImgGroup(
                this.mPreviouseSelectThumImgGroupIndex, pointTime);
        if (null == thumImgGroup) {
            // TODO binary search
            // 查找该pointTime所在大图
            int i = this.mPreviouseSelectThumImgGroupIndex;
            int count = this.getBigImgBrief().length;
            boolean isReverse = this.mPreviouseSelectThumImgGroupIndex > pointTime;
            if (isReverse) {
                count = this.mPreviouseSelectThumImgGroupIndex;
                i = 0;
            }
            while (i < count) {
                thumImgGroup = this.indexOfThumImgGroup(isReverse ? count - 1 - i : i, pointTime);
                if (null != thumImgGroup) {
                    this.mPreviouseSelectThumImgGroupIndex = i;
                    break;
                }
                i++;
            }

            // case check thumImgGroup is null
            if (null == thumImgGroup) {
                Logger.d(TAG, "need  check");
                if (isReverse) {
                    i = count;
                    count = this.getBigImgBrief().length;
                } else {
                    count = this.mPreviouseSelectThumImgGroupIndex;
                    i = 0;
                }

                isReverse = !isReverse;
                while (i < count) {
                    thumImgGroup = this.indexOfThumImgGroup(isReverse ? count - 1 - i : i,
                            pointTime);
                    if (null != thumImgGroup) {
                        this.mPreviouseSelectThumImgGroupIndex = i;
                        break;
                    }
                    i++;
                }
            }
        }
        return thumImgGroup;
    }
}
