/**
 * @Title: ReviewService
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/11 9:57
 */
package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Review;

import java.util.List;

public interface ReviewService {

    void add(Review c);

    void delete(int id);

    void update(Review c);

    Review get(int id);

    List list(int pid);

    int getCount(int pid);
}
