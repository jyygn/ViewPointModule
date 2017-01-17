package com.viewpoint.reader;

import com.letv.mobile.core.log.Logger;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImg;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImgGroup;
import com.letv.mobile.player.highlight.viewpoint.file.ViewPointsFile;

/**
 * Created by yanguannan on 16/9/8.
 */
public class TestViewPointReader {

    private String TAG = TestViewPointReader.class.getSimpleName();

    private boolean testReader(ViewPointsFile viewPointsFile) {
        String readerStr = "", forStr = "";
        ViewPointsReader viewPointsReader = new ViewPointsReader(viewPointsFile);
        Logger.d(TAG, "reader");
        int readerCount = 0;
        while (viewPointsReader.hasNext()) {
            ThumImg img = viewPointsReader.next();
            ++readerCount;
            readerStr += img.PointTime + "";
            Logger.d(TAG, "pointer " + viewPointsReader.getPointer() + "time " + img.PointTime);
        }

        int count = 0;
        Logger.d(TAG, "for ");
        for (int i = 0; i < viewPointsFile.getBigImgs().length; ++i) {
            ThumImgGroup thumImgGroup = viewPointsFile.getBigImgs()[i];
            int smallImgCount = 0;
            int pointTimeIndex = -1;
            for (int r = 1; r <= viewPointsFile.getThumRowLen(); r++) {
                for (int c = 1; c <= viewPointsFile.getThumColumnLen()
                        && smallImgCount < thumImgGroup.Count; c++) {

                    if (++smallImgCount > thumImgGroup.Count) {
                        break;
                    }
                    long time = thumImgGroup.PointTimes[++pointTimeIndex];
                    ++count;
                    Logger.d(TAG, "r " + r + " c " + c + " pointer " + pointTimeIndex + " time "
                            + time);
                    forStr += time + "";
                }
            }
        }
        Logger.d(TAG, readerCount + " is equals ........" + readerStr.equals(forStr) + " count = "
                + count);
        return readerStr.equals(forStr) && readerCount == count;
    }

    public void testReaderMoveTo(ViewPointsFile viewPointsFile) {
        ViewPointsReader viewPointsReader = new ViewPointsReader(viewPointsFile);
        Logger.d(TAG, "reader   move to group");
        ThumImgGroup thumImgGroup = viewPointsFile.getBigImgs()[viewPointsFile.getBigImgs().length - 1];
        viewPointsReader.moveToThumImgGroup(thumImgGroup);
        ThumImg img5408291 = null;
        while (viewPointsReader.hasNext()) {
            ThumImg img = viewPointsReader.next();
            if (img.PointTime == 5408291) {
                Logger.d("", "");
                img5408291 = img;
            }
            Logger.d(TAG, "pointer " + viewPointsReader.getPointer() + "time " + img.PointTime);
        }
        viewPointsReader.moveToThumImg(thumImgGroup, img5408291);
        if (viewPointsReader.hasNext()) {
            ThumImg temp = viewPointsReader.next();
            Logger.d(TAG, "temp pointer " + viewPointsReader.getPointer() + "time "
                    + temp.PointTime);
        }
    }
}
