/**
 * @Title: UploadedImageFile
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/9 15:47
 */
package com.how2java.tmall.util;

import org.springframework.web.multipart.MultipartFile;

public class UploadedImageFile {

    /**
     * 注： 这里的属性名称image必须和listCategory.jsp中的增加分类部分中的type="file"的name值保持一致。
     * <input id="categoryPic" accept="image/*" type="file" name="image" />
     * */

    MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
