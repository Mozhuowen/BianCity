package com.putaotown.geographic;

import java.io.Serializable;

public class GeoInfo implements Serializable
{
	private double longitude;
	private double latitude;
	private int lon_degree;
	private int lon_minute;
	private double lon_second;
	private int lat_degree;
	private int lat_minute;
	private double lat_second;
	private String address;
	private String country = "China";
	private String province;
	private String city;
	private String district;
	private String citycode;
	private float accuracy;
	private String street;
	private String road;
	private String freeaddr;
	private String screenpng;
	public void setScreenpng(String s) {
		this.screenpng = s;
	}
	public String getScreenpng() {
		return this.screenpng;
	}
	public void setFreeaddr(String a){
		this.freeaddr = a;
	}
	public String getFreeaddr(){
		return this.freeaddr;
	}
	public String getCitycode() {
		return citycode;
	}
	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getRoad() {
		return road;
	}
	public void setRoad(String road) {
		this.road = road;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public int getLon_degree() {
		return lon_degree;
	}
	public void setLon_degree(int lon_degree) {
		this.lon_degree = lon_degree;
	}
	public int getLon_minute() {
		return lon_minute;
	}
	public void setLon_minute(int lon_minute) {
		this.lon_minute = lon_minute;
	}
	public double getLon_second() {
		return lon_second;
	}
	public void setLon_second(double lon_second) {
		this.lon_second = lon_second;
	}
	public int getLat_degree() {
		return lat_degree;
	}
	public void setLat_degree(int lat_degree) {
		this.lat_degree = lat_degree;
	}
	public int getLat_minute() {
		return lat_minute;
	}
	public void setLat_minute(int lat_minute) {
		this.lat_minute = lat_minute;
	}
	public double getLat_second() {
		return lat_second;
	}
	public void setLat_second(double lat_second) {
		this.lat_second = lat_second;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	
}