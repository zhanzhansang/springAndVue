package com.su.springandvue.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.su.springandvue.common.Result;
import com.su.springandvue.entity.SysFile;
import com.su.springandvue.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件上传以及下载
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("${server.ip}")
    private String serverIp;

    @Resource
    private FileMapper fileMapper;


    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename(); //获取文件名
        String type = FileUtil.extName(originalFilename);     //获取文件类型
        long size = file.getSize();                           //获取长度

        //存储到磁盘
        String fileUuid = IdUtil.fastSimpleUUID() + "." +type;

        File uploadFile = new File(fileUploadPath+fileUuid);
        //判断配置的文件目录是否存在
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()){
            parentFile.mkdirs();
        }

        String url;
        //上传文件到此磁盘
        file.transferTo(uploadFile);
        //获取文件的MD5
        String md5 = SecureUtil.md5(uploadFile);
        //从数据库查找是否存在相同的记录
        SysFile dbFile = getFileByMd5(md5);
        if (dbFile != null){
            url = dbFile.getUrl();
            uploadFile.delete();
        }else {
//            url = "http://"+"localhost"+":8888/file/"+fileUuid;
            url = "http://"+serverIp+":8888/file/"+fileUuid;
        }

        //存储到数据库
        SysFile saveFile = new SysFile();
        saveFile.setName(originalFilename);
        saveFile.setSize(size / 1024);
        saveFile.setType(type);
        saveFile.setUrl(url);
        saveFile.setEnable(false);
        saveFile.setMd5(md5);
        fileMapper.insert(saveFile);

        return url;
    }

    /**
     * 通过文件的MD5查询是否存在
     * @param md5
     * @return
     */
    private SysFile getFileByMd5(String md5) {
        QueryWrapper<SysFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<SysFile> sysFileList = fileMapper.selectList(queryWrapper);
        return sysFileList.size() == 0 ? null : sysFileList.get(0);
    }

    /**
     * 文件下载接口 http://localhost:8888/file/{fileUuid}
     * @param fileUuid
     * @param response
     */
    @GetMapping("/{fileUuid}")
    public void download(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        //根据文件的唯一标识获取文件
        File uploadFile = new File(fileUploadPath + fileUuid);
        //设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileUuid,"UTF-8"));
        response.setContentType("application/octet-stream");

        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    //根据主键删除
    @CacheEvict(value = "files",key = "'findAll'")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        SysFile sysFile = fileMapper.selectById(id);
        sysFile.setIsDelete(true);
        fileMapper.updateById(sysFile);
        return Result.success();
    }

    //更新
    @CachePut(value="files",key = "'findAll'")
    @PostMapping("/update")
    public Result update(@RequestBody SysFile sysFile){
        QueryWrapper<SysFile> query = new QueryWrapper<>();
        query.eq("is_delete",false).eq("enable",true);
        fileMapper.updateById(sysFile);
        return  Result.success(fileMapper.selectList(query));
    }

    //批量删除
    @PostMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        QueryWrapper<SysFile> query = new QueryWrapper<>();
        query.in("id",ids);
        List<SysFile> sysFiles = fileMapper.selectList(query);
        for (SysFile sysFile : sysFiles) {
            sysFile.setIsDelete(true);
            fileMapper.updateById(sysFile);
        }

        return Result.success();
    }

    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name){

        QueryWrapper<SysFile> query = new QueryWrapper<>();
        query.eq("is_delete",false);
        if(!"".equals(name)){
            query.like("name",name);
        }


        return Result.success(fileMapper.selectPage(new Page<>(pageNum,pageSize),query));
    }

}
