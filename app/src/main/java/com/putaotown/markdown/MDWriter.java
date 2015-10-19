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
package com.putaotown.markdown;

import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView.BufferType;

public class MDWriter {   
    
    public static final String HEADER = "# ";
    public static final String HEADER2 = "## ";
    public static final String HEADER3 = "### ";
    public static final String CENTER_LEFT  = "{";
    public static final String CENTER_RIGHT = "}";
    public static final String BOLD = "**";
    public static final String LIST  = "- ";
    public static final String QUOTE = "> ";
    
    private class Position {
        int start;
        int end;
        public Position(int start,int end) {
            this.start = start;
            this.end = end;
        }
    }
    
    private final EditText mEditText;    
    
    public MDWriter(EditText edittext) {
        mEditText = edittext;
    }
    
    public void setContent(String content) {
        mEditText.setText(content,BufferType.EDITABLE);
    }
    
    public boolean setAsHeader() {
        String content = mEditText.getText().toString();
        Position position = getCurrentLinePosition(content);
        if(content.substring(position.start,position.end).startsWith(HEADER3)) {
            return false;
        }
//        Log.v("MDWriter info: ", "setAsHeader()! content: "+content);
        //修复不在开头就不能自动插入标题markdown的问题
        if(!this.getCurrentLineContent(content).startsWith("#")) {
            insert(position.start," ");
//            Log.v("MDWriter info: ", "setAsHeader()! insert blank space!");
        }   
        insert(position.start,"#");
//        Log.v("MDWriter info: ", "setAsHeader()! insert normal #");
        return true;
    }
        
    public boolean setAsCenter() {   
        String content = mEditText.getText().toString();
        Position position = getCurrentLinePosition(content);
        if(content.substring(position.start,position.end).startsWith(CENTER_LEFT)) {
            return false;
        }
        insert(position.start,CENTER_LEFT);
        insert(position.end+1,CENTER_RIGHT);             
        return true;
    }
    
    public boolean setAsBold() {
        insert(getCurrentPosition(),BOLD);
        return true;
    }
    
    public boolean setAsList() {
        String content = mEditText.getText().toString();
        Position position = getCurrentLinePosition(content); 
        if(content.substring(position.start,position.end).startsWith(LIST)) {
            return false;
        }
        insert(position.start,LIST);            
        return true;        
    }
    
    public boolean setAsQuote() {               
        String content = mEditText.getText().toString();
        Position position = getCurrentLinePosition(content); 
        if(content.substring(position.start,position.end).startsWith(QUOTE)) {
            return false;
        }
        insert(position.start,QUOTE);            
        return true;       
    }
    
    public String getTitle() {
        String content = mEditText.getText().toString();
        if("".equals(content)) {
            return "";
        }
        int end = content.indexOf("\n");        
        return content.substring(0,end==-1?content.length():end); 
    }
    
    public String getContent() {
        return mEditText.getText().toString();
    }

    protected Position getCurrentLinePosition(String content) {
        int index = 0;
        if("".equals(content)) {
            return new Position(0,0); 
        }
        Position position = new Position(-1,-1);        
        //Find the line header "\n"         
        index = getCurrentPosition();
        while(index>1 && content.charAt(index-1)!='\n') {
            index--;
        }
        position.start = index==1?0:index;
        index = getCurrentPosition();
        while(index<content.length() && content.charAt(index)!='\n' ) {
            index++;
        }
        position.end = index;
        return position;
    }
    
    protected String getCurrentLineContent(String content) {
    	int start = this.getCurrentPosition();
    	int end = start;
    	while (start > 1 && content.charAt(start -1 ) != '\n') {
    		start--;
    	}
    	while (end < content.length() && content.charAt(end + 1) != '\n') {
    		end++;
    	}
    	return content.substring(start, end);
    }
    
    protected int getCurrentPosition() {
        return mEditText.getSelectionStart();
    }
    
    protected void insert(int index,String text) {
        Editable editor = mEditText.getEditableText();
        if( index < 0 || index >= editor.length() ) {
            editor.append(text);
        }
        else{
            editor.insert(index,text);
        }
    }
}
