package com.art1001.supply.service.file.impl;

import com.art1001.supply.entity.file.MemberDownload;
import com.art1001.supply.mapper.file.MemberDownloadMapper;
import com.art1001.supply.service.file.MemberDownloadService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @ClassName MemberDownloadServiceImpl
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/5/13 13:55
 * @Discription 成员下载实现类
 */
@Service
public class MemberDownloadServiceImpl extends ServiceImpl<MemberDownloadMapper, MemberDownload> implements MemberDownloadService {

    @Resource
    private MemberDownloadMapper memberDownloadMapper;

    /**
     * 获取用户已下载信息
     * @param memberId
     * @return
     */
    @Override
    public List<MemberDownload> getIsDownload(String memberId) {
        List<MemberDownload> isDownload = memberDownloadMapper.getIsDownload(memberId);
        isDownload.forEach(r->{
            SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置格式
            r.setCreateTime(format.format(Long.valueOf(r.getCreateTime())));
        });
        return isDownload;

    }
}
