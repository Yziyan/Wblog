package com.xhy.wblog.controller;


import com.xhy.wblog.controller.result.Code;
import com.xhy.wblog.controller.result.PublicResult;
import com.xhy.wblog.controller.vo.fans.FansVo;
import com.xhy.wblog.controller.vo.users.UserVo;
import com.xhy.wblog.entity.User;
import com.xhy.wblog.service.FansService;
import com.xhy.wblog.service.UserService;
import com.xhy.wblog.utils.exception.ExceptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/fans")
public class FansController {

    //用户
    @Autowired
    private UserService userService;

    @Autowired
    private FansService fansService;

    //关注
    @RequestMapping("/subscription")
    public PublicResult subscription(@RequestBody FansVo fansVo){
        try {
            if(fansService.addSubscription(fansVo.getUserId(),fansVo.getOtherId())){
                return new PublicResult(true, Code.SAVE_OK,null,"关注成功!");
            }else {
                return new PublicResult(false,Code.SAVE_ERROR,null,"关注失败！");
            }
        }catch (Exception e){
            return new PublicResult(false,Code.SAVE_ERROR, ExceptUtil.getSimpleException(e),"网络出现波动！请重新尝试");
        }
    }

    //取消关注
    @RequestMapping("/cancelSubscription")
    public PublicResult cancelSubscription(@RequestBody FansVo fansVo){
        try {
            if(fansService.cancelSubscription(fansVo.getUserId(), fansVo.getOtherId())){
                return new PublicResult(true, Code.SAVE_OK,null,"取消关注成功!");
            }else {
                return new PublicResult(false,Code.SAVE_ERROR,null,"取消关注失败！");
            }
        }catch (Exception e){
            return new PublicResult(false,Code.SAVE_ERROR, ExceptUtil.getSimpleException(e),"网络出现波动！请重新尝试");
        }
    }

    //获取自己关注的人
    @RequestMapping("/getBeSubscript")
    public PublicResult getBeSubscript(@RequestBody FansVo fansVo){
        try{
            List<User> beSubscript = fansService.getBeSubscript(fansVo.getUserId());
            return new PublicResult(true,Code.QUERY_OK,beSubscript,"获取成功！");
        }catch (Exception e){
            return new PublicResult(false,Code.QUERY_ERROR, ExceptUtil.getSimpleException(e),"网络出现波动！请重新尝试");
        }
    }

}
