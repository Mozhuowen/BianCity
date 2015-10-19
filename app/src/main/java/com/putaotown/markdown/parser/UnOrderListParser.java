/*
 *  Copyright (C) 2015, Jhuster, All Rights Reserved
 *
 *  Author:  Jhuster(lujun.hust@gmail.com)
 *  
 *  https://github.com/Jhuster/JNote
 *  
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 */
package com.putaotown.markdown.parser;

import com.putaotown.markdown.Markdown;
import com.putaotown.markdown.Markdown.MDParser;
import com.putaotown.markdown.Markdown.MDWord;

public class UnOrderListParser extends MDParser {
    
    private static final char KEY = '-';

    @Override
    public MDWord parseLineFmt(String content) {
        if(content.charAt(0)!=KEY) {
            return MDWord.NULL;
        }
        return new MDWord("",1,Markdown.MD_FMT_UNORDER_LIST);
    }

    @Override
    public MDWord parseWordFmt(String content) {
        return MDWord.NULL;
    }

}
