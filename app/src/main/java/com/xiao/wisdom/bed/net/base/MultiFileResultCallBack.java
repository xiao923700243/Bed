package com.xiao.wisdom.bed.net.base;

import java.util.List;

/**
 * @author liyp
 * @version 1.0.0
 * @description 多文件上传回调
 * @createTime 2015/12/23
 * @editTime
 * @editor
 */
public abstract class MultiFileResultCallBack {
    public void onStart() {
    }


    public void onSuccess(List<FileUploadModel> fileUploadModels) {

    }

    public void onFailure(int statusCode, Exception e, FileUploadItem fileUploadItem, FileUploadModel fileUploadModel) {

    }

    public void onProgress(long bytesWritten, long totalSize, FileUploadItem fileUploadItem) {

    }
}
