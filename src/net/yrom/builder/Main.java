/*
 * Copyright (C) 2013 Yrom <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.yrom.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import net.yrom.builder.util.ManifestParser;

/*
 * implementation of http://blog.csdn.net/t12x3456/article/details/7957117
 */
public class Main {

    /**
     * 项目路径
     */
    private static String   PROJECT_BASE_PATH;
    /**
     * apk保存路径
     */
    private static String   APK_SAVE_PATH;
    /**
     * 生成的apk名称
     */
    private static String   APK_FILE_NAME;
    /**
     * 重命名apk的前缀
     */
    private static String   RENAME_PREFIX;
    private static String   VERSION_NAME;
    private static int      VERSION_CODE;
    /**
     * 统计渠道 umeng、baidu (TODO 支持多个渠道)
     */
    private static String   CHANNEL_NAME;
    /**
     * 渠道数组(TODO)
     */
    private static String[] CHANNEL_VALUES;
    static {
        Properties p = new Properties();
        Reader reader;
        try {
            reader = new FileReader("config.properties");
            p.load(reader);
            PROJECT_BASE_PATH = p.getProperty("projectBasePath");
            APK_SAVE_PATH = p.getProperty("savePath");
            APK_FILE_NAME = p.getProperty("fileName");
            RENAME_PREFIX = p.getProperty("renamePrefix");
            VERSION_NAME = p.getProperty("newVersionName");
            String code = p.getProperty("newVersionCode");
            VERSION_CODE = code == null ? 0 : Integer.parseInt(code);
            CHANNEL_NAME = p.getProperty("channelName");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {
        File manifestFile = new File(PROJECT_BASE_PATH, "AndroidManifest.xml");
        File saveFolder = new File(APK_SAVE_PATH);
        saveFolder.mkdirs();
        long startTime = 0L;
        long endTime = 0L;
        long totalTime = 0L;
        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
        try {

            System.out.println("---------ant批量自动化打包开始----------");
            startTime = System.currentTimeMillis();
            date.setTimeInMillis(startTime);
            System.out.println("开始时间为:" + sdf.format(date.getTime()));

            ManifestParser parser = new ManifestParser(manifestFile.getAbsolutePath());
            parser.newVersionInfo(VERSION_CODE, VERSION_NAME);
            if(VERSION_CODE == 0) VERSION_CODE = Integer.parseInt(parser.getVersionCode());
            // TODO 用CHANNEL_VALUES替代
            BufferedReader br = new BufferedReader(new FileReader("market.txt"));
            String flag = null;
            while ((flag = br.readLine()) != null) {
                if (flag.contains("#"))
                    continue;
                // 先修改manifest文件:读取临时文件中的占位符修改为市场标识,然后写入manifest.xml中
                parser.replaceMetaData(CHANNEL_NAME, flag);
                parser.write();
                // 执行打包命令
                AntBuilder builder = new AntBuilder(PROJECT_BASE_PATH + File.separator + "build.xml", PROJECT_BASE_PATH);
                builder.runTarget("clean");
                builder.runTarget("release");
                // 打完包后执行重命名加拷贝操作
                File apk = new File(PROJECT_BASE_PATH + File.separator + "bin" + File.separator + APK_FILE_NAME);// bin目录下签名的apk文件
                File releaseFile = new File(saveFolder, RENAME_PREFIX + VERSION_CODE + "_" + flag + ".apk");
                FileUtils.copyFile(apk, releaseFile);
                System.out.println("file ------>" + apk.getAbsolutePath());
                System.out.println("rename------>" + releaseFile.getAbsolutePath());
            }
            br.close();
            System.out.println("---------ant批量自动化打包结束----------");
            endTime = System.currentTimeMillis();
            date.setTimeInMillis(endTime);
            System.out.println("结束时间为:" + sdf.format(date.getTime()));
            totalTime = endTime - startTime;
            System.out.println("耗费时间为:" + getConsumedDate(totalTime));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("---------ant批量自动化打包中发生异常----------");
            endTime = System.currentTimeMillis();
            date.setTimeInMillis(endTime);
            System.out.println("发生异常时间为:" + sdf.format(date.getTime()));
            totalTime = endTime - startTime;
            System.out.println("耗费时间为:" + getConsumedDate(totalTime));
        }
    }

    /**
     * 根据所秒数,计算相差的时间并以**时**分**秒返回
     * 
     * @param d1
     * @param d2
     * @return
     */
    public static String getConsumedDate(long m) {
        m = m / 1000;
        String date = "";
        int nDay = (int) m / (24 * 60 * 60);
        int nHour = (int) (m - nDay * 24 * 60 * 60) / (60 * 60);
        int nMinute = (int) (m - nDay * 24 * 60 * 60 - nHour * 60 * 60) / 60;
        int nSecond = (int) m - nDay * 24 * 60 * 60 - nHour * 60 * 60 - nMinute * 60;
        date = nDay + "天" + nHour + "小时" + nMinute + "分" + nSecond + "秒";

        return date;
    }
}
