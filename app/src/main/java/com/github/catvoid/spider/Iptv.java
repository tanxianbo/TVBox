package com.github.catvoid.spider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Iptv extends Spider {
    private String siteUrl = "";
    private Context context = null;

    @Override
    public void init(Context context, String extend) throws Exception {
        this.context = context;
        this.siteUrl = extend;
        super.init(context);

    }

    protected HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        return headers;
    }

    /**
     * 获取分类数据 + 首页最近更新视频列表数据
     *
     * @param filter 是否开启筛选 关联的是 软件设置中 首页数据源里的筛选开关
     * @return
     */
    @Override
    public String homeContent(boolean filter) throws Exception {
        return this.httpRequest("/homeContent", null);
    }

    /**
     * 获取分类信息数据
     *
     * @param tid    连续剧电影综艺等分类id (class->type_id)
     * @param pg     页数 （滚屏触发）
     * @param filter 同homeContent方法中的filter
     * @param extend 筛选参数{k:v, k1:v1} ( filters->分类id->value->一元素对象)
     * @return
     */
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        extend.put("tid", tid);
        extend.put("pg", pg);
        String json = this.httpRequest("/categoryContent", extend);
        return json;
    }

    /**
     * 视频详情信息
     *
     * @param ids 视频id (list->vod_id)
     * @return
     */
    @Override
    public String detailContent(List<String> ids) throws Exception {
        HashMap<String, String> param = new HashMap<>();
        param.put("id", ids.get(0));
        String json = this.httpRequest("/detailContent", param);
        return json;
    }

    /**
     * 获取视频播放信息
     *
     * @param flag     播放源 (list->vod_play_from)
     * @param id       视频id (list->vod_play_url)
     * @param vipFlags 所有可能需要vip解析的源 (接口中的flags字段)
     * @return
     */
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        HashMap<String, String> param = new HashMap<>();
        param.put("flag", flag);
        param.put("id", id);
        param.put("vipFlags", TextUtils.join(",", vipFlags));
        String json = this.httpRequest("/playerContent", param);
        return json;
    }

    /**
     * 搜索
     *
     * @param key   搜索框的字
     * @param quick 是否播放页的快捷搜索
     * @return
     */
    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        HashMap<String, String> param = new HashMap<>();
        param.put("key", key);
        param.put("quick", quick ? "1" : "0");
        String json = this.httpRequest("/searchContent", param);
        return json;
    }

    private String httpRequest(String path, HashMap<String, String> params) throws Exception {
        Call call = null;
        if (params == null) {
            call = OkHttp.newCall(siteUrl + path);
        } else {
            ArrayMap<String, String> stringStringArrayMap = convertHashMapToArrayMap(params);
            call = OkHttp.newCall(siteUrl + path, stringStringArrayMap);
        }
        return call.execute().body().string();
    }

    @SuppressLint({"NewApi", "LocalSuppress"})
    private ArrayMap<String, String> convertHashMapToArrayMap(HashMap<String, String> hashMap) {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            arrayMap.put(entry.getKey(), entry.getValue());
        }
        return arrayMap;
    }
}
