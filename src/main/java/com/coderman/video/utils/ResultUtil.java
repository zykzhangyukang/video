package com.coderman.video.utils;


import com.coderman.video.constant.ResultConstant;
import com.coderman.video.vo.ResultVO;

import java.util.List;
import java.util.Map;

/**
 * @author coderman
 * @Title: 响应结果工具类
 * @Description: TOD
 * @date 2022/1/116:04
 */
public class ResultUtil {


    public static <T> ResultVO<T> getResult(Class<T> clazz, int code, String msg, T vo) {

        ResultVO<T> t = new ResultVO<>();
        t.setCode(code);
        t.setMsg(msg);
        t.setResult(vo);


        return t;
    }

    /**
     * 返回成功
     *
     * @return
     */
    public static ResultVO<Void> getSuccess() {
        return getSuccess(null);
    }


    /**
     * 返回成功并携带数据
     *
     * @return
     */
    public static ResultVO<Void> getSuccess(String msg) {
        ResultVO<Void> t = new ResultVO<>();
        t.setCode(ResultConstant.RESULT_CODE_200);
        t.setMsg(msg);
        t.setResult(null);
        return t;
    }


    /**
     * 返回成功并携带数据
     *
     * @return
     */
    public static <T> ResultVO<T> getSuccess(Class<T> clazz, T vo) {
        ResultVO<T> t = new ResultVO<>();
        t.setCode(ResultConstant.RESULT_CODE_200);
        t.setMsg(null);
        t.setResult(vo);
        return t;
    }


    /**
     * 返回成功并携带数据
     *
     * @return
     */
    public static <T> ResultVO<T> getFail(String msg) {
        ResultVO<T> t = new ResultVO<>();
        t.setCode(ResultConstant.RESULT_CODE_402);
        t.setMsg(msg);
        t.setResult(null);
        return t;
    }


    /**
     * 返回成功并携带数据
     *
     * @return
     */
    public static <T> ResultVO<T> getWarn(String msg) {
        ResultVO<T> t = new ResultVO<>();
        t.setCode(ResultConstant.RESULT_CODE_405);
        t.setMsg(msg);
        t.setResult(null);
        return t;
    }


    /**
     * 返回成功并携带数据
     *
     * @return
     */
    public static <T> ResultVO<T> getFail(int code, String msg) {
        ResultVO<T> t = new ResultVO<>();
        t.setCode(code);
        t.setMsg(msg);
        t.setResult(null);
        return t;
    }


    /**
     * 返回成功并携带数据
     *
     * @return
     */
    public static <T> ResultVO<T> getFail(Class<T> clazz, T vo, String msg) {
        ResultVO<T> t = new ResultVO<>();
        t.setCode(ResultConstant.RESULT_CODE_402);
        t.setMsg(msg);
        t.setResult(vo);
        return t;
    }


    /**
     * 返回成功并携带数据
     *
     * @return
     */
    public static <T> ResultVO<T> getWarn(Class<T> clazz, T vo, String msg) {
        ResultVO<T> t = new ResultVO<>();
        t.setCode(ResultConstant.RESULT_CODE_405);
        t.setMsg(msg);
        t.setResult(vo);
        return t;
    }

    public static <T> ResultVO<List<T>> getList(Class<T> clazz, int code, String msg, List<T> list) {


        ResultVO<List<T>> t = new ResultVO<>();

        t.setCode(code);
        t.setMsg(msg);
        t.setResult(list);

        return t;
    }


    public static <T> ResultVO<List<T>> getSuccessList(Class<T> clazz, List<T> list) {

        return getList(clazz, ResultConstant.RESULT_CODE_200, null, list);
    }


    public static <T> ResultVO<List<T>> getFailList(Class<T> clazz, List<T> list, String msg) {

        return getList(clazz, ResultConstant.RESULT_CODE_402, msg, list);
    }

    public static <T> ResultVO<List<T>> getWarnList(Class<T> clazz, List<T> list, String msg) {

        return getList(clazz, ResultConstant.RESULT_CODE_405, msg, list);
    }


    public static <K, V> ResultVO<Map<K, V>> getMap(Class<?> clazz, int code, String msg, Map<K, V> map) {
        ResultVO<Map<K, V>> result = new ResultVO<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setResult(map);
        return result;
    }

    public static <K, V> ResultVO<Map<K, V>> getSuccessMap(Class<?> clazz, Map<K, V> map) {
        return getMap(clazz, ResultConstant.RESULT_CODE_200, null, map);
    }

    public static <K, V> ResultVO<Map<K, V>> getFailMap(Class<?> clazz, Map<K, V> map, String msg) {
        return getMap(clazz, ResultConstant.RESULT_CODE_402, msg, map);
    }

    public static <K, V> ResultVO<Map<K, V>> getWarnMap(Class<?> clazz, Map<K, V> map, String msg) {
        return getMap(clazz, ResultConstant.RESULT_CODE_405, msg, map);
    }


    public static boolean isSuccess(ResultVO<?> resultVO) {
        return ResultConstant.RESULT_CODE_200.equals(resultVO.getCode());
    }

}
