package com.coderman.video.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/video")
public class VideoController {

    private final List<Category> categoryList = Arrays.asList(
            new Category(1L, "科技"),
            new Category(2L, "生活"),
            new Category(3L, "搞笑"),
            new Category(4L, "游戏")
    );

    private final List<Video> allVideos = new ArrayList<>();

    public VideoController() {
        // 模拟初始化视频数据
        for (int i = 1; i <= 30; i++) {
            allVideos.add(new Video(
                    (long) i,
                    "视频标题 " + i,
                    "https://static.yfsp.tv/upload/video/202504201421582178237.gif?w=238&h=340&format=jpg&mode=stretch?text=视频" + i,
                    "0" + (i % 10) + ":0" + (i % 6) + ":0" + (i % 9),
                    "用户" + (i % 5),
                    (long) ((i % 4) + 1), // 分类1-4
                    "https://www.w3schools.com/html/mov_bbb.mp4" // 示例视频源
            ));
        }
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) Long category,
                       Model model) {

        int pageSize = 9;
        List<Video> filtered = category == null ?
                allVideos :
                allVideos.stream()
                        .filter(v -> Objects.equals(v.getCategoryId(), category))
                        .collect(Collectors.toList());

        int start = page * pageSize;
        int end = Math.min(start + pageSize, filtered.size());

        List<Video> pageContent = filtered.subList(Math.min(start, filtered.size()), end);
        int totalPages = (int) Math.ceil((double) filtered.size() / pageSize);

        model.addAttribute("videos", pageContent);
        model.addAttribute("page", new Page(page, totalPages));
        model.addAttribute("categories", categoryList);
        model.addAttribute("selectedCategory", category);

        return "video/list";
    }

    @GetMapping("/play/{id}")
    @ResponseBody
    public String play(@PathVariable Long id) {
        Optional<Video> video = allVideos.stream()
                .filter(v -> Objects.equals(v.getId(), id))
                .findFirst();

        return video.map(Video::getPlayUrl).orElse("");
    }

    // 简单模拟分页对象
    public static class Page {
        public int number;
        public int totalPages;

        public Page(int number, int totalPages) {
            this.number = number;
            this.totalPages = totalPages;
        }

        public int getNumber() {
            return number;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

    // 假数据模型
    public static class Video {
        private Long id;
        private String title;
        private String coverUrl;
        private String duration;
        private String uploader;
        private Long categoryId;
        private String playUrl;

        public Video(Long id, String title, String coverUrl, String duration, String uploader, Long categoryId, String playUrl) {
            this.id = id;
            this.title = title;
            this.coverUrl = coverUrl;
            this.duration = duration;
            this.uploader = uploader;
            this.categoryId = categoryId;
            this.playUrl = playUrl;
        }

        // Getters（必须有，不然 Thymeleaf 读不到）
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getCoverUrl() { return coverUrl; }
        public String getDuration() { return duration; }
        public String getUploader() { return uploader; }
        public Long getCategoryId() { return categoryId; }
        public String getPlayUrl() { return playUrl; }
    }

    public static class Category {
        private Long id;
        private String name;

        public Category(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }
}
