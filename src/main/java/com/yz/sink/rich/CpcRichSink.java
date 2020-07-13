package com.yz.sink.rich;

import com.yz.pojo.WordDto;

import java.sql.PreparedStatement;

/**
 * @author yz
 * @Description
 * @create 2020-07-03 15:35
 */
public class CpcRichSink extends BaseRichSink<WordDto> {


    @Override
    protected void otherFieldConfirm(WordDto mode, PreparedStatement ps, int next) {

    }
}
