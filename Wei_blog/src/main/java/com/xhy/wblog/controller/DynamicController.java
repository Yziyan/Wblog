package com.xhy.wblog.controller;


import com.xhy.wblog.controller.result.Code;
import com.xhy.wblog.controller.result.PublicResult;
import com.xhy.wblog.controller.vo.dynamic.PublishVo;
import com.xhy.wblog.controller.vo.dynamic.DynamicIdVo;
import com.xhy.wblog.entity.Dynamic;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.CommentService;
import com.xhy.wblog.service.DynamicService;
import com.xhy.wblog.service.UserService;
import com.xhy.wblog.utils.exception.ExceptUtil;
import com.xhy.wblog.utils.upload.FileUpload;
import com.xhy.wblog.utils.upload.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// 动态发布模块

@RestController
@RequestMapping("/dynamic")
public class DynamicController {


    // 自动注入service
    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;


    // 发布动态,带文件的
    @RequestMapping("/publish")
    public PublicResult publish(@RequestParam(value = "file", required = false) MultipartFile[] files, PublishVo publishVo, HttpServletRequest request) {

        try {
            // 改动态的id
            Integer id = publishVo.getId();

            // 获取登录的user
            User user = (User) request.getSession().getAttribute("user");

            if (user != null) { // 登录过了 ，可以操作
                Map<String, Object> map = new HashMap<>();
                int i = 0;
                StringBuilder builder = new StringBuilder();
                UploadResult result;

                if (files != null) { // 如果有图片，那么就遍历保存图片
                    for (MultipartFile file : files) {
                        if (id == null || id <= 0) {
                            result = FileUpload.uploadImage(file, request, null);
                        } else {
                            // 如果是编辑，将以前的图片数据放这儿
                            result = FileUpload.uploadImage(file, request, dynamicService.getById(id).getFile());

                        }
                        map.put("file" + i, result);
                        // 将图片地址拼接起来。 并且用 ，隔开放在数据库中
                        builder.append(result.getImagePath() + ",");
                        i++;
                    }
                    builder.replace(builder.length() - 2, builder.length(), " " );
                }

                // 保存到数据库的图片，若是 “ ” 则变成null在传入。
                String filePath = String.valueOf(builder);
                if (filePath != null && filePath.length() == 0) filePath = null;
                // 将图片路径保存到数据库
                publishVo.setFileVo(filePath);

                // 传入数据保存。
                Dynamic resDynamic = dynamicService.save(publishVo);

                if (resDynamic != null) {
                    // 说明保存成功了。返回这条动态信息给前台
                    map.put("dynamic", resDynamic);
                }

                String msg = "编辑成功"; // 返回消息
                if (id == null || id <= 0) {
                    msg = "动态发布成功"; //
                }

                // 将文件名和文件路径返回，进行响应
                return new PublicResult(true, Code.PUSH_OK, map, msg);
            } else {
                return new PublicResult(false, Code.PUSH_ERROR, null, "请登录");
            }

        } catch (Exception e) {
            return new PublicResult(false, Code.PUSH_ERROR, ExceptUtil.getSimpleException(e), "发布失败");
        }

    }


    @RequestMapping("/remove")
    public PublicResult remove(@RequestBody DynamicIdVo removeVo){
        try{
        if(dynamicService.removeById(removeVo.getId())) {
            return new PublicResult(true, Code.DELETE_OK, null, "删除成功");
        }else {
            return new PublicResult(false,Code.DELETE_ERROR,null,"删除失败！");
        }
        }catch (Exception e){
            return new PublicResult(false,Code.DELETE_ERROR,ExceptUtil.getSimpleException(e),"出现了未知错误");
        }
    }

