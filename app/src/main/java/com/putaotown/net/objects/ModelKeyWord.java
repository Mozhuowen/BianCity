package com.putaotown.net.objects;

import java.util.List;

public class ModelKeyWord extends BaseRequest
{
	private String keyword;
	private List<String> words;
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public List<String> getWords() {
		return words;
	}
	public void setWords(List<String> words) {
		this.words = words;
	}
}