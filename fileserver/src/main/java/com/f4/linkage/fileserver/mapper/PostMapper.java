package com.f4.linkage.fileserver.mapper;

import com.f4.linkage.fileserver.model.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet resultSet, int i) throws SQLException {
        Post post = new Post();
        String poster_name = resultSet.getString("poster_name");

        post.setId(resultSet.getInt("id"));
        post.setPoster_name(poster_name);
        post.setPostAbstract(getAbstract(resultSet.getString("text")));

        post.setTime(resultSet.getString("time"));
        post.setPoster_icon("/global_icon/" + poster_name);
        return post;
    }

    private String getAbstract(String str){
        if(str.length() > 20){
            return str.substring(0, 20);
        }else {
            return str;
        }
    }
}
