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

    public static UploadResult uploadImage(MultipartFile file, HttpServletRequest request, String oldImage) throws IOException {
        // 若原来的字符串是 "" 返回null
        if (oldImage != null && oldImage.length() == 0) {
            oldImage = null;
        }

        // 文件上传
        ServletContext ctx = request.getServletContext();
        // ctx路径
        String ctxPath = ctx.getContextPath();

        // http://localhost:8080
        String url = String.valueOf(request.getRequestURL());
        String userUrl = url.substring(0, url.lastIndexOf(ctxPath));

        // 用来接收返回值
        UploadResult result = new UploadResult();
        Map<String, Object> map = new HashMap<>();
        if (file.isEmpty() || file.getSize() <= 0) { // 如果没有文件，返回原来的数据
            String odlFileName = oldImage.substring(oldImage.lastIndexOf("/"));
            result.setFileName(odlFileName);
            result.setFilePath(userUrl + ctxPath + "/" + oldImage);
            result.setImagePath(oldImage);
            return result;
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

        result.setFileName(newFileName);
        result.setFilePath(userUrl + ctxPath + "/" + image);
        result.setImagePath(image);
        return result;
    }

}