    @RequestMapping("/getOldDynamic")
    public PublicResult getOldDynamic(Integer reqCount){
        try {
            //获得动态
            // 每页条数
            Integer pageSize = 2;
            // 偏移量
            Integer pageNum = 2 * (reqCount - 1);
            List<Dynamic> newDynamic = dynamicService.findAllPage(pageNum, pageSize);
            if(newDynamic!=null){//动态不为空，去取得user
                for (Dynamic dynamic:newDynamic) {
                    User user = userService.selectById(dynamic.getUerId());
                    user.setPassword(null);
                    dynamic.setUser(user);
                }
                // 动态的总条数
                long totalCount = dynamicService.getCount();

                // 总页数
                long totalPages = (totalCount + pageSize - 1) / pageSize;
                if(reqCount > totalPages)// 如果请求的页数大于总页数，则告诉没有更多微博了！
                {
                    return new PublicResult(false, Code.QUERY_OVER,null,"没有动态了喔~请休息一下吧~");
                }
                return new PublicResult(true,Code.QUERY_OK,newDynamic,"获取成功!");
            }
            return  new PublicResult(false,Code.QUERY_ERROR,null,"获取失败！");
        }catch (Exception e){
            return  new PublicResult(false,Code.QUERY_ERROR,ExceptUtil.getSimpleException(e),"获取失败！");
        }
    }

    //获取最新的动态
    @RequestMapping("/getNewDynamic")
    public PublicResult getNewDynamic(){
        try {
            List<Dynamic> newDynamic = dynamicService.getNew();
            if(newDynamic!=null){
                return new PublicResult(true,Code.QUERY_OK,newDynamic,"获取成功！");
            }else {
                return new PublicResult(false,Code.QUERY_ERROR,null,"获取失败！");
            }
        }catch (Exception e){
            return new PublicResult(false,Code.QUERY_ERROR,ExceptUtil.getSimpleException(e),"出现了未知错误！");
        }
    }

    //获取最火的动态（最高的点赞数量）
    @RequestMapping("/getHotDynamic")
    public PublicResult getHotDynamic(){
        try {
            List<Dynamic> hotDynamic = dynamicService.getHot();
            if(hotDynamic!=null) {
                return new PublicResult(true, Code.QUERY_OK, hotDynamic, "获取成功！");
            }else {
                return new PublicResult(false,Code.QUERY_ERROR,null,"获取失败！");
            }
        } catch (Exception e) {
            return new PublicResult(false,Code.QUERY_ERROR,ExceptUtil.getSimpleException(e),"出现了未知错误！");
        }
    }

    @RequestMapping("/cancelLike")
    public PublicResult cancelLike(@RequestBody DynamicIdVo dynamicIdVo){
        try {
            if(dynamicService.updateDynamicHits(dynamicIdVo.getId(),false)){
                Dynamic byId = dynamicService.getById(dynamicIdVo.getId());
                User user = userService.selectById(byId.getUerId());
                user.setPassword(null);
                byId.setUser(user);
                return new PublicResult(true, Code.UPDATE_OK, byId, "获取成功！");
            } else {
                return new PublicResult(false,Code.QUERY_ERROR,null,"获取失败！");
            }
        }catch (Exception e){
            return new PublicResult(false,Code.QUERY_ERROR,ExceptUtil.getSimpleException(e),"出现了未知错误！");
        }
    }

    @RequestMapping("/setLike")
    public PublicResult setLike(@RequestBody DynamicIdVo dynamicIdVo){
        try {
            if(dynamicService.updateDynamicHits(dynamicIdVo.getId(),true)){
                Dynamic byId = dynamicService.getById(dynamicIdVo.getId());
                User user = userService.selectById(byId.getUerId());
                user.setPassword(null);
                byId.setUser(user);
                return new PublicResult(true, Code.UPDATE_OK, byId, "获取成功！");
            } else {
                return new PublicResult(false,Code.QUERY_ERROR,null,"获取失败！");
            }
        }catch (Exception e){
            return new PublicResult(false,Code.QUERY_ERROR,ExceptUtil.getSimpleException(e),"出现了未知错误！");
        }
    }

}
