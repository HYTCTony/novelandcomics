package com.nnmedia.comics.demo;

import android.content.Context;
import android.util.Log;

import com.nnmedia.comics.utils.Node;
import com.nnmedia.comics.utils.StringUtils;
import com.nnmedia.comics.utils.DecryptionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 腾讯动漫 Demo
 * 功能：搜索漫画 → 获取详情 → 获取章节 → 获取图片
 * 
 * 使用方法：
 * TencentDemo demo = new TencentDemo(context);
 * demo.runDemo();
 */
public class TencentDemo {

    private static final String TAG = "TencentDemo";

    // 腾讯动漫相关 URL
    private static final String BASE_URL = "https://m.ac.qq.com";
    private static final String SEARCH_URL = "https://m.ac.qq.com/search/result";

    private Context mContext;
    private OkHttpClient mHttpClient;

    public TencentDemo(Context context) {
        mContext = context;
        mHttpClient = new OkHttpClient();
    }

    /**
     * 运行完整 demo 流程
     */
    public void runDemo() {
        Log.d(TAG, "========== 开始腾讯动漫 Demo ==========");

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
            List<ChapterInfo> chapters = getChapterList(firstComic.cid);
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
    public List<ComicInfo> searchComic(String keyword, int page) {
        try {
            // 构造搜索请求
            String url = SEARCH_URL + "?word=" + keyword;
            Request request = new Request.Builder()
                    .url(url)
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
     * @param html HTML 内容
     * @return 漫画列表
     */
    private List<ComicInfo> parseSearchResult(String html) {
        List<ComicInfo> list = new ArrayList<>();

        try {
            Node body = new Node(html);
            List<Node> items = body.list(".comic-item");

            for (Node node : items) {
                // 提取 cid
                String href = node.attr("a", "href");
                String cid = "";
                if (href.contains("/comic/index/id/")) {
                    cid = href.substring("/comic/index/id/".length());
                }

                String title = node.text(".comic-title");
                String cover = node.attr(".cover-image", "src");
                String update = node.text(".comic-update");
                String author = "UNKNOWN"; // 腾讯动漫搜索结果没有作者信息

                ComicInfo comic = new ComicInfo();
                comic.cid = cid;
                comic.title = title;
                comic.cover = cover;
                comic.update = update;
                comic.author = author;

                list.add(comic);
            }

        } catch (Exception e) {
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
    public ComicDetail getComicDetail(String cid) {
        try {
            // 构造详情页请求
            String url = BASE_URL + "/comic/index/id/" + cid;
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
            Log.e(TAG, "获取详情失败", e);
            return null;
        }
    }

    /**
     * 解析漫画详情
     * 
     * @param html HTML 内容
     * @return 漫画详情
     */
    private ComicDetail parseComicDetail(String html) {
        ComicDetail detail = new ComicDetail();

        try {
            Node body = new Node(html);

            // 解析标题
            String title = body.text("div.head-title-tags > h1");
            if (title != null) {
                detail.title = title;
            }

            // 解析封面
            String cover = body.src("div.head-banner > img");
            if (cover != null) {
                detail.cover = cover;
            }

            // 解析作者
            String author = body.text("li.author-wr");
            if (author != null) {
                detail.author = author;
            }

            // 解析简介
            String intro = body.text("div.head-info-desc");
            if (intro != null) {
                detail.intro = intro;
            }

            // 解析更新时间（腾讯动漫详情页可能不直接显示，从章节列表获取）
            detail.update = "未知";

            // 默认连载状态
            detail.isFinish = false;

        } catch (Exception e) {
            Log.e(TAG, "解析详情失败", e);
        }

        return detail;
    }

    /**
     * 获取章节列表
     * 
     * @param cid 漫画 ID
     * @return 章节列表
     */
    private List<ChapterInfo> getChapterList(String cid) {
        try {
            String url = BASE_URL + "/comic/chapterList/id/" + cid;
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
     * 解析章节列表
     * 
     * @param html HTML 内容
     * @return 章节列表
     */
    private List<ChapterInfo> parseChapterList(String html) {
        List<ChapterInfo> list = new LinkedList<>();

        try {
            Node body = new Node(html);
            List<Node> items = body.list("ul.normal > li.chapter-item");

            int index = 0;
            for (Node node : items) {
                String title = node.text("a");
                String href = node.href("a");
                
                // 提取 path: /chapter/index/id/{cid}/cid/{path}
                String path = "";
                if (href.contains("/chapter/index/id/")) {
                    String[] parts = href.split("/cid/");
                    if (parts.length > 1) {
                        path = parts[1];
                    }
                }

                ChapterInfo chapter = new ChapterInfo();
                chapter.title = title;
                chapter.path = path;
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
            String url = BASE_URL + "/chapter/index/id/" + cid + "/cid/" + path;
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            String html = sendRequest(request);
            if (html == null) {
                Log.e(TAG, "图片页请求失败");
                return null;
            }

            return parseImageList(html);

        } catch (Exception e) {
            Log.e(TAG, "获取图片列表失败", e);
            return null;
        }
    }

    /**
     * 解析图片列表
     * 
     * @param html HTML 内容
     * @return 图片 URL 列表
     */
    private List<String> parseImageList(String html) {
        List<String> list = new ArrayList<>();

        try {
            // 提取加密的图片数据
            String str = StringUtils.match("data:\\s*'(.*)?',", html, 1);
            if (str == null || str.isEmpty()) {
                Log.e(TAG, "未找到图片数据");
                return list;
            }

            // 提取解密代码
            String nonce = StringUtils.match("<script>window.*?=(.*?)<\\/script>", html, 1);
            if (nonce == null || nonce.isEmpty()) {
                Log.e(TAG, "未找到解密代码");
                return list;
            }

            // 解密数据
            String decrypted = decodeData(str, nonce);
            if (decrypted == null || decrypted.isEmpty()) {
                Log.e(TAG, "解密数据失败");
                return list;
            }

            // 解析 JSON
            JSONObject object = new JSONObject(decrypted);
            JSONArray array = object.getJSONArray("picture");

            for (int i = 0; i < array.length(); i++) {
                String imageUrl = array.getJSONObject(i).getString("url");
                list.add(imageUrl);
            }

            Log.d(TAG, "成功解析 " + list.size() + " 张图片");

        } catch (Exception e) {
            Log.e(TAG, "解析图片列表失败", e);
        }

        return list;
    }

    /**
     * 解密数据
     * 
     * @param str 加密的数据
     * @param nonce 解密代码
     * @return 解密后的数据
     */
    private String decodeData(String str, String nonce) {
        try {
            // 解密 nonce
            nonce = DecryptionUtils.evalDecrypt(nonce);
            
            // 提取匹配模式
            Matcher m = Pattern.compile("\\d+[a-zA-Z]+").matcher(nonce);
            final List<String> matches = new ArrayList<>();
            while (m.find()) {
                matches.add(m.group(0));
            }
            
            // 应用解密的变换
            int len = matches.size();
            while ((len--) != 0) {
                str = splice(str,
                        Integer.parseInt(StringUtils.match("^\\d+", matches.get(len), 0)) & 255,
                        StringUtils.replaceAll(matches.get(len), "\\d+", "").length()
                );
            }
            
            // Base64 解密
            return DecryptionUtils.base64Decrypt(str);
            
        } catch (Exception e) {
            Log.e(TAG, "解密失败", e);
            return null;
        }
    }

    /**
     * 字符串拼接（解密用）
     * 
     * @param str 原始字符串
     * @param from 起始位置
     * @param length 要删除的长度
     * @return 处理后的字符串
     */
    private String splice(String str, int from, int length) {
        return str.substring(0, from) + str.substring(from + length, str.length());
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
