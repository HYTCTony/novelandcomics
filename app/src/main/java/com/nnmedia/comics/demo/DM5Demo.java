package com.nnmedia.comics.demo;

import android.content.Context;
import android.util.Log;

import com.nnmedia.comics.utils.DecryptionUtils;
import com.nnmedia.comics.utils.Node;
import com.nnmedia.comics.utils.StringUtils;
import com.nnmedia.novel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 动漫屋（DM5）Demo
 * 功能：搜索漫画 → 获取详情 → 获取章节 → 获取图片
 * 
 * 使用方法：
 * DM5Demo demo = new DM5Demo(context);
 * demo.runDemo();
 */
public class DM5Demo {

    private static final String TAG = "DM5Demo";

    // 动漫屋相关 URL
    private static final String BASE_URL = "http://www.dm5.com";
    private static final String MOBILE_URL = "http://m.dm5.com";
    private static final String SEARCH_URL = "http://m.dm5.com/pagerdata.ashx";

    private Context mContext;
    private OkHttpClient mHttpClient;

    public DM5Demo(Context context) {
        mContext = context;
        mHttpClient = new OkHttpClient();
    }

    /**
     * 运行完整 demo 流程
     */
    public void runDemo() {
        Log.d(TAG, "========== 开始动漫屋 Demo ==========");

        try {
            // 1. 搜索"海贼王"
            Log.d(TAG, "步骤 1: 搜索'海贼王'");
            List<ComicInfo> comics = searchComic("海贼王", 1);
            if (comics == null || comics.isEmpty()) {
                Log.e(TAG, "搜索失败或未找到结果");
                return;
            }

            // 打印搜索结果
            Log.d(TAG, "搜索结果：共 " + comics.size() + " 本漫画");
            for (int i = 0; i < Math.min(3, comics.size()); i++) {
                ComicInfo comic = comics.get(i);
                Log.d(TAG, "  " + (i + 1) + ". " + comic.title + " (" + comic.cid + ")");
            }

            // 2. 获取第一本漫画的详情
            Log.d(TAG, "步骤 2: 获取漫画详情");
            ComicInfo firstComic = comics.get(0);
            ComicDetail detail = getComicDetail(firstComic.cid);
            if (detail == null) {
                Log.e(TAG, "获取详情失败");
                return;
            }

            // 打印详情
            Log.d(TAG, "漫画详情：");
            Log.d(TAG, "  标题: " + detail.title);
            Log.d(TAG, "  作者: " + detail.author);
            Log.d(TAG, "  更新: " + detail.update);
            Log.d(TAG, "  简介: " + detail.intro);
            Log.d(TAG, "  封面: " + detail.cover);

            // 3. 获取章节列表
            Log.d(TAG, "步骤 3: 获取章节列表");
            List<ChapterInfo> chapters = getChapterList(firstComic.cid, detail);
            if (chapters == null || chapters.isEmpty()) {
                Log.e(TAG, "获取章节失败");
                return;
            }

            // 打印章节
            Log.d(TAG, "章节列表：共 " + chapters.size() + " 章");
            for (int i = 0; i < Math.min(5, chapters.size()); i++) {
                ChapterInfo chapter = chapters.get(i);
                Log.d(TAG, "  " + (i + 1) + ". " + chapter.title);
            }

            // 4. 获取第一章节的图片列表
            Log.d(TAG, "步骤 4: 获取章节图片列表");
            ChapterInfo firstChapter = chapters.get(0);
            List<String> images = getImageList(firstComic.cid, firstChapter.path);
            if (images == null || images.isEmpty()) {
                Log.e(TAG, "获取图片失败");
                return;
            }

            // 打印图片
            Log.d(TAG, "图片列表：共 " + images.size() + " 张");
            for (int i = 0; i < Math.min(3, images.size()); i++) {
                Log.d(TAG, "  " + (i + 1) + ". " + images.get(i));
            }

            Log.d(TAG, "========== Demo 完成 ==========");
            Log.d(TAG, "搜索结果: " + comics.size() + " 本漫画");
            Log.d(TAG, "章节总数: " + chapters.size() + " 章");
            Log.d(TAG, "图片总数: " + images.size() + " 张");

        } catch (Exception e) {
            Log.e(TAG, "Demo 运行异常", e);
        }
    }

    /**
     * 搜索漫画
     * 
     * @param keyword 搜索关键词
     * @param page 页码
     * @return 漫画列表
     */
    private List<ComicInfo> searchComic(String keyword, int page) {
        try {
            // 构造搜索请求
            RequestBody body = new FormBody.Builder()
                    .add("t", "7")
                    .add("pageindex", String.valueOf(page))
                    .add("title", keyword)
                    .build();

            Request request = new Request.Builder()
                    .url(SEARCH_URL)
                    .post(body)
                    .addHeader("Referer", MOBILE_URL)
                    .build();

            // 发送请求
            String html = sendRequest(request);
            if (html == null) {
                Log.e(TAG, "搜索请求失败");
                return null;
            }

            // 解析搜索结果
            return parseSearchResult(html);

        } catch (Exception e) {
            Log.e(TAG, "搜索失败", e);
            return null;
        }
    }

