package com.huli.foxread.rxhttp.parser;

import com.huli.foxread.entity.base.BaseEntity;
import com.huli.foxread.entity.base.PageList;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import androidx.annotation.NonNull;
import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.entity.ParameterizedTypeImpl;
import rxhttp.wrapper.exception.ParseException;
import rxhttp.wrapper.parse.AbstractParser;

/**
 * 项目名称：FoxRead
 * 创建人：Bill
 * 创建时间：2020/6/15  12:05
 * 备注：自定义Parser生成asXXXX方法
 * 输入T,输出T,并对code统一判断
 */
@Parser(name = "Response", wrappers = {List.class, PageList.class})
public class ResponseParser<T> extends AbstractParser<T> {

    //注意，以下两个构造方法是必须的

    /**
     * 此构造方法适用于任意Class对象，但更多用于带泛型的Class对象，如：List<Student>
     * <p>
     * 用法:
     * Java: .asParser(new ResponseParser<List<Student>>(){})
     * Kotlin: .asParser(object : ResponseParser<List<Student>>() {})
     * <p>
     * 注：此构造方法一定要用protected关键字修饰，否则调用此构造方法将拿不到泛型类型
     */
    protected ResponseParser() {
        super();
    }

    /**
     * 此构造方法仅适用于不带泛型的Class对象，如: Student.class
     * <p>
     * 用法
     * Java: .asParser(new ResponseParser<>(Student.class))   或者  .asResponse(Student.class)
     * Kotlin: .asParser(ResponseParser(Student::class.java)) 或者  .asResponse(Student::class.java)
     */
    public ResponseParser(Type type) {
        super(type);
    }

    @Override
    public T onParse(@NonNull okhttp3.Response response) throws IOException {
        final Type type = ParameterizedTypeImpl.get(BaseEntity.class, mType); //获取泛型类型
        BaseEntity<T> data = convert(response, type);
        T t = data.getData(); //获取data字段
        if (data.getError_code() != 0 || t == null) {//这里假设code不等于0，代表数据不正确，抛出异常
            throw new ParseException(String.valueOf(data.getError_code()), data.getMsg(), response);
        }
        return t;
    }
}