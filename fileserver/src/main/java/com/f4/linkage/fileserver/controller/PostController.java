package com.f4.linkage.fileserver.controller;

import com.f4.linkage.fileserver.dao.PostDao;
import com.f4.linkage.fileserver.model.*;
import com.f4.linkage.fileserver.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PostController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @Resource
    FileUtil fileUtil;

    @Resource
    PostDao postDao;

    @PostMapping("/post")
    ResponseEntity<String> uploadPost(Principal principal, @RequestParam("PostHtml")String postHtml){
        String username = principal.getName();
        fileUtil.updatePostID();
        if(postDao.insertPost(new Object[]{username, postHtml})){
            return ResponseEntity.ok("Upload successfully, and your post id is " + FileUtil.postID);
        }else {
            return ResponseEntity.status(500).body("Sorry, maybe try again!");
        }
    }

    @PostMapping("/post/delete")
    ResponseEntity<String> deletePost(@RequestParam("id")int id){
        if(postDao.deletePost(id)){
            return ResponseEntity.ok("Deleted successfully");
        }else {
            return ResponseEntity.status(500).body("Sorry, maybe try again!");
        }
    }

    @PostMapping("/post/img")
    PostImgInfo handlePostImg(@RequestParam("Picture")MultipartFile[] imgs){
        PostImgInfo postImgInfo = new PostImgInfo();
        List<String> imgUrls = new ArrayList<>();
        fileUtil.updatePostID();
        if(fileUtil.saveFiles(imgs, FileKind.PostPicture, imgUrls)){
            postImgInfo.setData(imgUrls);
            postImgInfo.setErrno(0);
        }else {
            postImgInfo.setErrno(1);
            postImgInfo.setData(null);
        }
        return postImgInfo;
    }

    @GetMapping("/post/search")
    List<Post> search(@RequestParam("Keyword")String keyword){
        return postDao.searchPost(keyword);
    }

    @GetMapping("/post/hot")
    void hot(){

    }

    @GetMapping("/post/check")
    List<Post> checkPost(Principal principal){
        String username = principal.getName();
        LOGGER.info("Return the moment of " + username);
        return postDao.getPosts(username);
    }

    @GetMapping("/post/home")
    List<Post> homePost(HttpServletRequest request, Principal principal){
        String username;
        username = request.getParameter("username");
        if(username == null) {
            username = principal.getName();
        }
        LOGGER.info("Return " + username + "'s private moments");
        return postDao.getPrivatePosts(username);
    }

    @PostMapping("/post/like")
    ResponseEntity<List<Like>> likePost(Principal principal, @RequestParam("PostId")int postId, @RequestParam("Action")String action){
        String username = principal.getName();
        switch (action){
            case "like":
                if(postDao.updatePostLike(username, postId, true)){
                    return ResponseEntity.ok().body(postDao.getPostLikeList(postId));
                }else {
                    return ResponseEntity.status(500).body(new ArrayList<>());
                }
            case "cancel":
                if(postDao.updatePostLike(username, postId, false)){
                    return ResponseEntity.ok().body(postDao.getPostLikeList(postId));
                }else {
                    return ResponseEntity.status(500).body(new ArrayList<>());
                }
            default:
                return ResponseEntity.status(406).body(new ArrayList<>());
        }
    }

    @PostMapping("/post/comment/add")
    ResponseEntity<List<Comment>> addComment(Principal principal, @RequestParam("PostId")int postId, @RequestParam("Comment")String comment){
        String username = principal.getName();
        if(postDao.insertComment(username, postId, comment)){
            return ResponseEntity.ok(postDao.getPostCommentList(postId));
        }else {
            return ResponseEntity.status(500).body(new ArrayList<Comment>());
        }
    }

    @PostMapping("/post/comment/delete")
    ResponseEntity<List<Comment>> deleteComment(Principal principal, @RequestParam("PostId")int postId){
        String username = principal.getName();
        if(postDao.deleteComment(username, postId)){
            return ResponseEntity.ok(postDao.getPostCommentList(postId));
        }else {
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
}