    /**
     * 解析搜索结果
     * 
     * @param html JSON 字符串
     * @return 漫画列表
     */
    private List<ComicInfo> parseSearchResult(String html) {
        List<ComicInfo> list = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(html);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                String url = object.optString("Url");
                String cid = url.split("/")[1];
                String title = object.optString("Title");
                String cover = object.optString("Pic");
                String update = object.optString("LastPartTime");

                JSONArray authorArray = object.optJSONArray("Author");
                String author = "";
                if (authorArray != null) {
                    for (int j = 0; j < authorArray.length(); j++) {
                        author += authorArray.optString(j);
                    }
                }

                ComicInfo comic = new ComicInfo();
                comic.cid = cid;
                comic.title = title;
                comic.cover = cover;
                comic.update = update;
                comic.author = author;

                list.add(comic);
            }

        } catch (JSONException e) {
            Log.e(TAG, "解析搜索结果失败", e);
        }

        return list;
    }

    /**
     * 获取漫画详情
     * 
     * @param cid 漫画 ID
     * @return 漫画详情
     */
    private ComicDetail getComicDetail(String cid) {
        try {
            // 构造详情页请求
            String url = BASE_URL + "/" + cid;
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            // 发送请求
            String html = sendRequest(request);
            if (html == null) {
                Log.e(TAG, "详情页请求失败");
                return null;
            }

            // 解析详情
            return parseComicDetail(html);

        } catch (Exception e) {
            Log.e(TAG, "获取获取详情失败", e);
            return null;
        }
    }

    /**
     * 解析漫画详情（使用 Node 类）
     * 
     * @param html HTML 内容
     * @return 漫画详情
     */
    private ComicDetail parseComicDetail(String html) {
        ComicDetail detail = new ComicDetail();

        try {
            // 使用 Node 类解析 HTML
            Node body = new Node(html);

            // 解析标题
            String title = body.text("div.banner_detail_form > div.info > p.title");
            if (title != null) {
                detail.title = title;
            }

            // 解析封面
            String cover = body.src("div.banner_detail_form > div.cover > img");
            if (cover != null) {
                detail.cover = cover;
            }

            // 解析更新时间
            String update = body.text("#tempc > div.detail-list-title > span.s > span");
            if (update != null) {
                detail.update = parseUpdateTime(update);
            }

            // 解析作者
            String author = body.text("div.banner_detail_form > div.info > p.subtitle > a");
            if (author != null) {
                detail.author = author;
            }

            // 解析简介
            String intro = body.text("div.banner_detail_form > div.info > p.content");
            if (intro != null) {
                intro = intro.replace("[+展开]", "").replace("[-折叠]", "");
                detail.intro = intro;
            }

            // 解析状态
            String status = body.text("div.banner_detail_form > div.info > p.tip > span:eq(0)");
            if (status != null) {
                detail.isFinish = status.contains("完结");
            }

        } catch (Exception e) {
            Log.e(TAG, "解析获取详情失败", e);
        }

        return detail;
    }

    /**
     * 获取章节列表
     * 
     * @param cid 漫画 ID
     * @param detail 漫画详情
     * @return 章节列表
     */
    private List<ChapterInfo> getChapterList(String cid, ComicDetail detail) {
        try {
            // 漫画详情页面已经包含章节列表，直接解析
            String url = BASE_URL + "/" + cid;
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            String html = sendRequest(request);
            if (html == null) {
                Log.e(TAG, "获取章节列表失败");
                return null;
            }

            return parseChapterList(html);

        } catch (Exception e) {
            Log.e(TAG, "获取章节列表失败", e);
            return null;
        }
    }

    /**
     * 解析章节列表（使用 Node 类）
     * 
     * @param html HTML 内容
     * @return 章节列表
     */
    private List<ChapterInfo> parseChapterList(String html) {
        List<ChapterInfo> list = new LinkedList<>();

        try {
            // 使用 Node 类解析 HTML
            Node body = new Node(html);
            
            // 查找章节列表区域
            Node chapterListNode = body.id("chapterlistload");
            if (chapterListNode == null || chapterListNode.get() == null) {
                Log.w(TAG, "未找到章节列表区域");
                return list;
            }

            // 查找所有章节链接
            List<Node> chapterLinks = chapterListNode.list("ul > li > a");
            if (chapterLinks.isEmpty()) {
                Log.w(TAG, "未找到章节链接");
                return list;
            }

            int index = 0;
            for (Node link : chapterLinks) {
                String href = link.href();
                String title = link.text();

                // 标题格式通常是 "第X话 标题" 或 "X 标题"，取第一部分
                String[] parts = title.split(" ");
                if (parts.length > 0) {
                    title = parts[0];
                }

                ChapterInfo chapter = new ChapterInfo();
                chapter.title = title;
                chapter.path = href;
                chapter.index = index++;

                list.add(chapter);
            }

        } catch (Exception e) {
            Log.e(TAG, "解析章节列表失败", e);
        }

        return list;
    }

    /**
     * 获取图片列表
     * 
     * @param cid 漫画 ID
     * @param path 章节路径
     * @return 图片 URL 列表
     */
    private List<String> getImageList(String cid, String path) {
        try {
            // 构造图片页请求
            String url = MOBILE_URL + "/" + path;
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Referer", MOBILE_URL + "/" + path)
                    .build();

            // 发送请求
            String html = sendRequest(request);
            if (html == null) {
                Log.e(TAG, "图片页请求失败");
                return null;
            }

            // 解析图片列表
            return parseImageList(html);

        } catch (Exception e) {
            Log.e(TAG, "获取图片列表失败", e);
            return null;
        }
    }

    /**
     * 解析图片列表（使用 DecryptionUtils）
     * 
     * @param html HTML 内容
     * @return 图片 URL 列表
     */
    private List<String> parseImageList(String html) {
        List<String> list = new ArrayList<>();

        try {
            // 查找 eval() 代码块
            int evalStart = html.indexOf("eval(");
            if (evalStart == -1) {
                Log.w(TAG, "未找到 eval() 代码块");
                return list;
            }

            int evalEnd = html.indexOf(");", evalStart);
            if (evalEnd == -1) {
                return list;
            }

            String evalCode = html.substring(evalStart, evalEnd + 2);

            Log.d(TAG, "找到 eval() 代码块，长度: " + evalCode.length());

            // 使用 DecryptionUtils 进行解密
            String decrypted = DecryptionUtils.evalDecrypt(evalCode, "newImgs");
            if (decrypted == null || decrypted.isEmpty()) {
                Log.e(TAG, "解密失败");
                return list;
            }

            Log.d(TAG, "解密成功，结果长度: " + decrypted.length());

            // 解析图片 URL 列表
            String[] imageArray = decrypted.split(",");
            for (String image : imageArray) {
                if (image != null && !image.trim().isEmpty()) {
                    list.add(image.trim());
                }
            }

            Log.d(TAG, "成功解析 " + list.size() + " 张图片");

        } catch (Exception e) {
            Log.e(TAG, "解析图片列表失败", e);
        }

        return list;
    }

    /**
     * 发送 HTTP 请求
     * 
     * @param request 请求对象
     * @return 响应内容
     */
    private String sendRequest(Request request) {
        try {
            Log.d(TAG, "发送请求: " + request.url());
            
            Response response = mHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String html = response.body().string();
                Log.d(TAG, "请求成功，响应长度: " + html.length());
                return html;
            } else {
                Log.e(TAG, "请求失败: " + response.code());
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "请求异常", e);
            return null;
        }
    }

    /**
     * 解析更新时间
     * 
     * @param update 更新时间字符串
     * @return 格式化后的日期
     */
    private String parseUpdateTime(String update) {
        if (update == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();

        if (update.contains("今天") || update.contains("分钟前")) {
            // 保持原样或转换为今天的日期
            return new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(calendar.getTime());
        } else if (update.contains("昨天")) {
            calendar.add(Calendar.DATE, -1);
            return new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(calendar.getTime());
        } else if (update.contains("前天")) {
            calendar.add(Calendar.DATE, -2);
            return new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(calendar.getTime());
        } else {
            // 尝试提取日期格式
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+-\\d+-\\d+");
            java.util.regex.Matcher matcher = pattern.matcher(update);
            if (matcher.find()) {
                return matcher.group();
            }

            // 尝试提取 "X月X号" 格式
            pattern = java.util.regex.Pattern.compile("(\\d+)月(\\d+)号");
            matcher = pattern.matcher(update);
            if (matcher.find()) {
                String month = matcher.group(1);
                String day = matcher.group(2);
                return calendar.get(Calendar.YEAR) + "-" + month + "-" + day;
            }
        }

        return update;
    }

    /**
     * 漫画信息
     */
    public static class ComicInfo {
        public String cid;      // 漫画 ID
        public String title;    // 标题
        public String cover;    // 封面图
        public String update;   // 更新时间
        public String author;   // 作者
    }

    /**
     * 漫画详情
     */
    public static class ComicDetail {
        public String title;       // 标题
        public String author;      // 作者
        public String update;      // 更新时间
        public String intro;       // 简介
        public String cover;       // 封面图
        public boolean isFinish;   // 是否完结
    }

    /**
     * 章节信息
     */
    public static class ChapterInfo {
        public int index;     // 章节序号
        public String title;   // 章节标题
        public String path;    // 章节路径
    }
}
