package com.post.blog.global.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class UriCreator {
    public static URI createUri(String defaultUrl, long resourceId) {
        return UriComponentsBuilder
                .newInstance()
                .path(defaultUrl+"/{resource-id}")
                .buildAndExpand(resourceId)
                .toUri();
    }
}
