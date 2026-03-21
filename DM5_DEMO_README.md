# DM5 Demo 使用说明

## 📦 创建的文件

### 1. DM5Demo.java
**路径**：`app/src/main/java/com/nnmedia/comics/demo/DM5Demo.java`  
**功能**：动漫屋（DM5）Demo 核心类

### 2. DM5DemoActivity.java
**路径**：`app/src/main/java/com/nnmedia/comics/demo/DM5DemoActivity.java`  
**功能**：Demo Activity，提供 UI 界面运行 demo

### 3. activity_dm5_demo.xml
**路径**：`app/src/main/res/layout/activity_dm5_demo.xml`  
**功能**：Demo Activity 的布局文件

### 4. AndroidManifest.xml（已修改）
**修改**：添加了 DM5DemoActivity 的注册

---

## 🚀 运行 Demo

### 方法 1：通过 adb 命令（推荐）

```bash
adb shell am start -n com.nnmedia.novel/com.nnmedia.comics.demo.DM5DemoActivity
```

### 方法 2：在代码中启动

```java
Intent intent = new Intent();
intent.setComponent(new ComponentName("com.nnmedia.novel", "com.nnmedia.comics.demo.DM5DemoActivity"));
startActivity(intent);
```

---

## 📋 Demo 流程

Demo 会自动执行以下步骤：

### 步骤 1：搜索"海贼王"
- 发送搜索请求到动漫屋 API
- 解析搜索结果（JSON 格式）
- 显示搜索结果的漫画列表

**搜索请求**：
```
POST http://m.dm5.com/pagerdata.ashx
参数：
- t: 7
- pageindex: 1
- title: 海贼王
```

### 步骤 2：获取第一本漫画的详情
- 从搜索结果中获取第一本漫画
- 发送详情页请求
- 解析漫画详情（标题、作者、简介、封面等）

**详情页请求**：
```
GET http://www.dm5.com/{cid}
```

### 步骤 3：获取章节列表
- 从详情页中解析章节列表
- 显示章节标题

### 步骤 4：获取第一章节的图片列表
- 发送章节页请求
- 解析图片 URL 列表（需要解密）

---

## 📊 输出示例

### 搜索结果
```
========== 动漫屋 Demo ==========
步骤 1: 搜索'海贼王'
搜索结果：共 10 本漫画
  1. 海贼王 ONE PIECE (manga100341)
  2. 航海王 ONE PIECE (manga101326)
  3. 海贼王 / ONE PIECE (manga100341)
```

### 漫画详情
```
步骤 2: 获取漫画详情
漫画详情：
  标题: 海贼王 ONE PIECE
  作者: 尾田荣一郎
  更新: 2024-03-20
  简介: 故事讲述了男主角蒙奇·D·路飞...
  封面: http://...
```

### 章节列表
```
步骤 3: 获取章节列表
章节列表：共 1000 章
  1. 第1话 ROMANCE DAWN
  2. 第2话 BROMANCE
  3. 第3话 第三个朋友
```

### 图片列表
```
步骤 4: 获取章节图片列表
图片列表：共 20 张
  1. http://...
  2. http://...
  3. http://...
```

---

## ⚠️ 注意事项

### 1. HTML 解析

Demo 中的 HTML 解析方法（`extractText()` 和 `extractAttr()`）是简化实现，仅用于演示。

**实际项目中应该使用**：
- Jsoup（推荐）
- Android HtmlCleaner
- 正则表达式

**示例**：
```java
// 使用 Jsoup
Document doc = Jsoup.parse(html);
String title = doc.select.select("div.banner_detail_form > div.info > p.title").text();
```

### 2. 图片 URL 解密

动漫屋的图片 URL 通过 `eval()` 代码加密，需要解密。

**Demo 中只是找到 eval() 代码块，但没有实现解密**。

**实际项目中需要实现**：
- `DecryptionUtils.evalDecrypt()` 方法
- 可以参考 Cimoc 项目中的实现：
  `/root/.openclaw/workspace/Cimoc/app/src/main/java/com/haleydu/cimoc/utils/DecryptionUtils.java`

### 3. 网络请求

Demo 使用 OkHttp 发送网络请求，实际项目中可以使用：
- RxHttp（项目中已集成）
- Retrofit
- OkHttp

### 4. 异步操作

Demo 使用新方法 Thread 执行异步操作，实际项目中可以使用：
- RxJava
- AsyncTask
- Coroutines（Kotlin）

---

## 🔧 自定义 Demo

### 搜索其他漫画

修改 `runDemo()` 方法：

```java
// 搜索其他漫画
List<ComicInfo> comics = searchComic("火影忍者", 1);
```

### 获取指定章节

修改 `runDemo()` 方法：

```java
// 获取第 10 章
ChapterInfo chapter = chapters.get(9);
List<String> images = getImageList(firstComic.cid, chapter.path);
```

---

## 📝 代码结构

### DM5Demo 类

```java
public class DM5Demo {
    // 运行完整 demo 流程
    public void runDemo();
    
    // 搜索漫画
    private List<ComicInfo> searchComic(String keyword, int page);
    
    // 获取漫画详情
    private ComicDetail getComicDetail(String cid);
    
    // 获取章节列表
    private List<ChapterInfo> getChapterList(String cid, ComicDetail detail);
    
    // 获取图片列表
    private List<String> getImageList(String cid, String path);
}
```

### 数据模型

```java
// 漫画信息
public static class ComicInfo {
    public String cid;      // 漫画 ID
    public String title;    // 标题
    public String cover;    // 封面图
    public String update;   // 更新时间
    public String author;   // 作者
}

// 漫画详情
public static class ComicDetail {
    public String title;       // 标题
    public String author;      // 作者
    public String update;      // 更新时间
    public String intro;       // 简介
    public String cover;       // 封面图
    public boolean isFinish;   // 是否完结
}

// 章节信息
public static class ChapterInfo {
    public int index;     // 章节序号

    public String title;   // 章节标题
    public String path;    // 章节路径
}
```

---

## 🎯 下一步

### 完善功能建议

1. **实现 HTML 解析**：使用 Jsoup 替换简化实现
2. **实现图片解密**：参考 Cimoc 项目实现解密逻辑
3. **添加错误处理**：完善异常捕获和用户提示
4. **添加进度显示**：显示下载和解析进度
5. **实现缓存**：缓存漫画详情和章节数据
6. **集成到 ComicsDetailActivity**：将功能集成到漫画详情页

---

## 📚 参考资源

### Cimoc 项目

- **DM5 Parser**：`/root/.openclaw/workspace/Cimoc/app/src/main/java/com/haleydu/cimoc/source/DM5.java`
- **DecryptionUtils**：`/root/.openclaw/workspace/Cimoc/app/src/main/java/com/haleydu/cimoc/utils/DecryptionUtils.java`
- **Manga**：`/root/.openclaw/workspace/Cimoc/app/src/main/java/com/haleydu/cimoc/core/Manga.java`

### 动漫屋网站

- **PC 端**：http://www.dm5.com
- **移动端**：http://m.dm5.com

---

## ✅ 总结

Demo 已经创建完成，可以运行查看效果。

**关键点**：
1. Demo 实现了从动漫屋获取漫画的完整流程
2. 使用 OkHttp 发送网络请求
3. 解析 JSON 和 HTML 数据
4. HTML 解析和图片解密需要进一步完善

**编译状态**：✅ 编译成功

**下一步**：运行 demo，查看日志输出
