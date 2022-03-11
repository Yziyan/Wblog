package com.xhy.wblog.utils.upload;

import com.xhy.wblog.entity.Constant;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileUpload {

    public static Map<String, String> uploadImage(MultipartFile file, HttpServletRequest request, String oldImage) throws IOException {
        // 若原来的字符串是 "" 返回null
        if (oldImage != null && oldImage.length() == 0) {
            oldImage = null;
        }

        // 文件上传
        ServletContext ctx = request.getServletContext();
        // ctx路径
        String ctxPath = ctx.getContextPath();
        Map<String, String> map = new HashMap<>();
        if (file.isEmpty() || file.getSize() <= 0) { // 如果没有文件，返回原来的数据
            String odlFileName = oldImage.substring(oldImage.lastIndexOf("/"));
            map.put("fileName", odlFileName);
            map.put("filePath", "http://localhost:8080/" + ctxPath + "/" + oldImage);
            map.put("imagePath", oldImage);
            return map;
        }

        // 获取原文件名
        String originalFilename = file.getOriginalFilename();
        // 文件后缀名
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成新文件名
        String newFileName = System.currentTimeMillis() + fileSuffix;


        //存储到数据库的文件路径
        String image = Constant.BASE_DIR + Constant.IMG_DIR + newFileName;
        // 写入磁盘的路径
        String filePath = ctx.getRealPath(image);
        // 写入磁盘，上传文件
        file.transferTo(new File(filePath));



        // 将文件名和文件路径返回，进行响应

        map.put("fileName", newFileName);
        map.put("filePath", "http://localhost:8080/" + ctxPath + "/" + image);
        map.put("imagePath", image);

        return map;
    }

}
